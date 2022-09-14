/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.lwjgl;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import de.kb1000.mcimage.target.lwjgl.generated.Callbacks;
import de.kb1000.mcimage.util.CStringConversion;
import de.kb1000.mcimage.util.dyncall.DynCallbackSVM.DCCallback;
import de.kb1000.mcimage.util.dyncall.DynCallbackSVM.DCCallbackHandler;
import org.graalvm.word.PointerBase;
import org.lwjgl.system.Callback;

import static de.kb1000.mcimage.util.dyncall.DynCallbackSVM.dcbNewCallback;

@TargetClass(Callback.class)
final class Target_org_lwjgl_system_Callback {
    @Substitute
    static long create(String signature, Object instance) {
        DCCallbackHandler funcptr = Callbacks.getNativeFunction(signature.charAt(signature.length() - 1));

        DCCallback handle;
        try (var cSignature = CStringConversion.toLatin1String(signature)) {
            handle = dcbNewCallback(cSignature.get(), funcptr, (PointerBase) ObjectHandles.create(instance));
        }
        if (handle.isNull()) {
            throw new IllegalStateException("Failed to create the DCCallback object");
        }

        return handle.rawValue();
    }
}
