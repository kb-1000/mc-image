/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.util.dyncall;

import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.struct.CField;
import org.graalvm.nativeimage.c.struct.CStruct;
import org.graalvm.word.PointerBase;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@CContext(DynCallSVM.Directives.class)
public class DynCallSVM {
    public static class Directives implements CContext.Directives {
        @Override
        public List<String> getHeaderFiles() {
            return List.of("\"" + Paths.get("dyncall", "dyncall_callback.h").toAbsolutePath() + "\"", "\"" + Paths.get("dyncall", "dynload.h").toAbsolutePath() + "\"");
        }

        @Override
        public List<String> getLibraries() {
            return List.of("dyncallback_s", "dynload_s");
        }

        @Override
        public List<String> getLibraryPaths() {
            return Collections.singletonList(Paths.get("").toAbsolutePath().toString());
        }
    }

    @CStruct
    public interface DCValue extends PointerBase {
        @CField("B")
        int getBool();

        @CField("B")
        void setBool(int bool);

        @CField("c")
        byte getChar();

        @CField("c")
        void setChar(byte char_);

        @CField("C")
        byte getUChar();

        @CField("C")
        void setUChar(byte char_);

        @CField("s")
        short getShort();

        @CField("s")
        void setShort(short short_);

        @CField("S")
        short getUShort();

        @CField("S")
        void setUShort(short short_);

        @CField("i")
        int getInt();

        @CField("i")
        void setInt(int int_);

        @CField("I")
        int getUInt();

        @CField("I")
        void setUInt(int int_);

        // long not implemented

        @CField("l")
        long getLongLong();

        @CField("l")
        void setLongLong(long long_);

        @CField("L")
        long getULongLong();

        @CField("L")
        void setULongLong(long long_);

        @CField("f")
        float getFloat();

        @CField("f")
        void setFloat(float float_);

        @CField("d")
        double getDouble();

        @CField("d")
        void setDouble(double double_);

        @CField("p")
        PointerBase getPointer();

        @CField("p")
        void setPointer(PointerBase pointer);

        // string seems unnecessary, should just be pointer anyways
    }
}
