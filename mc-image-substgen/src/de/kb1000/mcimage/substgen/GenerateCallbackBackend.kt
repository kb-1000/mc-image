/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.substgen

import com.oracle.svm.core.annotate.Uninterruptible
import com.oracle.svm.core.c.function.CEntryPointActions
import com.oracle.svm.core.c.function.CEntryPointOptions
import com.oracle.svm.core.c.function.CEntryPointSetup
import com.squareup.javapoet.*
import org.graalvm.nativeimage.Isolate
import org.graalvm.nativeimage.c.function.CEntryPoint
import org.graalvm.nativeimage.c.function.CEntryPointLiteral
import org.graalvm.word.PointerBase
import org.graalvm.word.WordFactory
import org.lwjgl.system.CallbackI
import javax.lang.model.element.Modifier

enum class CallbackTypes(val dyncallSig: Char, val lwjglSig: Char, val typeName: TypeName, val dcValueName: String) {
    VOID('v', 'V', TypeName.VOID, "") {
        override fun setResult(codeBlock: CodeBlock): CodeBlock {
            return codeBlock
        }
    },
    BOOLEAN('B', 'Z', TypeName.BOOLEAN, "setBool") {
        override fun convertFromLWJGL(codeBlock: CodeBlock): CodeBlock = CodeBlock.of("\$L ? 1 : 0", codeBlock)
    },
    BYTE('c', 'B', TypeName.BYTE, "setChar"),
    SHORT('s', 'S', TypeName.SHORT, "setShort"),
    INT('i', 'I', TypeName.INT, "setInt"),
    LONG('l', 'J', TypeName.LONG, "setLongLong"),
    FLOAT('f', 'F', TypeName.FLOAT, "setFloat"),
    DOUBLE('d', 'D', TypeName.DOUBLE, "setDouble"),
    POINTER('p', 'P', ClassName.get(PointerBase::class.java), "setPointer") {
        override fun convertFromLWJGL(codeBlock: CodeBlock): CodeBlock =
            CodeBlock.of("\$T.pointer(\$L)", WordFactory::class.java, codeBlock)
    },
    ;

    override fun toString(): String {
        return "$lwjglSig"
    }

    open fun setResult(codeBlock: CodeBlock): CodeBlock = CodeBlock.of("result.\$N(\$L)", dcValueName, convertFromLWJGL(codeBlock))
    open fun convertFromLWJGL(codeBlock: CodeBlock) = codeBlock
}


