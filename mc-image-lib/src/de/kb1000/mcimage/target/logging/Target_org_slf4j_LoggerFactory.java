/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.logging;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.SLF4JServiceProvider;

import java.net.URL;
import java.util.List;
import java.util.Set;

// Disable StaticLoggerBinder
@TargetClass(LoggerFactory.class)
final class Target_org_slf4j_LoggerFactory {
    @Substitute
    private static List<SLF4JServiceProvider> findServiceProviders() {
        return List.of(new org.apache.logging.slf4j.SLF4JServiceProvider());
    }

    @Substitute
    static Set<URL> findPossibleStaticLoggerBinderPathSet() {
        return Set.of();
    }
}
