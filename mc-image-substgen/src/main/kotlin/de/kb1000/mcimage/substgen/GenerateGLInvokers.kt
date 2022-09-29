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
import de.kb1000.mcimage.substgen.jaxb.Commands
import de.kb1000.mcimage.substgen.jaxb.Registry
import jakarta.xml.bind.JAXB
import jakarta.xml.bind.JAXBElement
import org.graalvm.nativeimage.c.function.CFunctionPointer
import org.graalvm.nativeimage.c.function.InvokeCFunctionPointer
import org.graalvm.word.PointerBase
import org.graalvm.word.SignedWord
import org.graalvm.word.UnsignedWord
import org.graalvm.word.WordFactory
import org.lwjgl.opengl.GL46
import org.lwjgl.opengl.GL46C
import java.io.Serializable
import java.lang.reflect.Method
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.lang.model.element.Modifier
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.writeBytes
import java.lang.reflect.Modifier as ReflectModifier

interface GLType {
    val lwjglType: TypeName
    val graalvmType: TypeName // TODO: maybe be a bit more exact with types? not too important though, if it works it works...
}

enum class GLPrimitiveType(override val lwjglType: TypeName, override val graalvmType: TypeName = lwjglType) : GLType {
    void(TypeName.VOID),
    khronos_int8_t(TypeName.BYTE),
    khronos_uint8_t(khronos_int8_t),
    khronos_int16_t(TypeName.SHORT),
    khronos_uint16_t(khronos_int16_t),
    khronos_int32_t(TypeName.INT),
    khronos_uint32_t(khronos_int32_t),
    khronos_int64_t(TypeName.LONG),
    khronos_uint64_t(khronos_int64_t),
    khronos_intptr_t(TypeName.LONG, ClassName.get(SignedWord::class.java)),
    khronos_uintptr_t(TypeName.LONG, ClassName.get(UnsignedWord::class.java)),
    khronos_ssize_t(khronos_intptr_t),
    khronos_usize_t(khronos_uintptr_t),
    khronos_float_t(TypeName.FLOAT),
    GLenum(TypeName.INT),
    GLboolean(TypeName.BOOLEAN),
    GLbitfield(TypeName.INT),
    GLvoid(void),
    GLbyte(khronos_int8_t),
    GLubyte(khronos_uint8_t),
    GLshort(khronos_int16_t),
    GLushort(khronos_uint16_t),
    GLint(TypeName.INT),
    GLuint(GLint),
    GLclampx(khronos_int32_t),
    GLsizei(TypeName.INT),
    GLfloat(khronos_float_t),
    GLclampf(khronos_float_t),
    GLdouble(TypeName.DOUBLE),
    GLclampd(TypeName.DOUBLE),
    GLchar(TypeName.BYTE),
    // should be void * on Apple but I don't think it matters here, we're not generating substitution methods for
    // extensions
    GLhandleARB(TypeName.INT),
    GLhalf(khronos_uint16_t),
    GLfixed(khronos_int32_t),
    GLintptr(khronos_intptr_t),
    GLsizeiptr(khronos_ssize_t),
    GLint64(khronos_int64_t),
    GLuint64(khronos_uint64_t),
    GLvdpauSurfaceNV(GLintptr),
    ;

    // typedef
    constructor(
            baseType: GLPrimitiveType,
            lwjglType: TypeName = baseType.lwjglType,
            graalvmType: TypeName = baseType.graalvmType
    ) : this(lwjglType, graalvmType)
}

data class GLPointerType(val baseType: GLType) : GLType {
    override val lwjglType: TypeName = TypeName.LONG
    override val graalvmType: TypeName = ClassName.get(PointerBase::class.java)

    override fun toString() = if (baseType is GLPointerType) "$baseType*" else "$baseType *"
}

