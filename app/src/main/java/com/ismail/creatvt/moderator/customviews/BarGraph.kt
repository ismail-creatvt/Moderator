package com.ismail.creatvt.moderator.customviews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.app.creatvt.interact.dpToPx
import com.app.creatvt.interact.spToPx
import com.ismail.creatvt.moderator.R
import com.ismail.creatvt.moderator.customviews.data.BarData
import kotlin.math.max

class BarGraph @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), ValueAnimator.AnimatorUpdateListener {

    companion object{
        private const val DEFAULT_NO_OF_LINES = 9
        private val DEFAULT_MULTIPLIERS = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
    }

    private var newData: List<BarData>? = null
    private var animationPercentage: Float = 0f
    private var yBaseLine: Int = 0
    private var maxDown: Int = 0
    private var maxUp: Int = 0
    private var stepYMultiplier: Int = 0
    private var barMargin = 0
    private var yStep: Int = 0
    private var data: List<BarData> = listOf()
    private val drawingArea = Rect()
    private val graphArea = Rect()
    private val upBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val downBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val yTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val upValueTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val downValueTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val noOfLines = DEFAULT_NO_OF_LINES
    private var barWidth = dpToPx(10f)
    private var pixelPerValue = 1f
    private var barRects:List<Pair<Rect, Rect>> = arrayListOf()
    private var multipliers = DEFAULT_MULTIPLIERS
    private var barPath = Path()
    private var arcRect = RectF()

    init {

        val a = context.obtainStyledAttributes(attrs, R.styleable.BarGraph, defStyleAttr, 0)

        val upBarColor = a.getColor(R.styleable.BarGraph_bg_upBarColor, 0xff99f90f.toInt())
        val downBarColor = a.getColor(R.styleable.BarGraph_bg_downBarColor, 0xfff99f09.toInt())
        val yAxisColor = a.getColor(R.styleable.BarGraph_bg_yAxisColor, 0xff494949.toInt())
        val yTextColor = a.getColor(R.styleable.BarGraph_bg_ytextColor, 0xff404040.toInt())
        val upValueTextColor = a.getColor(R.styleable.BarGraph_bg_upValuetextColor, upBarColor)
        val downValueTextColor = a.getColor(R.styleable.BarGraph_bg_downValuetextColor, downBarColor)

        barWidth = a.getDimensionPixelSize(R.styleable.BarGraph_bg_barWidth, dpToPx(10f).toInt()).toFloat()

        a.recycle()

        upBarPaint.color = upBarColor
        upBarPaint.style = Paint.Style.FILL

        downBarPaint.color = downBarColor
        downBarPaint.style = Paint.Style.FILL

        linePaint.color = yAxisColor
        linePaint.style = Paint.Style.STROKE

        yTextPaint.textSize = spToPx(12f)
        yTextPaint.color = yTextColor

        upValueTextPaint.textSize = spToPx(12f)
        upValueTextPaint.color = upValueTextColor
        upValueTextPaint.isFakeBoldText = true

        downValueTextPaint.textSize = spToPx(12f)
        downValueTextPaint.color = downValueTextColor
        downValueTextPaint.isFakeBoldText = true
    }

    fun setData(data:List<BarData>){
        this.data = data
        animateBars()
    }

    fun updateData(data:List<BarData>){
        this.newData = this.data
        this.data = data
        animateBars()
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        animationPercentage = animation?.animatedFraction?:0f
        postInvalidate()
    }

