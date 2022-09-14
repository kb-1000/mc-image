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
import com.oracle.svm.core.annotate.AlwaysInline;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.CustomBuffer;
import org.lwjgl.system.Pointer;

import java.nio.ByteBuffer;

@TargetClass(Pointer.class)
final class Target_org_lwjgl_system_Pointer {
    @TargetClass(Pointer.Default.class)
    static final class Default {
        @Alias
        static sun.misc.Unsafe UNSAFE;

        @Alias
        long address;

        @AlwaysInline("Try getting native-image to detect the allocateInstance call")
        @Substitute
        @SuppressWarnings("unchecked")
        static <T extends CustomBuffer<?>> T wrap(Class<? extends T> clazz, long address, int capacity) {
            @NotNull
            T buffer;
            try {
                buffer = (T) UNSAFE.allocateInstance(clazz);
            } catch (InstantiationException e) {
                throw new UnsupportedOperationException(e);
            }

            SubstrateUtil.cast(buffer, Default.class).address = address;
            Target_org_lwjgl_system_CustomBuffer customBuffer = SubstrateUtil.cast(buffer, Target_org_lwjgl_system_CustomBuffer.class);
            customBuffer.mark = -1;
            customBuffer.limit = capacity;
            customBuffer.capacity = capacity;

            return buffer;
        }

        @AlwaysInline("Try getting native-image to detect the allocateInstance call")
        @Substitute
        @SuppressWarnings("unchecked")
        static <T extends CustomBuffer<?>> T wrap(Class<? extends T> clazz, long address, int capacity, ByteBuffer container) {
            @NotNull
            T buffer;
            try {
                buffer = (T) UNSAFE.allocateInstance(clazz);
            } catch (InstantiationException e) {
                throw new UnsupportedOperationException(e);
            }

            SubstrateUtil.cast(buffer, Default.class).address = address;
            Target_org_lwjgl_system_CustomBuffer customBuffer = SubstrateUtil.cast(buffer, Target_org_lwjgl_system_CustomBuffer.class);
            customBuffer.mark = -1;
            customBuffer.limit = capacity;
            customBuffer.capacity = capacity;
            customBuffer.container = container;

            return buffer;
        }
    }
}
