/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.agent.transformers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.ProtectionDomain;

public class ClassDumper implements ClassFileTransformer {
    @Override
    public byte[] transform(Module module, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        var file = Path.of("classes", className + ".class");
        try {
            Files.createDirectories(file.getParent());
            try (final OutputStream os = Files.newOutputStream(file)) {
                os.write(classfileBuffer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
