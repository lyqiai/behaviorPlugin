package com.river.behaviorPlugin.click

import com.river.behaviorPlugin.BaseClassVisitor
import com.river.behaviorPlugin.BaseMethodVisitor
import com.river.behaviorPlugin.codeHelper.BehaviorHelper
import org.objectweb.asm.ClassVisitor


class ClickClassVisitor(cv: ClassVisitor, lambdaList: MutableList<String>?): BaseClassVisitor(cv, lambdaList) {

    override fun view() = "android/view/View"

    override fun listenerInterface() = "${view()}\$OnClickListener"

    override fun listenerFunction() = "onClick"

    override fun listenerFunctionDescriptor() = "(Landroid/view/View;)V"

    override fun insertEnterByteCode(mv: BaseMethodVisitor) {
        BehaviorHelper(mv)
            .buildEvent(BehaviorHelper.BuildStringLocalCode { "click" })
            .attachDefault()
            .build()
    }
}