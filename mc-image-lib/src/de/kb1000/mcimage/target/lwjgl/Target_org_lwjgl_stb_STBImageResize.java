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
import de.kb1000.mcimage.util.stb.STBImageResizeSVM;
import org.graalvm.word.WordFactory;
import org.lwjgl.stb.STBImageResize;

@TargetClass(STBImageResize.class)
final class Target_org_lwjgl_stb_STBImageResize {
    @Substitute
    static int nstbir_resize_uint8(long input_pixels, int input_w, int input_h, int input_stride_in_bytes, long output_pixels, int output_w, int output_h, int output_stride_in_bytes, int num_channels) {
        return STBImageResizeSVM.stbir_resize_uint8(WordFactory.pointer(input_pixels), input_w, input_h, input_stride_in_bytes, WordFactory.pointer(output_pixels), output_w, output_h, output_stride_in_bytes, num_channels);
    }
}
