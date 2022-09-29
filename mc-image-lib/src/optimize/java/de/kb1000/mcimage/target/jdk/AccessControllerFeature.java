/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.jdk;

/*@AutomaticFeature
public class AccessControllerFeature implements InternalFeature {
    @Override
    public void registerGraphBuilderPlugins(Providers providers, GraphBuilderConfiguration.Plugins plugins, ParsingReason reason) {
        final ResolvedJavaType accessControllerHelperType = providers.getMetaAccess().lookupJavaType(AccessControllerHelper.class);
        plugins.appendInlineInvokePlugin(new InlineInvokePlugin() {
            @Override
            public InlineInfo shouldInlineInvoke(GraphBuilderContext b, ResolvedJavaMethod method, ValueNode[] args) {
                if (method.getDeclaringClass().getName().contains("AccessController") && (method.getName().startsWith("doPrivileged") || method.getName().equals("checkPermission"))) {
                    String name = method.getName();
                    if ("doPrivilegedWithCombiner".equals(name))
                        name = "doPrivileged";
                    ResolvedJavaMethod method1 = accessControllerHelperType.findMethod(name, method.getSignature());
                    return InlineInfo.createStandardInlineInfo(Objects.requireNonNull(method1));
                }
                return null;
            }
        });
    }
}

@SuppressWarnings({"removal", "unused"})
class AccessControllerHelper {
    public static <T> T doPrivileged(PrivilegedAction<T> action) {
        return action.run();
    }

    public static <T> T doPrivileged(PrivilegedExceptionAction<T> action) throws Throwable {
        try {
            return action.run();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new PrivilegedActionException(e);
        }
    }

    public static <T> T doPrivileged(PrivilegedAction<T> action, AccessControlContext ctx) {
        return action.run();
    }

    public static <T> T doPrivileged(PrivilegedAction<T> action,
                                     AccessControlContext context,
                                     Permission... perms) {
        return action.run();
    }


    public static <T> T doPrivileged(PrivilegedExceptionAction<T> action, AccessControlContext ctx) throws Throwable {
        try {
            return action.run();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new PrivilegedActionException(e);
        }
    }

    public static <T> T doPrivileged(PrivilegedExceptionAction<T> action, AccessControlContext ctx, Permission... perms) throws Throwable {
        try {
            return action.run();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new PrivilegedActionException(e);
        }
    }

    public static void checkPermission(Permission perm) {}
}*/
