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
import org.graalvm.word.WordFactory;

@TargetClass(className = "org.lwjgl.stb.LibSTB")
final class Target_org_lwjgl_stb_LibSTB {
    @Substitute
    static void setupMalloc(long malloc, long calloc, long realloc, long free, long aligned_alloc, long aligned_free) {
        LWJGLNativeMallocSetup.org_lwjgl_malloc.get().write(WordFactory.pointer(malloc));
        LWJGLNativeMallocSetup.org_lwjgl_calloc.get().write(WordFactory.pointer(calloc));
        LWJGLNativeMallocSetup.org_lwjgl_realloc.get().write(WordFactory.pointer(realloc));
        LWJGLNativeMallocSetup.org_lwjgl_free.get().write(WordFactory.pointer(free));

        LWJGLNativeMallocSetup.org_lwjgl_aligned_alloc.get().write(WordFactory.pointer(aligned_alloc));
        LWJGLNativeMallocSetup.org_lwjgl_aligned_free.get().write(WordFactory.pointer(aligned_free));
    }
}
