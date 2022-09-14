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
import de.kb1000.mcimage.util.stb.STBImageSVM;
import org.graalvm.word.WordFactory;
import org.lwjgl.stb.STBImage;

@TargetClass(STBImage.class)
final class Target_org_lwjgl_stb_STBImage {
    @Substitute
    static long nstbi_load_from_memory(long buffer, int len, long x, long y, long channels_in_file, int desired_channels) {
        return STBImageSVM.stbi_load_from_memory(WordFactory.pointer(buffer), len, WordFactory.pointer(x), WordFactory.pointer(y), WordFactory.pointer(channels_in_file), desired_channels).rawValue();
    }

    @Substitute
    static long nstbi_failure_reason() {
        return STBImageSVM.stbi_failure_reason().rawValue();
    }

    @Substitute
    static void nstbi_image_free(long retval_from_stbi_load) {
        STBImageSVM.stbi_image_free(WordFactory.pointer(retval_from_stbi_load));
    }

    @Substitute
    static int nstbi_info_from_callbacks(long clbk, long user, long x, long y, long comp) throws Throwable {
        var result = STBImageSVM.stbi_info_from_callbacks(WordFactory.pointer(clbk), WordFactory.pointer(user), WordFactory.pointer(x), WordFactory.pointer(y), WordFactory.pointer(comp));
        CException.rethrow();
        return result;
    }
}
