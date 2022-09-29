/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.agent;

import de.kb1000.mcimage.agent.transformers.*;

import java.lang.instrument.Instrumentation;

public class OptimizePreMain {
    public static void premain(final String arg, final Instrumentation instrumentation) throws Throwable {
        PreMain.premain(arg, instrumentation);
        instrumentation.addTransformer(new AnnotationSubstitutionProcessorFixTransformer()); // only used in mc-image-lib.optimize
        instrumentation.addTransformer(new InputUtilTransformer());
        instrumentation.addTransformer(new MethodHandleFeatureTransformer());
        instrumentation.addTransformer(new FallbackFeatureTransformer());
        instrumentation.addTransformer(new EarlyClassAnalysisLoggerTransformer());
        instrumentation.addTransformer(new EnumSwitchCaseStableTransformer());
        instrumentation.addTransformer(new UnsafeAllocatorTransformer());
        //instrumentation.addTransformer(new ClassDumper());
}

    public static void agentmain(final String arg, final Instrumentation instrumentation) throws Throwable {
        premain(arg, instrumentation);
    }
}
