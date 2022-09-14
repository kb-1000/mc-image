/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.substgen

import com.oracle.svm.core.annotate.Substitute
import com.oracle.svm.core.annotate.TargetClass
import com.squareup.javapoet.*
import org.graalvm.nativeimage.PinnedObject
import org.graalvm.nativeimage.c.function.CFunctionPointer
import org.graalvm.nativeimage.c.function.InvokeCFunctionPointer
import org.graalvm.nativeimage.c.type.*
import org.graalvm.word.UnsignedWord
import org.graalvm.word.WordFactory
import org.lwjgl.system.JNI
import org.objectweb.asm.*
import java.nio.file.Files
import javax.lang.model.element.Modifier
import kotlin.reflect.KClass

private enum class LWJGLJNIType(
    val char: Char,
    jniType: KClass<*>,
    graalType: KClass<*> = jniType,
) {
    BYTE('B', Byte::class),
    DOUBLE('D', Double::class),
    FLOAT('F', Float::class),
    INT('I', Int::class),
    LONG('J', Long::class),
    POINTER('P', Long::class, UnsignedWord::class) {
        override fun convertFromJni(name: String): CodeBlock =
            CodeBlock.of("\$T.unsigned(\$N)", WordFactory::class.java, name)

        override fun convertToJni(): CodeBlock {
            return CodeBlock.of(".rawValue()")
        }
    },
    SHORT('S', Short::class),
    VOID('V', Void.TYPE.kotlin),
    BOOLEAN('Z', Boolean::class),
    BYTE_ARRAY('P', ByteArray::class, CCharPointer::class),
    DOUBLE_ARRAY('P', DoubleArray::class, CDoublePointer::class),
    FLOAT_ARRAY('P', FloatArray::class, CFloatPointer::class),
    INT_ARRAY('P', IntArray::class, CIntPointer::class),
    LONG_ARRAY('P', LongArray::class, CLongPointer::class),
    SHORT_ARRAY('P', ShortArray::class, CShortPointer::class),
    BOOLEAN_ARRAY('P', BooleanArray::class, CCharPointer::class),
    ;

    val jniType = jniType.java
    val graalType = graalType.java
    val isArray = this.jniType.isArray

    open fun convertFromJni(name: String): CodeBlock = if (!isArray) CodeBlock.of("\$N", name) else CodeBlock.of("\$NArray != null ? \$N.addressOfArrayElement(0) : \$T.nullPointer()", name, name, WordFactory::class.java)
    open fun convertToJni(): CodeBlock = CodeBlock.of("")
}

private val methodRegex = Regex("^(?:invoke|call)([JP]*)([BDFIJPSVZ])$")
internal fun generateJNIInvokerSubstitution() {
    openJar(JNI::class).use { fs ->
        val dir = fs.getPath("org", "lwjgl", "system")
        val classReader = ClassReader(Files.readAllBytes(dir.resolve("JNI.class")))
        val typeBuilder = TypeSpec.classBuilder("Target_org_lwjgl_system_JNI")
        typeBuilder.addAnnotation(
            AnnotationSpec.builder(TargetClass::class.java).addMember("value", "\$T.class", JNI::class.java).build()
        )
        typeBuilder.addModifiers(Modifier.FINAL)
        classReader.accept(object : ClassVisitor(Opcodes.ASM9) {
            override fun visitMethod(
                access: Int,
                name: String,
                descriptor: String,
                signature: String?,
                exceptions: Array<out String>?
            ): MethodVisitor? {
                // Skip the constructors, only process the actual methods
                val match = methodRegex.matchEntire(name) ?: return null
                val methodType = Type.getMethodType(descriptor)
                val longTypes = match.groupValues[1].iterator()
                val jniTypes = methodType.argumentTypes.dropLast(1).map {
                    if (it.sort == Type.ARRAY) {
                        print("")
                    }
                    if (it == Type.LONG_TYPE) {
                        when (longTypes.nextChar()) {
                            'J' -> LWJGLJNIType.LONG
                            'P' -> LWJGLJNIType.POINTER
                            else -> throw IllegalStateException()
                        }
                    } else {
                        for (jniType in LWJGLJNIType.values()) {
                            if (Type.getType(jniType.jniType) == it) {
                                return@map jniType
                            }
                        }
                        throw IllegalStateException()
                    }
                }
                val arrayParams = jniTypes.withIndex().filter { it.value.isArray }
                lateinit var jniReturnType: LWJGLJNIType
                for (jniType in LWJGLJNIType.values()) {
                    if (jniType.char == match.groupValues[2][0]) {
                        jniReturnType = jniType
                        break
                    }
                }
                val methodBuilder = MethodSpec.methodBuilder(name)
                methodBuilder.addAnnotation(Substitute::class.java)
                methodBuilder.addModifiers(Modifier.STATIC)
                methodBuilder.returns(jniReturnType.jniType)
                var i = 0
                methodBuilder.addParameters(jniTypes.map { ParameterSpec.builder(it.jniType, "param${i++}${if (it.isArray) "Array" else ""}").build() })
                methodBuilder.addParameter(Long::class.java, "__functionAddress")
                methodBuilder.addException(Throwable::class.java)
                val functionPointerType = TypeSpec.interfaceBuilder("FunctionPointer").apply {
                    addSuperinterface(CFunctionPointer::class.java)
                    i = 0
                    addMethod(
                        MethodSpec.methodBuilder("invoke")
                            .addAnnotation(InvokeCFunctionPointer::class.java)
                            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                            .returns(jniReturnType.graalType)
                            .addParameters(jniTypes.map { ParameterSpec.builder(it.graalType, "param${i++}").build() })
                            .build()
                    )
                }.build()
                methodBuilder.addCode("\$L", functionPointerType)
                if (jniReturnType != LWJGLJNIType.VOID)
                    methodBuilder.addStatement("\$T result", jniReturnType.jniType)
                i = 0
                if (arrayParams.isNotEmpty()) {
                    methodBuilder.addCode("try (")
                    for ((index, _) in arrayParams) {
                        methodBuilder.addCode("\$T param$index = \$T.create(param${index}Array);", PinnedObject::class.java, PinnedObject::class.java)
                    }
                    methodBuilder.addCode(") {\$>\n")
                }
                methodBuilder.addStatement("\$L", CodeBlock.builder()
                    .apply {
                        if (jniReturnType != LWJGLJNIType.VOID)
                            add("result = ")
                        add("\$T.<FunctionPointer>pointer(__functionAddress).invoke(", WordFactory::class.java)
                        jniTypes.firstOrNull()?.let { add(it.convertFromJni("param${i++}")) }
                        for (jniType in jniTypes.drop(1)) {
                            add(",\$W")
                            add(jniType.convertFromJni("param${i++}"))
                        }
                        add(")")
                        add(jniReturnType.convertToJni())
                    }
                    .build())
                if (arrayParams.isNotEmpty()) {
                    methodBuilder.addCode("\$<}\n")
                }
                methodBuilder.addStatement("\$T.rethrow()", ClassName.get("de.kb1000.mcimage.util", "CException"))
                if (jniReturnType != LWJGLJNIType.VOID)
                    methodBuilder.addStatement("return result")

                typeBuilder.addMethod(methodBuilder.build())
                return null
            }
        }, 0)
        JavaFile.builder("de.kb1000.mcimage.target.lwjgl.generated", typeBuilder.build()).indent("    ").build()
            .writeTo(targetDir)
    }
}