/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.util.dyncall;

import com.oracle.svm.core.annotate.Delete;
import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.function.CFunction;
import org.graalvm.nativeimage.c.function.CFunctionPointer;
import org.graalvm.nativeimage.c.struct.CStruct;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.word.PointerBase;

@CContext(DynCallSVM.Directives.class)
public class DynCallbackSVM {
    @CStruct(isIncomplete = true)
    public interface DCArgs extends PointerBase {
    }

    @CFunction
    public static native int dcbArgBool(DCArgs args);

    @CFunction
    public static native byte dcbArgChar(DCArgs args);

    @CFunction
    public static native short dcbArgShort(DCArgs args);

    @CFunction
    public static native int dcbArgInt(DCArgs args);

    // TODO: Not implemented yet
    @Delete
    public static native int dcbArgLong(DCArgs args);

    @CFunction
    public static native long dcbArgLongLong(DCArgs args);

    @CFunction
    public static native byte dcbArgUChar(DCArgs args);

    @CFunction
    public static native short dcbArgUShort(DCArgs args);

    @CFunction
    public static native int dcbArgUInt(DCArgs args);

    // TODO: Not implemented yet
    @Delete
    public static native int dcbArgULong(DCArgs args);

    @CFunction
    public static native long dcbArgULongLong(DCArgs args);

    @CFunction
    public static native float dcbArgFloat(DCArgs args);

    @CFunction
    public static native double dcbArgDouble(DCArgs args);

    @CFunction
    public static native PointerBase dcbArgPointer(DCArgs args);

    // dcbArg* here

    @CStruct(isIncomplete = true)
    public interface DCCallback extends PointerBase {
    }

    public interface DCCallbackHandler extends CFunctionPointer {
        byte invoke(DCCallback pcb, DCArgs args, DynCallSVM.DCValue result, PointerBase userdata);
    }

    @CFunction
    public static native DCCallback dcbNewCallback(CCharPointer signature, DCCallbackHandler funcptr, PointerBase userdata);

    @CFunction
    public static native void dcbInitCallback(DCCallback pcb, CCharPointer signature, DCCallbackHandler handler, PointerBase userdata);

    @CFunction
    public static native void dcbFreeCallback(DCCallback pcb);

    @CFunction
    public static native PointerBase dcbGetUserData(DCCallback pcb);
}
