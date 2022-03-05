package com.river.behaviorPlugin.chip

import com.river.behaviorPlugin.BaseClassVisitor
import com.river.behaviorPlugin.BaseMethodVisitor
import com.river.behaviorPlugin.codeHelper.BehaviorHelper
import com.river.behaviorPlugin.codeHelper.StringConnHelper
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2021/11/9
 **/
open class ChipClassVisitor(cv: ClassVisitor, lambdaList: MutableList<String>?) :
    BaseClassVisitor(cv, lambdaList) {
    private val checkIdArgIndex: Int
        get() {
            val argumentTypes = Type.getArgumentTypes(descriptor)
            val index = argumentTypes.indexOfFirst { it.descriptor == "I" }
            return index + if (AdviceAdapter.ACC_STATIC and access == AdviceAdapter.ACC_STATIC) 0 else 1
        }

    override fun view(): String {
        return "com/google/android/material/chip/ChipGroup"
    }

    override fun listenerInterface(): String {
        return "${view()}\$OnCheckedChangeListener"
    }

    override fun listenerFunction(): String {
        return "onCheckedChanged"
    }

    override fun listenerFunctionDescriptor(): String {
        return "(Lcom/google/android/material/chip/ChipGroup;I)V"
    }

    open fun eventName(): String = "chip_check"

    /**
     * buildData:
     * if (checkId != -1) {
     *  val chip = view.findViewById(checkId) as TextView
     *  val text = chip.text
     *  val data = map{text}
     * }
     */
    override fun insertEnterByteCode(mv: BaseMethodVisitor) {
        val label = mv.newLabel()
        mv.visitVarInsn(Opcodes.ILOAD, checkIdArgIndex)
        mv.visitInsn(Opcodes.ICONST_M1)
        mv.visitJumpInsn(Opcodes.IF_ICMPEQ, label)

        BehaviorHelper(mv)
            .buildEvent(BehaviorHelper.BuildStringLocalCode { eventName() })
            .buildData {
                val elementText = mv.newLocal(Type.getType("Ljava/lang/CharSequence;"))
                mv.visitVarInsn(Opcodes.ALOAD, mv.viewArgIndex)
                mv.visitVarInsn(Opcodes.ILOAD, checkIdArgIndex)
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    view(),
                    "findViewById",
                    "(I)Landroid/view/View;",
                    false
                )
                mv.visitTypeInsn(Opcodes.CHECKCAST, "android/widget/TextView")
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "android/widget/TextView",
                    "getText",
                    "()Ljava/lang/CharSequence;",
                    false
                )
                mv.visitVarInsn(Opcodes.ASTORE, elementText)

                StringConnHelper(mv)
                    .connect("{\"text\": \"")
                    .connect(Type.getType("Ljava/lang/CharSequence;"), elementText)
                    .connect("\"}")
                    .build()
            }
            .attachDefault()
            .build()

        mv.visitLabel(label)
    }
}