package com.river.behaviorPlugin.dialog

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
class DialogClassVisitor(cv: ClassVisitor, lambdaList: MutableList<String>?): BaseClassVisitor(cv, lambdaList) {
    private val idArgIndex: Int get() {
        val argumentTypes = Type.getArgumentTypes(descriptor)
        val index = argumentTypes.indexOfFirst { it.descriptor == "I" }
        return index + if (AdviceAdapter.ACC_STATIC and access == AdviceAdapter.ACC_STATIC) 0 else 1
    }

    override fun view(): String = "android/content/DialogInterface"

    override fun listenerInterface(): String = "${view()}\$OnClickListener"

    override fun listenerFunction(): String = "onClick"

    override fun listenerFunctionDescriptor(): String = "(Landroid/content/DialogInterface;I)V"

    override fun insertEnterByteCode(mv: BaseMethodVisitor) {
        BehaviorHelper(mv)
            .buildEvent(BehaviorHelper.BuildStringLocalCode { "dialog_click" })
            .buildContext(BehaviorHelper.BuildBehaviorCode {
                val context = mv.newLocal(Type.getType(String::class.java))
                mv.visitVarInsn(Opcodes.ALOAD, mv.viewArgIndex)
                mv.visitTypeInsn(Opcodes.CHECKCAST, "androidx/appcompat/app/AlertDialog")
                mv.visitVarInsn(Opcodes.ILOAD, idArgIndex)
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "androidx/appcompat/app/AlertDialog",
                    "getButton",
                    "(I)Landroid/widget/Button;",
                    false
                )
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "android/widget/Button",
                    "getContext",
                    "()Landroid/content/Context;",
                    false
                )
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/Object",
                    "getClass",
                    "()Ljava/lang/Class;",
                    false
                )
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/Class",
                    "getCanonicalName",
                    "()Ljava/lang/String;",
                    false
                )
                mv.visitVarInsn(Opcodes.ASTORE, context)

                context
            })
            .buildElementContent {
                val content = mv.newLocal(Type.getType(String::class.java))
                mv.visitVarInsn(Opcodes.ALOAD, mv.viewArgIndex)
                mv.visitTypeInsn(Opcodes.CHECKCAST, "androidx/appcompat/app/AlertDialog")
                mv.visitVarInsn(Opcodes.ILOAD, idArgIndex)
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "androidx/appcompat/app/AlertDialog",
                    "getButton",
                    "(I)Landroid/widget/Button;",
                    false
                )
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "android/widget/Button",
                    "getText",
                    "()Ljava/lang/CharSequence;",
                    false
                )
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/Object",
                    "toString",
                    "()Ljava/lang/String;",
                    false
                )
                mv.visitVarInsn(Opcodes.ASTORE, content)
                content
            }
            .buildElementType(BehaviorHelper.BuildStringLocalCode { "android/widget/Button" })
            .buildParentTraceId {
                val parentTraceId = mv.newLocal(Type.getType(String::class.java))
                mv.visitVarInsn(Opcodes.ALOAD, mv.viewArgIndex)
                mv.visitTypeInsn(Opcodes.CHECKCAST, "android/app/Dialog")
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "android/app/Dialog",
                    "getWindow",
                    "()Landroid/view/Window;",
                    false
                )
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "android/view/Window",
                    "getDecorView",
                    "()Landroid/view/View;",
                    false
                )
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "android/view/View",
                    "getRootView",
                    "()Landroid/view/View;",
                    false
                )
                mv.visitFieldInsn(
                    Opcodes.GETSTATIC,
                    "com/river/behavior/R\$id",
                    "asm_trace_node",
                    "I"
                )
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "android/view/View",
                    "getTag",
                    "(I)Ljava/lang/Object;",
                    false
                )
                mv.visitTypeInsn(Opcodes.CHECKCAST, "com/river/behavior/TraceNode")
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "com/river/behavior/TraceNode",
                    "getTraceId",
                    "()Ljava/lang/String;",
                    false
                )
                mv.visitVarInsn(Opcodes.ASTORE, parentTraceId)
                parentTraceId
            }
            .build()
    }
}