val customGLTypes = mapOf(
        "GLeglClientBufferEXT" to GLPointerType(GLPrimitiveType.void),
        "GLeglImageOES" to GLPointerType(GLPrimitiveType.void),
        "GLcharARB" to GLPrimitiveType.GLchar,
        "GLhalfARB" to GLPrimitiveType.GLhalf,
        "GLintptrARB" to GLPrimitiveType.GLintptr,
        "GLsizeiptrARB" to GLPrimitiveType.GLsizeiptr,
        "GLint64EXT" to GLPrimitiveType.GLint64,
        "GLuint64EXT" to GLPrimitiveType.GLuint64,
        "GLsync" to GLPointerType(GLPrimitiveType.void), // actually a pointer to a struct, can't model that though
        "struct _cl_context" to GLPrimitiveType.void, // not actually true, but close enough
        "struct _cl_event" to GLPrimitiveType.void,
        "GLDEBUGPROC" to GLPointerType(GLPrimitiveType.void),
        "GLDEBUGPROCARB" to GLPointerType(GLPrimitiveType.void),
        "GLDEBUGPROCKHR" to GLPointerType(GLPrimitiveType.void),
        "GLDEBUGPROCAMD" to GLPointerType(GLPrimitiveType.void),
        "GLhalfNV" to GLPrimitiveType.GLhalf,
        "GLVULKANPROCNV" to GLPointerType(GLPrimitiveType.void),
        "int" to GLPrimitiveType.GLint,
)

data class GLCommand(val name: String, val returnType: GLType, val parameters: List<Pair<String, GLType>>)

fun convert(typeA: TypeName, typeB: TypeName, expression: CodeBlock): CodeBlock {
    if (typeA == typeB) {
        return expression
    } else if (typeA is ClassName && (typeA.simpleName().contains(Regex("Pointer|Word"))) && typeB == TypeName.LONG) {
        return CodeBlock.of("\$L.rawValue()", expression)
    } else if (typeB is ClassName && typeB.simpleName().contains("Pointer") && typeA == TypeName.LONG) {
        return CodeBlock.of("\$T.pointer(\$L)", WordFactory::class.java, expression)
    } else if (typeB is ClassName && typeB.simpleName().contains("SignedWord") && typeA == TypeName.LONG) {
        return CodeBlock.of("\$T.signed(\$L)", WordFactory::class.java, expression)
    } else if (typeB is ClassName && typeB.simpleName().contains("UnsignedWord") && typeA == TypeName.LONG) {
        return CodeBlock.of("\$T.unsigned(\$L)", WordFactory::class.java, expression)
    }
    throw IllegalArgumentException("Don't know how to convert $typeA to $typeB")
}

fun parseType(list: List<Serializable>): GLType {
    if (list.isEmpty()) {
        throw IllegalArgumentException()
    }
    var i = 0
    var type: GLType? = null
    while (i < list.size) {
        val element = list[i]
        if (element is JAXBElement<*>) {
            when (element.name.localPart) {
                "ptype" -> {
                    val value = element.value.toString().trim()
                    type = customGLTypes[value] ?: GLPrimitiveType.valueOf(value)
                }
                else -> {
                    throw IllegalArgumentException("$list")
                }
            }
        } else if (element is String) {
            var str: String = element.trim()
            while (str.isNotEmpty()) {
                if (str.startsWith("const")) {
                    str = str.drop("const".length)
                }
                if (str.startsWith("void")) {
                    if (type != null) {
                        throw IllegalStateException("void after type was set, $list")
                    }
                    type = GLPrimitiveType.void
                    str = str.drop("void".length)
                }
                if (str.startsWith("int")) {
                    if (type != null) {
                        throw IllegalStateException("int after type was set, $list")
                    }
                    type = GLPrimitiveType.GLint
                    str = str.drop("int".length)
                }
                if (str.startsWith('*')) {
                    type = GLPointerType(type!!)
                    str = str.drop(1)
                }
                str = str.trim()
            }
        }
        i++
    }
    return type ?: GLPrimitiveType.void
}

