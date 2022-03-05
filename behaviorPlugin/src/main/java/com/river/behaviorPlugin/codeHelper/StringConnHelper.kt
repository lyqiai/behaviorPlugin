package com.river.behaviorPlugin.codeHelper

import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2021/11/9
 * @Desc: 拼接String
 **/
class StringConnHelper(private val mv: AdviceAdapter) {
    init {
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder")
        mv.visitInsn(Opcodes.DUP)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false)
    }

    fun connect(string: String): StringConnHelper {
        mv.visitLdcInsn(string)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "append",
            "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
            false
        )
        return this
    }

    fun connect(type: Type, variable: Int): StringConnHelper {
        var opcode = when (type.sort) {
            Type.INT -> Opcodes.ILOAD
            Type.SHORT -> Opcodes.ILOAD
            Type.LONG -> Opcodes.LLOAD
            Type.BOOLEAN -> Opcodes.ILOAD
            Type.BYTE -> Opcodes.ILOAD
            Type.CHAR -> Opcodes.ILOAD
            Type.FLOAT -> Opcodes.FLOAD
            Type.DOUBLE -> Opcodes.DLOAD
            else -> Opcodes.ALOAD
        }
        mv.visitVarInsn(opcode, variable)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "append",
            "(${type.descriptor})Ljava/lang/StringBuilder;",
            false
        )
        return this
    }

    fun build(): Int {
        val string = mv.newLocal(Type.getType("Ljava/lang/String;"))
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "toString",
            "()Ljava/lang/String;",
            false
        )
        mv.visitVarInsn(Opcodes.ASTORE, string)
        return string
    }
}