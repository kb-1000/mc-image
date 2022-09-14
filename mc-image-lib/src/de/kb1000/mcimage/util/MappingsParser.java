/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.util;

import net.fabricmc.mapping.tree.TinyMappingFactory;
import net.fabricmc.mapping.tree.TinyTree;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class MappingsParser {
    public static final Map<String, String> CLASS_MAPPED_UNMAPPED;
    public static final Map<String, String> METHOD_MAPPED_UNMAPPED;
    public static final Map<String, String> FIELD_MAPPED_UNMAPPED;
    static {
        final String obfNamespace = "hashed";
        final String runtimeNamespace = "official";
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(MappingsParser.class.getResourceAsStream("/hashed/mappings.tiny")))){
            TinyTree tree = TinyMappingFactory.loadWithDetection(br, true);
            BinaryOperator<String> duplicateHandler = (a, b) -> {
                if (!a.equals(b)) {
                    throw new RuntimeException("Error: duplicate key with different values");
                }
                return a;
            };
            CLASS_MAPPED_UNMAPPED = tree.getClasses().stream().collect(Collectors.toUnmodifiableMap(c -> c.getName(obfNamespace), c -> c.getName(runtimeNamespace), duplicateHandler));
            METHOD_MAPPED_UNMAPPED = tree.getClasses().stream().flatMap(c -> c.getMethods().stream()).collect(Collectors.toUnmodifiableMap(m -> m.getName(obfNamespace), m -> m.getName(runtimeNamespace), duplicateHandler));
            FIELD_MAPPED_UNMAPPED = tree.getClasses().stream().flatMap(c -> c.getFields().stream()).collect(Collectors.toUnmodifiableMap(f -> f.getName(obfNamespace), f -> f.getName(runtimeNamespace), duplicateHandler));
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }
}
