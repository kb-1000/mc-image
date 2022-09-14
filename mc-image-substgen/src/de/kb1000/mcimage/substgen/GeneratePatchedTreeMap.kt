/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.substgen

import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.commons.Remapper
import org.objectweb.asm.commons.SignatureRemapper
import org.objectweb.asm.signature.SignatureReader
import org.objectweb.asm.signature.SignatureVisitor
import org.objectweb.asm.signature.SignatureWriter
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.MethodNode
import java.io.InputStream
import java.io.ObjectInputStream
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.div
import kotlin.io.path.writeBytes

val OBJECT_INPUT_STREAM: Type = Type.getType(ObjectInputStream::class.java)
val OBJECT: Type = Type.getType(Any::class.java)
val ITERATOR: Type = Type.getType(Iterator::class.java)

fun generatePatchedTreeMap() {
    val targetDir = Path("java.base")

    val targetFile = targetDir / "java" / "util" / "TreeMap.class"
    targetFile.parent.createDirectories()

    val bytecode = TreeMap::class.java.module.getResourceAsStream("java/util/TreeMap.class").use(InputStream::readAllBytes)
    val classReader = ClassReader(bytecode)
    val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES)
    classReader.accept(object : ClassVisitor(ASM9, object : ClassVisitor(ASM9, classWriter) {
        override fun visitMethod(access: Int, name: String, descriptor: String, signature: String?, exceptions: Array<out String>?): MethodVisitor {
            val type: Type = Type.getMethodType(descriptor)
            val useOis = type.argumentTypes.contains(OBJECT_INPUT_STREAM)
            return object : MethodVisitor(ASM9, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                override fun visitMethodInsn(opcode: Int, owner: String, name: String, descriptor: String, isInterface: Boolean) {
                    val newDescriptor = if ("buildFromSorted" == name) {
                        val methodType = Type.getMethodType(descriptor)
                        Type.getMethodDescriptor(methodType.returnType, *methodType.argumentTypes.map {
                            if (useOis && it == ITERATOR) OBJECT
                            else if (!useOis && it == OBJECT_INPUT_STREAM) OBJECT
                            else it
                        }.toTypedArray())
                    } else descriptor
                    super.visitMethodInsn(opcode, owner, name, newDescriptor, isInterface)
                }
            }
        }
    }) {
        override fun visitMethod(access: Int, name: String, descriptor: String, signature: String?, exceptions: Array<String>?): MethodVisitor? {
            fun visitMethod(access: Int, name: String, descriptor: String, signature: String?, exceptions: Array<String>?): MethodVisitor? =
                    super.visitMethod(access, name, descriptor, signature, exceptions)

            val type: Type = Type.getMethodType(descriptor)
            if ("buildFromSorted" == name)
                return object : MethodNode(ASM9) {
                    override fun visitEnd() {
                        val thisOffset = if (access and ACC_STATIC != 0) 0 else 1
                        val argTypes: Array<Type> = type.argumentTypes
                        val oisIndex = argTypes.indexOf(OBJECT_INPUT_STREAM)
                        val iterIndex = argTypes.indexOf(ITERATOR)
                        val noOisType = Type.getMethodType(type.returnType, *argTypes.clone().apply { this[oisIndex] = OBJECT })
                        val noIterType = Type.getMethodType(type.returnType, *argTypes.clone().apply { this[iterIndex] = OBJECT })
                        fun replaceArg(argType: Type, index: Int, nonNullIndex: Int, methodType: Type) {
                            fun processSignature(signature: String?, isType: Boolean): String? {
                                // should only touch parameters, but works for now
                                val signatureReader = SignatureReader(signature ?: return null)
                                val signatureWriter = SignatureWriter()
                                val signatureVisitor = object : SignatureRemapper(signatureWriter, object : Remapper() {
                                    override fun map(internalName: String): String {
                                        if (Type.getObjectType(internalName) == argType) return OBJECT.internalName
                                        return super.map(internalName)
                                    }
                                }) {
                                    var ignoreTypeArgs = false
                                    override fun visitClassType(name: String) {
                                        if (Type.getObjectType(name) == argType) {
                                            ignoreTypeArgs = true
                                            super.visitClassType(OBJECT.internalName)
                                        } else
                                            super.visitClassType(name)
                                    }

                                    override fun visitInnerClassType(name: String) {
                                        if (!ignoreTypeArgs)
                                            super.visitInnerClassType(name)
                                    }

                                    override fun visitTypeArgument() {
                                        if (!ignoreTypeArgs)
                                            super.visitTypeArgument()
                                    }

                                    override fun visitTypeArgument(wildcard: Char): SignatureVisitor {
                                        if (ignoreTypeArgs)
                                            return object : SignatureVisitor(ASM9) {}
                                        return super.visitTypeArgument(wildcard)
                                    }

                                    override fun visitEnd() {
                                        super.visitEnd()
                                        ignoreTypeArgs = false
                                    }
                                }
                                if (isType)
                                    signatureReader.acceptType(signatureVisitor)
                                else
                                    signatureReader.accept(signatureVisitor)
                                return signatureWriter.toString()
                            }
                            accept(object : MethodVisitor(ASM9, visitMethod(access, name, methodType.descriptor, processSignature(signature, false), exceptions)) {
                                var lastInstructionIsNull = false
                                var lastInstructionIsNonNullParam = false
                                override fun visitVarInsn(opcode: Int, varIndex: Int) {
                                    lastInstructionIsNull = false
                                    lastInstructionIsNonNullParam = false
                                    if (varIndex == nonNullIndex)
                                        lastInstructionIsNonNullParam = true
                                    if (varIndex != index)
                                        super.visitVarInsn(opcode, varIndex)
                                    else {
                                        if (opcode != ALOAD)
                                            throw IllegalStateException()
                                        visitInsn(ACONST_NULL)
                                    }
                                }

                                override fun visitInsn(opcode: Int) {
                                    lastInstructionIsNull = false
                                    lastInstructionIsNonNullParam = false
                                    if (opcode == ACONST_NULL)
                                        lastInstructionIsNull = true
                                    super.visitInsn(opcode)
                                }

                                override fun visitIntInsn(opcode: Int, operand: Int) {
                                    lastInstructionIsNull = false
                                    lastInstructionIsNonNullParam = false
                                    super.visitIntInsn(opcode, operand)
                                }

                                override fun visitTypeInsn(opcode: Int, type: String) {
                                    lastInstructionIsNull = false
                                    lastInstructionIsNonNullParam = false
                                    super.visitTypeInsn(opcode, type)
                                }

                                override fun visitFieldInsn(opcode: Int, owner: String, name: String, descriptor: String) {
                                    lastInstructionIsNull = false
                                    lastInstructionIsNonNullParam = false
                                    super.visitFieldInsn(opcode, owner, name, descriptor)
                                }

                                override fun visitMethodInsn(opcode: Int, owner: String, name: String, descriptor: String, isInterface: Boolean) {
                                    lastInstructionIsNull = false
                                    lastInstructionIsNonNullParam = false
                                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
                                }

                                override fun visitInvokeDynamicInsn(name: String, descriptor: String, bootstrapMethodHandle: Handle, vararg bootstrapMethodArguments: Any?) {
                                    lastInstructionIsNull = false
                                    lastInstructionIsNonNullParam = false
                                    super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, *bootstrapMethodArguments)
                                }

                                override fun visitJumpInsn(opcode: Int, label: Label) {
                                    if ((lastInstructionIsNonNullParam || lastInstructionIsNull) && (opcode == IFNULL || opcode == IFNONNULL)) {
                                        super.visitInsn(POP)
                                        if ((opcode == IFNULL) == lastInstructionIsNull) {
                                            super.visitJumpInsn(GOTO, label)
                                        }
                                    } else super.visitJumpInsn(opcode, label)
                                    lastInstructionIsNull = false
                                    lastInstructionIsNonNullParam = false
                                }

                                override fun visitLabel(label: Label) {
                                    lastInstructionIsNull = false
                                    lastInstructionIsNonNullParam = false
                                    super.visitLabel(label)
                                }

                                override fun visitLdcInsn(value: Any) {
                                    lastInstructionIsNull = false
                                    lastInstructionIsNonNullParam = false
                                    super.visitLdcInsn(value)
                                }

                                override fun visitIincInsn(varIndex: Int, increment: Int) {
                                    lastInstructionIsNull = false
                                    lastInstructionIsNonNullParam = false
                                    super.visitIincInsn(varIndex, increment)
                                }

                                override fun visitTableSwitchInsn(min: Int, max: Int, dflt: Label, vararg labels: Label) {
                                    lastInstructionIsNull = false
                                    lastInstructionIsNonNullParam = false
                                    super.visitTableSwitchInsn(min, max, dflt, *labels)
                                }

                                override fun visitLookupSwitchInsn(dflt: Label, keys: IntArray, labels: Array<Label>) {
                                    lastInstructionIsNull = false
                                    lastInstructionIsNonNullParam = false
                                    super.visitLookupSwitchInsn(dflt, keys, labels)
                                }

                                override fun visitMultiANewArrayInsn(descriptor: String, numDimensions: Int) {
                                    lastInstructionIsNull = false
                                    lastInstructionIsNonNullParam = false
                                    super.visitMultiANewArrayInsn(descriptor, numDimensions)
                                }

                                override fun visitLocalVariable(name: String, descriptor: String, signature: String?, start: Label, end: Label, localIndex: Int) {
                                    val newSignature: String?
                                    val newDescriptor = if (localIndex == index && (instructions.first { it is LabelNode } as LabelNode).label == start) {
                                        newSignature = processSignature(signature, true)
                                        OBJECT.descriptor
                                    } else {
                                        newSignature = signature
                                        descriptor
                                    }
                                    super.visitLocalVariable(name, newDescriptor, newSignature, start, end, localIndex)
                                }
                            })
                        }
                        replaceArg(OBJECT_INPUT_STREAM, oisIndex + thisOffset, iterIndex + thisOffset, noOisType)
                        replaceArg(ITERATOR, iterIndex + thisOffset, oisIndex + thisOffset, noIterType)
                    }
                }
            return visitMethod(access, name, descriptor, signature, exceptions)
        }
    }, ClassReader.SKIP_FRAMES)
    val classReReader = ClassReader(classWriter.toByteArray())
    val classReWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES)
    classReReader.accept(classReWriter, ClassReader.SKIP_FRAMES)
    targetFile.writeBytes(classReWriter.toByteArray())
}