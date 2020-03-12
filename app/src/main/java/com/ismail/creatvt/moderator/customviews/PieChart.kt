package com.ismail.creatvt.moderator.customviews

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.GestureDetector
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
) : View(context, attrs, defStyleAttr), ValueAnimator.AnimatorUpdateListener, GestureDetector.OnGestureListener {
    private var touchPoint: Point?=null
    private var touchPath = Path()
    private var animationPercentage: Float = 0f

    private val gestureDetector = GestureDetector(context, this)
    private var data = listOf<PieData>()
    private var piePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private var slicePath = Path()
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
            var angle = createSlicePath(totalAngle, item.value, total, true)
            createTouchPath()
            touchPath.op(slicePath, Path.Op.DIFFERENCE)
            piePaint.color = item.color
            if(touchPath.isEmpty && touchPoint != null){
                angle = createSlicePath(totalAngle, item.value, total, false)
                canvas?.drawPath(slicePath, piePaint)
                touchPoint = null
            } else{
                canvas?.drawPath(slicePath, piePaint)
            }
            totalAngle += angle
        }

        canvas?.restore()
    }

    private fun createTouchPath() {
        if(touchPoint != null){
            val x = touchPoint?.x?.toFloat()?:0f
            val y = touchPoint?.y?.toFloat()?:0f
            touchPath.reset()
            touchPath.moveTo(x, y)
            touchPath.addRect(x - 2f, y - 2f, x + 2f, y + 2f, Path.Direction.CW)
            touchPath.close()
        }
    }

    private fun createSlicePath(startAngle:Float, value:Int, totalValue:Int, small:Boolean): Float {
        slicePath.reset()
        val radius = if(small) outerRect.width()/2f else drawingRect.width()/2f
        val xOuter = outerRect.centerX() + radius * sin(startAngle)
        val yOuter = outerRect.centerY() + radius * cos(startAngle)
        slicePath.moveTo(outerRect.centerX(), outerRect.centerY())
        slicePath.lineTo(xOuter, yOuter)
        val angle = ((value.toFloat()/totalValue.toFloat()) * 360f) * animationPercentage
        slicePath.arcTo(if(small) outerRect else drawingRect, startAngle, angle, true)
        slicePath.lineTo(outerRect.centerX(), outerRect.centerY())
        slicePath.close()
        return angle
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    override fun onShowPress(p0: MotionEvent?) {

    }

    fun vibrate(){
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if(Build.VERSION.SDK_INT >= 26){
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else{
            vibrator.vibrate(2000)
        }
    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        return false
    }

    override fun onDown(p0: MotionEvent?): Boolean {
        if(p0 != null) {
            touchPoint = Point(p0.x.toInt(), p0.y.toInt())
            invalidate()
            return true
        }
        return false
    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        return false
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        return false
    }

    override fun onLongPress(p0: MotionEvent?) {

    }

    private fun calculateRects() {
        val partition = drawingRect.width()/9
        innerRect.left = drawingRect.centerX() - partition*2
        innerRect.right = drawingRect.centerX() + partition*2
        innerRect.top = drawingRect.centerY() - partition*2
        innerRect.bottom = drawingRect.centerY() + partition*2

        outerRect.left = innerRect.left - partition * 2
        outerRect.right = innerRect.right + partition * 2
        outerRect.top = innerRect.top - partition * 2
        outerRect.bottom = innerRect.bottom + partition * 2
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