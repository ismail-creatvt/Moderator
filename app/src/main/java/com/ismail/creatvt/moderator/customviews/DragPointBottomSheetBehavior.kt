package com.ismail.creatvt.moderator.customviews

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class DragPointBottomSheetBehavior(context: Context, attrs: AttributeSet?) :
    BottomSheetBehavior<View>(context, attrs) {

    var draggableView:View?=null
                set(value) {
                    field = value
                }

    private var dragRect = Rect()

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: View,
        event: MotionEvent
    ): Boolean {
        if(draggableView == null){
            return super.onInterceptTouchEvent(parent, child, event)
        }
        draggableView!!.getHitRect(dragRect)
        if(dragRect.contains(event.rawX.toInt(), event.rawY.toInt())){
            return super.onInterceptTouchEvent(parent, child, event)
        }
        return false
    }


}