internal fun generateCallbackBackend() {
    JavaFile.builder(
        "de.kb1000.mcimage.target.lwjgl.generated",
        TypeSpec.classBuilder("Callbacks")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .apply {
                CallbackTypes.values().forEach { callbackType: CallbackTypes ->
                    addMethod(
                        MethodSpec.methodBuilder("cbHandler$callbackType")
                            .addAnnotation(
                                AnnotationSpec.builder(Uninterruptible::class.java)
                                    .addMember("reason", "\$S", "Only way to implement LWJGL's context behavior")
                                    .addMember("calleeMustBe", "false")
                                    .build()
                            )
                            .addAnnotation(
                                AnnotationSpec.builder(CEntryPoint::class.java)
                                    .addMember("publishAs", CEntryPoint.Publish.NotPublished)
                                    .addMember("include", CEntryPoint.NotIncludedAutomatically::class)
                                    .build()
                            )
                            .addAnnotation(
                                AnnotationSpec.builder(CEntryPointOptions::class.java)
                                    .addMember("prologue", CEntryPointOptions.NoPrologue::class)
                                    .addMember("epilogue", CEntryPointOptions.NoEpilogue::class)
                                    .build()
                            )
                            .addModifiers(Modifier.STATIC)
                            .returns(TypeName.BYTE)
                            .addParameter(
                                ClassName.get("de.kb1000.mcimage.util.dyncall", "DynCallbackSVM", "DCCallback"),
                                "cb"
                            )
                            .addParameter(
                                ClassName.get("de.kb1000.mcimage.util.dyncall", "DynCallbackSVM", "DCArgs"),
                                "args"
                            )
                            .addParameter(
                                ClassName.get("de.kb1000.mcimage.util.dyncall", "DynCallSVM", "DCValue"),
                                "result"
                            )
                            .addParameter(
                                ParameterizedTypeName.get(
                                    ClassName.get(
                                        "de.kb1000.mcimage.target.lwjgl",
                                        "ObjectHandle"
                                    ), ClassName.get(CallbackI::class.java).nestedClass(callbackType.toString())
                                ), "userdata"
                            )
                            .addStatement(
                                "boolean async = !\$T.isCurrentThreadAttachedTo((\$T) SINGLE_ISOLATE_SENTINEL)",
                                CEntryPointActions::class.java,
                                Isolate::class.java
                            )
                            .addCode("if (async) {\$>\n")
                            .addStatement(
                                "\$T.enterAttachThread((\$T) SINGLE_ISOLATE_SENTINEL, true)",
                                CEntryPointActions::class.java,
                                Isolate::class.java
                            )
                            .addCode("\$<} else {\$>\n")
                            .addStatement(
                                "\$T.enterIsolate((\$T) SINGLE_ISOLATE_SENTINEL)",
                                CEntryPointActions::class.java,
                                Isolate::class.java
                            )
                            .addCode("\$<}\n\n")
                            .addStatement("actualCbHandler$callbackType(async, args, result, userdata)")
                            .addCode("\nif (async) {\$>\n")
                            .addStatement("\$T.leaveDetachThread()", CEntryPointActions::class.java)
                            .addStatement("return '${callbackType.dyncallSig}'")
                            .addCode("\$<} else {\$>\n")
                            .addStatement("\$T.leave()", CEntryPointActions::class.java)
                            .addStatement("return '${callbackType.dyncallSig}'")
                            .addCode("\$<}\n\n")
                            .build()
                    )
                    addMethod(
                        MethodSpec.methodBuilder("actualCbHandler$callbackType")
                            .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                            .addParameter(TypeName.BOOLEAN, "async")
                            .addParameter(
                                ClassName.get("de.kb1000.mcimage.util.dyncall", "DynCallbackSVM", "DCArgs"),
                                "args"
                            )
                            .addParameter(
                                ClassName.get("de.kb1000.mcimage.util.dyncall", "DynCallSVM", "DCValue"),
                                "result"
                            )
                            .addParameter(
                                ParameterizedTypeName.get(
                                    ClassName.get(
                                        "de.kb1000.mcimage.target.lwjgl",
                                        "ObjectHandle"
                                    ), ClassName.get(CallbackI::class.java).nestedClass(callbackType.toString())
                                ), "userdata"
                            )
                            .addCode("try {\$>\n")
                            // XXX: write the return value into result (let's hope MC doesn't use any callbacks with
                            //  return values...)
                            //  (this does seem to be the case) (or not)

                            .addStatement(
                                "\$L",
                                callbackType.setResult(
                                    CodeBlock.of(
                                        "\$T.get(userdata).callback(args.rawValue())",
                                        ClassName.get("de.kb1000.mcimage.target.lwjgl", "ObjectHandles")
                                    )
                                )
                            )
                            .addCode("\$<} catch (\$T t) {\$>\n", Throwable::class.java)
                            .addCode("if (!async) {\$>\n")
                            .addStatement("\$T.pendingException.set(t)", ClassName.get("de.kb1000.mcimage.util", "CException"))
                            .addCode("\$<} else {\$>\n")
                            .addStatement(
                                "System.err.println(\$S)",
                                "[LWJGL] Exception in callback that was invoked asynchronously from a native thread.\n"
                            )
                            .addStatement("t.printStackTrace(System.err)")
                            .addStatement("System.err.flush()")
                            .addCode("\$<}\n")
                            .addCode("\$<}\n")
                            .build()
                    )
                    addField(
                        FieldSpec.builder(
                            ParameterizedTypeName.get(
                                ClassName.get(CEntryPointLiteral::class.java),
                                ClassName.get("de.kb1000.mcimage.util.dyncall", "DynCallbackSVM", "DCCallbackHandler")
                            ),
                            "CALLBACK_HANDLER_LITERAL_${callbackType.name}",
                            Modifier.PUBLIC,
                            Modifier.STATIC,
                            Modifier.FINAL
                        )
                            .initializer(
                                "\$T.create(\$T.class, \$S, new Class<?>[] { \$T.class, \$T.class, \$T.class, \$T.class })",
                                CEntryPointLiteral::class.java,
                                ClassName.get("de.kb1000.mcimage.target.lwjgl.generated", "Callbacks"),
                                "cbHandler$callbackType",
                                ClassName.get("de.kb1000.mcimage.util.dyncall", "DynCallbackSVM", "DCCallback"),
                                ClassName.get("de.kb1000.mcimage.util.dyncall", "DynCallbackSVM", "DCArgs"),
                                ClassName.get("de.kb1000.mcimage.util.dyncall", "DynCallSVM", "DCValue"),
                                ClassName.get("de.kb1000.mcimage.target.lwjgl", "ObjectHandle")
                            )
                            .build()
                    )
                }
            }
            .addMethod(
                MethodSpec.methodBuilder("getNativeFunction")
                    .returns(ClassName.get("de.kb1000.mcimage.util.dyncall", "DynCallbackSVM", "DCCallbackHandler"))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(TypeName.CHAR, "type")
                    .addCode("switch (type) {\$>\n")
                    .apply {
                        CallbackTypes.values().forEach { callbackType: CallbackTypes ->
                            addCode("\$<case '${callbackType.dyncallSig}':\$>\n")
                            addStatement("return CALLBACK_HANDLER_LITERAL_${callbackType.name}.getFunctionPointer()")
                        }
                    }
                    .addCode("\$<default:\$>\n")
                    .addStatement("throw new \$T()", IllegalArgumentException::class.java)
                    .addCode("\$<}\n")
                    .build()
            )
            .build()
    )
        .addStaticImport(CEntryPointSetup::class.java, "SINGLE_ISOLATE_SENTINEL")
        .indent("    ")
        .build().writeTo(targetDir)
}