package com.example.myapplication

import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup

/**
 * @description:
 * @author Wandervogel
 * @date :2024/4/2
 * @version 1.0.0
 */
fun View.setVisible(isVisible: Boolean){
    TransitionManager.beginDelayedTransition(this.rootView as? ViewGroup)
    visibility = if(isVisible) View.VISIBLE else View.GONE
}