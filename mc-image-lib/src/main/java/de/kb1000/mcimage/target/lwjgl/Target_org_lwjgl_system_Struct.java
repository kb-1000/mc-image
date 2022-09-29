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
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import de.kb1000.mcimage.util.Environment;
import org.lwjgl.system.Struct;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

import static de.kb1000.mcimage.util.HostedConstants.UNSAFE;

@TargetClass(className = "org.lwjgl.system.Struct", onlyWith = Environment.ClientOnly.class)
final class Target_org_lwjgl_system_Struct {
    @Nullable
    @Alias
    ByteBuffer container;

    @Alias
    native int sizeof();

    @Substitute
    static Struct wrap(Class<? extends Struct> clazz, long address) {
        Struct struct;
        try {
            struct = (Struct) UNSAFE.allocateInstance(clazz);
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }

        SubstrateUtil.cast(struct, Target_org_lwjgl_system_Pointer.Default.class).address = address;

        return struct;
    }

    @Substitute
    static Struct wrap(Class<? extends Struct> clazz, long address, ByteBuffer container) {
        Struct struct;
        try {
            struct = (Struct) UNSAFE.allocateInstance(clazz);
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }

        SubstrateUtil.cast(struct, Target_org_lwjgl_system_Pointer.Default.class).address = address;
        SubstrateUtil.cast(struct, Target_org_lwjgl_system_Struct.class).container = container;

        return struct;
    }

    @Substitute
    Struct wrap(long address, int index, ByteBuffer container) {
        Struct struct;
        try {
            struct = (Struct) UNSAFE.allocateInstance(this.getClass());
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }

        SubstrateUtil.cast(struct, Target_org_lwjgl_system_Pointer.Default.class).address = address + Integer.toUnsignedLong(index) * sizeof();
        SubstrateUtil.cast(struct, Target_org_lwjgl_system_Struct.class).container = container;

        return struct;
    }
}
