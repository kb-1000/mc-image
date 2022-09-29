/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.util;

import com.oracle.svm.core.threadlocal.FastThreadLocalFactory;
import com.oracle.svm.core.threadlocal.FastThreadLocalObject;

public class CException {
    public static final FastThreadLocalObject<Throwable> pendingException = FastThreadLocalFactory.createObject(Throwable.class, "CException.pendingException");

    public static void rethrow() throws Throwable {
        Throwable t;
        if ((t = pendingException.get()) != null) {
            pendingException.set(null);
            throw t;
        }
    }
}
