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

import static jdk.internal.org.objectweb.asm.Opcodes.ASM8;

public class SubstitutionRemapperTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className.startsWith("de/kb1000/mcimage/target")) {
            ClassReader classReader = new ClassReader(classfileBuffer);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
            classReader.accept(new ClassVisitor(ASM8, classWriter) {
                @Override
                public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                    return new FieldVisitor(ASM8, super.visitField(access, name, descriptor, signature, value)) {
                        @Override
                        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                            AnnotationVisitor av = super.visitAnnotation(descriptor, visible);
                            if ("Lcom/oracle/svm/core/annotate/TargetElement;".equals(descriptor)) {
                                return new AnnotationVisitor(ASM8, av) {
                                    @Override
                                    public void visit(String name, Object value) {
                                        if ("name".equals(name))
                                            super.visit(name, Mappings.FIELD_MAPPED_UNMAPPED.getOrDefault((String) value, (String) value));
                                        else
                                            super.visit(name, value);
                                    }
                                };
                            }
                            return av;
                        }
                    };
                }

                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    return new MethodVisitor(ASM8, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                        @Override
                        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                            AnnotationVisitor av = super.visitAnnotation(descriptor, visible);
                            if ("Lcom/oracle/svm/core/annotate/TargetElement;".equals(descriptor)) {
                                return new AnnotationVisitor(ASM8, av) {
                                    @Override
                                    public void visit(String name, Object value) {
                                        if ("name".equals(name))
                                            super.visit(name, Mappings.METHOD_MAPPED_UNMAPPED.getOrDefault((String) value, (String) value));
                                        else
                                            super.visit(name, value);
                                    }
                                };
                            }
                            return av;
                        }
                    };
                }
            }, 0);
            return classWriter.toByteArray();
        }
        return null;
    }
}
