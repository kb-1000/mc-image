/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.lwjgl;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Delete;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.lwjgl.system.MemoryStack;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

import static org.lwjgl.system.MemoryUtil.memAddress;

@SuppressWarnings("InstantiationOfUtilityClass")
@TargetClass(MemoryStack.class)
final class Target_org_lwjgl_system_MemoryStack {
    @Alias
    Target_org_lwjgl_system_MemoryStack(@Nullable ByteBuffer container, long address, int size) {
    }

    @Substitute
    public static Target_org_lwjgl_system_MemoryStack create(ByteBuffer buffer) {
        long address = memAddress(buffer);
        int size = buffer.remaining();
        return new Target_org_lwjgl_system_MemoryStack(buffer, address, size);
    }

    @Substitute
    static Target_org_lwjgl_system_MemoryStack ncreate(long address, int size) {
        return new Target_org_lwjgl_system_MemoryStack(null, address, size);
    }

    @TargetClass(className = "org.lwjgl.system.MemoryStack$DebugMemoryStack")
    @Delete
    static final class Target_DebugMemoryStack {
    }
}
