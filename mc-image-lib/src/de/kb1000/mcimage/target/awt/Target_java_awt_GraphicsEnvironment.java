/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.awt;

import com.oracle.svm.core.ParsingReason;
import com.oracle.svm.core.annotate.*;
import com.oracle.svm.core.graal.InternalFeature;
import jdk.vm.ci.meta.JavaKind;
import jdk.vm.ci.meta.ResolvedJavaMethod;
import org.graalvm.compiler.api.replacements.SnippetReflectionProvider;
import org.graalvm.compiler.nodes.ConstantNode;
import org.graalvm.compiler.nodes.graphbuilderconf.GraphBuilderConfiguration;
import org.graalvm.compiler.nodes.graphbuilderconf.GraphBuilderContext;
import org.graalvm.compiler.nodes.graphbuilderconf.InvocationPlugin;
import org.graalvm.compiler.nodes.graphbuilderconf.InvocationPlugins;
import org.graalvm.compiler.phases.util.Providers;

import java.awt.*;

// TODO: MC uses this, for whatever reason
// reachability handler uses this, can't delete
@TargetClass(className = "java.awt.GraphicsEnvironment")
@Substitute
final class Target_java_awt_GraphicsEnvironment {
    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.FromAlias)
    static Boolean headless = true;

    @Substitute
    static String getHeadlessMessage() {
        return null;
    }

    @Substitute
    static boolean isHeadless() {
        return true;
    }

    @Substitute
    static boolean getHeadlessProperty() {
        return true;
    }
}

// this helps constant folding remove the server gui code
@AutomaticFeature
final class AWTGraphicsEnvironmentHeadlessFeature implements InternalFeature {
    @Override
    public void registerInvocationPlugins(Providers providers, SnippetReflectionProvider snippetReflection, GraphBuilderConfiguration.Plugins plugins, ParsingReason reason) {
        var invocationPlugins = plugins.getInvocationPlugins();
        var registration = new InvocationPlugins.Registration(invocationPlugins, GraphicsEnvironment.class);
        registration.register(new InvocationPlugin("isHeadless") {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver) {
                b.addPush(JavaKind.Boolean, ConstantNode.forBoolean(true, b.getGraph()));
                return true;
            }
        });
        registration.register(new InvocationPlugin("getHeadlessProperty") {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver) {
                b.addPush(JavaKind.Boolean, ConstantNode.forBoolean(true, b.getGraph()));
                return true;
            }
        });
    }
}
