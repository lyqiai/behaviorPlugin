package com.river.behaviorPlugin.seekBar

import com.river.behaviorPlugin.BaseClassVisitor
import com.river.behaviorPlugin.BaseMethodVisitor
import com.river.behaviorPlugin.codeHelper.BehaviorHelper
import com.river.behaviorPlugin.codeHelper.StringConnHelper
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2021/11/9
 **/
class SeekBarClassVisitor(cv: ClassVisitor, lambdaList: MutableList<String>?) : BaseClassVisitor(cv, lambdaList) {
    override fun view(): String = "android/widget/SeekBar"

    override fun listenerInterface(): String = "${view()}\$OnSeekBarChangeListener"

    override fun listenerFunction(): String = "onStopTrackingTouch"

    override fun listenerFunctionDescriptor(): String = "(Landroid/widget/SeekBar;)V"

    /**
     * buildData:
     * val max = seekBar.max
     * val min = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) seekBar.min else 0
     * val progress = seekBar.progress
     * val data = map{max, min, progress}
     */
    override fun insertEnterByteCode(mv: BaseMethodVisitor) {
        BehaviorHelper(mv)
            .buildEvent(BehaviorHelper.BuildStringLocalCode { "seek_bar_progress" })
            .buildData {
                val max = mv.newLocal(Type.INT_TYPE)
                mv.visitVarInsn(Opcodes.ALOAD, mv.viewArgIndex)
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "android/widget/SeekBar",
                    "getMax",
                    "()I",
                    false
                )
                mv.visitVarInsn(Opcodes.ISTORE, max)

                val min = mv.newLocal(Type.INT_TYPE)
                mv.visitFieldInsn(Opcodes.GETSTATIC, "android/os/Build\$VERSION", "SDK_INT", "I")
                mv.visitIntInsn(Opcodes.BIPUSH, 26)
                val defaultMinLabel = Label()
                mv.visitJumpInsn(Opcodes.IF_ICMPLT, defaultMinLabel)
                mv.visitVarInsn(Opcodes.ALOAD, mv.viewArgIndex)
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "android/widget/SeekBar",
                    "getMax",
                    "()I",
                    false
                )
                mv.visitVarInsn(Opcodes.ISTORE, min)
                val minEndLabel = Label()
                mv.visitJumpInsn(Opcodes.GOTO, minEndLabel)
                mv.visitLabel(defaultMinLabel)
                mv.visitLdcInsn(0)
                mv.visitVarInsn(Opcodes.ISTORE, min)
                mv.visitLabel(minEndLabel)

                val progress = mv.newLocal(Type.INT_TYPE)
                mv.visitVarInsn(Opcodes.ALOAD, mv.viewArgIndex)
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "android/widget/SeekBar",
                    "getProgress",
                    "()I",
                    false
                )
                mv.visitVarInsn(Opcodes.ISTORE, progress)

                StringConnHelper(mv)
                    .connect("""{"max":""")
                    .connect(Type.INT_TYPE, max)
                    .connect(""", "min": """)
                    .connect(Type.INT_TYPE, min)
                    .connect(""", "progress": """)
                    .connect(Type.INT_TYPE, progress)
                    .connect("}")
                    .build()
            }
            .attachDefault()
            .build()
    }
}