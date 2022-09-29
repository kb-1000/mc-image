/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.awt;

import com.oracle.svm.core.annotate.Delete;
import com.oracle.svm.core.annotate.TargetClass;

import java.awt.event.ActionEvent;

// TODO: MC uses this, for whatever reason
@TargetClass(ActionEvent.class)
//@Substitute
final class Target_java_awt_ActionEvent {
    @Delete
    native String getActionCommand();

    @Delete
    native long getWhen();

    @Delete
    native int getModifiers();

    @Delete
    native String paramString();
}
