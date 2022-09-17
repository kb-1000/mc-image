/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.lwjgl;

import com.oracle.svm.core.annotate.KeepOriginal;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import de.kb1000.mcimage.util.Environment;
import org.graalvm.word.WordFactory;

@TargetClass(className = "org.lwjgl.system.jni.JNINativeInterface", onlyWith = Environment.ClientOnly.class)
@Substitute // Kill the JNI interface, it's not needed here...
final class Target_org_lwjgl_system_jni_JNINativeInterface {
    // ... except for global references, which are used by Callback. Emulate that here.
    @Substitute
    static long NewGlobalRef(Object obj) {
        return ObjectHandles.create(obj).rawValue();
    }

    @Substitute
    static void nDeleteGlobalRef(long globalRef) {
        ObjectHandles.destroy(WordFactory.signed(globalRef));
    }

    @KeepOriginal
    static native void DeleteGlobalRef(long globalRef);
}
