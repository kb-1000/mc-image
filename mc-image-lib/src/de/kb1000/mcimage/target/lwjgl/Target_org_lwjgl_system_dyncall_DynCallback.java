/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.lwjgl;

import com.oracle.svm.core.annotate.Delete;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import de.kb1000.mcimage.util.dyncall.DynCallbackSVM;
import org.graalvm.word.WordFactory;
import org.lwjgl.system.dyncall.DynCallback;

@TargetClass(DynCallback.class)
final class Target_org_lwjgl_system_dyncall_DynCallback {
    @Substitute
    static long ndcbNewCallback(long signature, long funcptr, long userdata) {
        return DynCallbackSVM.dcbNewCallback(WordFactory.pointer(signature), WordFactory.pointer(funcptr), WordFactory.pointer(userdata)).rawValue();
    }

    @Substitute
    static void ndcbInitCallback(long pcb, long signature, long handler, long userdata) {
        DynCallbackSVM.dcbInitCallback(WordFactory.pointer(pcb), WordFactory.pointer(signature), WordFactory.pointer(handler), WordFactory.pointer(userdata));
    }

    @Substitute
    static void ndcbFreeCallback(long pcb) {
        DynCallbackSVM.dcbFreeCallback(WordFactory.pointer(pcb));
    }

    @Substitute
    static long ndcbGetUserData(long pcb) {
        return DynCallbackSVM.dcbGetUserData(WordFactory.pointer(pcb)).rawValue();
    }

    @Substitute
    static int ndcbArgBool(long args) {
        return DynCallbackSVM.dcbArgBool(WordFactory.pointer(args));
    }

    @Substitute
    static byte ndcbArgChar(long args) {
        return DynCallbackSVM.dcbArgChar(WordFactory.pointer(args));
    }

    @Substitute
    static short ndcbArgShort(long args) {
        return DynCallbackSVM.dcbArgShort(WordFactory.pointer(args));
    }

    @Substitute
    static int ndcbArgInt(long args) {
        return DynCallbackSVM.dcbArgInt(WordFactory.pointer(args));
    }

    // TODO: Not implemented yet
    @Delete
    static int ndcbArgLong(long args) {
        return DynCallbackSVM.dcbArgLong(WordFactory.pointer(args));
    }

    @Substitute
    static long ndcbArgLongLong(long args) {
        return DynCallbackSVM.dcbArgLongLong(WordFactory.pointer(args));
    }

    @Substitute
    static byte ndcbArgUChar(long args) {
        return DynCallbackSVM.dcbArgUChar(WordFactory.pointer(args));
    }

    @Substitute
    static short ndcbArgUShort(long args) {
        return DynCallbackSVM.dcbArgUShort(WordFactory.pointer(args));
    }

    @Substitute
    static int ndcbArgUInt(long args) {
        return DynCallbackSVM.dcbArgUInt(WordFactory.pointer(args));
    }

    // TODO: Not implemented yet
    @Delete
    static int ndcbArgULong(long args) {
        return DynCallbackSVM.dcbArgULong(WordFactory.pointer(args));
    }

    @Substitute
    static long ndcbArgULongLong(long args) {
        return DynCallbackSVM.dcbArgULongLong(WordFactory.pointer(args));
    }

    @Substitute
    static float ndcbArgFloat(long args) {
        return DynCallbackSVM.dcbArgFloat(WordFactory.pointer(args));
    }

    @Substitute
    static double ndcbArgDouble(long args) {
        return DynCallbackSVM.dcbArgDouble(WordFactory.pointer(args));
    }

    @Substitute
    static long ndcbArgPointer(long args) {
        return DynCallbackSVM.dcbArgPointer(WordFactory.pointer(args)).rawValue();
    }
}
