package com.river.behaviorPlugin.behaviorView

import com.river.behaviorPlugin.entry.BehaviorViewData
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes.ASM6

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2021/11/9
 **/
class BehaviorViewCollectClassVisitor(cv: ClassVisitor?): ClassVisitor(ASM6, cv) {
    val data = BehaviorViewData("", "", "", "", "")

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        data.view = name
    }


    override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
        var av = super.visitAnnotation(descriptor, visible)

        if (av != null && descriptor == "Lcom/river/behavior/BehaviorView;") {
            av = object : AnnotationVisitor(ASM6, av) {
                override fun visit(name: String?, value: Any?) {
                    super.visit(name, value)
                    if (name == "event") {
                        data.event = value as String
                    } else if (name == "function") {
                        data.function = value as String
                    } else if (name == "functionDesc") {
                        data.functionDesc = value as String
                    } else if (name == "interfaceClz") {
                        data.interfaceClz = value as String
                    } else if (name == "contentViewId") {
                        data.contentViewId = value as Int
                    }
                }

                override fun visitEnd() {
                    super.visitEnd()
                    BehaviorViewClassVisitor.behaviorViewDataList.add(data)
                }
            }
        }

        return av
    }
}