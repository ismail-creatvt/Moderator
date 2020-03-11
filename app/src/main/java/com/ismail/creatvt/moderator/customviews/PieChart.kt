package com.ismail.creatvt.moderator.customviews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.ismail.creatvt.moderator.customviews.data.PieData
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class PieChart @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), ValueAnimator.AnimatorUpdateListener {
    private var animationPercentage: Float = 0f

    private var data = listOf<PieData>()
    private var piePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private var piePath = Path()
    private var outerRect = RectF()
    private var innerRect = RectF()
    private var innerClipPath = Path()
    private var drawingRect = RectF()
    fun setData(data:List<PieData>){
        this.data = data
        animatePie()
    }

    private fun animatePie() {
        ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener(this@PieChart)
            duration = 500
            startDelay = 500
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        animatePie()
    }
    override fun onAnimationUpdate(animation: ValueAnimator?) {
        animationPercentage = animation?.animatedFraction?:0f
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        calculateBounds()
        calculateRects()

        var total = 0
        for(item in data){
            total += item.value
        }

        canvas?.save()

        innerClipPath.reset()
        innerClipPath.addCircle(width/2f, height/2f, innerRect.width()/2f, Path.Direction.CW)
        if(Build.VERSION.SDK_INT >= 26){
            canvas?.clipOutPath(innerClipPath)
        } else{
            canvas?.clipPath(innerClipPath)
        }
        var totalAngle = 0f

        data.forEach { item ->
            piePaint.color = item.color
            piePath.reset()
            val startAngle = totalAngle
            val xOuter = outerRect.centerX() + outerRect.width()/2 * sin(startAngle)
            val yOuter = outerRect.centerY() + outerRect.width()/2 * cos(startAngle)
            piePath.moveTo(outerRect.centerX(), outerRect.centerY())
            piePath.lineTo(xOuter, yOuter)
            val angle = ((item.value.toFloat()/total.toFloat()) * 360f) * animationPercentage
            piePath.arcTo(outerRect, startAngle, angle, true)
            totalAngle += angle
            piePath.lineTo(outerRect.centerX(), outerRect.centerY())
            piePath.close()
            canvas?.drawPath(piePath, piePaint)
        }

        canvas?.restore()
    }

    private fun calculateRects() {
        val partition = drawingRect.width()/3
        innerRect.left = drawingRect.centerX() - partition/2f
        innerRect.right = drawingRect.centerX() + partition/2f
        innerRect.top = drawingRect.centerY() - partition/2f
        innerRect.bottom = drawingRect.centerY() + partition/2f

        outerRect.left = innerRect.left - partition/2f
        outerRect.right = innerRect.right + partition/2f
        outerRect.top = innerRect.top - partition/2f
        outerRect.bottom = innerRect.bottom + partition/2f
    }

    private fun calculateBounds() {
        val size = min(width, height) - max(max(paddingTop, paddingBottom), max(paddingStart, paddingEnd))
        val centerX = width/2
        val centerY = height/2

        drawingRect.left = centerX - size/2f
        drawingRect.right = centerX + size/2f
        drawingRect.top = centerY - size/2f
        drawingRect.bottom = centerY + size/2f
    }


}