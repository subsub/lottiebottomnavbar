package com.subkhansarif.bottomnavbar

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by subkhansarif on 24/09/18
 **/

class UnswipeableViewPager: ViewPager {
    private var isSwipeEnabled: Boolean = false

    constructor(context: Context): super(context) {
        this.isSwipeEnabled = true
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.isSwipeEnabled = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (isSwipeEnabled) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (isSwipeEnabled) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    fun enableSwipe(enabled: Boolean) {
        isSwipeEnabled = enabled
    }
}