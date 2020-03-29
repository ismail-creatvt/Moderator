package com.ismail.creatvt.moderator.customviews

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.app.creatvt.interact.getColorCompat
import com.app.creatvt.interact.getHeight
import com.ismail.creatvt.moderator.R
import com.ismail.creatvt.moderator.customviews.data.PieData
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class PieChart @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), ValueAnimator.AnimatorUpdateListener, GestureDetector.OnGestureListener {
    private var noDataPieColor: Int = 0
    private var noDataText: String = ""
    private var touchPoint: Point?=null
    private var touchPath = Path()
    private var animationPercentage: Float = 0f
    private var tempPath = Path()

    private var noDataTextPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private var valueTextPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

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

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.PieChart, defStyleAttr, 0)

        valueTextPaint.color = a.getColor(
            R.styleable.PieChart_pc_valueTextColor,
            context.getColorCompat(R.color.defaultValueTextColor)
        )
        valueTextPaint.textSize = a.getDimension(
            R.styleable.PieChart_pc_valueTextSize,
            resources.getDimension(R.dimen.defaultValueTextSize)
        )

        noDataTextPaint.color = a.getColor(
            R.styleable.PieChart_pc_noDataTextColor,
            context.getColorCompat(R.color.defaultNoDataTextColor)
        )
        noDataTextPaint.textSize = a.getDimension(
            R.styleable.PieChart_pc_noDataTextSize,
            resources.getDimension(R.dimen.defaultNoDataTextSize)
        )

        noDataPieColor = a.getColor(R.styleable.PieChart_pc_noDataPieColor, 0xff333333.toInt())
        noDataText = a.getString(R.styleable.PieChart_pc_noDataText)
            ?: resources.getString(R.string.no_data_available)

        a.recycle()
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

        if (total == 0) {
            canvas?.save()
            clipInnerCircle(canvas)
            piePaint.color = noDataPieColor
            createSlicePath(0f, 1, 1, true)
            canvas?.drawPath(slicePath, piePaint)
            canvas?.restore()
            val noDataTextWidth = noDataTextPaint.measureText(noDataText)
            canvas?.drawText(
                noDataText,
                innerRect.centerX() - noDataTextWidth / 2f,
                innerRect.centerY() - noDataTextPaint.getHeight() / 2f,
                noDataTextPaint
            )
            return
        }
        canvas?.save()

        clipInnerCircle(canvas)
        var totalAngle = 0f

        data.forEach { item ->
            var small = true
            var angle = createSlicePath(totalAngle, item.value, total, small)
            createTouchPath()
            tempPath.reset()
            tempPath.set(slicePath)
            tempPath.op(innerClipPath, Path.Op.DIFFERENCE)
            touchPath.op(tempPath, Path.Op.DIFFERENCE)
            piePaint.color = item.color
            if(touchPath.isEmpty && touchPoint != null){
                small = false
                angle = createSlicePath(totalAngle, item.value, total, small)
                canvas?.drawPath(slicePath, piePaint)
                touchPoint = null
            } else{
                canvas?.drawPath(slicePath, piePaint)
            }
            if (animationPercentage == 1f && item.value > 0) {
                val crossSectionWidth =
                    if (!small) (drawingRect.width() - innerRect.width()) / 2f else (outerRect.width() - innerRect.width()) / 2f
                val radius = innerRect.width() / 2f + crossSectionWidth / 2f
                val textX =
                    outerRect.centerX() + radius * sin(Math.toDegrees(totalAngle + angle / 2.0)).toFloat()
                val textY =
                    outerRect.centerY() + radius * cos(Math.toDegrees(totalAngle + angle / 2.0)).toFloat()
                val textWidth = valueTextPaint.measureText(item.value.toString())
                canvas?.drawText(
                    item.value.toString(),
                    textX - textWidth,
                    textY - valueTextPaint.getHeight(),
                    valueTextPaint
                )
            }
            totalAngle += angle
        }

        canvas?.restore()
    }

    private fun clipInnerCircle(canvas: Canvas?) {
        innerClipPath.reset()
        innerClipPath.addCircle(width / 2f, height / 2f, innerRect.width() / 2f, Path.Direction.CW)
        if (Build.VERSION.SDK_INT >= 26) {
            canvas?.clipOutPath(innerClipPath)
        } else {
            canvas?.clipPath(innerClipPath)
        }
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
        var angle = ((value.toFloat() / totalValue.toFloat()) * 360f) * animationPercentage
        if (angle == 360f) {
            angle = 359.9f
        }
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