/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.lwjgl;

import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import com.oracle.svm.core.c.CGlobalData;
import com.oracle.svm.core.c.CGlobalDataFactory;
import de.kb1000.mcimage.util.Environment;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.c.function.CFunctionPointer;
import org.graalvm.nativeimage.c.struct.SizeOf;
import org.graalvm.nativeimage.c.type.*;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.impl.RuntimeClassInitializationSupport;
import org.graalvm.word.WordFactory;

import static de.kb1000.mcimage.target.lwjgl.AllocFunctionPointers.*;

final class AllocFunctionPointers {
    static final CGlobalData<CFunctionPointer> malloc = CGlobalDataFactory.forSymbol("malloc");
    static final CGlobalData<CFunctionPointer> calloc = CGlobalDataFactory.forSymbol("calloc");
    static final CGlobalData<CFunctionPointer> realloc = CGlobalDataFactory.forSymbol("realloc");
    static final CGlobalData<CFunctionPointer> free = CGlobalDataFactory.forSymbol("free");
    static final CGlobalData<CFunctionPointer> aligned_alloc = CGlobalDataFactory.forSymbol("aligned_alloc");
    static final CGlobalData<CFunctionPointer> aligned_free = CGlobalDataFactory.forSymbol("aligned_free");
}

@TargetClass(className = "org.lwjgl.system.MemoryAccessJNI", onlyWith = Environment.ClientOnly.class)
final class Target_org_lwjgl_system_MemoryAccessJNI {
    @Substitute
    static int getPointerSize() {
        return SizeOf.get(CCharPointerPointer.class); // from the Unsafe substitutions
    }

    @Substitute
    private static long malloc() {
        return malloc.get().rawValue();
    }

    @Substitute
    private static long calloc() {
        return calloc.get().rawValue();
    }

    @Substitute
    private static long realloc() {
        return realloc.get().rawValue();
    }

    @Substitute
    private static long free() {
        return free.get().rawValue();
    }

    @Substitute
    private static long aligned_alloc() {
        return aligned_alloc.get().rawValue();
    }

    @Substitute
    private static long aligned_free() {
        return aligned_free.get().rawValue();
    }

    @Substitute
    static byte ngetByte(long ptr) {
        return WordFactory.<CCharPointer>pointer(ptr).read();
    }

    @Substitute
    static short ngetShort(long ptr) {
        return WordFactory.<CShortPointer>pointer(ptr).read();
    }

    @Substitute
    static int ngetInt(long ptr) {
        return WordFactory.<CIntPointer>pointer(ptr).read();
    }

    @Substitute
    static long ngetLong(long ptr) {
        return WordFactory.<CLongPointer>pointer(ptr).read();
    }

    @Substitute
    static float ngetFloat(long ptr) {
        return WordFactory.<CFloatPointer>pointer(ptr).read();
    }

    @Substitute
    static double ngetDouble(long ptr) {
        return WordFactory.<CDoublePointer>pointer(ptr).read();
    }

    @Substitute
    static long ngetAddress(long ptr) {
        return WordFactory.<CCharPointerPointer>pointer(ptr).read().rawValue();
    }

    @Substitute
    static void nputByte(long ptr, byte value) {
        WordFactory.<CCharPointer>pointer(ptr).write(value);
    }

    @Substitute
    static void nputShort(long ptr, short value) {
        WordFactory.<CShortPointer>pointer(ptr).write(value);
    }

    @Substitute
    static void nputInt(long ptr, int value) {
        WordFactory.<CIntPointer>pointer(ptr).write(value);
    }

    @Substitute
    static void nputLong(long ptr, long value) {
        WordFactory.<CLongPointer>pointer(ptr).write(value);
    }

    @Substitute
    static void nputFloat(long ptr, float value) {
        WordFactory.<CFloatPointer>pointer(ptr).write(value);
    }

    @Substitute
    static void nputDouble(long ptr, double value) {
        WordFactory.<CDoublePointer>pointer(ptr).write(value);
    }

    @Substitute
    static void nputAddress(long ptr, long value) {
        WordFactory.<CCharPointerPointer>pointer(ptr).write(WordFactory.pointer(value));
    }
}

@AutomaticFeature
class MemoryAccessJNIFeature implements Feature {
    @Override
    public boolean isInConfiguration(IsInConfigurationAccess access) {
        return !Environment.SERVER;
    }

    @Override
    public void duringSetup(DuringSetupAccess access) {
        RuntimeClassInitializationSupport rci = ImageSingletons.lookup(RuntimeClassInitializationSupport.class);
        rci.rerunInitialization("org.lwjgl.system.MemoryAccessJNI", "Contains pointers to libc allocation functions");
    }
}
