/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.mc;

import com.oracle.svm.core.annotate.TargetClass;
import de.kb1000.mcimage.util.MappingsParser;
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;

import java.util.function.Function;

@Platforms(Platform.HOSTED_ONLY.class)
public class MinecraftClassNameProvider implements Function<TargetClass, String> {
    @Override
    public String apply(TargetClass targetClass) {
        return MappingsParser.CLASS_MAPPED_UNMAPPED.getOrDefault(targetClass.className(), targetClass.className()).replace('/', '.');
    }
}
