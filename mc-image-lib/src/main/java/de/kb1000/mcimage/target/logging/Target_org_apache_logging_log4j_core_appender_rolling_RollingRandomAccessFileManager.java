/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.logging;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.logging.log4j.core.appender.rolling.RollingRandomAccessFileManager;

import java.io.RandomAccessFile;

@TargetClass(RollingRandomAccessFileManager.class)
final class Target_org_apache_logging_log4j_core_appender_rolling_RollingRandomAccessFileManager {
    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Reset)
    private RandomAccessFile randomAccessFile;
}
