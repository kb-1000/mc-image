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
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.FunctionProvider;

import javax.annotation.Nullable;

@TargetClass(GL.class)
public final class Target_org_lwjgl_opengl_GL {
    @TargetClass(className = "org.lwjgl.opengl.GL$ICD")
    interface ICD {
        @Alias
        void set(@Nullable GLCapabilities caps);

        @Alias
        @Nullable
        GLCapabilities get();
    }

    @TargetClass(className = "org.lwjgl.opengl.GL$ICDStatic")
    static final class ICDStatic {
        @Alias
        @Nullable
        static GLCapabilities tempCaps;

        @Substitute
        void set(@Nullable GLCapabilities caps) {
            if (tempCaps == null) {
                tempCaps = caps;
            } else if (tempCaps != caps && caps != null) {
                throw new IllegalStateException("Capabilities already set!");
            }
        }
    }

    @Alias
    static ThreadLocal<GLCapabilities> capabilitiesTLS;

    @Alias
    static ICD icd;

    @Alias
    static FunctionProvider functionProvider;

    @Alias
    static native void create();

    @Alias
    static native GLCapabilities createCapabilities(boolean forwardCompatible);

    @Alias
    public static native GLCapabilities getICD();

    @Substitute
    static void setCapabilities(@Nullable GLCapabilities caps) {
        capabilitiesTLS.set(caps);
        icd.set(caps);
    }

    @Substitute
    static GLCapabilities createCapabilities() {
        if (functionProvider == null) {
            create();
        }
        return createCapabilities(false);
    }
}
