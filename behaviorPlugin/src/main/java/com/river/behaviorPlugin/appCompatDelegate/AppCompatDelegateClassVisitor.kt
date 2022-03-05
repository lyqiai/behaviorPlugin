package com.river.behaviorPlugin.appCompatDelegate

import com.river.behaviorPlugin.AbsLifeClassVisitor
import com.river.behaviorPlugin.BaseMethodVisitor
import com.river.behaviorPlugin.codeHelper.BehaviorHelper
import com.river.behaviorPlugin.codeHelper.StringConnHelper
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2021/11/9
 **/
class AppCompatDelegateClassVisitor(cv: ClassVisitor, lambdaList: MutableList<String>?) : AbsLifeClassVisitor(cv, lambdaList) {
    override fun createEvent(): String = "create"

    override fun destroyEvent() = "destroy"

    override fun onCreate() = "onCreate"

    override fun onCreateDesc() = "(Landroid/os/Bundle;)V"

    override fun onResume() = "onResume"

    override fun onResumeDesc() = "()V"

    override fun onStop() = "onStop"

    override fun onStopDesc() = "()V"

    override fun onDestroy() = "onDestroy"

    override fun onDestroyDesc() = "()V"

    override fun needReduce() = clzName == "androidx/appcompat/app/AppCompatDelegateImpl"

    override fun insertEnterByteCode(mv: BaseMethodVisitor) {
        if (name == onCreate() && descriptor == onCreateDesc()) {
            val label = mv.newLabel()
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, clzName, "ensureWindow", "()V", false)
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitFieldInsn(Opcodes.GETFIELD, clzName, "mCreated", "Z")
            mv.visitJumpInsn(Opcodes.IFNE, label)
            insertTraceNodeForRootView(mv)
            insertOnCreateByteCode(mv)
            mv.visitLabel(label)
        }
        if (name == onResume() && descriptor == onResumeDesc()) {
            insertOnResumeByteCode(mv)
        }
        if (name == onStop() && descriptor == onStopDesc()) {
            insertOnStopByteCode(mv)
        }
        if (name == onDestroy() && descriptor == onDestroyDesc()) {
            val label = mv.newLabel()
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitFieldInsn(Opcodes.GETFIELD, clzName, "mIsDestroyed", "Z")
            mv.visitJumpInsn(Opcodes.IFNE, label)
            insertOnDestroyByteCode(mv)
            mv.visitLabel(label)
        }
    }

    private fun createEvent(mv: BaseMethodVisitor): Int {
        val event = mv.newLocal(Type.getType(String::class.java))
        val store = mv.newLabel()

        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitFieldInsn(Opcodes.GETFIELD, clzName, "mHost", "Ljava/lang/Object;")
        mv.visitTypeInsn(Opcodes.INSTANCEOF, "android/app/Activity")
        val dialog = mv.newLabel()
        mv.visitJumpInsn(Opcodes.IFEQ, dialog)
        mv.visitLdcInsn("page_create")
        mv.visitJumpInsn(Opcodes.GOTO, store)

        mv.visitLabel(dialog)
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitFieldInsn(Opcodes.GETFIELD, clzName, "mHost", "Ljava/lang/Object;")
        mv.visitTypeInsn(Opcodes.INSTANCEOF, "android/app/Dialog")
        val unKnow = mv.newLabel()
        mv.visitJumpInsn(Opcodes.IFEQ, unKnow)
        mv.visitLdcInsn("dialog_create")
        mv.visitJumpInsn(Opcodes.GOTO, store)

        mv.visitLabel(unKnow)
        mv.visitLdcInsn("unKnow_create")
        mv.visitJumpInsn(Opcodes.GOTO, store)


        mv.visitLabel(store)
        mv.visitVarInsn(Opcodes.ASTORE, event)

        return event
    }

    private fun destroyEvent(mv: BaseMethodVisitor): Int {
        val event = mv.newLocal(Type.getType(String::class.java))
        val store = mv.newLabel()

        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitFieldInsn(Opcodes.GETFIELD, clzName, "mHost", "Ljava/lang/Object;")
        mv.visitTypeInsn(Opcodes.INSTANCEOF, "androidx/appcompat/app/AppCompatActivity")
        val dialog = mv.newLabel()
        mv.visitJumpInsn(Opcodes.IFEQ, dialog)
        mv.visitLdcInsn("page_destroy")
        mv.visitJumpInsn(Opcodes.GOTO, store)

        mv.visitLabel(dialog)
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitFieldInsn(Opcodes.GETFIELD, clzName, "mHost", "Ljava/lang/Object;")
        mv.visitTypeInsn(Opcodes.INSTANCEOF, "android/app/Dialog")
        val unKnow = mv.newLabel()
        mv.visitJumpInsn(Opcodes.IFEQ, unKnow)
        mv.visitLdcInsn("dialog_destroy")
        mv.visitJumpInsn(Opcodes.GOTO, store)

        mv.visitLabel(unKnow)
        mv.visitLdcInsn("unKnow_destroy")
        mv.visitJumpInsn(Opcodes.GOTO, store)


        mv.visitLabel(store)
        mv.visitVarInsn(Opcodes.ASTORE, event)

        return event
    }

    /**
     * buildContext:
     * this.getClass().getCanonicalName()
     *
     */
    override fun insertOnCreateByteCode(mv: BaseMethodVisitor) {
        BehaviorHelper(mv)
            .buildEvent(BehaviorHelper.BuildBehaviorCode { createEvent(mv) })
            .buildContext(BehaviorHelper.BuildBehaviorCode {
                val context = mv.newLocal(Type.getType(String::class.java))
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitFieldInsn(
                    Opcodes.GETFIELD,
                    clzName,
                    "mContext",
                    "Landroid/content/Context;"
                )
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
     * buildData:
     * data = {totalStay: ASM_BEHAVIOR_TOTAL_RESUMED_TIME}
     */
    override fun insertOnDestroyByteCode(mv: BaseMethodVisitor) {
        BehaviorHelper(mv)
            .buildEvent(BehaviorHelper.BuildBehaviorCode { destroyEvent(mv) })
            .buildData {
                val totalStay = mv.newLocal(Type.LONG_TYPE)
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitFieldInsn(
                    Opcodes.GETFIELD,
                    clzName,
                    AbsLifeClassVisitor.ASM_BEHAVIOR_TOTAL_RESUMED_TIME,
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
                mv.visitFieldInsn(
                    Opcodes.GETFIELD,
                    clzName,
                    "mContext",
                    "Landroid/content/Context;"
                )
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

    override fun getRooTView(mv: BaseMethodVisitor) {
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitFieldInsn(Opcodes.GETFIELD, clzName, "mWindow", "Landroid/view/Window;")
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
}