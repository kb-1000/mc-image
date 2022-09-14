/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.agent;

import com.oracle.svm.core.SubstrateOptions;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.hosted.substitute.AnnotatedField;
import com.oracle.svm.hosted.substitute.ComputedValueField;
import jdk.vm.ci.meta.ResolvedJavaField;

public class Hooks {
    public static boolean equalsAnnotationSubstitutionProcessorOriginalField(ResolvedJavaField existingAlias, ResolvedJavaField original, ResolvedJavaField computedAlias) {
        // standard case
        if (existingAlias.equals(original)) return true;
        if (existingAlias instanceof ComputedValueField existingComputedValueField && existingComputedValueField.getRecomputeValueKind() == RecomputeFieldValue.Kind.None && computedAlias instanceof ComputedValueField computedValueField) {
            // TODO: should check caching and other flags, but this should do for now
            return true;
        }
        // this should be safe to allow replacing (@InjectAccessors replacing an @Alias field without re-computation)
        return existingAlias instanceof ComputedValueField computedValueField && computedValueField.getRecomputeValueKind() == RecomputeFieldValue.Kind.None && computedAlias instanceof AnnotatedField;
    }

    public static boolean transformFallbackInConfig(boolean inConfig) {
        return inConfig && !Integer.valueOf(0).equals(SubstrateOptions.FallbackThreshold.getValue());
    }
}
