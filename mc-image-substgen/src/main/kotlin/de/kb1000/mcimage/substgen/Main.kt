/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

@file:JvmName("Main")

package de.kb1000.mcimage.substgen

import java.nio.file.FileSystem
import java.nio.file.FileSystems
import kotlin.io.path.Path
import kotlin.io.path.toPath
import kotlin.reflect.KClass

fun openJar(c: KClass<*>): FileSystem =
        FileSystems.newFileSystem(c.java.protectionDomain.codeSource.location.toURI().toPath())

var targetDir = Path("mc-image-lib", "gen")

fun main(args: Array<String>) {
    if (args.isNotEmpty())
        targetDir = Path(args[0])
    generateJNIInvokerSubstitution()
    generateCallbackBackend()
    generateGLInvokers()
    generatePatchedTreeMap()
    generatePatchedSharedSecrets()
}
