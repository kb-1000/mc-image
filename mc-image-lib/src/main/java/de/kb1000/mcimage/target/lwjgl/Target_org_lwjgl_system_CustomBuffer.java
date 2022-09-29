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
import com.oracle.svm.core.annotate.TargetClass;
import de.kb1000.mcimage.util.Environment;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

// TODO: there are Unsafe calls here
@TargetClass(className = "org.lwjgl.system.CustomBuffer", onlyWith = Environment.ClientOnly.class)
final class Target_org_lwjgl_system_CustomBuffer {
    @Alias
    @Nullable
    ByteBuffer container;

    @Alias
    int mark, position, limit, capacity;

    // the Unsafe calls are in here, but the methods are not used, so we can @Delete them (to ensure they won't be, either)
    @Delete
    native Target_org_lwjgl_system_CustomBuffer slice();
    @Delete
    native Target_org_lwjgl_system_CustomBuffer slice(int offset, int capacity);
    @Delete
    native Target_org_lwjgl_system_CustomBuffer duplicate();

}
