package com.river.behaviorPlugin

import com.river.behaviorPlugin.entry.BehaviorViewData
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2021/11/9
 **/
open class BaseMultipleClassVisitor(cv: ClassVisitor, lambdaList: MutableList<String>?) :
    BaseClassVisitor(cv, lambdaList) {

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor? {
        this.access = access
        this.name = name
        this.descriptor = descriptor

        var mv = super.visitMethod(access, name, descriptor, signature, exceptions)

        if (ignore || mv == null) return mv

        for (item in data()) {
            mv = object : BaseMethodVisitor(this, mv, access, name, descriptor) {
                override fun view() = item.view

                override fun listenerInterface() = item.interfaceClz

                override fun listenerFunction() = item.function

                override fun listenerFunctionDescriptor() = item.functionDesc

                override fun insertEnterByteCode() {
                    this@BaseMultipleClassVisitor.insertEnterByteCode(this, item)
                }
            }
        }

        return mv
    }

    open fun insertEnterByteCode(mv: BaseMethodVisitor, data: BehaviorViewData) {}

    open fun data(): List<BehaviorViewData> = listOf()
}