fun generateGLInvokers() {
    val glXml = Path("gl.xml")
    if (!glXml.exists()) {
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
                .uri(URI.create("https://raw.githubusercontent.com/KhronosGroup/OpenGL-Registry/main/xml/gl.xml"))
                .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofByteArray())
        glXml.writeBytes(response.body())
    }

    val allMethods: List<Method> =
            (ClassIterator(GL46C::class.java).asSequence() + ClassIterator(GL46::class.java).asSequence()).flatMap { clazz -> clazz.declaredMethods.asSequence() }
                    .toList()
    val registry = JAXB.unmarshal(glXml.toFile(), Registry::class.java)
    val commands = registry.commentOrTypesOrGroups.firstNotNullOf { it as? Commands }.command.map {
        val proto = it.proto
        val name =
                (proto.content.last() as JAXBElement<*>).also { if (it.name.localPart != "name") throw IllegalArgumentException() }.value as String
        GLCommand(name, parseType(proto.content.dropLast(1)), it.param.map {
            val name =
                    (it.content.last() as JAXBElement<*>).also { if (it.name.localPart != "name") throw IllegalArgumentException() }.value as String
            name to parseType(it.content.dropLast(1))
        })
    }
    val nativeMethods = allMethods.filter { (it.modifiers and ReflectModifier.NATIVE) != 0 }
    val classes = nativeMethods.map { it.declaringClass }.toSortedSet(Comparator.comparing(Class<*>::getName))
    for (clazz in classes) {
        val typeSpec = TypeSpec.classBuilder("Target_${clazz.name.replace('.', '_')}")
                .addAnnotation(
                        AnnotationSpec.builder(TargetClass::class.java)
                                .addMember("className", "\$S", clazz.name)
                                .addMember("onlyWith", ClassName.get("de.kb1000.mcimage.util", "Environment", "ClientOnly"))
                                .build()
                )
                .addModifiers(Modifier.FINAL)
                .addMethods(nativeMethods.asSequence().filter { it.declaringClass == clazz }.map { nativeMethod ->
                    val command = commands.single { it.name == nativeMethod.name.trimStart('n') }
                    MethodSpec.methodBuilder(nativeMethod.name)
                            .addAnnotation(Substitute::class.java)
                            .addModifiers(Modifier.STATIC)
                            .returns(command.returnType.lwjglType)
                            .addParameters(command.parameters.map { (name, type) ->
                                ParameterSpec.builder(type.lwjglType, name).build()
                            })
                            .addCode(
                                    "\$L", TypeSpec
                                    .interfaceBuilder("FunctionPointer")
                                    .addSuperinterface(CFunctionPointer::class.java)
                                    .addMethod(
                                            MethodSpec.methodBuilder("invoke")
                                                    .addAnnotation(InvokeCFunctionPointer::class.java)
                                                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                                    .returns(command.returnType.graalvmType)
                                                    .addParameters(command.parameters.map { (name, type) ->
                                                        ParameterSpec.builder(
                                                                type.graalvmType,
                                                                name
                                                        ).build()
                                                    })
                                                    .build()
                                    )
                                    .build()
                            )
                            .addStatement(
                                    if (command.returnType.lwjglType != TypeName.VOID) "return \$L" else "\$L",
                                    convert(
                                            command.returnType.graalvmType,
                                            command.returnType.lwjglType,
                                            CodeBlock.builder().add(
                                                    "\$T.<FunctionPointer>pointer(\$T.getICD().\$L).invoke(",
                                                    WordFactory::class.java,
                                                    ClassName.get("de.kb1000.mcimage.target.lwjgl", "Target_org_lwjgl_opengl_GL"),
                                                    command.name
                                            ).add(List(command.parameters.size) { "\$L" }.joinToString(separator = ", "),
                                                    *command.parameters.map { (name, type) ->
                                                        convert(
                                                                type.lwjglType,
                                                                type.graalvmType,
                                                                CodeBlock.of("\$N", name)
                                                        )
                                                    }.toTypedArray()
                                            ).add(")").build()
                                    )
                            )
                            .build()
                }.asIterable())
                .build()
        JavaFile.builder("de.kb1000.mcimage.target.lwjgl.generated", typeSpec)
                .indent("    ").build().writeTo(targetDir)
    }
}

class ClassIterator(var currentClass: Class<*>) : Iterator<Class<*>> {
    override fun hasNext(): Boolean {
        return currentClass.superclass != null
    }

    override fun next(): Class<*> {
        val currentClass = currentClass
        this.currentClass = currentClass.superclass
        return currentClass
    }
}