/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.util.stb;

import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.function.CFunction;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.VoidPointer;
import org.graalvm.word.PointerBase;

@CContext(LibSTBDirectives.class)
public class STBImageSVM {
    @CFunction
    public static native CCharPointer stbi_load_from_memory(CCharPointer buffer, int len, CIntPointer x, CIntPointer y, CIntPointer channels_in_file, int desired_channels);

    @CFunction
    public static native CCharPointer stbi_failure_reason();

    @CFunction
    public static native void stbi_image_free(VoidPointer retval_from_stbi_load);

    @CFunction
    public static native int stbi_info_from_callbacks(/*STBIIOCallbacks*/ PointerBase clbk, VoidPointer user, CIntPointer x, CIntPointer y, CIntPointer comp);
}
