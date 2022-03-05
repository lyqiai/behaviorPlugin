package com.river.behaviorPlugin.codeHelper

import com.river.behaviorPlugin.BaseMethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2021/11/9
 * @Desc: 默认补全BehaviorHelper:buildElementId,buildElementType,buildElementContent,buildContext
 **/
class BehaviorDefaultAttach(private val behaviorHelper: BehaviorHelper) {
    private val mv: BaseMethodVisitor = behaviorHelper.mv

    /**
     * BehaviorHelper.getViewId(id)
     */
    private fun buildElementId(): Int {
        val elementId = mv.newLocal(Type.getType("Ljava/lang/String;"))
        mv.visitFieldInsn(
            Opcodes.GETSTATIC,
            "com/river/behavior/BehaviorHelper",
            "INSTANCE",
            "Lcom/river/behavior/BehaviorHelper;"
        )
        mv.visitVarInsn(Opcodes.ALOAD, mv.viewArgIndex)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "com/river/behavior/BehaviorHelper",
            "getIdName",
            "(Landroid/view/View;)Ljava/lang/String;",
            false
        )
        mv.visitVarInsn(Opcodes.ASTORE, elementId)

        return elementId
    }

    /**
     * view.class.canonicalName
     */
    private fun buildElementType(): Int {
        val elementType = mv.newLocal(Type.getType("Ljava/lang/String;"))
        mv.visitVarInsn(Opcodes.ALOAD, mv.viewArgIndex)
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
        mv.visitVarInsn(Opcodes.ASTORE, elementType)

        return elementType
    }

    /**
     * BehaviorHelper.getViewText(view)
     */
    private fun buildElementContent(): Int {
        val content = mv.newLocal(Type.getType("Ljava/lang/String;"))
        mv.visitFieldInsn(
            Opcodes.GETSTATIC,
            "com/river/behavior/BehaviorHelper",
            "INSTANCE",
            "Lcom/river/behavior/BehaviorHelper;"
        )
        mv.visitVarInsn(Opcodes.ALOAD, mv.viewArgIndex)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "com/river/behavior/BehaviorHelper",
            "getViewText",
            "(Landroid/view/View;)Ljava/lang/String;",
            false
        )
        mv.visitVarInsn(Opcodes.ASTORE, content)

        return content
    }

    /**
     * view.context.class.canonicalName
     * @return Int
     */
    private fun buildContext(): Int {
        val context = mv.newLocal(Type.getType("Ljava/lang/String;"))
        mv.visitVarInsn(AdviceAdapter.ALOAD, mv.viewArgIndex)
        mv.visitMethodInsn(
            AdviceAdapter.INVOKEVIRTUAL,
            "android/view/View",
            "getContext",
            "()Landroid/content/Context;",
            false
        )
        mv.visitMethodInsn(
            AdviceAdapter.INVOKEVIRTUAL,
            "java/lang/Object",
            "getClass",
            "()Ljava/lang/Class;",
            false
        )
        mv.visitMethodInsn(
            AdviceAdapter.INVOKEVIRTUAL,
            "java/lang/Class",
            "getCanonicalName",
            "()Ljava/lang/String;",
            false
        )
        mv.visitVarInsn(AdviceAdapter.ASTORE, context)

        return context
    }

    fun attach() {
        behaviorHelper.elementId ?: behaviorHelper.buildElementId(this::buildElementId)
        behaviorHelper.elementType ?: behaviorHelper.buildElementType(this::buildElementType)
        behaviorHelper.elementContent ?: behaviorHelper.buildElementContent(this::buildElementContent)
        behaviorHelper.context ?: behaviorHelper.buildContext(this::buildContext)
    }
}