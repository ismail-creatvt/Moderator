package com.ismail.creatvt.moderator

import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

class BottomSheetCornerDrawable(
    private val backgroundColor: Int
): Drawable() {
    private var mAlpha: Int = 1
    private var animationPercentage: Float = 0f
    var cornerRadius:Float = 0f
                set(value){
                    field = value
                }
    private var mPath = Path()
    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var bottomSheetBehavior: BottomSheetBehavior<out View> ?= null
                    set(value) {
                        field = value
                        value?.addBottomSheetCallback(bottomSheetBehaviorCallback)
                    }

    private var bottomSheetBehaviorCallback = object: BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            animationPercentage = 1f - slideOffset
            invalidateSelf()
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {

        }
    }

    init {
        mPaint.color = backgroundColor
        mPaint.style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas) {

        val colorFilter = mPaint.colorFilter
        if (backgroundColor.ushr(24) != 0 || colorFilter != null) {
            mPaint.color = backgroundColor
            createPath()
            canvas.drawPath(mPath, mPaint)
            // Restore original color filter.
            mPaint.colorFilter = colorFilter
        }
    }

    private fun createPath() {
        mPath.reset()
        val animatedCornerRadius = animationPercentage * cornerRadius
        mPath.moveTo(0f, intrinsicHeight.toFloat())
        mPath.lineTo(0f, animatedCornerRadius)
        val cornerOvalSize = animatedCornerRadius * 2f
        mPath.arcTo(0f, 0f, cornerOvalSize, cornerOvalSize, 180f, 90f, true)
        mPath.lineTo((intrinsicWidth - animatedCornerRadius), 0f)
        mPath.arcTo(intrinsicWidth - cornerOvalSize, 0f, intrinsicWidth.toFloat(), cornerOvalSize, 270f, 90f, true)
        mPath.lineTo(intrinsicWidth.toFloat(), intrinsicHeight.toFloat())
        mPath.lineTo(0f, intrinsicHeight.toFloat())
        mPath.close()
    }

    override fun setAlpha(alpha: Int) {
        this.mAlpha = alpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setColorFilter(p0: ColorFilter?) {
        mPaint.colorFilter = p0
    }
}