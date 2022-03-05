package com.river.behaviorPlugin.lambda

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.ASM6

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2021/11/9
 **/
class LambdaClassVisitor(
    cv: ClassVisitor,
    val listenerFunction: String?,
    val listenerInterface: String?
) : ClassVisitor(ASM6, cv) {
    val lambdas = mutableListOf<String>()
    lateinit var clzName: String

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        clzName = name
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (mv != null) {
            return LambdaMethodVisitor(
                listenerFunction,
                listenerInterface,
                this,
                mv,
                access,
                name,
                descriptor
            )
        }
        return mv
    }
}