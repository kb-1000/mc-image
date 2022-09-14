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
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PreMain {
    @SuppressWarnings("unchecked")
    public static void premain(final String arg, final Instrumentation instrumentation) throws Throwable {
        instrumentation.redefineModule(Module.class.getModule(), Set.of(), Map.of(), Map.of(MethodHandles.Lookup.class.getPackageName(), Set.of(PreMain.class.getModule())), Set.of(), Map.of());
        Set<Module> everyoneSet = (Set<Module>) UnsafeUtil.IMPL_LOOKUP.findStaticGetter(Module.class, "EVERYONE_SET", Set.class).invokeExact();
        // make JDK internal ASM accessible, avoid bundling our own
        instrumentation.redefineModule(Object.class.getModule(), Set.of(), Map.of(
                "jdk.internal.misc", everyoneSet
        ), Map.of(
                "java.lang", everyoneSet,
                "java.lang.invoke", everyoneSet,
                "jdk.internal.misc", everyoneSet
        ), Set.of(), Map.of());
        for (var module : (Iterable<Module>) Stream.concat(modulesFromClass(ClassLoader.getSystemClassLoader().loadClass("com.oracle.svm.core.annotate.TargetClass")), modulesFromClass(ClassLoader.getSystemClassLoader().loadClass("org.graalvm.compiler.graph.Node"))).distinct()::iterator) {
            Map<String, Set<Module>> packageToModule = module.getPackages().stream().collect(Collectors.toUnmodifiableMap(Function.identity(), packageName -> everyoneSet));
            instrumentation.redefineModule(module, Set.of(), packageToModule, packageToModule, Set.of(), Map.of());
        }
        instrumentation.addTransformer(new JEmallocAllocatorClinitTransformer());
        //instrumentation.addTransformer(new SVMClassInitAnalysisTransformer());
        instrumentation.addTransformer(new AWTNoToolkitTransformer());
        instrumentation.addTransformer(new SubstitutionRemapperTransformer());
        //instrumentation.addTransformer(new ClassDumper());
        if (!Config.traceReflectionMethods.isEmpty()) {
            instrumentation.addTransformer(new SVMReflectionMethodTraceTransformer());
        }
    }

    public static void agentmain(final String arg, final Instrumentation instrumentation) throws Throwable {
        premain(arg, instrumentation);
    }

    public static Stream<Module> modulesFromClass(Class<?> clazz) {
        Module module = clazz.getModule();
        return Stream.concat(Stream.of(module), Stream.ofNullable(module.getLayer()).flatMap(PreMain::modulesFromLayer));
    }

    public static Stream<Module> modulesFromLayer(ModuleLayer layer) {
        return Stream.concat(layer.parents().stream().flatMap(PreMain::modulesFromLayer), layer.modules().stream());
    }
}
