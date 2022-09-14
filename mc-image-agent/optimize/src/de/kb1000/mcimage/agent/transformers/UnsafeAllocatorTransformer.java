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

public class UnsafeAllocatorTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        return switch (className) {
            case "com/google/gson/internal/UnsafeAllocator" -> {
                final var classReader = new ClassReader(classfileBuffer);
                final var classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                classReader.accept(new ClassVisitor(ASM8, classWriter) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                        if ("create".equals(name)) {
                            var mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                            if (mv != null) {
                                mv.visitCode();
                                mv.visitFieldInsn(GETSTATIC, className, "INSTANCE", 'L' + className + ';');
                                mv.visitInsn(ARETURN);
                                mv.visitMaxs(-1, -1);
                                mv.visitEnd();
                            }
                            mv = super.visitMethod(ACC_PUBLIC | ACC_STATIC, "<clinit>", "()V", null, null);
                            if (mv != null) {
                                mv.visitCode();
                                mv.visitMethodInsn(INVOKESTATIC, className, "create$", descriptor, false);
                                mv.visitFieldInsn(PUTSTATIC, className, "INSTANCE", 'L' + className + ';');
                                mv.visitInsn(RETURN);
                                mv.visitMaxs(-1, -1);
                                mv.visitEnd();
                            }
                            var fv = super.visitField(ACC_PUBLIC | ACC_STATIC | ACC_FINAL, "INSTANCE", 'L' + className + ';', null, null);
                            if (fv != null)
                                fv.visitEnd();
                            name = "create$";
                        }
                        return super.visitMethod(access, name, descriptor, signature, exceptions);
                    }
                }, 0);
                yield classWriter.toByteArray();
            }
            default -> null;
        };
    }
}
