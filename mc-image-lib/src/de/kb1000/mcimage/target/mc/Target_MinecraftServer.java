/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.mc;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import com.oracle.svm.core.annotate.TargetElement;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

@TargetClass(MinecraftServer.class)
final class Target_MinecraftServer {
    @Alias
    @TargetElement(name = "f_tcqyqsco")
    static Logger LOGGER;

    @Substitute
    @TargetElement(name = "m_sednkjtc")
    static void loadServerIcon_lambda1(Target_ServerMetadata metadata, File file) {
        // see, it's not my problem if you provide an invalid image
        // just don't do that, and you'll be fine
        try {
            byte[] icon = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
            metadata.setServerIcon("data:image/png;base64," + new String(icon, StandardCharsets.UTF_8));
        } catch (Exception e) {
            LOGGER.error("Couldn't load server icon", e);
        }
    }
}
