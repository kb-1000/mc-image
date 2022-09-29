/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.commons;

import com.oracle.svm.core.SubstrateUtil;
import com.oracle.svm.core.annotate.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.exception.CloneFailedException;

@TargetClass(Object.class)
final class Target_java_lang_Object {
    @Alias
    protected native Object clone();
}


@TargetClass(ObjectUtils.class)
final class Target_org_apache_commons_lang3_ObjectUtils {
    @AlwaysInline("Try enforcing static binding")
    @Substitute
    static <T> T clone(final T obj) {
        if (obj instanceof Cloneable) {
            final Object result;
            // TODO: can we use the clone accessor for arrays too? if so, remove the array special casing
            //       or make it use the same code without catches
            /*if (obj.getClass().isArray()) {
                final Class<?> componentType = obj.getClass().getComponentType();
                if (!componentType.isPrimitive()) {
                    result = ((Object[]) obj).clone();
                } else {
                    int length = Array.getLength(obj);
                    result = Array.newInstance(componentType, length);
                    while (length-- > 0) {
                        Array.set(result, length, Array.get(obj, length));
                    }
                }
            } else {*/
            try {
                result = SubstrateUtil.cast(obj, Target_java_lang_Object.class).clone();
            } catch (final Exception e) {
                throw new CloneFailedException("Exception cloning Cloneable type "
                        + obj.getClass().getName(), e);
            }
            //}
            @SuppressWarnings("unchecked") // OK because input is of type T
            final T checked = (T) result;
            return checked;
        }

        return null;
    }

    @AlwaysInline("Try enforcing static binding")
    @AnnotateOriginal
    static native <T> T cloneIfPossible(final T obj);
}
