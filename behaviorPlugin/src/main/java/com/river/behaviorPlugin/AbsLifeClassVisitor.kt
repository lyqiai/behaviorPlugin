package com.river.behaviorPlugin

import com.river.behaviorPlugin.codeHelper.BehaviorHelper
import com.river.behaviorPlugin.codeHelper.StringConnHelper
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2021/11/9
 * @Desc:
 * onCreate：page_create事件
 * onResume: 曝光时间重置
 * onStop： 计算曝光时间
 * onDestroy： page_destroy事件
 **/
abstract class AbsLifeClassVisitor(cv: ClassVisitor, lambdaList: MutableList<String>?) :
    BaseClassVisitor(cv, lambdaList) {
    abstract fun createEvent(): String

    abstract fun destroyEvent(): String

    abstract fun onCreate(): String

    abstract fun onCreateDesc(): String

    abstract fun onResume(): String

    abstract fun onResumeDesc(): String

    abstract fun onStop(): String

    abstract fun onStopDesc(): String

    abstract fun onDestroy(): String

    abstract fun onDestroyDesc(): String

    abstract fun needReduce(): Boolean

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
            override fun onMethodEnter() {
                if (needReduce()) {
                    insertEnterByteCode(this)
                }
            }
        }
    }

    override fun insertEnterByteCode(mv: BaseMethodVisitor) {
        if (name == onCreate() && descriptor == onCreateDesc()) {
            insertTraceNodeForRootView(mv)
            insertOnCreateByteCode(mv)
        }
        if (name == onResume() && descriptor == onResumeDesc()) {
            insertOnResumeByteCode(mv)
        }
        if (name == onStop() && descriptor == onStopDesc()) {
            insertOnStopByteCode(mv)
        }
        if (name == onDestroy() && descriptor == onDestroyDesc()) {
            insertOnDestroyByteCode(mv)
        }
    }

    /**
     * rootView设置tag(com.river.trace.R.id.asm_trace_node, TraceNode)
     *
     * val parentTraceId = ActiveTraceNode.traceId
     * val traceId = TraceId.get()
     * val traceNode = TraceNode(traceId, parentTraceId)
     * window.decorView.rootView.setTag(com.river.trace.R.id.asm_trace_node, traceNode)
     */
    fun insertTraceNodeForRootView(mv: BaseMethodVisitor) {
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

        getRooTView(mv)
        mv.visitFieldInsn(Opcodes.GETSTATIC, "com/river/behavior/R\$id", "asm_trace_node", "I")
        mv.visitVarInsn(Opcodes.ALOAD, traceNode)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "android/view/View",
            "setTag",
            "(ILjava/lang/Object;)V",
            false
        )
    }

    /**
     * buildContext:
     * this.getClass().getCanonicalName()
     *
     */
    open fun insertOnCreateByteCode(mv: BaseMethodVisitor) {
        BehaviorHelper(mv)
            .buildEvent(BehaviorHelper.BuildStringLocalCode { createEvent() })
            .buildContext(BehaviorHelper.BuildBehaviorCode {
                val context = mv.newLocal(Type.getType(String::class.java))
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/Object",
                    "getClass",
                    "()Ljava/lang/Class;",
                    false
                )
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/Class",
                    "getCanonicalName",
                    "()Ljava/lang/String;",
                    false
                )
                mv.visitVarInsn(Opcodes.ASTORE, context)
                context
            })
            .buildTraceId {
                getTraceId(mv)
            }
            .buildParentTraceId {
                getParentTraceId(mv)
            }
            .build()
    }

    /**
     * ASM_BEHAVIOR_RESUMED_TIME = System.currentTimeMillis()
     */
    fun insertOnResumeByteCode(mv: BaseMethodVisitor) {
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "java/lang/System",
            "currentTimeMillis",
            "()J",
            false
        )
        mv.visitFieldInsn(
            Opcodes.PUTFIELD,
            clzName,
            AbsLifeClassVisitor.Companion.ASM_BEHAVIOR_RESUMED_TIME,
            "J"
        )
    }

    /**
     * ASM_BEHAVIOR_TOTAL_RESUMED_TIME += System.currentTimeMillis() - ASM_BEHAVIOR_RESUMED_TIME
     */
    fun insertOnStopByteCode(mv: BaseMethodVisitor) {
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitFieldInsn(
            Opcodes.GETFIELD,
            clzName,
            AbsLifeClassVisitor.Companion.ASM_BEHAVIOR_TOTAL_RESUMED_TIME,
            "J"
        )
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "java/lang/System",
            "currentTimeMillis",
            "()J",
            false
        )
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitFieldInsn(
            Opcodes.GETFIELD,
            clzName,
            AbsLifeClassVisitor.Companion.ASM_BEHAVIOR_RESUMED_TIME,
            "J"
        )
        mv.visitInsn(Opcodes.LSUB)
        mv.visitInsn(Opcodes.LADD)
        mv.visitFieldInsn(
            Opcodes.PUTFIELD,
            clzName,
            AbsLifeClassVisitor.Companion.ASM_BEHAVIOR_TOTAL_RESUMED_TIME,
            "J"
        )

    }

    /**
     * buildData:
     * data = {totalStay: ASM_BEHAVIOR_TOTAL_RESUMED_TIME}
     */
    open fun insertOnDestroyByteCode(mv: BaseMethodVisitor) {
        BehaviorHelper(mv)
            .buildEvent(BehaviorHelper.BuildStringLocalCode { destroyEvent() })
            .buildData {
                val totalStay = mv.newLocal(Type.LONG_TYPE)
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitFieldInsn(
                    Opcodes.GETFIELD,
                    clzName,
                    AbsLifeClassVisitor.Companion.ASM_BEHAVIOR_TOTAL_RESUMED_TIME,
                    "J"
                )
                mv.visitVarInsn(Opcodes.LSTORE, totalStay)

                StringConnHelper(mv)
                    .connect("""{"totalStay":""")
                    .connect(Type.LONG_TYPE, totalStay)
                    .connect("}")
                    .build()
            }
            .buildContext(BehaviorHelper.BuildBehaviorCode {
                val context = mv.newLocal(Type.getType(String::class.java))
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/Object",
                    "getClass",
                    "()Ljava/lang/Class;",
                    false
                )
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/Class",
                    "getCanonicalName",
                    "()Ljava/lang/String;",
                    false
                )
                mv.visitVarInsn(Opcodes.ASTORE, context)
                context
            })
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
                mv.visitVarInsn(Opcodes.ALOAD, traceId)
                mv.visitFieldInsn(
                    Opcodes.PUTSTATIC,
                    "com/river/behavior/ActiveTraceNode",
                    "traceId",
                    "Ljava/lang/String;"
                )
                traceId
            }
            .buildParentTraceId {
                getTraceId(mv)
            }
            .build()
    }

    /**
     * val traceNode = getRootView().getTag(com.river.trace.R.id.asm_trace_node) as TraceNode
     */
    fun getTraceNode(mv: BaseMethodVisitor): Int {
        val traceNode = mv.newLocal(Type.getType("Lcom/river/behavior/TraceNode;"))
        val rootView = mv.newLocal(Type.getType("Landroid/view/View;"))
        getRooTView(mv)
        mv.visitVarInsn(Opcodes.ASTORE, rootView)

        val notInitLabel = mv.newLabel()
        val storeTraceNode = mv.newLabel()

        mv.visitVarInsn(Opcodes.ALOAD, rootView)
        mv.visitJumpInsn(Opcodes.IFNULL, notInitLabel)

        mv.visitVarInsn(Opcodes.ALOAD, rootView)
        mv.visitFieldInsn(Opcodes.GETSTATIC, "com/river/behavior/R\$id", "asm_trace_node", "I")
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "android/view/View",
            "getTag",
            "(I)Ljava/lang/Object;",
            false
        )
        mv.visitTypeInsn(Opcodes.CHECKCAST, "com/river/behavior/TraceNode")
        mv.visitJumpInsn(Opcodes.GOTO, storeTraceNode)

        mv.visitLabel(notInitLabel)
        mv.visitTypeInsn(Opcodes.NEW, "com/river/behavior/TraceNode")
        mv.visitInsn(Opcodes.DUP)
        mv.visitLdcInsn("")
        mv.visitInsn(Opcodes.ACONST_NULL)
        mv.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            "com/river/behavior/TraceNode",
            "<init>",
            "(Ljava/lang/String;Ljava/lang/String;)V",
            false
        )

        mv.visitLabel(storeTraceNode)
        mv.visitVarInsn(Opcodes.ASTORE, traceNode)
        return traceNode
    }

    /**
     * val traceId = traceNode.getTraceId()
     */
    fun getTraceId(mv: BaseMethodVisitor): Int {
        val traceNode = getTraceNode(mv)
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
        return traceId
    }

    /**
     * val parentTraceId = traceNode.getParentTraceId()
     */
    fun getParentTraceId(mv: BaseMethodVisitor): Int {
        val traceNode = getTraceNode(mv)
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
        return parentTraceId
    }

    open fun getRooTView(mv: BaseMethodVisitor) {
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            clzName,
            "getWindow",
            "()Landroid/view/Window;",
            false
        )
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "android/view/Window",
            "getDecorView",
            "()Landroid/view/View;",
            false
        )
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "android/view/View",
            "getRootView",
            "()Landroid/view/View;",
            false
        )
    }

    /**
     * ASM_BEHAVIOR_TOTAL_RESUMED_TIME: Long
     * ASM_BEHAVIOR_RESUMED_TIME: Long
     */
    override fun visitEnd() {
        if (needReduce()) {
            cv.visitField(
                Opcodes.ACC_PRIVATE,
                AbsLifeClassVisitor.Companion.ASM_BEHAVIOR_TOTAL_RESUMED_TIME,
                "J",
                null,
                null
            )?.visitEnd()

            cv.visitField(
                Opcodes.ACC_PRIVATE,
                AbsLifeClassVisitor.Companion.ASM_BEHAVIOR_RESUMED_TIME,
                "J",
                null,
                null
            )?.visitEnd()
        }
        super.visitEnd()
    }

    companion object {
        //页面曝光时长
        const val ASM_BEHAVIOR_TOTAL_RESUMED_TIME = "ASM_BEHAVIOR_TOTAL_RESUMED_TIME"

        //页面开始曝光时间点
        const val ASM_BEHAVIOR_RESUMED_TIME = "ASM_BEHAVIOR_RESUMED_TIME"
    }
}