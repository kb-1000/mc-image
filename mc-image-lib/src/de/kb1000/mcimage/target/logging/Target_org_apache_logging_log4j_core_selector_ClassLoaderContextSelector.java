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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.selector.ClassLoaderContextSelector;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.hosted.Feature;

import java.net.URI;
import java.util.List;
import java.util.Map;

@TargetClass(ClassLoaderContextSelector.class)
final class Target_org_apache_logging_log4j_core_selector_ClassLoaderContextSelector {
    @Substitute
    private LoggerContext findContext(ClassLoader loaderOrNull) {
        return ImageSingletons.lookup(LoggerContext.class);
    }

    @Substitute
    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final Map.Entry<String, Object> entry,
                                    final boolean currentContext, final URI configLocation) {
        return ImageSingletons.lookup(LoggerContext.class);
    }

    @Substitute
    private LoggerContext locateContext(final ClassLoader loaderOrNull, final Map.Entry<String, Object> entry,
                                        final URI configLocation) {
        return ImageSingletons.lookup(LoggerContext.class);
    }

    @Substitute
    public void removeContext(final LoggerContext context) {
    }

    @Substitute
    public List<LoggerContext> getLoggerContexts() {
        return ImageSingletons.lookup(LoggerContextSelectorFeature.LoggerContextListWrapper.class).value;
    }
}

@AutomaticFeature
class LoggerContextSelectorFeature implements Feature {
    static class LoggerContextListWrapper {
        List<LoggerContext> value;

        LoggerContextListWrapper() {
            value = List.of(ImageSingletons.lookup(LoggerContext.class));
        }
    }

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        ImageSingletons.add(LoggerContext.class, (LoggerContext) LogManager.getContext(access.getApplicationClassLoader(), false));
        ImageSingletons.add(org.apache.logging.log4j.spi.LoggerContext.class, ImageSingletons.lookup(LoggerContext.class));
        ImageSingletons.add(LoggerContextListWrapper.class, new LoggerContextListWrapper());
    }
}
