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

import static jdk.internal.org.objectweb.asm.Opcodes.ASM8;

// workaround for https://github.com/oracle/graal/issues/4982
public class HashMapComparableForTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(Module module, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        switch (className) {
            case "com/oracle/svm/core/jdk.Target_java_util_HashMap":
            case "com/oracle/svm/core/jdk.Target_java_util_concurrent_ConcurrentHashMap":
                ClassReader classReader = new ClassReader(classfileBuffer);
                ClassWriter classWriter = new ClassWriter(classReader, 0);
                classReader.accept(new ClassVisitor(ASM8) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                        return name.equals("comparableClassFor") ? null : super.visitMethod(access, name, descriptor, signature, exceptions);
                    }
                }, 0);
                return classWriter.toByteArray();
            default:
                return null;
        }
    }
}
