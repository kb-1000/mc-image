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
import com.oracle.svm.core.posix.headers.Dlfcn;
import de.kb1000.mcimage.util.Environment;
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.word.WordFactory;

@TargetClass(className = "org.lwjgl.system.linux.DynamicLinkLoader", onlyWith = Environment.ClientOnly.class)
@Platforms(Platform.LINUX.class)
final class Target_org_lwjgl_system_linux_DynamicLinkLoader {
    @Substitute
    static long ndlopen(long filename, int mode) {
        return Dlfcn.dlopen(WordFactory.pointer(filename), mode).rawValue();
    }

    @Substitute
    static long ndlerror() {
        return Dlfcn.dlerror().rawValue();
    }

    @Substitute
    static long ndlsym(long handle, long name) {
        return Dlfcn.dlsym(WordFactory.pointer(handle), WordFactory.pointer(name)).rawValue();
    }

    @Substitute
    static int ndlclose(long handle) {
        return Dlfcn.dlclose(WordFactory.pointer(handle));
    }
}
