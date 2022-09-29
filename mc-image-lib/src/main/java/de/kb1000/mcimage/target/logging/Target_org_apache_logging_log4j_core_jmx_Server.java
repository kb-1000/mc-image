/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.logging;

import com.oracle.svm.core.annotate.Delete;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.logging.log4j.core.jmx.Server;

import javax.management.MBeanServer;

// Disable JMX support of log4j
@TargetClass(Server.class)
final class Target_org_apache_logging_log4j_core_jmx_Server {
    @Substitute
    private static boolean isJmxDisabled() {
        return true;
    }

    @Substitute
    static void reregisterMBeansAfterReconfigure() {
    }

    @Delete
    static native void reregisterMBeansAfterReconfigure(final MBeanServer mbs);

    @Substitute
    static void unregisterMBeans() {
    }

    @Delete
    static native void unregisterMBeans(final MBeanServer mbs);

    @Substitute
    static void unregisterLoggerContext(final String loggerContextName) {
    }

    @Delete
    static native void unregisterLoggerContext(final String contextName, final MBeanServer mbs);
}
