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
import de.kb1000.mcimage.util.Environment;
import de.kb1000.mcimage.util.stb.STBVorbisSVM;
import org.graalvm.word.WordFactory;

@TargetClass(className = "org.lwjgl.stb.STBVorbis", onlyWith = Environment.ClientOnly.class)
final class Target_org_lwjgl_stb_STBVorbis {
    @Substitute
    static void nstb_vorbis_get_info(long f, long __result) {
        STBVorbisSVM.nstb_vorbis_get_info(WordFactory.pointer(f), WordFactory.pointer(__result));
    }

    @Substitute
    static int nstb_vorbis_get_error(long f) {
        return STBVorbisSVM.stb_vorbis_get_error(WordFactory.pointer(f));
    }

    @Substitute
    static void nstb_vorbis_close(long f) {
        STBVorbisSVM.stb_vorbis_close(WordFactory.pointer(f));
    }

    @Substitute
    static long nstb_vorbis_open_pushdata(long datablock, int datablock_length_in_bytes, long datablock_memory_consumed_in_bytes, long error, long alloc_buffer) {
        return STBVorbisSVM.stb_vorbis_open_pushdata(WordFactory.pointer(datablock), datablock_length_in_bytes, WordFactory.pointer(datablock_memory_consumed_in_bytes), WordFactory.pointer(error), WordFactory.pointer(alloc_buffer)).rawValue();
    }

    @Substitute
    static int nstb_vorbis_decode_frame_pushdata(long f, long datablock, int datablock_length_in_bytes, long channels, long output, long samples) {
        return STBVorbisSVM.stb_vorbis_decode_frame_pushdata(WordFactory.pointer(f), WordFactory.pointer(datablock), datablock_length_in_bytes, WordFactory.pointer(channels), WordFactory.pointer(output), WordFactory.pointer(samples));
    }
}
