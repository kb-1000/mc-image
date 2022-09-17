/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.lwjgl;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import de.kb1000.mcimage.util.Environment;
import org.lwjgl.openal.ALCapabilities;

import javax.annotation.Nullable;

@TargetClass(className = "org.lwjgl.openal.AL", onlyWith = Environment.ClientOnly.class)
final class Target_org_lwjgl_openal_AL {
    @TargetClass(className = "org.lwjgl.openal.AL", innerClass = "ICDStatic", onlyWith = Environment.ClientOnly.class)
    static final class ICDStatic {
        @Alias
        @Nullable
        static ALCapabilities tempCaps;

        @Substitute
        void set(@Nullable ALCapabilities caps) {
            if (tempCaps == null) {
                tempCaps = caps;
            } else if (tempCaps != caps && caps != null) {
                throw new IllegalStateException("Capabilities already set!");
            }
        }
    }
}
