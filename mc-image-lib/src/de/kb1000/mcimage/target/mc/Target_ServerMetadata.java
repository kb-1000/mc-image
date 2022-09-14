/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.mc;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.TargetClass;
import com.oracle.svm.core.annotate.TargetElement;

@TargetClass(className = "net/minecraft/unmapped/C_bwttende", classNameProvider = MinecraftClassNameProvider.class)
final class Target_ServerMetadata {
    @Alias
    @TargetElement(name = "m_tjisblji")
    native void setServerIcon(String serverIcon);
}
