package com.ismail.creatvt.moderator.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ismail.creatvt.moderator.R

class TopRoundedFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var animationPercentage: Float = 1f

    var cornerRadius:Float = 0f
        set(value){
            field = value
        }
    private var mPath = Path()
    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun setBottomSheetBehavior(bottomSheetBehavior: BottomSheetBehavior<out View>){
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
    }

    private var bottomSheetCallback = object:BottomSheetBehavior.BottomSheetCallback(){
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            animationPercentage = 1f - slideOffset
            invalidate()
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {

        }

    }
    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.TopRoundedFrameLayout)
        cornerRadius = a.getDimension(R.styleable.TopRoundedFrameLayout_trsv_cornerRadius, 0f)
        mPaint.color =
            a.getColor(R.styleable.TopRoundedFrameLayout_trsv_backgroundColor, 0xffffffff.toInt())
        mPaint.style = Paint.Style.FILL
        a.recycle()
        setWillNotDraw(false)
    }

    private fun createPath() {
        mPath.reset()
        val animatedCornerRadius = animationPercentage * cornerRadius
        mPath.moveTo(0f, height.toFloat())
        mPath.lineTo(0f, animatedCornerRadius)
        val cornerOvalSize = animatedCornerRadius * 2f
        mPath.arcTo(0f, 0f, cornerOvalSize, cornerOvalSize, 180f, 90f, false)
        mPath.lineTo((width - animatedCornerRadius), 0f)
        mPath.arcTo(width - cornerOvalSize, 0f, width.toFloat(), cornerOvalSize, 270f, 90f, false)
        mPath.lineTo(width.toFloat(), height.toFloat())
        mPath.lineTo(0f, height.toFloat())
        mPath.close()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        createPath()
        canvas?.drawPath(mPath, mPaint)
    }

}