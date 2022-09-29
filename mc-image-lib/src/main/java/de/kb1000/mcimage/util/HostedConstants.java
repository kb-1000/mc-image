/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.util;

import jdk.internal.misc.Unsafe;
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

public class HostedConstants {
    @Platforms(Platform.HOSTED_ONLY.class)
    public static final MethodHandles.Lookup IMPL_LOOKUP;
    public static final Unsafe UNSAFE = Unsafe.getUnsafe();

    static {
        try {
            final Field implLookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            implLookupField.setAccessible(true);
            IMPL_LOOKUP = (MethodHandles.Lookup) implLookupField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
