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
import org.apache.logging.log4j.message.DefaultFlowMessageFactory;
import org.apache.logging.log4j.message.FlowMessageFactory;
import org.apache.logging.log4j.message.MessageFactory2;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;
import org.apache.logging.log4j.spi.AbstractLogger;

@TargetClass(AbstractLogger.class)
final class Target_org_apache_logging_log4j_spi_AbstractLogger {
    @Substitute
    private static MessageFactory2 createDefaultMessageFactory() {
        return new ParameterizedMessageFactory();
    }

    @Substitute
    private static FlowMessageFactory createDefaultFlowMessageFactory() {
        return new DefaultFlowMessageFactory();
    }
}
