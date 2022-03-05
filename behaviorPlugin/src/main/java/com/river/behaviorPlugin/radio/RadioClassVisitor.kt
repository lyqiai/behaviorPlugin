package com.river.behaviorPlugin.radio

import com.river.behaviorPlugin.chip.ChipClassVisitor
import org.objectweb.asm.ClassVisitor

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2021/11/9
 **/
class RadioClassVisitor(cv: ClassVisitor, lambdaList: MutableList<String>?): ChipClassVisitor(cv, lambdaList) {
    override fun view(): String {
        return "android/widget/RadioGroup"
    }

    override fun listenerInterface() = "${view()}\$OnCheckedChangeListener"

    override fun listenerFunctionDescriptor(): String {
        return "(Landroid/widget/RadioGroup;I)V"
    }

    override fun eventName(): String = "radio_check"
}