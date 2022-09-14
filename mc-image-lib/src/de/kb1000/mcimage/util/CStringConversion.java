/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.util;

import org.graalvm.nativeimage.PinnedObject;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;

public class CStringConversion {
    public static CTypeConversion.CCharPointerHolder toLatin1String(String s) {
        return new CCharPointerHolderImpl(s);
    }

    private static final class CCharPointerHolderImpl implements CTypeConversion.CCharPointerHolder {
        private final PinnedObject cString;

        CCharPointerHolderImpl(String javaString) {
            int len = javaString.length();
            byte[] bytes = new byte[len + 1];
            // Using the "broken" getBytes method is intended here, it seems to behave just like LWJGL's encodeASCII
            javaString.getBytes(0, len, bytes, 0);
            cString = PinnedObject.create(bytes);
        }

        @Override
        public CCharPointer get() {
            return cString.addressOfArrayElement(0);
        }

        @Override
        public void close() {
            cString.close();
        }
    }
}
