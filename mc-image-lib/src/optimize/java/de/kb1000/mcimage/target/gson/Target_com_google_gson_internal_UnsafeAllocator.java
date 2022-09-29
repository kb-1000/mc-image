/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.gson;

import com.google.gson.internal.UnsafeAllocator;
import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.hosted.Feature;

@TargetClass(UnsafeAllocator.class)
final class Target_com_google_gson_internal_UnsafeAllocator {
    @Substitute
    static UnsafeAllocator create() {
        return ImageSingletons.lookup(UnsafeAllocator.class);
    }
}

@AutomaticFeature
final class UnsafeAllocatorFeature implements Feature {
    @Override
    public void duringSetup(DuringSetupAccess access) {
        UnsafeAllocator allocator = UnsafeAllocator.create();
        ImageSingletons.add(UnsafeAllocator.class, allocator);
        access.registerObjectReplacer(object -> object instanceof UnsafeAllocator ? allocator : object);
    }
}
