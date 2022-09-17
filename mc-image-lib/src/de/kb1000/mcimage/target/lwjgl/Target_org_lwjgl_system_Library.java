/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.lwjgl;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import de.kb1000.mcimage.util.Environment;

import java.util.function.Consumer;

@TargetClass(className = "org.lwjgl.system.Library", onlyWith = Environment.ClientOnly.class)
final class Target_org_lwjgl_system_Library {
    // loadSystem *should* only be used for loading JNI libs, and JNI libs are unwanted here.
    // Disable this code.
    @Substitute
    static void loadSystem(String name) {
    }

    @Substitute
    static void loadSystem(Consumer<String> load, Consumer<String> loadLibrary, Class<?> context, String name) {
    }
}
