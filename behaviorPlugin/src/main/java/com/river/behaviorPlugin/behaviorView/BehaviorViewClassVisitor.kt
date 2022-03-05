package com.river.behaviorPlugin.behaviorView

import com.river.behaviorPlugin.BaseMethodVisitor
import com.river.behaviorPlugin.BaseMultipleClassVisitor
import com.river.behaviorPlugin.codeHelper.BehaviorHelper
import com.river.behaviorPlugin.entry.BehaviorViewData
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2021/11/9
 **/
class BehaviorViewClassVisitor(cv: ClassVisitor, lambdaList: MutableList<String>?) :
    BaseMultipleClassVisitor(cv, lambdaList) {

    override fun data() = behaviorViewDataList

    override fun insertEnterByteCode(mv: BaseMethodVisitor, data: BehaviorViewData) {
        val behaviorHelper = BehaviorHelper(mv)
            .buildEvent(BehaviorHelper.BuildStringLocalCode { data.event })
            .buildElementId {
                val elementId = mv.newLocal(Type.getType(String::class.java))
                mv.visitFieldInsn(
                    Opcodes.GETSTATIC,
                    "com/river/behavior/BehaviorHelper",
                    "INSTANCE",
                    "Lcom/river/behavior/BehaviorHelper;"
                )
                mv.visitVarInsn(Opcodes.ALOAD, mv.viewArgIndex)
                mv.visitLdcInsn(data.view)
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "com/river/behavior/BehaviorHelper",
                    "customRootViewIdName",
                    "(Landroid/view/View;Ljava/lang/String;)Ljava/lang/String;",
                    false
                )
                mv.visitVarInsn(Opcodes.ASTORE, elementId)
                elementId
            }

        if (data.contentViewId != 0) {
            behaviorHelper.buildElementContent {
                val elementContent = mv.newLocal(Type.getType(String::class.java))
                mv.visitFieldInsn(
                    Opcodes.GETSTATIC,
                    "com/river/behavior/BehaviorHelper",
                    "INSTANCE",
                    "Lcom/river/behavior/BehaviorHelper;"
                )
                mv.visitVarInsn(Opcodes.ALOAD, mv.viewArgIndex)
                mv.visitLdcInsn(data.view)
                mv.visitLdcInsn(data.contentViewId)
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "com/river/behavior/BehaviorHelper",
                    "customViewContent",
                    "(Landroid/view/View;Ljava/lang/String;I)Ljava/lang/String;",
                    false
                )
                mv.visitVarInsn(Opcodes.ASTORE, elementContent)
                elementContent
            }
        }

        behaviorHelper
            .attachDefault()
            .build()
    }

    companion object {
        val behaviorViewDataList = mutableListOf<BehaviorViewData>()
    }
}