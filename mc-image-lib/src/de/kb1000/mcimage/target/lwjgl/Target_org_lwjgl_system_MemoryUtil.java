/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.lwjgl;

import com.oracle.svm.core.SubstrateUtil;
import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Delete;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.graalvm.word.WordFactory;
import org.lwjgl.system.MemoryUtil;
import sun.misc.Unsafe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.*;

import static org.lwjgl.system.MemoryUtil.*;

@TargetClass(MemoryUtil.class)
final class Target_org_lwjgl_system_MemoryUtil {
    @SuppressWarnings("NotNullFieldNotInitialized")
    @Nonnull
    @Alias
    //@Delete
    private static Unsafe UNSAFE;

    @Alias
    static native long getAllocationSize(int elements, int elementShift);

    @Substitute
    static void memFree(@Nullable Buffer ptr) {
        if (ptr != null) {
            nmemFree(SubstrateUtil.cast(ptr, Target_java_nio_Buffer.class).address);
        }
    }

    @Alias
    private static native <T extends Buffer> T realloc(@Nullable T old_p, T new_p, int size);

    @Substitute
    static ByteBuffer memRealloc(@Nullable ByteBuffer ptr, int size) {
        return realloc(ptr, memByteBuffer(nmemReallocChecked(ptr == null ? NULL : SubstrateUtil.cast(ptr, Target_java_nio_Buffer.class).address, size), size), size);
    }

    @Substitute
    static ShortBuffer memRealloc(@Nullable ShortBuffer ptr, int size) {
        return realloc(ptr, memShortBuffer(nmemReallocChecked(ptr == null ? NULL : SubstrateUtil.cast(ptr, Target_java_nio_Buffer.class).address, getAllocationSize(size, 1)), size), size);
    }

    @Substitute
    static IntBuffer memRealloc(@Nullable IntBuffer ptr, int size) {
        return realloc(ptr, memIntBuffer(nmemReallocChecked(ptr == null ? NULL : SubstrateUtil.cast(ptr, Target_java_nio_Buffer.class).address, getAllocationSize(size, 2)), size), size);
    }

    @Substitute
    static LongBuffer memRealloc(@Nullable LongBuffer ptr, int size) {
        return realloc(ptr, memLongBuffer(nmemReallocChecked(ptr == null ? NULL : SubstrateUtil.cast(ptr, Target_java_nio_Buffer.class).address, getAllocationSize(size, 3)), size), size);
    }

    @Substitute
    static FloatBuffer memRealloc(@Nullable FloatBuffer ptr, int size) {
        return realloc(ptr, memFloatBuffer(nmemReallocChecked(ptr == null ? NULL : SubstrateUtil.cast(ptr, Target_java_nio_Buffer.class).address, getAllocationSize(size, 2)), size), size);
    }

    @Substitute
    static DoubleBuffer memRealloc(@Nullable DoubleBuffer ptr, int size) {
        return realloc(ptr, memDoubleBuffer(nmemReallocChecked(ptr == null ? NULL : SubstrateUtil.cast(ptr, Target_java_nio_Buffer.class).address, getAllocationSize(size, 3)), size), size);
    }

    @Substitute
    static void memAlignedFree(@Nullable ByteBuffer ptr) {
        if (ptr != null) {
            nmemAlignedFree(SubstrateUtil.cast(ptr, Target_java_nio_Buffer.class).address);
        }
    }

    @Delete
    static native void memReport(MemoryAllocationReport report);

    @Delete
    static native void memReport(MemoryAllocationReport report, MemoryAllocationReport.Aggregate groupByStackTrace, boolean groupByThread);

    @Substitute
    static long memAddress0(@Nonnull Buffer buffer) {
        return SubstrateUtil.cast(buffer, Target_java_nio_Buffer.class).address;
    }

    @Substitute
    static <T> T memGlobalRefToObject(long globalRef) {
        return ObjectHandles.get(WordFactory.pointer(globalRef));
    }
}

@TargetClass(java.nio.Buffer.class)
final class Target_java_nio_Buffer {
    @Alias
    long address;
}
