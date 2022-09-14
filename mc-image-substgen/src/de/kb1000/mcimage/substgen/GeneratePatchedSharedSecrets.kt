/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.substgen

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes.ASM9
import java.io.InputStream
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.div
import kotlin.io.path.writeBytes

fun generatePatchedSharedSecrets() {
    val targetDir = Path("java.base")

    val targetFile = targetDir / "jdk" / "internal" / "access" / "SharedSecrets.class"
    targetFile.parent.createDirectories()

    val bytecode = TreeMap::class.java.module.getResourceAsStream("jdk/internal/access/SharedSecrets.class").use(InputStream::readAllBytes)
    val classReader = ClassReader(bytecode)
    val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES)
    classReader.accept(object : ClassVisitor(ASM9, classWriter) {
        override fun visitField(
            access: Int,
            name: String,
            descriptor: String,
            signature: String?,
            value: Any?
        ): FieldVisitor? {
            val fv = super.visitField(access, name, descriptor, signature, value) ?: return null
            fv.visitAnnotation("Ljdk/internal/vm/annotation/Stable;", true)?.visitEnd()
            return fv
        }
    }, 0)
    targetFile.writeBytes(classWriter.toByteArray())
}