package com.river.behaviorPlugin.fragment

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
class FragmentLifeClassVisitor(cv: ClassVisitor, lambdaList: MutableList<String>?) : AbsLifeClassVisitor(cv, lambdaList) {
    override fun needReduce() = clzName == "androidx/fragment/app/Fragment"

    override fun createEvent() = "fragment_create"

    override fun destroyEvent() = "fragment_destroy"

    override fun onCreate() = "onViewCreated"

    override fun onCreateDesc() = "(Landroid/view/View;Landroid/os/Bundle;)V"

    override fun onResume() = "onResume"

    override fun onResumeDesc() = "()V"

    override fun onStop() = "onStop"

    override fun onStopDesc() = "()V"

    override fun onDestroy() = "onDestroyView"

    override fun onDestroyDesc() = "()V"

    override fun insertOnCreateByteCode(mv: BaseMethodVisitor) {
        BehaviorHelper(mv)
            .buildData {
                val tag = mv.newLocal(Type.getType(String::class.java))
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitFieldInsn(Opcodes.GETFIELD, clzName, "mTag", "Ljava/lang/String;")
                mv.visitVarInsn(Opcodes.ASTORE, tag)

                StringConnHelper(mv)
                    .connect("""{"tag": """)
                    .connect(Type.getType(String::class.java), tag)
                    .connect("}")
                    .build()
            }
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

    override fun insertOnDestroyByteCode(mv: BaseMethodVisitor) {
        BehaviorHelper(mv)
            .buildEvent(BehaviorHelper.BuildStringLocalCode { destroyEvent() })
            .buildData {
                val totalStay = mv.newLocal(Type.LONG_TYPE)
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitFieldInsn(Opcodes.GETFIELD, clzName, ASM_BEHAVIOR_TOTAL_RESUMED_TIME, "J")
                mv.visitVarInsn(Opcodes.LSTORE, totalStay)

                val tag = mv.newLocal(Type.getType(String::class.java))
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitFieldInsn(Opcodes.GETFIELD, clzName, "mTag", "Ljava/lang/String;")
                mv.visitVarInsn(Opcodes.ASTORE, tag)

                StringConnHelper(mv)
                    .connect("""{"totalStay":""")
                    .connect(Type.LONG_TYPE, totalStay)
                    .connect(""","tag":""")
                    .connect(Type.getType(String::class.java), tag)
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

    override fun getRooTView(mv: BaseMethodVisitor) {
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, clzName, "getView", "()Landroid/view/View;", false)
    }
}