/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.awt;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

// TODO: MC uses this, for whatever reason
// reachability handler uses this, can't delete
@TargetClass(className = "java.awt.GraphicsEnvironment")
@Substitute
final class Target_java_awt_GraphicsEnvironment {
    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.FromAlias)
    static Boolean headless = true;

    @Substitute
    static String getHeadlessMessage() {
        return null;
    }

    @Substitute
    static boolean isHeadless() {
        return true;
    }

    @Substitute
    static boolean getHeadlessProperty() {
        return true;
    }
}
