/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.jdk;

/*@TargetClass(className = "java.lang.invoke.DelegatingMethodHandle")
final class Target_java_lang_invoke_DelegatingMethodHandle {
    @Delete
    Target_java_lang_invoke_DelegatingMethodHandle(MethodHandle target) {
    }

    @Delete
    Target_java_lang_invoke_DelegatingMethodHandle(MethodType type, MethodHandle target) {
    }

    @Delete
    Target_java_lang_invoke_DelegatingMethodHandle(MethodType type, Target_java_lang_invoke_LambdaForm form) {
    }

    @Delete
    native MethodHandle getTarget();
}

@TargetClass(MethodHandles.Lookup.class)
final class Target_java_lang_invoke_MethodHandles_Lookup {
    @Delete
    native MethodHandle getDirectMethod(byte refKind, Class<?> refc, Target_java_lang_invoke_MemberName method, MethodHandles.Lookup callerLookup);

    @Delete
    native MethodHandle getDirectMethodCommon(byte refKind, Class<?> refc, Target_java_lang_invoke_MemberName method,
                                              boolean checkSecurity,
                                              boolean doRestrict,
                                              MethodHandles.Lookup boundCaller);
    @Delete
    native MethodHandle getDirectMethodNoSecurityManager(byte refKind, Class<?> refc, Target_java_lang_invoke_MemberName method, MethodHandles.Lookup callerLookup);

    @Delete
    native MethodHandle getDirectMethodNoRestrictInvokeSpecial(Class<?> refc, Target_java_lang_invoke_MemberName method, MethodHandles.Lookup callerLookup);

    @Delete
    native MethodHandle findStatic(Class<?> refc, String name, MethodType type);

    @Delete
    native MethodHandle findVirtual(Class<?> refc, String name, MethodType type);

    @Delete
    native MethodHandle findSpecial(Class<?> refc, String name, MethodType type, Class<?> specialCaller);

    @Delete
    native MethodHandle getDirectConstructor(Class<?> refc, Target_java_lang_invoke_MemberName ctor);

    @Delete
    native MethodHandle getDirectConstructorNoSecurityManager(Class<?> refc, Target_java_lang_invoke_MemberName ctor);

    @Delete
    native MethodHandle getDirectConstructorCommon(Class<?> refc, Target_java_lang_invoke_MemberName ctor,
                                                   boolean checkSecurity);
}

@TargetClass(className = "java.lang.invoke.MethodHandleImpl")
final class Target_java_lang_invoke_MethodHandleImpl {
    @Delete
    static native MethodHandle makeVarargsCollector(MethodHandle target, Class<?> arrayType);
}

@TargetClass(MethodHandle.class)
final class Target_java_lang_invoke_MethodHandle2 {
    @Delete
    native MethodHandle asVarargsCollector(Class<?> arrayType);

    @Delete
    native MethodHandle withVarargs(boolean makeVarargs);

    @Delete
    native MethodHandle setVarargs(Target_java_lang_invoke_MemberName member);
}*/
