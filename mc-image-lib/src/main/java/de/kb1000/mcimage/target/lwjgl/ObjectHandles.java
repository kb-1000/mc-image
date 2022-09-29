/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.lwjgl;

import org.graalvm.compiler.serviceprovider.GraalUnsafeAccess;
import org.graalvm.word.ComparableWord;
import org.graalvm.word.WordFactory;
import sun.misc.Unsafe;

public final class ObjectHandles {
    private static final Unsafe UNSAFE = GraalUnsafeAccess.getUnsafe();

    // 1023 callbacks should (hopefully) be enough
    // can always increase if we need more
    private static final Object[] objects = new Object[1023];

    private static final ComparableWord nullHandle = WordFactory.signed(0);

    private static volatile int unusedHandleSearchIndex = 0;


    private static boolean isNotInRange(ObjectHandle<?> handle) {
        return handle.rawValue() < 1 || handle.rawValue() > 1024;
    }

    private static int toIndex(ObjectHandle<?> handle) {
        return (int) handle.rawValue() - 1;
    }

    private static long getObjectArrayByteOffset(int index) {
        return Unsafe.ARRAY_OBJECT_BASE_OFFSET + (long) index * Unsafe.ARRAY_OBJECT_INDEX_SCALE;
    }

    @SuppressWarnings("unchecked")
    public static <T> ObjectHandle<T> create(T object) {
        if (object == null) return (ObjectHandle<T>) nullHandle;

        int startIndex = unusedHandleSearchIndex;
        int i = startIndex;
        do {
            if (i == 1023) i = 0;
            if (objects[i] == null) {
                if (UNSAFE.compareAndSwapObject(objects, getObjectArrayByteOffset(i), null, object)) {
                    unusedHandleSearchIndex = i != 1022 ? i + 1 : 0;
                    return WordFactory.signed(i + 1);
                }
            }
        } while (++i != startIndex);
        throw new OutOfMemoryError("Cannot allocate more callback object handles!");
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(ObjectHandle<T> handle) {
        if (handle.equal(nullHandle)) return null;
        if (isNotInRange(handle)) {
            throw new IllegalArgumentException("Invalid handle");
        }
        int index = toIndex(handle);
        return (T) UNSAFE.getObjectVolatile(objects, getObjectArrayByteOffset(index));
    }

    public static void destroy(ObjectHandle<?> handle) {
        if (isNotInRange(handle)) {
            throw new IllegalArgumentException("Invalid handle");
        }

        int index = toIndex(handle);
        UNSAFE.putOrderedObject(objects, getObjectArrayByteOffset(index), null);
    }
}
