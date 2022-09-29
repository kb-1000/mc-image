/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.guava;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.oracle.svm.core.ParsingReason;
import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.graal.InternalFeature;
import de.kb1000.mcimage.util.HostedConstants;
import de.kb1000.mcimage.util.ThrowableUtil;
import jdk.vm.ci.meta.JavaKind;
import jdk.vm.ci.meta.ResolvedJavaMethod;
import org.graalvm.compiler.api.replacements.SnippetReflectionProvider;
import org.graalvm.compiler.core.common.type.AbstractPointerStamp;
import org.graalvm.compiler.nodes.ConstantNode;
import org.graalvm.compiler.nodes.NodeView;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.graphbuilderconf.GraphBuilderConfiguration;
import org.graalvm.compiler.nodes.graphbuilderconf.GraphBuilderContext;
import org.graalvm.compiler.nodes.graphbuilderconf.InvocationPlugin;
import org.graalvm.compiler.nodes.graphbuilderconf.InvocationPlugins;
import org.graalvm.compiler.phases.util.Providers;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Type;

@AutomaticFeature
public class GuavaFeature implements InternalFeature {
    private static final MethodHandle mapsCapacity;

    static {
        try {
            mapsCapacity = HostedConstants.IMPL_LOOKUP.findStatic(Maps.class, "capacity", MethodType.methodType(int.class, int.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static int mapsCapacity(int expectedSize) {
        try {
            return (int) mapsCapacity.invokeExact(expectedSize);
        } catch (Throwable e) {
            throw ThrowableUtil.sneakyThrow(e);
        }
    }

    /*@Override
    public void registerGraphBuilderPlugins(Providers providers, GraphBuilderConfiguration.Plugins plugins, ParsingReason reason) {
        plugins.appendInlineInvokePlugin(new InlineInvokePlugin() {
            @Override
            public InlineInfo shouldInlineInvoke(GraphBuilderContext b, ResolvedJavaMethod method, ValueNode[] args) {
                if (method.getDeclaringClass().getName().equals("Lcom/google/common/collect/Maps;") && (method.getName().startsWith("new")))
                    return InlineInfo.createStandardInlineInfo(method);
                return null;
            }
        });
    }*/

    @Override
    public void registerInvocationPlugins(Providers providers, SnippetReflectionProvider snippetReflection, GraphBuilderConfiguration.Plugins plugins, ParsingReason reason) {
        var r = new InvocationPlugins.Registration(plugins.getInvocationPlugins(), Preconditions.class);
        class CheckNotNullPlugin extends InvocationPlugin {
            public CheckNotNullPlugin(Type... argumentTypes) {
                super("checkNotNull", argumentTypes);
            }

            @Override
            public boolean execute(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver, ValueNode[] argsIncludingReceiver) {
                if (((AbstractPointerStamp)argsIncludingReceiver[0].stamp(NodeView.DEFAULT)).nonNull()) {
                    b.push(JavaKind.Object, argsIncludingReceiver[0]);
                    return true;
                }
                return false;
            }
        }
        for (var method : Preconditions.class.getDeclaredMethods())
            if (method.getName().equals("checkNotNull"))
                r.register(new CheckNotNullPlugin(method.getParameterTypes()));
        r = new InvocationPlugins.Registration(plugins.getInvocationPlugins(), Maps.class);
        r.register(new InvocationPlugin("capacity", int.class) {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver, ValueNode arg) {
                var constant = arg.asJavaConstant();
                if (constant == null)
                    return false;
                b.addPush(JavaKind.Int, ConstantNode.forInt(mapsCapacity(constant.asInt()), b.getGraph()));
                return true;
            }
        });
    }
}
