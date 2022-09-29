/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.agent;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

public class UnsafeUtil {
    public static final MethodHandles.Lookup IMPL_LOOKUP;

    static {
        try {
            Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            field.setAccessible(true);
            IMPL_LOOKUP = (MethodHandles.Lookup) field.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
           throw new RuntimeException(e);
        }
    }
}
