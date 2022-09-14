/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.mc;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.TargetClass;
import com.oracle.svm.core.annotate.TargetElement;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@TargetClass(className = "net/minecraft/unmapped/C_ayikuhxa", classNameProvider = MinecraftClassNameProvider.class)
final class Target_NativeImage {
    @Alias
    Target_NativeImage(int width, int height, boolean useStb) {
    }

    @Alias
    @TargetElement(name = "m_mtdfbkkq")
    static native Target_NativeImage read(InputStream stream) throws IOException;

    @Alias
    @TargetElement(name = "m_meplyfkd")
    static native Target_NativeImage read(ByteBuffer stream) throws IOException;

    @Alias
    @TargetElement(name = "m_xqjgvqoz")
    native int getHeight();

    @Alias
    @TargetElement(name = "m_glzwbqla")
    native byte[] getBytes();

    @Alias
    @TargetElement(name = "m_urfoahpt")
    native void copyFrom(Target_NativeImage image);

    @Alias
    native void close();

    @Alias
    @TargetElement(name = "m_csziyqyg")
    native void fillRect(int x, int y, int width, int height, int color);

    @Alias
    @TargetElement(name = "m_pdrvxmhc")
    native void copyRect(int x, int y, int translateX, int translateY, int width, int height, boolean flipX, boolean flipY);

    @Alias
    @TargetElement(name = "m_tqikshlo")
    native void upload(int level, int offsetX, int offsetY, boolean close);
}
