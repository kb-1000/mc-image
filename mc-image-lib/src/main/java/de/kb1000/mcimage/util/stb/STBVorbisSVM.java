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
import org.graalvm.word.PointerBase;

@CContext(LibSTBDirectives.class)
public class STBVorbisSVM {
    // This uses a C wrapper function
    @CFunction
    public static native void nstb_vorbis_get_info(/*STBVorbis*/PointerBase f, /*STBVorbisInfo*/PointerBase __result);

    @CFunction
    public static native int stb_vorbis_get_error(/*STBVorbis*/PointerBase f);

    @CFunction
    public static native void stb_vorbis_close(/*STBVorbis*/PointerBase f);

    @CFunction
    public static native /*STBVorbis*/PointerBase stb_vorbis_open_pushdata(CCharPointer datablock, int datablock_length_in_bytes, CIntPointer datablock_memory_consumed_in_bytes, CIntPointer error, /*STBVorbisAlloc*/PointerBase alloc_buffer);

    @CFunction
    public static native int stb_vorbis_decode_frame_pushdata(/*STBVorbis*/PointerBase f, CCharPointer datablock, int datablock_length_in_bytes, CIntPointer channels, /*float *** */ PointerBase output, CIntPointer samples);
}
