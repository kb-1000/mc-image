/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.agent;

import java.util.Map;

@SuppressWarnings("unchecked")
public class Mappings {
    public static final Map<String, String> CLASS_MAPPED_UNMAPPED;
    public static final Map<String, String> METHOD_MAPPED_UNMAPPED;
    public static final Map<String, String> FIELD_MAPPED_UNMAPPED;

    static {
        try {
            Class<?> mappingParser = ClassLoader.getSystemClassLoader().loadClass("de.kb1000.mcimage.util.MappingsParser");
            CLASS_MAPPED_UNMAPPED = (Map<String, String>) mappingParser.getField("CLASS_MAPPED_UNMAPPED").get(null);
            METHOD_MAPPED_UNMAPPED = (Map<String, String>) mappingParser.getField("METHOD_MAPPED_UNMAPPED").get(null);
            FIELD_MAPPED_UNMAPPED = (Map<String, String>) mappingParser.getField("FIELD_MAPPED_UNMAPPED").get(null);
            Util.isLoaded = true;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static class Util {
        public static boolean isLoaded = false;
    }
}
