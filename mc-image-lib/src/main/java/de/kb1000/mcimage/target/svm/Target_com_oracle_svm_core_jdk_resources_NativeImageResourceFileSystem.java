/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.svm;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import com.oracle.svm.core.jdk.resources.NativeImageResourceFileSystem;

import java.util.Map;

@TargetClass(NativeImageResourceFileSystem.class)
final class Target_com_oracle_svm_core_jdk_resources_NativeImageResourceFileSystem {
    // ZipFileSystem doesn't seem to have this check, thus Minecraft doesn't set create=true
    // Because modifying MC code directly would require version-specific code, kill the check instead
    @Substitute
    static boolean isTrue(Map<String, ?> env) {
        return true;
    }
}
