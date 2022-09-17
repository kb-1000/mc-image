/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.lwjgl;

import com.oracle.svm.core.ParsingReason;
import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.classinitialization.EnsureClassInitializedNode;
import com.oracle.svm.core.graal.InternalFeature;
import com.oracle.svm.core.util.VMError;
import de.kb1000.mcimage.util.Environment;
import jdk.vm.ci.meta.*;
import org.graalvm.compiler.api.replacements.SnippetReflectionProvider;
import org.graalvm.compiler.nodes.ConstantNode;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.graphbuilderconf.GraphBuilderConfiguration;
import org.graalvm.compiler.nodes.graphbuilderconf.GraphBuilderContext;
import org.graalvm.compiler.nodes.graphbuilderconf.InvocationPlugin;
import org.graalvm.compiler.nodes.graphbuilderconf.InvocationPlugins;
import org.graalvm.compiler.nodes.java.NewInstanceNode;
import org.graalvm.compiler.nodes.java.StoreFieldNode;
import org.graalvm.compiler.phases.util.Providers;
import org.lwjgl.system.Callback;
import org.lwjgl.system.Pointer;
import org.lwjgl.system.Struct;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Objects;

@AutomaticFeature
public class PointerGraphBuilderPluginFeature implements InternalFeature {
    @Override
    public boolean isInConfiguration(IsInConfigurationAccess access) {
        return !Environment.SERVER;
    }

    @Override
    public void registerInvocationPlugins(Providers providers, SnippetReflectionProvider snippetReflection, GraphBuilderConfiguration.Plugins plugins, ParsingReason reason) {
        InvocationPlugins invocationPlugins = plugins.getInvocationPlugins();
        InvocationPlugins.Registration r = new InvocationPlugins.Registration(invocationPlugins, Pointer.Default.class);
        class PointerInvocationPlugin extends InvocationPlugin {
            public PointerInvocationPlugin(String name, Type... argumentTypes) {
                super(name, argumentTypes);
            }

            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver, ValueNode clazz, ValueNode address, ValueNode capacity) {
                return apply(b, targetMethod, receiver, clazz, address, capacity, null);
            }

            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver, ValueNode clazz, ValueNode address, ValueNode capacity, ValueNode container) {
                if (!clazz.isConstant()) {
                    System.err.println("Error: " + clazz + " is not a constant");
                }
                /*
                 * The allocated class must be null-checked already before the class initialization
                 * check.
                 */
                // aren't we already null checking by the below code at compile time?
                ValueNode clazzNonNull = b.nullCheckedValue(clazz, DeoptimizationAction.None);
                b.add(new EnsureClassInitializedNode(clazzNonNull));
                ResolvedJavaType bufferType = b.getConstantReflection().asJavaType(clazz.asConstant());
                NewInstanceNode buffer = b.add(new NewInstanceNode(bufferType, true));
                while (bufferType != null && !bufferType.getName().equals("Lorg/lwjgl/system/CustomBuffer;")) {
                    bufferType = bufferType.getSuperclass();
                }
                Objects.requireNonNull(bufferType);
                ResolvedJavaField addressField = null;
                ResolvedJavaField markField = null;
                ResolvedJavaField limitField = null;
                ResolvedJavaField capacityField = null;
                ResolvedJavaField containerField = null;
                for (final ResolvedJavaField field : bufferType.getInstanceFields(true)) {
                    switch (field.getName()) {
                        case "address" -> addressField = field;
                        case "mark" -> markField = field;
                        case "limit" -> limitField = field;
                        case "capacity" -> capacityField = field;
                        case "container" -> containerField = field;
                    }
                }
                b.add(new StoreFieldNode(buffer, Objects.requireNonNull(addressField), address));
                b.add(new StoreFieldNode(buffer, Objects.requireNonNull(markField), ConstantNode.forInt(-1, b.getGraph())));
                b.add(new StoreFieldNode(buffer, Objects.requireNonNull(limitField), capacity));
                b.add(new StoreFieldNode(buffer, Objects.requireNonNull(capacityField), capacity));
                if (container != null)
                    b.add(new StoreFieldNode(buffer, Objects.requireNonNull(containerField), container));
                b.push(JavaKind.Object, buffer);
                return true;
            }
        };
        r.register(new PointerInvocationPlugin("wrap", Class.class, long.class, int.class));
        r.register(new PointerInvocationPlugin("wrap", Class.class, long.class, int.class, ByteBuffer.class));
        r = new InvocationPlugins.Registration(invocationPlugins, Struct.class);
        class StructInvocationPlugin extends InvocationPlugin {

            public StructInvocationPlugin(String name, Type... argumentTypes) {
                super(name, argumentTypes);
            }

            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver, ValueNode clazz, ValueNode address) {
                return apply(b, targetMethod, receiver, clazz, address, null);
            }

            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver, ValueNode clazz, ValueNode address, ValueNode container) {
                if (!clazz.isConstant()) {
                    System.err.println("Error: " + clazz + " is not a constant");
                }
                /*
                 * The allocated class must be null-checked already before the class initialization
                 * check.
                 */
                // aren't we already null checking by the below code at compile time?
                ValueNode clazzNonNull = b.nullCheckedValue(clazz, DeoptimizationAction.None);
                b.add(new EnsureClassInitializedNode(clazzNonNull));
                ResolvedJavaType structType = b.getConstantReflection().asJavaType(clazz.asConstant());
                NewInstanceNode buffer = b.add(new NewInstanceNode(structType, true));
                while (structType != null && !structType.getName().equals("Lorg/lwjgl/system/Struct;")) {
                    structType = structType.getSuperclass();
                }
                Objects.requireNonNull(structType);
                ResolvedJavaField addressField = null;
                ResolvedJavaField containerField = null;
                for (final ResolvedJavaField field : structType.getInstanceFields(true)) {
                    switch (field.getName()) {
                        case "address" -> addressField = field;
                        case "container" -> containerField = field;
                    }
                }
                b.add(new StoreFieldNode(buffer, Objects.requireNonNull(addressField), address));
                if (container != null)
                    b.add(new StoreFieldNode(buffer, Objects.requireNonNull(containerField), container));
                b.push(JavaKind.Object, buffer);
                return true;
            }
        };
        r.register(new StructInvocationPlugin("wrap", Class.class, long.class));
        r.register(new StructInvocationPlugin("wrap", Class.class, long.class, ByteBuffer.class));
        r = new InvocationPlugins.Registration(invocationPlugins, Callback.class);
        VMError.guarantee(!Pointer.BITS32, "Graal doesn't support 64-bit platforms");
        r.register(new InvocationPlugin("__stdcall", String.class) {
            @Override
            public boolean apply(GraphBuilderContext b, ResolvedJavaMethod targetMethod, Receiver receiver, ValueNode arg) {
                // stdcall never applies on 64-bit platforms
                b.push(JavaKind.Object, arg);
                return true;
            }
        });
    }

}
