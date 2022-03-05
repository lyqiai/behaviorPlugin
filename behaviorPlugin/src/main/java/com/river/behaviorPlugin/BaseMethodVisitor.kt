package com.river.behaviorPlugin

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
import java.util.ArrayList

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2021/11/9
 **/
abstract class BaseMethodVisitor(
    val cv: BaseClassVisitor,
    mv: MethodVisitor?,
    access: Int,
    name: String,
    desc: String
) : AdviceAdapter(ASM6, mv, access, name, desc) {
    //对应view()返回的类信息所在方法输入参数下标
    val viewArgIndex: Int by lazy {
        val argumentTypes = Type.getArgumentTypes(desc)
        val index = argumentTypes.indexOfFirst { it.descriptor == "L${view()};" || it.descriptor == "Landroid/view/View;" }
        index + if (ACC_STATIC and access == ACC_STATIC) 0 else 1
    }

    //链路ID
    var traceId: Int? = null

    override fun onMethodEnter() {
        if (allowInsertByteCode()) {
            insertTraceIdByteCode()
            insertEnterByteCode()
        }
    }

    /**
     * 是否插入代码
     * @return Boolean
     */
    private fun allowInsertByteCode(): Boolean = isImplListener() || isLambda()

    /**
     * 是否实现接口
     * @return Boolean
     */
    private fun isImplListener(): Boolean {
        val verifyMethodName = name == listenerFunction()
        val verifyMethodDesc = methodDesc == listenerFunctionDescriptor()
        val verifyImplListener = cv.interfaces?.any { it == listenerInterface() } ?: false

        return verifyMethodName && verifyMethodDesc && verifyImplListener
    }

    /**
     * 是否是lambda
     * @return Boolean
     */
    private fun isLambda(): Boolean {
        return cv.lambdaList?.contains(name) ?: false
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

    open fun insertEnterByteCode() {}

    /**
     * 方法进入插入链路ID，并将ActiveTraceNode.traceId设置
     * val traceId = TraceId.get()
     * ActiveTraceNode.traceId = traceId
     */
    private fun insertTraceIdByteCode() {
        traceId = newLocal(Type.getType(String::class.java))
        visitFieldInsn(
            Opcodes.GETSTATIC,
            "com/river/behavior/TraceId",
            "INSTANCE",
            "Lcom/river/behavior/TraceId;"
        )
        visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "com/river/behavior/TraceId",
            "get",
            "()Ljava/lang/String;",
            false
        )
        visitVarInsn(Opcodes.ASTORE, traceId!!)

        visitVarInsn(Opcodes.ALOAD, traceId!!)
        visitFieldInsn(
            Opcodes.PUTSTATIC,
            "com/river/behavior/ActiveTraceNode",
            "traceId",
            "Ljava/lang/String;"
        )
    }


     fun peekValue(prevIndex: Int) {
        val field = AdviceAdapter::class.java.getDeclaredField("stackFrame")
        field.isAccessible = true
        val stackFrame = field.get(this) as ArrayList<Any>?
         println(stackFrame)
        //stackFrame?.add(stackFrame[stackFrame.size - 1 - prevIndex])
    }
}