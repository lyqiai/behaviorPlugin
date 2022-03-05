package com.river.behaviorPlugin.lambda

import org.objectweb.asm.Handle
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2021/11/9
 **/
class LambdaMethodVisitor(
    val listenerFunction: String?,
    val listenerInterface: String?,
    val cv: LambdaClassVisitor,
    mv: MethodVisitor,
    access: Int,
    name: String,
    desc: String
) : AdviceAdapter(ASM6, mv, access, name, desc) {
    override fun visitInvokeDynamicInsn(
        name: String,
        descriptor: String,
        bootstrapMethodHandle: Handle?,
        vararg bootstrapMethodArguments: Any?
    ) {
        val verifyMethodName = name == listenerFunction
        val verifyMethodDesc = descriptor.endsWith("L$listenerInterface;")
        if (verifyMethodName && verifyMethodDesc) {
            for (bootstrapMethodArgument in bootstrapMethodArguments) {
                if (bootstrapMethodArgument is Handle) {
                    cv.lambdas.add(bootstrapMethodArgument.name)
                }
            }
        }
        super.visitInvokeDynamicInsn(
            name,
            descriptor,
            bootstrapMethodHandle,
            *bootstrapMethodArguments
        )
    }

}