    fun animateBars(){
        ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener(this@BarGraph)
            interpolator = AccelerateDecelerateInterpolator()
            duration = 500
            startDelay = 500
            start()
        }
    }

    private fun calculateValues() {
        barRects = List(data.size) { Pair(Rect(), Rect()) }

        val maxValue = maxUp + maxDown
        stepYMultiplier = 0

        while(stepYMultiplier == 0){
            for(multiplier in multipliers){
                if((multiplier * (noOfLines - 1)) >= maxValue){
                    stepYMultiplier = multiplier
                }
            }
            if(stepYMultiplier != 0) break

            for(i in multipliers.indices){
                multipliers.add(i, multipliers[i] * 2)
            }
        }

        yStep = graphArea.height() / noOfLines
        pixelPerValue = (yStep.toFloat() / stepYMultiplier.toFloat())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if(data.isEmpty()) return

        maxUp = data.maxBy { it.upValue }?.upValue?:0
        maxDown = data.maxBy { it.downValue }?.downValue?:0

        calculateRects()
        calculateValues()

        val barSectionWidth = graphArea.width() / data.size
        barMargin = ((barSectionWidth - barWidth)/2).toInt()

        calculateBarRects()

        for(i in 0..noOfLines){
            val lineY = graphArea.top + i * yStep
            val textHeight = yTextPaint.fontMetrics.descent + yTextPaint.fontMetrics.ascent
            if(lineY == yBaseLine){
                canvas?.drawText(0.toString(), drawingArea.left.toFloat(), lineY.toFloat() - textHeight/2, yTextPaint)
            } else if(lineY < yBaseLine){
                val value = ((yBaseLine - lineY)/yStep) * stepYMultiplier
                canvas?.drawText(value.toString(), drawingArea.left.toFloat(), lineY.toFloat() - textHeight/2, yTextPaint)
            } else if(lineY > yBaseLine){
                val value = ((lineY - yBaseLine)/yStep) * stepYMultiplier
                canvas?.drawText(value.toString(), drawingArea.left.toFloat(), lineY.toFloat() - textHeight/2, yTextPaint)
            }
            canvas?.drawLine(graphArea.left.toFloat(), lineY.toFloat(), graphArea.right.toFloat(), lineY.toFloat(), linePaint)
        }

        Log.d("PixelPerValue", "value == $pixelPerValue")
        for(index in barRects.indices){
            val rects = barRects[index]
            createUpPath(rects.first)
            canvas?.drawPath(barPath, upBarPaint)
            createDownPath(rects.second)
            canvas?.drawPath(barPath, downBarPaint)
            if(animationPercentage == 1f){
                drawUpValue(canvas, index, rects.first)
                drawDownValue(canvas, index, rects.second)
            }
        }

    }

    private fun drawUpValue(canvas:Canvas?, index:Int, rect:Rect) {
        val text = data[index].upValue.toString()
        val textHeight = upValueTextPaint.fontMetrics.descent + upValueTextPaint.fontMetrics.ascent
        val textWidth = upValueTextPaint.measureText(text)
        val textX = rect.left.toFloat() + rect.width()/2 - textWidth/2
        val textY = rect.top + textHeight
        canvas?.drawText(text, textX, textY, upValueTextPaint)
    }

    private fun drawDownValue(canvas:Canvas?, index:Int, rect:Rect) {
        val text = data[index].downValue.toString()
        val textHeight = downValueTextPaint.fontMetrics.descent + downValueTextPaint.fontMetrics.ascent
        val textWidth = downValueTextPaint.measureText(text)
        val textX = rect.left.toFloat() + rect.width()/2 - textWidth/2
        val textY = rect.bottom - (textHeight * 1.5f)
        canvas?.drawText(text, textX, textY, downValueTextPaint)
    }

    private fun createUpPath(rect: Rect) {
        barPath.reset()
        barPath.moveTo(rect.left.toFloat(), rect.bottom.toFloat())
        barPath.lineTo(rect.left.toFloat(), rect.top.toFloat() + rect.width()/2)

        arcRect.left = rect.left.toFloat()
        arcRect.right = rect.right.toFloat()
        arcRect.bottom = rect.top.toFloat() + rect.width()
        arcRect.top = rect.top.toFloat()

        barPath.arcTo(arcRect, 180f, 180f, true)
        barPath.lineTo(rect.right.toFloat(), rect.bottom.toFloat())
        barPath.lineTo(rect.left.toFloat(), rect.bottom.toFloat())
        barPath.close()
    }

    private fun createDownPath(rect: Rect) {
        barPath.reset()
        barPath.moveTo(rect.left.toFloat(), rect.top.toFloat())
        barPath.lineTo(rect.left.toFloat(), rect.bottom.toFloat() - rect.width()/2)

        arcRect.left = rect.left.toFloat()
        arcRect.right = rect.right.toFloat()
        arcRect.bottom = rect.bottom.toFloat()
        arcRect.top = rect.bottom.toFloat() - rect.width()

        barPath.arcTo(arcRect, 180f, -180f, true)
        barPath.lineTo(rect.right.toFloat(), rect.top.toFloat())
        barPath.lineTo(rect.left.toFloat(), rect.top.toFloat())
        barPath.close()
    }

    private fun calculateRects() {
        drawingArea.left = paddingLeft
        drawingArea.right = width - paddingRight
        drawingArea.bottom = height - paddingBottom
        drawingArea.top = paddingTop

        val textHeight = yTextPaint.fontMetrics.descent + yTextPaint.fontMetrics.ascent
        Log.d("BarGraph","fontMetrics : ${yTextPaint.fontMetrics.descent} + ${yTextPaint.fontMetrics.ascent} = $textHeight")
        graphArea.left = drawingArea.left + yTextPaint.measureText((max(maxUp, maxDown) * 10).toString()).toInt()
        graphArea.right = drawingArea.right
        graphArea.bottom = drawingArea.bottom - paddingBottom + textHeight.toInt()
        graphArea.top = drawingArea.top + paddingTop - textHeight.toInt()
    }

    private fun calculateBarRects() {
        for(index in data.indices){
            val item = data[index]
            val xPoint = (graphArea.left + barMargin + (index * (barMargin * 2 + barWidth))).toInt()
            yBaseLine = graphArea.top + (yStep * (noOfLines - 1)/2)

            while(yBaseLine < (graphArea.top + (maxUp * pixelPerValue))){
                yBaseLine += yStep
            }

            while((maxDown * pixelPerValue) > (graphArea.bottom - yBaseLine)){
                yBaseLine -= yStep
            }

            barRects[index].first.left = xPoint
            barRects[index].first.right = xPoint + barWidth.toInt()
            barRects[index].first.top = yBaseLine - (item.upValue * pixelPerValue * animationPercentage).toInt()
            barRects[index].first.bottom = yBaseLine

            barRects[index].second.left = xPoint
            barRects[index].second.right = xPoint + barWidth.toInt()
            barRects[index].second.top = yBaseLine
            barRects[index].second.bottom = yBaseLine + (item.downValue * pixelPerValue * animationPercentage).toInt()
        }
    }
}