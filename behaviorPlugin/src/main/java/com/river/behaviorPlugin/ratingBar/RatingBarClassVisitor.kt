package com.river.behaviorPlugin.ratingBar

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
class RatingBarClassVisitor(cv: ClassVisitor, lambdaList: MutableList<String>?): BaseClassVisitor(cv, lambdaList) {
    private val ratingArgIndex: Int get() {
        val argumentTypes = Type.getArgumentTypes(descriptor)
        val index = argumentTypes.indexOfFirst { it.descriptor == "F" }
        return index + if (AdviceAdapter.ACC_STATIC and access == AdviceAdapter.ACC_STATIC) 0 else 1
    }

    override fun view(): String = "android/widget/RatingBar"

    override fun listenerInterface(): String = "${view()}\$OnRatingBarChangeListener"

    override fun listenerFunction(): String = "onRatingChanged"

    override fun listenerFunctionDescriptor(): String = "(Landroid/widget/RatingBar;FZ)V"

    /**
     * buildData:
     * val numStars = ratingBar.numStars
     * val stepSize = ratingBar.stepSize
     * val data = map{numStars, stepSize, rating}
     */
    override fun insertEnterByteCode(mv: BaseMethodVisitor) {
        BehaviorHelper(mv)
            .buildEvent(BehaviorHelper.BuildStringLocalCode { "rating_bar_change" })
            .buildData {
                val numStars = mv.newLocal(Type.INT_TYPE)
                mv.visitVarInsn(Opcodes.ALOAD, mv.viewArgIndex)
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "android/widget/RatingBar",
                    "getNumStars",
                    "()I",
                    false
                )
                mv.visitVarInsn(Opcodes.ISTORE, numStars)

                val stepSize = mv.newLocal(Type.FLOAT_TYPE)
                mv.visitVarInsn(Opcodes.ALOAD, mv.viewArgIndex)
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "android/widget/RatingBar",
                    "getStepSize",
                    "()F",
                    false
                )
                mv.visitVarInsn(Opcodes.FSTORE, stepSize)

                StringConnHelper(mv)
                    .connect("""{"numStars": """)
                    .connect(Type.INT_TYPE, numStars)
                    .connect(""", "stepSize": """)
                    .connect(Type.FLOAT_TYPE, stepSize)
                    .connect(""", "rating": """)
                    .connect(Type.FLOAT_TYPE, ratingArgIndex)
                    .connect("}")
                    .build()
            }
            .attachDefault()
            .build()
    }
}