/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.jdk;

import com.oracle.svm.core.annotate.Delete;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(className = "java.io.ObjectInputStream")
@Delete
final class Target_java_io_ObjectInputStream {
    @TargetClass(className = "java.io.ObjectInputStream", innerClass = "GetField")
    @Delete
    static final class Target_GetField {}
}
