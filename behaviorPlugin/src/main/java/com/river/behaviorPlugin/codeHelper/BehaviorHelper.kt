package com.river.behaviorPlugin.codeHelper

import com.river.behaviorPlugin.BaseMethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2021/11/9
 * @Desc: 生成behavior对象帮助类
 **/
class BehaviorHelper(val mv: BaseMethodVisitor) {
    var event: Int? = null
    var data: Int? = null
    var elementId: Int? = null
    var elementType: Int? = null
    var elementContent: Int? = null
    var context: Int? = null
    var systemLanguage: Int? = null
    var systemVersion: Int? = null
    var systemModel: Int? = null
    var deviceBrand: Int? = null
    var time: Int? = null
    var traceId: Int? = null
    var parentTraceId: Int? = null

    fun buildEvent(builder: BuildStringLocalCode): BehaviorHelper {
        event = mv.newLocal(Type.getType("Ljava/lang/String;"))
        mv.visitLdcInsn(builder.buildCode())
        mv.visitVarInsn(Opcodes.ASTORE, event!!)
        return this
    }

    fun buildEvent(builder: BuildBehaviorCode): BehaviorHelper {
        event = builder.buildCode()
        return this
    }

    fun buildData(builder: BuildBehaviorCode): BehaviorHelper {
        data = builder.buildCode()
        return this
    }

    fun buildElementId(builder: BuildBehaviorCode): BehaviorHelper {
        elementId = builder.buildCode()
        return this
    }

    fun buildElementType(builder: BuildBehaviorCode): BehaviorHelper {
        elementType = builder.buildCode()
        return this
    }

    fun buildElementType(builder: BuildStringLocalCode): BehaviorHelper {
        elementType = mv.newLocal(Type.getType("Ljava/lang/String;"))
        mv.visitLdcInsn(builder.buildCode())
        mv.visitVarInsn(Opcodes.ASTORE, elementType!!)
        return this
    }

    fun buildElementContent(builder: BuildBehaviorCode): BehaviorHelper {
        elementContent = builder.buildCode()
        return this
    }

    fun buildContext(builder: BuildBehaviorCode): BehaviorHelper {
        context = builder.buildCode()
        return this
    }

    fun buildContext(builder: BuildStringLocalCode): BehaviorHelper {
        context = mv.newLocal(Type.getType("Ljava/lang/String;"))
        mv.visitLdcInsn(builder.buildCode())
        mv.visitVarInsn(Opcodes.ASTORE, context!!)
        return this
    }

    fun buildSystemLanguage(builder: BuildBehaviorCode): BehaviorHelper {
        systemLanguage = builder.buildCode()
        return this
    }

    fun buildSystemVersion(builder: BuildBehaviorCode): BehaviorHelper {
        systemVersion = builder.buildCode()
        return this
    }

    fun buildSystemModel(builder: BuildBehaviorCode): BehaviorHelper {
        systemModel = builder.buildCode()
        return this
    }

    fun buildDeviceBrand(builder: BuildBehaviorCode): BehaviorHelper {
        deviceBrand = builder.buildCode()
        return this
    }

    fun buildTime(builder: BuildBehaviorCode): BehaviorHelper {
        time = builder.buildCode()
        return this
    }

    fun buildTraceId(builder: BuildBehaviorCode): BehaviorHelper {
        traceId = builder.buildCode()
        return this
    }

    fun buildParentTraceId(builder: BuildBehaviorCode): BehaviorHelper {
        parentTraceId = builder.buildCode()
        return this
    }

    fun attachDefault(): BehaviorHelper {
        BehaviorDefaultAttach(this).attach()
        return this
    }

    /**
     * val elementId = elementId ?: ""
     * val elementType = elementType ?: ""
     * val elementContent = elementContent ?: ""
     * val systemLanguage = systemLanguage ?: BehaviorHelper.getSystemLanguage()
     * val systemVersion = systemLanguage ?: BehaviorHelper.getSystemVersion()
     * val systemModel = systemLanguage ?: BehaviorHelper.getSystemModel()
     * val deviceBrand = systemLanguage ?: BehaviorHelper.getDeviceBrand()
     * val time = systemLanguage ?: BehaviorHelper.getTime()
     * val parentTraceId = BehaviorHelper.findParentTraceNode(view).traceId
     *
     * val behavior = Behavior(...)
     * BehaviorManager.sendBehavior(behavior)
     */
    fun build() {
        data = data ?: buildEmptyLocal()
        context = context ?: buildEmptyLocal()
        elementId = elementId ?: buildEmptyLocal()
        elementType = elementType ?: buildEmptyLocal()
        elementContent = elementContent ?: buildEmptyLocal()

        if (systemLanguage == null) {
            systemLanguage = mv.newLocal(Type.getType("Ljava/lang/String;"))
            mv.visitFieldInsn(
                Opcodes.GETSTATIC,
                "com/river/behavior/BehaviorHelper",
                "INSTANCE",
                "Lcom/river/behavior/BehaviorHelper;"
            )
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "com/river/behavior/BehaviorHelper",
                "getSystemLanguage",
                "()Ljava/lang/String;",
                false
            )
            mv.visitVarInsn(Opcodes.ASTORE, systemLanguage!!)
        }

