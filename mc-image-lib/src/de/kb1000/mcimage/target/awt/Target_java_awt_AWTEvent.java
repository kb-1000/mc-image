/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.awt;

import com.oracle.svm.core.SubstrateUtil;
import com.oracle.svm.core.annotate.Delete;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

import java.awt.*;

@TargetClass(className = "java.awt.peer.ComponentPeer")
@Delete
final class Target_java_awt_peer_ComponentPeer {
}

@TargetClass(className = "java.security.AccessControlContext")
final class Target_java_security_AccessControlContext {
}

// TODO: MC uses this, for whatever reason
@TargetClass(AWTEvent.class)
//@Substitute
final class Target_java_awt_AWTEvent {
    @Delete
    byte[] bdata;

    @Delete
    int id;

    @Delete
    boolean consumed;

    @Delete
    Target_java_security_AccessControlContext acc;

    @Delete
    boolean focusManagerIsDispatching;

    @Delete
    boolean isPosted;

    @Delete
    boolean isSystemGenerated;

    @Delete
    native void setSource(Object newSource);

    @Delete
    native void nativeSetSource(Target_java_awt_peer_ComponentPeer peer);

    @Substitute
    Target_java_awt_AWTEvent(Object source, int id) {
        SubstrateUtil.cast(this, Target_java_util_EventObject.class).source = source;
    }

    @Substitute
    public String toString() {
        return super.toString();
    }
}
