/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.commons;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@TargetClass(className = "org.apache.commons.io.Java7Support")
@Substitute
final class Target_org_apache_commons_io_Java7Support {
    @Substitute
    static boolean isSymLink(File file) {
        return Files.isSymbolicLink(file.toPath());
    }

    @Substitute
    static File readSymbolicLink(File symlink) throws IOException {
        return Files.readSymbolicLink(symlink.toPath()).toFile();
    }

    @Substitute
    static boolean exists(File file) {
        return Files.exists(file.toPath());
    }

    @Substitute
    static File createSymbolicLink(File symlink, File target) throws IOException {
        if (!exists(symlink)) {
            return Files.createSymbolicLink(symlink.toPath(), target.toPath()).toFile();
        }
        return symlink;
    }

    @Substitute
    static void delete(File file) throws IOException {
        Files.delete(file.toPath());
    }

    @Substitute
    static boolean isAtLeastJava7() {
        return true;
    }
}
