/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.jdk;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.TargetClass;
import com.oracle.svm.methodhandles.MethodHandleFeature;
import de.kb1000.mcimage.util.HostedConstants;
import jdk.vm.ci.meta.MetaAccessProvider;
import jdk.vm.ci.meta.ResolvedJavaField;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

@TargetClass(value = MethodHandle.class)
final class Target_java_lang_invoke_MethodHandle {
    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Custom, declClass = MethodHandleRegisterer.class)
    MethodType type;
    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Custom, declClass = MethodHandleRegisterer.class)
    Target_java_lang_invoke_LambdaForm form;
}

@TargetClass(className = "java.lang.invoke.DirectMethodHandle")
final class Target_java_lang_invoke_DirectMethodHandle {
    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Custom, declClass = MethodHandleRegisterer.class)
    Target_java_lang_invoke_MemberName member;
}

@TargetClass(className = "java.lang.invoke.LambdaForm")
final class Target_java_lang_invoke_LambdaForm {
}

@TargetClass(className = "java.lang.invoke.MemberName")
final class Target_java_lang_invoke_MemberName {
}

@Platforms(Platform.HOSTED_ONLY.class)
final class MethodHandleRegisterer implements RecomputeFieldValue.CustomFieldValueTransformer {
    static final MethodHandle register;

    static {
        try {
            register = HostedConstants.IMPL_LOOKUP.findSpecial(MethodHandleFeature.class, "registerMethodHandle", MethodType.methodType(Object.class, Object.class), MethodHandleFeature.class);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object transform(MetaAccessProvider metaAccess, ResolvedJavaField original, ResolvedJavaField annotated, Object receiver, Object originalValue) {
        try {
            Object o = register.invokeExact(ImageSingletons.lookup(MethodHandleFeature.class), receiver);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return originalValue;
    }

    @Override
    public RecomputeFieldValue.ValueAvailability valueAvailability() {
        return RecomputeFieldValue.ValueAvailability.BeforeAnalysis;
    }
}
