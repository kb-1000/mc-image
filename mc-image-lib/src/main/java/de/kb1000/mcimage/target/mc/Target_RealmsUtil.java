/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.mc;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.TargetClass;
import com.oracle.svm.core.annotate.TargetElement;
import de.kb1000.mcimage.util.Environment;

import java.util.Map;

@TargetClass(className = "net/minecraft/unmapped/C_veycqpnp", classNameProvider = MinecraftClassNameProvider.class, onlyWith = Environment.ClientOnly.class)
final class Target_RealmsUtil {
    @Alias
    @TargetElement(name = "m_gbelqhtj")
    static native Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(String uuid);
}
