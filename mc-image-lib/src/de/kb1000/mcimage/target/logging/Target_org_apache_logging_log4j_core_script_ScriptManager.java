/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.logging;

import com.oracle.svm.core.annotate.AlwaysInline;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.script.AbstractScript;
import org.apache.logging.log4j.core.script.ScriptManager;
import org.apache.logging.log4j.core.util.WatchManager;

@Substitute
@TargetClass(ScriptManager.class)
final class Target_org_apache_logging_log4j_core_script_ScriptManager {
    @Substitute
    @AlwaysInline("")
    public Target_org_apache_logging_log4j_core_script_ScriptManager(final Configuration configuration, final WatchManager watchManager) {
        throw new RuntimeException();
    }

    @Substitute
    @AlwaysInline("")
    public void addScript(final AbstractScript script) {
    }
}
