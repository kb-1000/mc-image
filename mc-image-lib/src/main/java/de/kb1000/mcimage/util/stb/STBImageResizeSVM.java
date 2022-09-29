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

@CContext(LibSTBDirectives.class)
public class STBImageResizeSVM {
    @CFunction
    public static native int stbir_resize_uint8(CCharPointer input_pixels, int input_w, int input_h, int input_stride_in_bytes, CCharPointer output_pixels, int output_w, int output_h, int output_stride_in_bytes, int num_channels);
}
