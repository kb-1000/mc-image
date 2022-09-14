/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.agent.transformers;

import de.kb1000.mcimage.agent.BooleanVariable;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.FieldVisitor;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM8;

public class EnumSwitchCaseStableTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(Module module, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        char c = className.charAt(className.length() - 1);
        if (c <= '9' && c >= '0' && className.contains("$")) {
            var isChanged = new BooleanVariable();
            ClassReader classReader = new ClassReader(classfileBuffer);
            ClassWriter classWriter = new ClassWriter(classReader, 0);
            classReader.accept(new ClassVisitor(ASM8, classWriter) {
                @Override
                public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                    FieldVisitor fv = super.visitField(access, name, descriptor, signature, value);
                    if (fv != null && name.startsWith("$SwitchMap$") && descriptor.equals("[I")) {
                        isChanged.value = true;
                        fv.visitAnnotation("Ljdk/internal/vm/annotation/Stable;", true);
                    }
                    return fv;
                }
            }, 0);
            if (isChanged.value)
                return classWriter.toByteArray();
        }
        return null;
    }
}
