package com.river.behaviorPlugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.river.behaviorPlugin.behaviorView.BehaviorViewCollectClassVisitor
import com.river.behaviorPlugin.click.ClickClassVisitor
import com.river.behaviorPlugin.lambda.LambdaClassVisitor
import com.river.behaviorPlugin.util.TransformUtil
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.util.CheckClassAdapter

/**
 * 插件transform基础类，实现增量更新，接收实现类的classVisitor进行注入处理
 */
abstract class BaseTransform : Transform() {
    /**
     * 返回需要处理classVisitor
     * @return Array<Class<out BaseClassVisitor>>
     */
    abstract fun classVisitor(): Array<Class<out BaseClassVisitor>>

    /**
     * 处理打包流程中文件及jar包
     * @param transformInvocation TransformInvocation
     */
    override fun transform(transformInvocation: TransformInvocation) {
        val incremental = transformInvocation.isIncremental
        val inputs = transformInvocation.inputs
        val outputProvider = transformInvocation.outputProvider

        if (!incremental) {
            outputProvider.deleteAll()
        }

        //此次遍历收集@BehaviorView信息
        for (input in inputs) {
            for (jarInput in input.jarInputs) {
                TransformUtil.handleJarInput(jarInput, outputProvider, incremental, object: TransformUtil.HandleByteCode {
                    override fun handle(bytes: ByteArray): ByteArray {
                        return collectBehaviorView(bytes)
                    }

                    override fun onlyMap() = true
                })
            }

            for (directoryInput in input.directoryInputs) {
                TransformUtil.handleDirInput(directoryInput, outputProvider, incremental, object: TransformUtil.HandleByteCode {
                    override fun handle(bytes: ByteArray): ByteArray {
                        return collectBehaviorView(bytes)
                    }

                    override fun onlyMap() = true
                })
            }
        }

        //此次遍历注入代码
        for (input in inputs) {
            for (jarInput in input.jarInputs) {
                TransformUtil.handleJarInput(jarInput, outputProvider, incremental, object: TransformUtil.HandleByteCode {
                    override fun handle(bytes: ByteArray): ByteArray {
                        return handleByteCode(bytes)
                    }
                })
            }

            for (directoryInput in input.directoryInputs) {
                TransformUtil.handleDirInput(directoryInput, outputProvider, incremental, object: TransformUtil.HandleByteCode {
                    override fun handle(bytes: ByteArray): ByteArray {
                        return handleByteCode(bytes)
                    }
                })
            }
        }
    }

    private fun collectBehaviorView(bytes: ByteArray): ByteArray {
        val classReader = ClassReader(bytes)
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        val checkClassAdapter = CheckClassAdapter(classWriter, false)
        val cv = BehaviorViewCollectClassVisitor(checkClassAdapter)
        classReader.accept(cv, 0)
        return bytes
    }

    /**
     * 处理字节码
     * @param bytes ByteArray
     * @return ByteArray
     */
    private fun handleByteCode(bytes: ByteArray): ByteArray {
        val classReader = ClassReader(bytes)
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        val checkClassAdapter = CheckClassAdapter(classWriter, false)

        val cvClzs = classVisitor()
        var temp: ClassVisitor = checkClassAdapter
        for (cvClz in cvClzs) {
            val tempCvConstructor = cvClz.getDeclaredConstructor(ClassVisitor::class.java, List::class.java)
            val tempCvLambda = tempCvConstructor.newInstance(CheckClassAdapter(null, false), null)
            val lambdas = mutableSetOf<String>()
            if (tempCvLambda is BaseMultipleClassVisitor) {
                for (item in tempCvLambda.data()) {
                    val lambdaCv = LambdaClassVisitor(
                        CheckClassAdapter(null, false),
                        item.function,
                        item.interfaceClz
                    )
                    classReader.accept(lambdaCv, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
                    lambdas.addAll(lambdaCv.lambdas)
                }
            } else {
                val lambdaCv = LambdaClassVisitor(
                    CheckClassAdapter(null, false),
                    tempCvLambda.listenerFunction(),
                    tempCvLambda.listenerInterface()
                )
                classReader.accept(lambdaCv, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
                lambdas.addAll(lambdaCv.lambdas)
            }

            val cvConstructor = cvClz.getDeclaredConstructor(ClassVisitor::class.java, List::class.java)
            val cv = cvConstructor.newInstance(temp, lambdas.toMutableList())
            temp = cv
        }

        classReader.accept(temp, ClassReader.EXPAND_FRAMES)

        return classWriter.toByteArray()
    }

    /**
     * 处理输入类型
     */
    override fun getInputTypes() = TransformManager.CONTENT_CLASS

    /**
     * 处理输入范围
     */
    override fun getScopes() = TransformManager.SCOPE_FULL_PROJECT

    /**
     * 支持增量更新
     * @return Boolean
     */
    override fun isIncremental() = true
}