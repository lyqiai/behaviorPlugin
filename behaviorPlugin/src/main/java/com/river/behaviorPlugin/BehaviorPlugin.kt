package com.river.behaviorPlugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 插件入口
 */
class BehaviorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val behaviorExt = project.extensions.create<BehaviorExtension>("behaviorExt", BehaviorExtension::class.java)
        project.afterEvaluate {
            behaviorExt.excludeActivity = behaviorExt.excludeActivity.map { it.replace(".", "/") }.toTypedArray()
            BehaviorConfig.behaviorParams = behaviorExt
        }

        val appExtension = project.extensions.findByType(AppExtension::class.java)
        appExtension?.registerTransform(BehaviorTransform())
    }
}