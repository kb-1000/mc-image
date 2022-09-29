/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.agent.transformers;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

public class FallbackFeatureTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className.equals("com/oracle/svm/hosted/FallbackFeature")) {
            ClassReader classReader = new ClassReader(classfileBuffer);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
            classReader.accept(new ClassVisitor(ASM8, classWriter) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                    if (name.equals("isInConfiguration")) {
                        return new MethodVisitor(ASM8, mv) {
                            @Override
                            public void visitInsn(int opcode) {
                                if (opcode == IRETURN) {
                                    super.visitMethodInsn(INVOKESTATIC, "de/kb1000/mcimage/agent/Hooks", "transformFallbackInConfig", "(Z)Z", false);
                                }
                                super.visitInsn(opcode);
                            }
                        };
                    }
                    return mv;
                }
            }, 0);
            return classWriter.toByteArray();
        }
        return null;
    }
}
