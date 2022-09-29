/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.substgen

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import kotlin.reflect.KClass

fun AnnotationSpec.Builder.addMember(name: String, type: TypeName): AnnotationSpec.Builder =
    addMember(name, "\$T.class", type)

fun AnnotationSpec.Builder.addMember(name: String, clazz: Class<*>): AnnotationSpec.Builder =
    addMember(name, ClassName.get(clazz))

inline fun AnnotationSpec.Builder.addMember(name: String, clazz: KClass<*>): AnnotationSpec.Builder =
    addMember(name, clazz.java)

fun AnnotationSpec.Builder.addMember(name: String, enum: Enum<*>): AnnotationSpec.Builder =
    addMember(name, "\$T.\$L", enum.javaClass, enum.name)
