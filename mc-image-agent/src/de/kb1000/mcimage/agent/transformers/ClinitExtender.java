/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.agent.transformers;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

public abstract class ClinitExtender extends ClassVisitor {
    private final boolean atEnd;
    private boolean visitedClinit = false;

    public ClinitExtender(ClassVisitor classVisitor, boolean atEnd) {
        super(Opcodes.ASM8, classVisitor);
        this.atEnd = atEnd;
    }

    protected abstract void emitClinitCode(MethodVisitor mv);

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (name.equals("<clinit>")) {
            visitedClinit = true;
            return new MethodVisitor(Opcodes.ASM8, mv) {
                @Override
                public void visitCode() {
                    super.visitCode();
                    if (!atEnd) {
                        emitClinitCode(this);
                    }
                }

                @Override
                public void visitInsn(int opcode) {
                    if (atEnd && opcode == Opcodes.RETURN) {
                        emitClinitCode(this);
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        if (!visitedClinit) {
            MethodVisitor mv = visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
            mv.visitCode();
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(-1, -1);
            mv.visitEnd();
        }
        super.visitEnd();
    }
}
