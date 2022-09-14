/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.logging;

import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.commons.logging.LogFactory;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.hosted.Feature;

@TargetClass(LogFactory.class)
final class Target_org_apache_commons_logging_LogFactory {
    @Substitute
    static LogFactory getFactory() {
        return ImageSingletons.lookup(LogFactory.class);
    }
}

@AutomaticFeature
final class LogFactoryFeature implements Feature {
    @Override
    public void duringSetup(DuringSetupAccess access) {
        ImageSingletons.add(LogFactory.class, LogFactory.getFactory());
    }
}
