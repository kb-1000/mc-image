/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.agent.transformers;

import de.kb1000.mcimage.agent.Mappings;
import jdk.internal.org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

import static de.kb1000.mcimage.agent.Mappings.Util.isLoaded;
import static jdk.internal.org.objectweb.asm.Opcodes.ASM8;
import static jdk.internal.org.objectweb.asm.Opcodes.POP;

public class InputUtilTransformer implements ClassFileTransformer {
    private static class Util {
        private static final String inputUtilName = Mappings.CLASS_MAPPED_UNMAPPED.get("net/minecraft/unmapped/C_vdmgqzcl");
    }
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        // Defer loading until we see the first Minecraft class
        if (!isLoaded && className.length() < 4) {
            isLoaded = true;
        }
        if (isLoaded) {
            // InputUtil
            if (className.equals(Util.inputUtilName)) {
                ClassReader classReader = new ClassReader(classfileBuffer);
                ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
                classReader.accept(new ClassVisitor(ASM8, classWriter) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                        if (name.equals("<clinit>")) {
                            return new MethodVisitor(ASM8, mv) {
                                @Override
                                public void visitLdcInsn(Object value) {
                                    if ("GLFW_RAW_MOUSE_MOTION".equals(value)) {
                                        super.visitInsn(POP);
                                        super.visitLdcInsn(Type.getObjectType("de/kb1000/mcimage/util/GLFWAlias"));
                                    }
                                    super.visitLdcInsn(value);
                                }
                            };
                        }
                        return mv;
                    }
                }, 0);
                return classWriter.toByteArray();
            }
        }
        return null;
    }
}
