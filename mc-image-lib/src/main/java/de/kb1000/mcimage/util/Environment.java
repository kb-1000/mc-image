/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.util;

import java.util.function.BooleanSupplier;

public class Environment {
    public static final boolean SERVER = Boolean.getBoolean("de.kb1000.mcimage.server");

    public static final class ServerOnly implements BooleanSupplier {
        @Override
        public boolean getAsBoolean() {
            return SERVER;
        }
    }
    public static final class ClientOnly implements BooleanSupplier {
        @Override
        public boolean getAsBoolean() {
            return !SERVER;
        }
    }
}
