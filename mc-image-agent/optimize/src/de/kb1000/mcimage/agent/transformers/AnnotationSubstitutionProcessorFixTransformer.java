/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.agent.transformers;

import de.kb1000.mcimage.agent.Hooks;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

// that moment when you have to ASM into a code replacement engine to fix it

public class AnnotationSubstitutionProcessorFixTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className.equals("com/oracle/svm/hosted/substitute/AnnotationSubstitutionProcessor")) {
            ClassReader classReader = new ClassReader(classfileBuffer);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
            classReader.accept(new ClassVisitor(ASM8, classWriter) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    MethodVisitor mv_ = super.visitMethod(access, name, descriptor, signature, exceptions);
                    if (mv_ != null && name.equals("handleFieldInAliasClass") && descriptor.equals("(Ljava/lang/reflect/Field;Ljava/lang/Class;)V")) {
                        return new MethodNode(ASM8) {
                            @Override
                            public void visitEnd() {
                                super.visitEnd();
                                AbstractInsnNode insn = instructions.getFirst();
                                while (!(insn.getOpcode() == INVOKEINTERFACE &&
                                        ((MethodInsnNode) insn).owner.equals("java/util/Map") &&
                                        ((MethodInsnNode) insn).name.equals("replaceAll") &&
                                        ((MethodInsnNode) insn).desc.equals("(Ljava/util/function/BiFunction;)V")))
                                    insn = insn.getNext();
                                while (!(insn.getOpcode() == INVOKEVIRTUAL &&
                                        ((MethodInsnNode) insn).owner.equals("java/lang/Object") &&
                                        ((MethodInsnNode) insn).name.equals("equals") &&
                                        ((MethodInsnNode) insn).desc.equals("(Ljava/lang/Object;)Z")))
                                    insn = insn.getPrevious();
                                instructions.insertBefore(insn, new VarInsnNode(ALOAD, localVariables.stream().filter(local -> local.name.equals("computedAlias")).toArray(LocalVariableNode[]::new)[0].index));
                                instructions.set(insn, new MethodInsnNode(INVOKESTATIC, Type.getInternalName(Hooks.class), "equalsAnnotationSubstitutionProcessorOriginalField", "(Ljdk/vm/ci/meta/ResolvedJavaField;Ljdk/vm/ci/meta/ResolvedJavaField;Ljdk/vm/ci/meta/ResolvedJavaField;)Z", false));
                                accept(mv_);
                            }
                        };
                    }
                    return mv_;
                }
            }, 0);
            return classWriter.toByteArray();
        }
        return null;
    }
}
