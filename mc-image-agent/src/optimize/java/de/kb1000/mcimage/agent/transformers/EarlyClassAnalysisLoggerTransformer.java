/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.agent.transformers;

import jdk.internal.org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

public class EarlyClassAnalysisLoggerTransformer implements ClassFileTransformer {
    public static Predicate methodPredicate;
    public static BiPredicate fieldPredicate;

    @Override
    public byte[] transform(Module module, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            if (className.equals("com/oracle/svm/hosted/classinitialization/EarlyClassInitializerAnalysis")) {
                final ClassReader classReader = new ClassReader(classfileBuffer);
                final ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
                classReader.accept(new ClassVisitor(ASM8, classWriter) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                        if (name.equals("canInitializeWithoutSideEffects") && descriptor.equals("(Ljdk/vm/ci/meta/ResolvedJavaMethod;Ljava/util/Set;Lorg/graalvm/compiler/options/OptionValues;Lorg/graalvm/compiler/debug/DebugContext;)Z"))
                            return new MethodVisitor(ASM8, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                                @Override
                                public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                                    if (owner.equals("com/oracle/svm/hosted/classinitialization/AbortOnDisallowedNode") && name.equals("<init>")) {
                                        super.visitInsn(DUP);
                                        super.visitVarInsn(ALOAD, 2);
                                        super.visitFieldInsn(PUTFIELD, "com/oracle/svm/hosted/classinitialization/AbortOnDisallowedNode", "analyzedClasses", Type.getDescriptor(Set.class));
                                    }
                                }
                            };
                        return super.visitMethod(access, name, descriptor, signature, exceptions);
                    }
                }, 0);
                return classWriter.toByteArray();
            } else if (className.equals("com/oracle/svm/hosted/classinitialization/AbortOnRecursiveInliningPlugin")) {
                final ClassReader classReader = new ClassReader(classfileBuffer);
                final ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
                classReader.accept(new ClassVisitor(ASM8, classWriter) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                        if (name.equals("shouldInlineInvoke")) {
                            return new MethodVisitor(ASM8, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                                @Override
                                public void visitCode() {
                                    super.visitCode();
                                    super.visitFieldInsn(GETSTATIC, Type.getInternalName(EarlyClassAnalysisLoggerTransformer.class), "methodPredicate", Type.getDescriptor(Predicate.class));
                                    super.visitVarInsn(ALOAD, 2);
                                    super.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Predicate.class), "test", "(Ljava/lang/Object;)Z", true);
                                    final Label label = new Label();
                                    super.visitJumpInsn(IFEQ, label);
                                    super.visitFieldInsn(GETSTATIC, "org/graalvm/compiler/nodes/graphbuilderconf/InlineInvokePlugin$InlineInfo", "DO_NOT_INLINE_WITH_EXCEPTION", "Lorg/graalvm/compiler/nodes/graphbuilderconf/InlineInvokePlugin$InlineInfo;");
                                    super.visitInsn(ARETURN);
                                    super.visitLabel(label);
                                    super.visitFrame(F_SAME, 0, null, 0, null);
                                }
                            };
                        }
                        return super.visitMethod(access, name, descriptor, signature, exceptions);
                    }
                }, 0);
                return classWriter.toByteArray();
            } else if (className.equals("com/oracle/svm/hosted/classinitialization/AbortOnDisallowedNode")) {
                final ClassReader classReader = new ClassReader(classfileBuffer);
                final ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);
                classReader.accept(new ClassVisitor(ASM8, classWriter) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                        if (name.equals("nodeAdded")) {
                            var fv = super.visitField(0, "analyzedClasses", Type.getDescriptor(Set.class), null, null);
                            if (fv != null) fv.visitEnd();
                            return new MethodVisitor(ASM8, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                                @Override
                                public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                                    if (name.equals("getTargetMethod")) {
                                        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                                        super.visitInsn(DUP);
                                        super.visitFieldInsn(GETSTATIC, Type.getInternalName(EarlyClassAnalysisLoggerTransformer.class), "methodPredicate", Type.getDescriptor(Predicate.class));
                                        super.visitInsn(SWAP);
                                        super.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Predicate.class), "test", "(Ljava/lang/Object;)Z", true);
                                        final Label label = new Label();
                                        super.visitJumpInsn(IFEQ, label);
                                        super.visitInsn(RETURN);
                                        super.visitLabel(label);
                                        super.visitFrame(F_SAME, 0, null, 0, null);
                                    } else if (name.equals("isStatic")) {
                                        super.visitFieldInsn(GETSTATIC, Type.getInternalName(EarlyClassAnalysisLoggerTransformer.class), "fieldPredicate", Type.getDescriptor(BiPredicate.class));
                                        super.visitInsn(SWAP);
                                        super.visitVarInsn(ALOAD, 0);
                                        super.visitFieldInsn(GETFIELD, className, "analyzedClasses", Type.getDescriptor(Set.class));
                                        super.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(BiPredicate.class), "test", "(Ljava/lang/Object;Ljava/lang/Object;)Z", true);
                                    } else {
                                        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                                    }
                                }
                            };
                        }
                        return super.visitMethod(access, name, descriptor, signature, exceptions);
                    }
                }, ClassReader.SKIP_FRAMES);
                return classWriter.toByteArray();
            }
            return null;
        } catch (Exception e) {
            throw (IllegalClassFormatException) new IllegalClassFormatException().initCause(e);
        }
    }
}
