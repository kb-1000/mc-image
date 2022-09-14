/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.agent.transformers;

import de.kb1000.mcimage.agent.Config;
import jdk.internal.org.objectweb.asm.*;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

public class SVMReflectionMethodTraceTransformer implements ClassFileTransformer {
    /*public static Class<?> substrateMethodAccessorType;
    public static MethodHandle substrateMethodAccessorGetExecutable;*/

    public static void validateReflectionMethod(Executable reflectionMethod) throws Throwable {
        if (reflectionMethod == null) return;
        var s = Type.getDescriptor(reflectionMethod.getDeclaringClass()) + reflectionMethod.getName() + (reflectionMethod instanceof Method m ? Type.getMethodDescriptor(m) : Type.getConstructorDescriptor((Constructor<?>) reflectionMethod));
        if (Config.traceReflectionMethods.contains(s)) {
            throw (RuntimeException)(SVMReflectionMethodTraceTransformer.class.getClassLoader().loadClass("com.oracle.graal.pointsto.constraints.UnsupportedFeatureException").getConstructor(String.class).newInstance("Method not allowed for reflection: " + s));
        }
    }

    @SuppressWarnings("unused") // Used by ASM code
    public static void validateReflectionMethodConstant(Object constant) throws Throwable {
        if (constant instanceof Executable executable) {
            validateReflectionMethod(executable);
        } else if (constant instanceof TypeVariableImpl typeVariable) {
            validateReflectionMethodConstant(typeVariable.getGenericDeclaration());
        }/* else if (substrateMethodAccessorType.isInstance(constant)) {
            validateReflectionMethod((Executable) substrateMethodAccessorGetExecutable.invoke(constant));
        }*/
    }

