/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.lwjgl;

import com.oracle.svm.core.c.CGlobalData;
import com.oracle.svm.core.c.CGlobalDataFactory;
import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.type.CCharPointerPointer;

@CContext(LWJGLNativeMallocSetup.Directives.class)
public final class LWJGLNativeMallocSetup {
    public static class Directives implements CContext.Directives {
    }

    // not actually char pointers, but should still generate correct code
    // TODO: clean up the types
    static final CGlobalData<CCharPointerPointer> org_lwjgl_malloc = CGlobalDataFactory.forSymbol("org_lwjgl_malloc");
    static final CGlobalData<CCharPointerPointer> org_lwjgl_calloc = CGlobalDataFactory.forSymbol("org_lwjgl_calloc");
    static final CGlobalData<CCharPointerPointer> org_lwjgl_realloc = CGlobalDataFactory.forSymbol("org_lwjgl_realloc");
    static final CGlobalData<CCharPointerPointer> org_lwjgl_free = CGlobalDataFactory.forSymbol("org_lwjgl_free");

    static final CGlobalData<CCharPointerPointer> org_lwjgl_aligned_alloc = CGlobalDataFactory.forSymbol("org_lwjgl_aligned_alloc");
    static final CGlobalData<CCharPointerPointer> org_lwjgl_aligned_free = CGlobalDataFactory.forSymbol("org_lwjgl_aligned_free");
}
