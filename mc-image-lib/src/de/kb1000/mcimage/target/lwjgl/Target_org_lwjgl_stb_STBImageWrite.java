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
import de.kb1000.mcimage.util.CException;
import de.kb1000.mcimage.util.stb.STBImageWriteSVM;
import org.graalvm.word.WordFactory;
import org.lwjgl.stb.STBImageWrite;

@TargetClass(STBImageWrite.class)
final class Target_org_lwjgl_stb_STBImageWrite {
    @Substitute
    static long nstbi_write_png_compression_level() {
        return STBImageWriteSVM.stbi_write_png_compression_level.get().rawValue();
    }

    @Substitute
    static long nstbi_write_force_png_filter() {
        return STBImageWriteSVM.stbi_write_force_png_filter.get().rawValue();
    }

    @Substitute
    static long nstbi_zlib_compress() {
        return STBImageWriteSVM.stbi_zlib_compress.get().rawValue();
    }

    @Substitute
    static long nstbi_write_tga_with_rle() {
        return STBImageWriteSVM.stbi_write_tga_with_rle.get().rawValue();
    }

    @Substitute
    static int nstbi_write_png_to_func(long func, long context, int w, int h, int comp, long data, int stride_in_bytes) throws Throwable {
        var result = STBImageWriteSVM.stbi_write_png_to_func(WordFactory.pointer(func), WordFactory.pointer(context), w, h, comp, WordFactory.pointer(data), stride_in_bytes);
        CException.rethrow();
        return result;
    }
}