    @Override
    public byte[] transform(ClassLoader loader, final String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        switch (className) {
            case "com/oracle/svm/reflect/hosted/ReflectionFeature":
            case "com/oracle/svm/core/jdk11/reflect/SubstrateReflectionAccessorFactoryJDK11":
            case "com/oracle/svm/core/jdk11/reflect/SubstrateMethodAccessorJDK11":
            case "com/oracle/svm/core/reflect/SubstrateMethodAccessor":
            case "com/oracle/svm/core/jdk11/reflect/SubstrateConstructorAccessorJDK11":
            case "com/oracle/svm/core/reflect/SubstrateConstructorAccessor": {
                ClassReader classReader = new ClassReader(classfileBuffer);
                ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
                /*classReader.accept(new ClinitExtender(classWriter, false) {
                    @Override
                    protected void emitClinitCode(MethodVisitor mv) {
                        mv.visitLdcInsn(Type.getObjectType(className));
                        mv.visitFieldInsn(Opcodes.PUTSTATIC, Type.getInternalName(SVMReflectionMethodTraceTransformer.class), "substrateMethodAccessorType", Type.getDescriptor(Class.class));
                        mv.visitLdcInsn(new Handle(Opcodes.H_GETFIELD, className, "executable", Type.getDescriptor(Executable.class), false));
                        mv.visitFieldInsn(Opcodes.PUTSTATIC, Type.getInternalName(SVMReflectionMethodTraceTransformer.class), "substrateMethodAccessorGetExecutable", Type.getDescriptor(MethodHandle.class));
                    }
                }, 0);*/
                classReader.accept(new ClassVisitor(Opcodes.ASM8, classWriter) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                        if ((name.equals("<init>") && (className.contains("SubstrateMethodAccessor") || className.contains("SubstrateConstructorAccessor"))) || name.equals("createMethodAccessor") || name.equals("createConstructorAccessor") || name.equals("createAccessor")) {
                            return new MethodVisitor(Opcodes.ASM8, mv) {
                                @Override
                                public void visitCode() {
                                    super.visitCode();
                                    super.visitVarInsn(Opcodes.ALOAD, 1);
                                    super.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(SVMReflectionMethodTraceTransformer.class), "validateReflectionMethod", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Executable.class)), false);
                                }
                            };
                        }
                        return mv;
                    }
                }, 0);
                return classWriter.toByteArray();
            }
            case "com/oracle/svm/core/meta/DirectSubstrateObjectConstant": {
                ClassReader classReader = new ClassReader(classfileBuffer);
                ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
                classReader.accept(new ClassVisitor(Opcodes.ASM8, classWriter) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                        var mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                        if (name.equals("<init>")) {
                            return new MethodVisitor(Opcodes.ASM8, mv) {
                                @Override
                                public void visitCode() {
                                    super.visitCode();
                                    super.visitVarInsn(Opcodes.ALOAD, 1);
                                    super.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(SVMReflectionMethodTraceTransformer.class), "validateReflectionMethodConstant", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Object.class)), false);
                                }
                            };
                        }
                        return mv;
                    }
                }, 0);
                return classWriter.toByteArray();
            }
            case "com/oracle/svm/reflect/hosted/ExecutableAccessorComputer": {
                ClassReader classReader = new ClassReader(classfileBuffer);
                ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
                classReader.accept(new ClassVisitor(Opcodes.ASM8, classWriter) {
                    private boolean visitedClinit = false;

                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                    /*if (name.equals("<clinit>")) {
                        visitedClinit = true;
                        return new MethodVisitor(Opcodes.ASM8, mv) {
                            @Override
                            public void visitInsn(int opcode) {
                                if (opcode == Opcodes.RETURN) {
                                    super.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(System.class), "getProperties", Type.getMethodDescriptor(Type.getType(Properties.class)), false);
                                    super.visitLdcInsn("de.kb1000.mcimage.SVMReflectionMethodTrace.traceReflectionMethods");
                                    super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Properties.class), "remove", Type.getMethodDescriptor(Type.getType(Object.class), Type.getType(Object.class)), false);
                                    super.visitFieldInsn(Opcodes.PUTSTATIC, className, "$mcimage$traceReflectionMethods", Type.getDescriptor(Set.class));
                                }
                                super.visitInsn(opcode);
                            }
                        };
                    }*/
                        if (name.equals("compute") && descriptor.equals("(Ljdk/vm/ci/meta/MetaAccessProvider;Ljdk/vm/ci/meta/ResolvedJavaField;Ljdk/vm/ci/meta/ResolvedJavaField;Ljava/lang/Object;)Ljava/lang/Object;")) {
                            return new MethodVisitor(Opcodes.ASM8, mv) {
                                @Override
                                public void visitTypeInsn(int opcode, String type) {
                                    super.visitTypeInsn(opcode, type);
                                    if (opcode == Opcodes.CHECKCAST && type.equals("java/lang/reflect/Executable")) {
                                        super.visitInsn(Opcodes.DUP);
                                        super.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(SVMReflectionMethodTraceTransformer.class), "validateReflectionMethod", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Executable.class)), false);

                                    }
                                }
                            };
                        }
                        return mv;
                    }

                /*@Override
                public void visitEnd() {
                    {
                        var fieldVisitor = super.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC, "$mcimage$traceReflectionMethods", "Ljava/util/Set;", "Ljava/util/Set<Ljava/lang/String;>;", null);
                        fieldVisitor.visitEnd();
                    }
                    {
                        var methodVisitor = super.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, "$mcimage$validateReflectionMethod", "(Ljava/lang/reflect/Executable;)V", null, null);
                        methodVisitor.visitCode();
                        Label label0 = new Label();
                        methodVisitor.visitLabel(label0);
                        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Executable", "getDeclaringClass", "()Ljava/lang/Class;", false);
                        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "jdk/internal/org/objectweb/asm/Type", "getDescriptor", "(Ljava/lang/Class;)Ljava/lang/String;", false);
                        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Executable", "getName", "()Ljava/lang/String;", false);
                        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                        methodVisitor.visitTypeInsn(Opcodes.INSTANCEOF, "java/lang/reflect/Method");
                        Label label1 = new Label();
                        methodVisitor.visitJumpInsn(Opcodes.IFEQ, label1);
                        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                        methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/reflect/Method");
                        methodVisitor.visitVarInsn(Opcodes.ASTORE, 2);
                        Label label2 = new Label();
                        methodVisitor.visitLabel(label2);
                        methodVisitor.visitVarInsn(Opcodes.ALOAD, 2);
                        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "jdk/internal/org/objectweb/asm/Type", "getMethodDescriptor", "(Ljava/lang/reflect/Method;)Ljava/lang/String;", false);
                        Label label3 = new Label();
                        methodVisitor.visitJumpInsn(Opcodes.GOTO, label3);
                        methodVisitor.visitLabel(label1);
                        methodVisitor.visitFrame(Opcodes.F_FULL, 1, new Object[] {"java/lang/reflect/Executable"}, 2, new Object[] {"java/lang/String", "java/lang/String"});
                        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                        methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/reflect/Constructor");
                        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "jdk/internal/org/objectweb/asm/Type", "getConstructorDescriptor", "(Ljava/lang/reflect/Constructor;)Ljava/lang/String;", false);
                        methodVisitor.visitLabel(label3);
                        methodVisitor.visitFrame(Opcodes.F_FULL, 1, new Object[] {"java/lang/reflect/Executable"}, 3, new Object[] {"java/lang/String", "java/lang/String", "java/lang/String"});
                        methodVisitor.visitInvokeDynamicInsn("makeConcatWithConstants", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false), new Object[]{"\u0001\u0001\u0001"});
                        methodVisitor.visitVarInsn(Opcodes.ASTORE, 1);
                        Label label4 = new Label();
                        methodVisitor.visitLabel(label4);
                        methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, className, "$mcimage$traceReflectionMethods", "Ljava/util/Set;");
                        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
                        methodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Set", "contains", "(Ljava/lang/Object;)Z", true);
                        Label label5 = new Label();
                        methodVisitor.visitJumpInsn(Opcodes.IFEQ, label5);
                        methodVisitor.visitTypeInsn(Opcodes.NEW, "java/lang/IllegalArgumentException");
                        methodVisitor.visitInsn(Opcodes.DUP);
                        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
                        methodVisitor.visitInvokeDynamicInsn("makeConcatWithConstants", "(Ljava/lang/String;)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false), new Object[]{"Method not allowed for reflection: \u0001"});
                        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
                        methodVisitor.visitInsn(Opcodes.ATHROW);
                        methodVisitor.visitLabel(label5);
                        methodVisitor.visitFrame(Opcodes.F_APPEND,1, new Object[] {"java/lang/String"}, 0, null);
                        methodVisitor.visitInsn(Opcodes.RETURN);
                        Label label7 = new Label();
                        methodVisitor.visitLabel(label7);
                        methodVisitor.visitLocalVariable("m", "Ljava/lang/reflect/Method;", null, label2, label1, 2);
                        methodVisitor.visitLocalVariable("reflectionMethod", "Ljava/lang/reflect/Executable;", null, label0, label7, 0);
                        methodVisitor.visitLocalVariable("s", "Ljava/lang/String;", null, label4, label7, 1);
                        methodVisitor.visitMaxs(-1, -1);
                        methodVisitor.visitEnd();
                    }
                    if (!visitedClinit){
                        var methodVisitor = super.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
                        methodVisitor.visitCode();
                        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(System.class), "getProperties", Type.getMethodDescriptor(Type.getType(Properties.class)), false);
                        methodVisitor.visitLdcInsn("de.kb1000.mcimage.SVMReflectionMethodTrace.traceReflectionMethods");
                        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Properties.class), "remove", Type.getMethodDescriptor(Type.getType(Object.class), Type.getType(Object.class)), false);
                        methodVisitor.visitFieldInsn(Opcodes.PUTSTATIC, className, "$mcimage$traceReflectionMethods", Type.getDescriptor(Set.class));
                        methodVisitor.visitInsn(Opcodes.RETURN);
                        methodVisitor.visitMaxs(-1, -1);
                        methodVisitor.visitEnd();
                    }
                    super.visitEnd();
                }*/
                }, 0);
                return classWriter.toByteArray();
            }
            default:
                return null;
        }
    }
}
