package com.river.behaviorPlugin.recycleView

import com.river.behaviorPlugin.BaseClassVisitor
import com.river.behaviorPlugin.BaseMethodVisitor
import com.river.behaviorPlugin.codeHelper.BehaviorHelper
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2021/11/9
 **/
class QuickRecycleViewChildClickClassVisitor(cv: ClassVisitor, lambdaList: MutableList<String>?) :
    BaseClassVisitor(cv, lambdaList) {
    private val adapterArgIndex: Int
        get() {
            val argumentTypes = Type.getArgumentTypes(descriptor)
            val index = argumentTypes.indexOfFirst { it.descriptor == "Lcom/chad/library/adapter/base/BaseQuickAdapter;" }
            return index + if (AdviceAdapter.ACC_STATIC and access == AdviceAdapter.ACC_STATIC) 0 else 1
        }

    private val positionArgIndex: Int
        get() {
            return adapterArgIndex + 2
        }

    override fun view() = "android/view/View"

    override fun listenerInterface() = "com/chad/library/adapter/base/listener/OnItemChildClickListener"

    override fun listenerFunction() = "onItemChildClick"

    override fun listenerFunctionDescriptor() = "(Lcom/chad/library/adapter/base/BaseQuickAdapter;Landroid/view/View;I)V"

    override fun insertEnterByteCode(mv: BaseMethodVisitor) {
        BehaviorHelper(mv)
            .buildEvent(BehaviorHelper.BuildStringLocalCode { "rv_child_click" })
            .buildData {
                val data = mv.newLocal(Type.getType(String::class.java))

                val obj = mv.newLocal(Type.getType("Ljava/lang/Object;"))
                mv.visitVarInsn(Opcodes.ALOAD, adapterArgIndex)
                mv.visitVarInsn(Opcodes.ILOAD, positionArgIndex)
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "com/chad/library/adapter/base/BaseQuickAdapter",
                    "getItem",
                    "(I)Ljava/lang/Object;",
                    false
                )
                mv.visitVarInsn(Opcodes.ASTORE, obj)

                val gson = mv.newLocal(Type.getType("Lcom/google/gson/Gson;"))
                mv.visitTypeInsn(Opcodes.NEW, "com/google/gson/Gson")
                mv.visitInsn(Opcodes.DUP)
                mv.visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    "com/google/gson/Gson",
                    "<init>",
                    "()V",
                    false
                )
                mv.visitVarInsn(Opcodes.ASTORE, gson)

                mv.visitVarInsn(Opcodes.ALOAD, gson)
                mv.visitVarInsn(Opcodes.ALOAD, obj)
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "com/google/gson/Gson",
                    "toJson",
                    "(Ljava/lang/Object;)Ljava/lang/String;",
                    false
                )
                mv.visitVarInsn(Opcodes.ASTORE, data)

                data
            }
            .attachDefault()
            .build()
    }
}