package com.river.behaviorPlugin.checkbox

import com.river.behaviorPlugin.BaseClassVisitor
import com.river.behaviorPlugin.BaseMethodVisitor
import com.river.behaviorPlugin.codeHelper.BehaviorHelper
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2021/11/9
 **/
class CheckboxClassVisitor(cv: ClassVisitor, lambdaList: MutableList<String>?) :
    BaseClassVisitor(cv, lambdaList) {
    private val checkedArgIndex: Int
        get() {
            val argumentTypes = Type.getArgumentTypes(descriptor)
            val index = argumentTypes.indexOfFirst { it.descriptor == "Z" }
            return index + if (Opcodes.ACC_STATIC and access == Opcodes.ACC_STATIC) 0 else 1
        }

    override fun view() = "android/widget/CompoundButton"

    override fun listenerInterface() = "${view()}\$OnCheckedChangeListener"

    override fun listenerFunction() = "onCheckedChanged"

    override fun listenerFunctionDescriptor() = "(Landroid/widget/CompoundButton;Z)V"

    override fun insertEnterByteCode(mv: BaseMethodVisitor) {
        BehaviorHelper(mv)
            .buildEvent(BehaviorHelper.BuildStringLocalCode { "check" })
            .buildData {
                val data = mv.newLocal(Type.getType("Ljava/lang/String;"))
                mv.visitVarInsn(Opcodes.ILOAD, checkedArgIndex)
                mv.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "java/lang/String",
                    "valueOf",
                    "(Z)Ljava/lang/String;",
                    false
                )
                mv.visitVarInsn(Opcodes.ASTORE, data)
                data
            }
            .attachDefault()
            .build()
    }
}