/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.logging;

import com.oracle.graal.pointsto.infrastructure.OriginalClassProvider;
import com.oracle.graal.pointsto.util.GraalAccess;
import com.oracle.svm.core.ParsingReason;
import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.graal.InternalFeature;
import com.oracle.svm.hosted.FeatureImpl;
import com.oracle.svm.hosted.analysis.Inflation;
import com.oracle.svm.hosted.classinitialization.ConfigurableClassInitialization;
import com.oracle.svm.hosted.classinitialization.InitKind;
import de.kb1000.mcimage.util.HostedConstants;
import de.kb1000.mcimage.util.ThrowableUtil;
import io.netty.util.internal.logging.InternalLoggerFactory;
import jdk.internal.vm.annotation.Stable;
import jdk.vm.ci.meta.*;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.LogManager;
import org.graalvm.compiler.api.replacements.SnippetReflectionProvider;
import org.graalvm.compiler.nodes.ConstantNode;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.graphbuilderconf.GraphBuilderConfiguration;
import org.graalvm.compiler.nodes.graphbuilderconf.GraphBuilderContext;
import org.graalvm.compiler.nodes.graphbuilderconf.InvocationPlugin;
import org.graalvm.compiler.nodes.graphbuilderconf.InvocationPlugins;
import org.graalvm.compiler.phases.util.Providers;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.impl.RuntimeClassInitializationSupport;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@AutomaticFeature
@Platforms(Platform.HOSTED_ONLY.class)
public class LogFactoryInitializerFeature implements InternalFeature {
    private static final MethodHandle computeInitKindAndMaybeInitializeClass;
    static {
        try {
            computeInitKindAndMaybeInitializeClass = HostedConstants.IMPL_LOOKUP.findVirtual(ConfigurableClassInitialization.class, "computeInitKindAndMaybeInitializeClass", MethodType.methodType(InitKind.class, Class.class, boolean.class, Set.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static InitKind computeInitKindAndMaybeInitializeClass(ConfigurableClassInitialization this$0, Class<?> clazz, boolean memoize, Set<Class<?>> earlyClassInitializerAnalyzedClasses) {
        try {
            return (InitKind) computeInitKindAndMaybeInitializeClass.invokeExact(this$0, clazz, memoize, earlyClassInitializerAnalyzedClasses);
        } catch (Throwable e) {
            throw ThrowableUtil.sneakyThrow(e);
        }
    }

    Inflation bb;

    @Override
    public void afterRegistration(AfterRegistrationAccess access) {
        try {
            final Set<String> factoryClasses = Set.of("Lorg/apache/commons/logging/LogFactory;", "Lorg/slf4j/LoggerFactory;", "Lorg/apache/logging/log4j/LogManager;", "Lio/netty/util/internal/logging/InternalLoggerFactory;", "Lorg/apache/logging/log4j/status/StatusLogger;");
            final Set<String> javaTimeClasses = Set.of("Ljava/time/format/DateTimeFormatter;", "Ljava/time/ZoneId;", "Ljava/time/ZoneOffset;");
            LogFactoryInitializerFeature.class.getClassLoader().loadClass("de.kb1000.mcimage.agent.transformers.EarlyClassAnalysisLoggerTransformer").getField("methodPredicate").set(null, (Predicate<ResolvedJavaMethod>) method ->
                    factoryClasses.contains(method.getDeclaringClass().getName()) && method.isStatic() && method.getName().startsWith("get")
                            || method.getName().equals("getClass") && method.getParameters().length == 0
                            || method.getDeclaringClass().getName().equals("Ljava/lang/Class;") && method.getName().equals("getName") && method.getParameters().length == 0
                            || (method.getDeclaringClass().getName().equals("Ljava/lang/Math;") || method.getDeclaringClass().getName().equals("Ljava/lang/StrictMath;")) && !method.getName().equals("random")
                            || method.getDeclaringClass().getName().equals("Ljava/util/regex/Pattern;") && method.getName().equals("compile")
                            || method.getDeclaringClass().getName().equals("Ljava/nio/charset/Charset;") && method.isStatic()
                            || javaTimeClasses.contains(method.getDeclaringClass().getName()) && method.getName().startsWith("of")
                            || method.getDeclaringClass().getName().equals("Ljava/util/TimeZone;") && method.getName().equals("getTimeZone")
                            || method.getDeclaringClass().getName().equals("Ljava/util/Locale;") && method.getName().equals("forLanguageTag"));
            // immutable classes, TODO: add more, and logic to auto-detect
            final Set<String> safeClasses = Set.of("Ljava/util/Locale;", "Ljava/lang/String;", "Ljava/util/regex/Pattern;", "Ljava/nio/charset/Charset;", "Ljava/lang/Class;", "Ljava/math/BigInteger;", "Ljava/lang/Integer;");
            LogFactoryInitializerFeature.class.getClassLoader().loadClass("de.kb1000.mcimage.agent.transformers.EarlyClassAnalysisLoggerTransformer").getField("fieldPredicate").set(null, (BiPredicate<ResolvedJavaField, Set<Class<?>>>) (field, analyzedClasses) -> {
                boolean isStatic = field.isStatic();
                if (!isStatic)
                    return false;
                ResolvedJavaType declaringClass = field.getDeclaringClass();
                if (computeInitKindAndMaybeInitializeClass((ConfigurableClassInitialization)ImageSingletons.lookup(RuntimeClassInitializationSupport.class), OriginalClassProvider.getJavaClass(GraalAccess.getOriginalSnippetReflection(), declaringClass), true, analyzedClasses) != InitKind.BUILD_TIME || !declaringClass.isInitialized())
                    return true;
                if ((field.isFinal() || field.isAnnotationPresent(Stable.class)) && bb != null && bb.getAnnotationSubstitutionProcessor().findSubstitution(field).orElse(field) == field) {
                    var type = field.getType().resolve(declaringClass);
                    //var originalClass = OriginalClassProvider.getJavaClass(GraalAccess.getOriginalSnippetReflection(), type);
                    if (type.isPrimitive() || safeClasses.contains(type.getName()))
                        // TODO: @Stable only applies once non-zero
                        return false;
                    ConstantReflectionProvider constantReflection = GraalAccess.getOriginalProviders().getConstantReflection();
                    var value = constantReflection.readFieldValue(field, null);
                    // unknown value, assume mutable (TODO: is this too pessimistic?)
                    if (value == null)
                        return true;
                    // @Stable only applies once non-null
                    if (value.isNull())
                        return !field.isFinal();
                    if (type.isArray()) {
                        if (Integer.valueOf(0).equals(constantReflection.readArrayLength(value))) {
                            return false;
                        }
                    } else {
                        var fields = type.getInstanceFields(true);
                        if (fields.length != 0) {
                            for (var instanceField : fields) {
                                if (!field.isFinal())
                                    return true;
                                // TODO: recursive analysis, this is fine for now
                                if (!field.getType().resolve(type).isPrimitive() && !safeClasses.contains(field.getType().getName()))
                                    return true;
                            }
                        }
                        return false;
                    }
                }
                return true;
            });
        } catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void duringSetup(DuringSetupAccess access) {
        bb = (Inflation) ((FeatureImpl.DuringSetupAccessImpl) access).getBigBang();
    }

    @Override
    public void registerInvocationPlugins(Providers providers, SnippetReflectionProvider snippetReflection, GraphBuilderConfiguration.Plugins plugins, ParsingReason reason) {
        var r = new InvocationPlugins.Registration(plugins.getInvocationPlugins(), LogFactory.class);
        r.register(new InvocationPlugin("getFactory") {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver) {
                var factory = ImageSingletons.lookup(LogFactory.class);
                b.addPush(JavaKind.Object, ConstantNode.forConstant(snippetReflection.forObject(factory), b.getMetaAccess(), b.getGraph()));
                return true;
            }
        });
        r.register(new InvocationPlugin("getLog", String.class) {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver, ValueNode arg) {
                var constant = arg.asJavaConstant();
                if (constant == null) return false;

                b.addPush(JavaKind.Object, ConstantNode.forConstant(snippetReflection.forObject(LogFactory.getLog(snippetReflection.asObject(String.class, constant))), b.getMetaAccess(), b.getGraph()));
                return true;
            }
        });
        r.register(new InvocationPlugin("getLog", Class.class) {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver, ValueNode arg) {
                var constant = arg.asJavaConstant();
                if (constant == null) return false;

                b.addPush(JavaKind.Object, ConstantNode.forConstant(snippetReflection.forObject(LogFactory.getLog(snippetReflection.asObject(Class.class, constant))), b.getMetaAccess(), b.getGraph()));
                return true;
            }
        });
        r = new InvocationPlugins.Registration(plugins.getInvocationPlugins(), LoggerFactory.class);
        r.register(new InvocationPlugin("getProvider") {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver) {
                Object provider;
                try {
                    Method providerMethod = LoggerFactory.class.getDeclaredMethod("getProvider");
                    providerMethod.setAccessible(true);
                    provider = providerMethod.invoke(null);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                b.addPush(JavaKind.Object, ConstantNode.forConstant(snippetReflection.forObject(provider), b.getMetaAccess(), b.getGraph()));
                return true;
            }
        });
        r.register(new InvocationPlugin("getILoggerFactory") {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver) {
                b.addPush(JavaKind.Object, ConstantNode.forConstant(snippetReflection.forObject(LoggerFactory.getILoggerFactory()), b.getMetaAccess(), b.getGraph()));
                return true;
            }
        });
        r.register(new InvocationPlugin("getLogger", String.class) {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver, ValueNode arg) {
                var constant = arg.asJavaConstant();
                if (constant == null) return false;

                b.addPush(JavaKind.Object, ConstantNode.forConstant(snippetReflection.forObject(LoggerFactory.getLogger(snippetReflection.asObject(String.class, constant))), b.getMetaAccess(), b.getGraph()));
                return true;
            }
        });
        r.register(new InvocationPlugin("getLogger", Class.class) {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver, ValueNode arg) {
                var constant = arg.asJavaConstant();
                if (constant == null) return false;

                b.addPush(JavaKind.Object, ConstantNode.forConstant(snippetReflection.forObject(LoggerFactory.getLogger(snippetReflection.asObject(Class.class, constant))), b.getMetaAccess(), b.getGraph()));
                return true;
            }
        });
        r = new InvocationPlugins.Registration(plugins.getInvocationPlugins(), LogManager.class);
        r.register(new InvocationPlugin("getLogger") {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver) {
                ResolvedJavaMethod method = b.getMethod();
                if (method == null) return false;
                var clazz = snippetReflection.asObject(Class.class, b.getConstantReflection().asJavaClass(method.getDeclaringClass()));
                b.addPush(JavaKind.Object, ConstantNode.forConstant(snippetReflection.forObject(LogManager.getLogger(clazz)), b.getMetaAccess(), b.getGraph()));
                return true;
            }
        });
        r.register(new InvocationPlugin("getLogger", Class.class) {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver, ValueNode arg) {
                var constant = arg.asJavaConstant();
                if (constant == null) return false;

                b.addPush(JavaKind.Object, ConstantNode.forConstant(snippetReflection.forObject(LogManager.getLogger(snippetReflection.asObject(Class.class, constant))), b.getMetaAccess(), b.getGraph()));
                return true;
            }
        });
        r.register(new InvocationPlugin("getLogger", String.class) {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver, ValueNode arg) {
                var constant = arg.asJavaConstant();
                if (constant == null) return false;

                b.addPush(JavaKind.Object, ConstantNode.forConstant(snippetReflection.forObject(LogManager.getLogger(snippetReflection.asObject(String.class, constant))), b.getMetaAccess(), b.getGraph()));
                return true;
            }
        });
        r = new InvocationPlugins.Registration(plugins.getInvocationPlugins(), InternalLoggerFactory.class);
        r.register(new InvocationPlugin("getInstance", Class.class) {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver, ValueNode arg) {
                var constant = arg.asJavaConstant();
                if (constant == null) return false;

                b.addPush(JavaKind.Object, ConstantNode.forConstant(snippetReflection.forObject(InternalLoggerFactory.getInstance(snippetReflection.asObject(Class.class, constant))), b.getMetaAccess(), b.getGraph()));
                return true;
            }
        });
        r.register(new InvocationPlugin("getInstance", String.class) {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver, ValueNode arg) {
                var constant = arg.asJavaConstant();
                if (constant == null) return false;

                b.addPush(JavaKind.Object, ConstantNode.forConstant(snippetReflection.forObject(InternalLoggerFactory.getInstance(snippetReflection.asObject(String.class, constant))), b.getMetaAccess(), b.getGraph()));
                return true;
            }
        });
        r.register(new InvocationPlugin("getDefaultFactory") {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver) {
                var factory = ImageSingletons.lookup(InternalLoggerFactory.class);
                b.addPush(JavaKind.Object, ConstantNode.forConstant(snippetReflection.forObject(factory), b.getMetaAccess(), b.getGraph()));
                return true;
            }
        });
        r = new InvocationPlugins.Registration(plugins.getInvocationPlugins(), Class.class);
        r.register(new InvocationPlugin("getName", InvocationPlugin.Receiver.class) {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver) {
                if (!receiver.isConstant()) return false;
                JavaConstant constant = receiver.get().asJavaConstant();
                if (constant == null) throw new IllegalStateException("receiver claimed to be constant but isn't");
                b.addPush(JavaKind.Object, ConstantNode.forConstant(snippetReflection.forObject(snippetReflection.asObject(Class.class, constant).getName()), b.getMetaAccess(), b.getGraph()));
                return true;
            }
        });
        r = new InvocationPlugins.Registration(plugins.getInvocationPlugins(), Charset.class);
        r.register(new InvocationPlugin("forName", String.class) {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver, ValueNode arg) {
                var constant = arg.asJavaConstant();
                if (constant == null) return false;
                try {
                    b.addPush(JavaKind.Object, ConstantNode.forConstant(snippetReflection.forObject(Charset.forName(snippetReflection.asObject(String.class, constant))), b.getMetaAccess(), b.getGraph()));
                } catch (UnsupportedCharsetException e) {
                    return false;
                }
                return true;
            }
        });
        r = new InvocationPlugins.Registration(plugins.getInvocationPlugins(), Locale.class);
        r.register(new InvocationPlugin("forLanguageTag", String.class) {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver, ValueNode arg) {
                var constant = arg.asJavaConstant();
                if (constant == null) return false;
                b.addPush(JavaKind.Object, ConstantNode.forConstant(snippetReflection.forObject(Locale.forLanguageTag(snippetReflection.asObject(String.class, constant))), b.getMetaAccess(), b.getGraph()));
                return true;
            }
        });
    }
}
