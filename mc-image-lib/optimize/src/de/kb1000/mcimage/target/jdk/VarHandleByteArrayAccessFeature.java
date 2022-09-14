/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.jdk;

/*@AutomaticFeature
public class VarHandleByteArrayAccessFeature implements InternalFeature {
    @Override
    public void registerGraphBuilderPlugins(Providers providers, GraphBuilderConfiguration.Plugins plugins, ParsingReason reason) {
        plugins.appendInlineInvokePlugin(new InlineInvokePlugin() {
            @Override
            public InlineInfo shouldInlineInvoke(GraphBuilderContext b, ResolvedJavaMethod method, ValueNode[] args) {
                String className = method.getDeclaringClass().getUnqualifiedName();
                if ((className.startsWith("VarHandleByte") || className.startsWith("VarHandleReferences")) && (method.getName().startsWith("set") || method.getName().startsWith("get") || method.getName().startsWith("compare") || method.getName().startsWith("weak"))) {
                    return InlineInfo.createStandardInlineInfo(method);
                }
                return null;
            }
        });
    }
}*/
