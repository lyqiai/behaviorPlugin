package com.river.behaviorPlugin.popupWindow

import com.river.behaviorPlugin.BaseClassVisitor
import com.river.behaviorPlugin.BaseMethodVisitor
import com.river.behaviorPlugin.codeHelper.BehaviorHelper
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2021/11/9
 **/
class PopupWindowClassVisitor(cv: ClassVisitor, lambdaList: MutableList<String>?) :
    BaseClassVisitor(cv, lambdaList) {

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor? {
        var mv = super.visitMethod(access, name, descriptor, signature, exceptions)

        if (mv != null) {
            mv = object : BaseMethodVisitor(this, mv, access, name, descriptor) {
                override fun onMethodEnter() {}

                override fun visitMethodInsn(
                    opcode: Int,
                    owner: String,
                    name: String,
                    descriptor: String,
                    isInterface: Boolean
                ) {
                    val showMethod = listOf("showAtLocation", "showAsDropDown")
                    if (opcode == Opcodes.INVOKEVIRTUAL &&
                        owner == "android/widget/PopupWindow" &&
                        showMethod.contains(name)
                    ) {
                        createTempOwner(this, owner, descriptor) {
                            insertOnCreate(this, insertRootTraceNode(this, it))
                        }
                    }

                    if (opcode == Opcodes.INVOKEVIRTUAL && owner == "android/widget/PopupWindow" && name == "dismiss") {
                        createTempOwner(this, owner, descriptor) {
                            insertOnDestroy(this, it)
                        }
                    }
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
                }
            }
        }

        return mv
    }

    private fun insertRootTraceNode(mv: BaseMethodVisitor, popupWindow: Int): Int {
        val parentTraceId = mv.newLocal(Type.getType(String::class.java))
        mv.visitFieldInsn(
            Opcodes.GETSTATIC,
            "com/river/behavior/ActiveTraceNode",
            "traceId",
            "Ljava/lang/String;"
        )
        mv.visitVarInsn(Opcodes.ASTORE, parentTraceId)

        val traceId = mv.newLocal(Type.getType(String::class.java))
        mv.visitFieldInsn(
            Opcodes.GETSTATIC,
            "com/river/behavior/TraceId",
            "INSTANCE",
            "Lcom/river/behavior/TraceId;"
        )
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "com/river/behavior/TraceId",
            "get",
            "()Ljava/lang/String;",
            false
        )
        mv.visitVarInsn(Opcodes.ASTORE, traceId)

        val traceNode = mv.newLocal(Type.getType("Lcom/river/behavior/TraceNode;"))
        mv.visitTypeInsn(Opcodes.NEW, "com/river/behavior/TraceNode")
        mv.visitInsn(Opcodes.DUP)
        mv.visitVarInsn(Opcodes.ALOAD, traceId)
        mv.visitVarInsn(Opcodes.ALOAD, parentTraceId)
        mv.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            "com/river/behavior/TraceNode",
            "<init>",
            "(Ljava/lang/String;Ljava/lang/String;)V",
            false
        )
        mv.visitVarInsn(Opcodes.ASTORE, traceNode)

        mv.visitVarInsn(Opcodes.ALOAD, popupWindow)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "android/widget/PopupWindow",
            "getContentView",
            "()Landroid/view/View;",
            false
        )
        mv.visitFieldInsn(Opcodes.GETSTATIC, "com/river/behavior/R\$id", "asm_trace_node", "I")
        mv.visitVarInsn(Opcodes.ALOAD, traceNode)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "android/view/View",
            "setTag",
            "(ILjava/lang/Object;)V",
            false
        )

        return traceNode
    }

    private fun insertOnCreate(mv: BaseMethodVisitor, traceNode: Int) {
        BehaviorHelper(mv)
            .buildEvent(BehaviorHelper.BuildStringLocalCode { "popup_create" })
            .buildTraceId {
                val traceId = mv.newLocal(Type.getType(String::class.java))
                mv.visitVarInsn(Opcodes.ALOAD, traceNode)
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "com/river/behavior/TraceNode",
                    "getTraceId",
                    "()Ljava/lang/String;",
                    false
                )
                mv.visitVarInsn(Opcodes.ASTORE, traceId)
                traceId
            }
            .buildParentTraceId {
                val parentTraceId = mv.newLocal(Type.getType(String::class.java))
                mv.visitVarInsn(Opcodes.ALOAD, traceNode)
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "com/river/behavior/TraceNode",
                    "getParentTraceId",
                    "()Ljava/lang/String;",
                    false
                )
                mv.visitVarInsn(Opcodes.ASTORE, parentTraceId)
                parentTraceId
            }
            .build()
    }

    private fun insertOnDestroy(mv: BaseMethodVisitor, popupWindow: Int) {
        BehaviorHelper(mv)
            .buildEvent(BehaviorHelper.BuildStringLocalCode { "popup_destroy" })
            .buildTraceId {
                val traceId = mv.newLocal(Type.getType(String::class.java))
                mv.visitFieldInsn(
                    Opcodes.GETSTATIC,
                    "com/river/behavior/TraceId",
                    "INSTANCE",
                    "Lcom/river/behavior/TraceId;"
                )
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "com/river/behavior/TraceId",
                    "get",
                    "()Ljava/lang/String;",
                    false
                )
                mv.visitVarInsn(Opcodes.ASTORE, traceId)
                traceId
            }
            .buildParentTraceId {
                val parentTraceId = mv.newLocal(Type.getType(String::class.java))
                mv.visitVarInsn(Opcodes.ALOAD, popupWindow)
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "android/widget/PopupWindow",
                    "getContentView",
                    "()Landroid/view/View;",
                    false
                )
                mv.visitFieldInsn(
                    Opcodes.GETSTATIC,
                    "com/river/behavior/R\$id",
                    "asm_trace_node",
                    "I"
                )
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "android/view/View",
                    "getTag",
                    "(I)Ljava/lang/Object;",
                    false
                )
                mv.visitTypeInsn(Opcodes.CHECKCAST, "com/river/behavior/TraceNode")
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "com/river/behavior/TraceNode",
                    "getTraceId",
                    "()Ljava/lang/String;",
                    false
                )
                mv.visitVarInsn(Opcodes.ASTORE, parentTraceId)
                parentTraceId
            }
            .build()
    }

    private fun createTempOwner(
        mv: BaseMethodVisitor,
        owner: String,
        descriptor: String,
        callback: (Int) -> Unit
    ) {
        val types = Type.getArgumentTypes(descriptor)
        val tempArgs = IntArray(types.size)
        for (i in types.size - 1 downTo 0) {
            val type = types[i]
            val tempArg: Int = mv.newLocal(type)
            mv.visitVarInsn(type.getOpcode(Opcodes.ISTORE), tempArg)
            tempArgs[i] = tempArg
        }

        mv.visitInsn(Opcodes.DUP)
        val obj: Int = mv.newLocal(Type.getType("L$owner;"))
        mv.visitVarInsn(Opcodes.ASTORE, obj)
        callback.invoke(obj)

        for (tempArg in tempArgs) {
            val index = tempArgs.indexOf(tempArg)
            val type = types[index]
            mv.visitVarInsn(type.getOpcode(Opcodes.ILOAD), tempArg)
        }
    }
}