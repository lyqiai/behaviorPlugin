package com.river.behaviorPlugin

import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.ASM6

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2021/11/9
 * @Desc:
 * 1： 提供类全限定名字段
 * 2： 提供父类全限定名字段
 * 3： 提供对@BehaviorIgnore注解支持
 * 4:  维护lambda列表对应的静态方法
 **/
abstract class BaseClassVisitor(cv: ClassVisitor, lambdaList: MutableList<String>?) : ClassVisitor(ASM6, cv) {
    //类全限定名
    var clzName: String = ""

    //父类全限定名
    var superClzName: String? = null

    //实现接口
    var interfaces: Array<out String>? = null

    //标记是否被@BehaviorIgnore标注
    var ignore: Boolean = false

    var lambdaList = lambdaList

    var access = 0
    var name = ""
    var descriptor = ""

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
        superClzName = superName
        this.interfaces = interfaces
    }

    override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
        if (descriptor == "Lcom/river/behavior/BehaviorIgnore;" || descriptor == "Lcom/river/behavior/BehaviorView;") {
            ignore = true
        }
        return super.visitAnnotation(descriptor, visible)
    }

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

        return object : BaseMethodVisitor(this, mv, access, name, descriptor) {
            override fun view() = this@BaseClassVisitor.view()

            override fun listenerInterface() = this@BaseClassVisitor.listenerInterface()

            override fun listenerFunction() = this@BaseClassVisitor.listenerFunction()

            override fun listenerFunctionDescriptor() = this@BaseClassVisitor.listenerFunctionDescriptor()

            override fun insertEnterByteCode() {
                this@BaseClassVisitor.insertEnterByteCode(this)
            }
        }
    }


    /**
     * view类
     * @return String?
     */
    open fun view(): String? = null

    /**
     * 接口
     * @return String?
     */
    open fun listenerInterface(): String? = null

    /**
     * 接口方法
     * @return String?
     */
    open fun listenerFunction(): String? = null

    /**
     * 方法签名
     * @return String?
     */
    open fun listenerFunctionDescriptor(): String? = null

    open fun insertEnterByteCode(mv: BaseMethodVisitor) {}
}