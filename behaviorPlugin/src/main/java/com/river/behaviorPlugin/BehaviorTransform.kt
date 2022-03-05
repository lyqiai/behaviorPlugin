package com.river.behaviorPlugin


import com.river.behaviorPlugin.appCompatDelegate.AppCompatDelegateClassVisitor
import com.river.behaviorPlugin.behaviorView.BehaviorViewClassVisitor
import com.river.behaviorPlugin.checkbox.CheckboxClassVisitor
import com.river.behaviorPlugin.chip.ChipClassVisitor
import com.river.behaviorPlugin.click.ClickClassVisitor
import com.river.behaviorPlugin.dialog.DialogClassVisitor
import com.river.behaviorPlugin.fragment.FragmentLifeClassVisitor
import com.river.behaviorPlugin.popupWindow.PopupWindowClassVisitor
import com.river.behaviorPlugin.radio.RadioClassVisitor
import com.river.behaviorPlugin.ratingBar.RatingBarClassVisitor
import com.river.behaviorPlugin.recycleView.QuickRecycleViewChildClickClassVisitor
import com.river.behaviorPlugin.recycleView.QuickRecycleViewItemClickClassVisitor
import com.river.behaviorPlugin.seekBar.SeekBarClassVisitor

/**
 * 负责注册classVisitor
 */
class BehaviorTransform : BaseTransform() {
    override fun classVisitor() = arrayOf<Class<out BaseClassVisitor>>(
        PopupWindowClassVisitor::class.java,
        QuickRecycleViewChildClickClassVisitor::class.java,
        QuickRecycleViewItemClickClassVisitor::class.java,
        BehaviorViewClassVisitor::class.java,
        DialogClassVisitor::class.java,
        RatingBarClassVisitor::class.java,
        SeekBarClassVisitor::class.java,
        RadioClassVisitor::class.java,
        ChipClassVisitor::class.java,
        CheckboxClassVisitor::class.java,
        ClickClassVisitor::class.java,
        FragmentLifeClassVisitor::class.java,
        AppCompatDelegateClassVisitor::class.java
    )

    override fun getName() = NAME

    companion object {
        const val NAME = "BehaviorTransform"
    }
}