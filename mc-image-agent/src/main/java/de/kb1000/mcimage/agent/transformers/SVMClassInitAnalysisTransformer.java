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

import static jdk.internal.org.objectweb.asm.Opcodes.*;

// conditional breakpoints are too slow
public class SVMClassInitAnalysisTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(Module module, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className.equals("com/oracle/svm/hosted/classinitialization/ConfigurableClassInitialization")) {
            ClassReader classReader = new ClassReader(classfileBuffer);
            ClassWriter classWriter = new ClassWriter(classReader, 0);
            classReader.accept(new ClassVisitor(ASM8, classWriter) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    if (name.equals("computeInitKindAndMaybeInitializeClass")) {
                        return new MethodVisitor(ASM8, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                            @Override
                            public void visitCode() {
                                super.visitCode();
                                Label label = new Label();
                                super.visitVarInsn(ALOAD, 1);
                                super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
                                super.visitLdcInsn("bcu");
                                super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false);
                                super.visitJumpInsn(IFEQ, label);
                                super.visitMethodInsn(INVOKESTATIC, "de/kb1000/mcimage/agent/DebugHooks", "hook", "()V", false);
                                super.visitFrame(F_SAME, Type.getArgumentTypes(descriptor).length + 1, null, 0, null);
                                super.visitLabel(label);
                            }
                        };
                    }
                    return super.visitMethod(access, name, descriptor, signature, exceptions);
                }
            }, 0);
            return classWriter.toByteArray();
        }
        return null;
    }
}
