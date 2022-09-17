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
import de.kb1000.mcimage.util.Environment;
import org.lwjgl.PointerBuffer;

import javax.annotation.Nullable;

@TargetClass(className = "org.lwjgl.system.ThreadLocalUtil", onlyWith = Environment.ClientOnly.class)
@Substitute
final class Target_org_lwjgl_system_ThreadLocalUtil {
    @Substitute
    static PointerBuffer getAddressesFromCapabilities(Object caps) {
        // never used, just kill
        return null;
    }

    @Substitute
    static void setFunctionMissingAddresses(@Nullable Class<?> capabilitiesClass, int index) {
    }
}