        if (systemVersion == null) {
            systemVersion = mv.newLocal(Type.getType("Ljava/lang/String;"))
            mv.visitFieldInsn(
                Opcodes.GETSTATIC,
                "com/river/behavior/BehaviorHelper",
                "INSTANCE",
                "Lcom/river/behavior/BehaviorHelper;"
            )
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "com/river/behavior/BehaviorHelper",
                "getSystemVersion",
                "()Ljava/lang/String;",
                false
            )
            mv.visitVarInsn(Opcodes.ASTORE, systemVersion!!)
        }

        if (systemModel == null) {
            systemModel = mv.newLocal(Type.getType("Ljava/lang/String;"))
            mv.visitFieldInsn(
                Opcodes.GETSTATIC,
                "com/river/behavior/BehaviorHelper",
                "INSTANCE",
                "Lcom/river/behavior/BehaviorHelper;"
            )
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "com/river/behavior/BehaviorHelper",
                "getSystemModel",
                "()Ljava/lang/String;",
                false
            )
            mv.visitVarInsn(Opcodes.ASTORE, systemModel!!)
        }

        if (deviceBrand == null) {
            deviceBrand = mv.newLocal(Type.getType("Ljava/lang/String;"))
            mv.visitFieldInsn(
                Opcodes.GETSTATIC,
                "com/river/behavior/BehaviorHelper",
                "INSTANCE",
                "Lcom/river/behavior/BehaviorHelper;"
            )
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "com/river/behavior/BehaviorHelper",
                "getDeviceBrand",
                "()Ljava/lang/String;",
                false
            )
            mv.visitVarInsn(Opcodes.ASTORE, deviceBrand!!)
        }

        if (time == null) {
            time = mv.newLocal(Type.getType("Ljava/lang/String;"))
            mv.visitFieldInsn(
                Opcodes.GETSTATIC,
                "com/river/behavior/BehaviorHelper",
                "INSTANCE",
                "Lcom/river/behavior/BehaviorHelper;"
            )
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "com/river/behavior/BehaviorHelper",
                "getTime",
                "()Ljava/lang/String;",
                false
            )
            mv.visitVarInsn(Opcodes.ASTORE, time!!)
        }

        if (traceId == null) {
            traceId = mv.traceId
        }

        if (parentTraceId == null) {
            parentTraceId = mv.newLocal(Type.getType("Ljava/lang/String;"))

            mv.visitFieldInsn(
                Opcodes.GETSTATIC,
                "com/river/behavior/BehaviorHelper",
                "INSTANCE",
                "Lcom/river/behavior/BehaviorHelper;"
            )
            mv.visitVarInsn(Opcodes.ALOAD, mv.viewArgIndex)
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "com/river/behavior/BehaviorHelper",
                "findParentTraceNode",
                "(Landroid/view/View;)Lcom/river/behavior/TraceNode;",
                false
            )
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "com/river/behavior/TraceNode",
                "getTraceId",
                "()Ljava/lang/String;",
                false
            )

            mv.visitVarInsn(Opcodes.ASTORE, parentTraceId!!)
        }

        val behavior = mv.newLocal(Type.getType("Lcom/river/behavior/Behavior;"))
        mv.visitTypeInsn(Opcodes.NEW, "com/river/behavior/Behavior")
        mv.visitInsn(Opcodes.DUP)
        mv.visitVarInsn(Opcodes.ALOAD, event!!)
        mv.visitVarInsn(Opcodes.ALOAD, data!!)
        mv.visitVarInsn(Opcodes.ALOAD, elementId!!)
        mv.visitVarInsn(Opcodes.ALOAD, elementType!!)
        mv.visitVarInsn(Opcodes.ALOAD, elementContent!!)
        mv.visitVarInsn(Opcodes.ALOAD, context!!)
        mv.visitVarInsn(Opcodes.ALOAD, systemLanguage!!)
        mv.visitVarInsn(Opcodes.ALOAD, systemVersion!!)
        mv.visitVarInsn(Opcodes.ALOAD, systemModel!!)
        mv.visitVarInsn(Opcodes.ALOAD, deviceBrand!!)
        mv.visitVarInsn(Opcodes.ALOAD, time!!)
        mv.visitVarInsn(Opcodes.ALOAD, traceId!!)
        mv.visitVarInsn(Opcodes.ALOAD, parentTraceId!!)

        mv.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            "com/river/behavior/Behavior",
            "<init>",
            "(${"Ljava/lang/String;".repeat(13)})V",
            false
        )
        mv.visitVarInsn(Opcodes.ASTORE, behavior)

        mv.visitFieldInsn(
            Opcodes.GETSTATIC,
            "com/river/behavior/BehaviorManager",
            "INSTANCE",
            "Lcom/river/behavior/BehaviorManager;"
        )
        mv.visitVarInsn(Opcodes.ALOAD, behavior)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "com/river/behavior/BehaviorManager",
            "sendBehavior",
            "(Lcom/river/behavior/Behavior;)V",
            false
        )
    }

    fun buildEmptyLocal(): Int {
        val local = mv.newLocal(Type.getType("Ljava/lang/String;"))
        mv.visitLdcInsn("")
        mv.visitVarInsn(Opcodes.ASTORE, local)
        return local
    }

    fun interface BuildBehaviorCode {
        fun buildCode(): Int
    }

    fun interface BuildStringLocalCode {
        fun buildCode(): String
    }


}