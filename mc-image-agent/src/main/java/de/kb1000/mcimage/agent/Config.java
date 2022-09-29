/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.agent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Config {
    public static final Set<String> traceReflectionMethods;

    static {
        ArrayList<String> traceReflectionMethodsList = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("mc-image-config.txt"), StandardCharsets.UTF_8))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.startsWith("traceReflectionMethod:")) {
                    traceReflectionMethodsList.add(line.substring("traceReflectionMethod:".length()));
                }
            }
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        traceReflectionMethods = new HashSet<>(traceReflectionMethodsList);
    }
}
