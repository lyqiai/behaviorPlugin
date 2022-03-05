package com.river.behaviorPlugin.entry

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2021/11/9
 **/
data class BehaviorViewData(
    //事件名称
    var event: String,
    //view类
    var view: String,
    //事件方法名
    var function: String,
    //事件方法签名
    var functionDesc: String,
    //接口
    var interfaceClz: String,
    //内容ViewId
    var contentViewId: Int = 0
)