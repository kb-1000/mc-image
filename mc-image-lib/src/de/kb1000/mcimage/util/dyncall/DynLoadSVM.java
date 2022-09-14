/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.util.dyncall;

import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.function.CFunction;
import org.graalvm.nativeimage.c.struct.CStruct;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.VoidPointer;
import org.graalvm.word.PointerBase;

@CContext(DynCallSVM.Directives.class)
public final class DynLoadSVM {
    @CStruct(isIncomplete = true)
    public interface DLLib extends PointerBase {
    }

    @CStruct(isIncomplete = true)
    public interface DLSyms extends PointerBase {
    }

    @CFunction
    public static native DLLib dlLoadLibrary(CCharPointer libPath);

    @CFunction
    public static native void dlFreeLibrary(DLLib pLib);

    @CFunction
    public static native VoidPointer dlFindSymbol(DLLib pLib, CCharPointer pSymbolName);

    @CFunction
    public static native int dlGetLibraryPath(DLLib pLib, CCharPointer sOut, int bufSize);
}
