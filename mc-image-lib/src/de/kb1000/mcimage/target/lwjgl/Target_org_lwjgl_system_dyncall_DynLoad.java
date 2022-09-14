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
import de.kb1000.mcimage.util.dyncall.DynLoadSVM;
import org.graalvm.word.WordFactory;
import org.lwjgl.system.dyncall.DynLoad;

@TargetClass(DynLoad.class)
final class Target_org_lwjgl_system_dyncall_DynLoad {
    @Substitute
    static long ndlLoadLibrary(long libpath) {
        return DynLoadSVM.dlLoadLibrary(WordFactory.pointer(libpath)).rawValue();
    }

    @Substitute
    static void ndlFreeLibrary(long pLib) {
        DynLoadSVM.dlFreeLibrary(WordFactory.pointer(pLib));
    }

    @Substitute
    static long ndlFindSymbol(long pLib, long pSymbolName) {
        return DynLoadSVM.dlFindSymbol(WordFactory.pointer(pLib), WordFactory.pointer(pSymbolName)).rawValue();
    }

    @Substitute
    static int ndlGetLibraryPath(long pLib, long sOut, int bufSize) {
        return DynLoadSVM.dlGetLibraryPath(WordFactory.pointer(pLib), WordFactory.pointer(sOut), bufSize);
    }
}
