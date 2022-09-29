/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.util.stb;

import org.graalvm.nativeimage.c.CContext;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class LibSTBDirectives implements CContext.Directives {
    @Override
    public List<String> getHeaderFiles() {
        return Collections.singletonList("\"" + Paths.get("stb.h").toAbsolutePath() + "\"");
    }

    @Override
    public List<String> getLibraries() {
        return List.of("stb", "m");
    }

    @Override
    public List<String> getLibraryPaths() {
        return Collections.singletonList(Paths.get("").toAbsolutePath().toString());
    }

    @Override
    public List<String> getOptions() {
        return List.of("-I" + Paths.get("").toAbsolutePath(), "-I" + Paths.get("stb").toAbsolutePath());
    }
}
