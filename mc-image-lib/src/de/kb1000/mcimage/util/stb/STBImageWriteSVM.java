/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.util.stb;

import com.oracle.svm.core.c.CGlobalData;
import com.oracle.svm.core.c.CGlobalDataFactory;
import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.function.CFunction;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CCharPointerPointer;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.VoidPointer;
import org.graalvm.word.PointerBase;

@CContext(LibSTBDirectives.class)
public class STBImageWriteSVM {
    public static final CGlobalData<CIntPointer> stbi_write_png_compression_level = CGlobalDataFactory.forSymbol("stbi_write_png_compression_level");
    public static final CGlobalData<CIntPointer> stbi_write_force_png_filter = CGlobalDataFactory.forSymbol("stbi_write_force_png_filter");
    // Actually a function pointer variable, but pointer is pointer, I guess
    public static final CGlobalData<CCharPointerPointer> stbi_zlib_compress = CGlobalDataFactory.forSymbol("stbi_zlib_compress");
    public static final CGlobalData<CIntPointer> stbi_write_tga_with_rle = CGlobalDataFactory.forSymbol("stbi_write_tga_with_rle");

    @CFunction
    public static native int stbi_write_png_to_func(/*STBIWriteCallbackI*/ PointerBase func, VoidPointer context, int w, int h, int comp, CCharPointer data, int stride_in_bytes);
}
