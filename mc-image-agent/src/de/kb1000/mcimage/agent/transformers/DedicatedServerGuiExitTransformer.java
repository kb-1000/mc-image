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
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import static de.kb1000.mcimage.agent.Mappings.Util.isLoaded;
import static jdk.internal.org.objectweb.asm.Opcodes.*;

public class DedicatedServerGuiExitTransformer implements ClassFileTransformer {
    private static class Util {
        private static final String dedicatedServer = Mappings.CLASS_MAPPED_UNMAPPED.get("net/minecraft/unmapped/C_buaiqtuw");
        private static final String guiFieldName = Mappings.FIELD_MAPPED_UNMAPPED.get("f_taiusxkj");
    }

    @Override
    public byte[] transform(Module module, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        // Defer loading until we see the first Minecraft class
        if (!isLoaded && className.length() < 4) {
            isLoaded = true;
        }
        if (isLoaded) {
            if (className.equals(Util.dedicatedServer)) {
                ClassReader classReader = new ClassReader(classfileBuffer);
                ClassWriter classWriter = new ClassWriter(classReader, 0);
                classReader.accept(new ClassVisitor(ASM8, classWriter) {
                    @Override
                    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                        return name.equals(Util.guiFieldName) ? null : super.visitField(access, name, descriptor, signature, value);
                    }

                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                        return new MethodVisitor(ASM8, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                            @Override
                            public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                                if (name.equals(Util.guiFieldName) && owner.equals(Util.dedicatedServer)) {
                                    if (opcode == GETFIELD) {
                                        super.visitInsn(POP);
                                        super.visitInsn(ACONST_NULL);
                                    } else if (opcode == PUTFIELD) {
                                        super.visitInsn(POP2);
                                    }
                                } else
                                    super.visitFieldInsn(opcode, owner, name, descriptor);
                            }
                        };
                    }
                }, 0);
                return classWriter.toByteArray();
            }
        }
        return null;
    }
}
