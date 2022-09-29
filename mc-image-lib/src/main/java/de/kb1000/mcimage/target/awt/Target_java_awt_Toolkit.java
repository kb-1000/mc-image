/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.awt;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(className = "java.awt.Toolkit")
@Substitute // @Delete would break the reachability handler, and @Substitute still deletes all members
final class Target_java_awt_Toolkit {
    // Can't remove everything but can kill the library loading
    // (unfortunately, that's not going to keep it from linking AWT in, this is prevented in the agent)
    @Substitute
    static void loadLibraries() {}

    @Substitute
    static void initStatic() {}
}
