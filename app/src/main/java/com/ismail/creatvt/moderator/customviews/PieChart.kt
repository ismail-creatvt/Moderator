package com.ismail.creatvt.moderator.customviews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class PieChart @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var data = listOf<Pair<Int, Int>>()
    private var piePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private var piePath = Path()
    private var outerRect = RectF()
    private var innerRect = RectF()
    private var drawingRect = RectF()

    fun setData(data:List<Pair<Int, Int>>){
        this.data = data
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        calculateBounds()
        calculateRects()

        var total = 0
        for(item in data){
            total += item.second
        }

        var totalAngle = 0f
        for(index in data.indices){
            val item = data[index]
            piePaint.color = item.first
            piePath.reset()
            val startAngle = totalAngle

            val xInner = if(startAngle == 0f) {
                innerRect.right
            } else {
                innerRect.centerX() + innerRect.width() / 2 * sin(startAngle)
            }
            val yInner = if(startAngle == 0f){
                innerRect.centerY()
            } else{
                innerRect.centerY() + innerRect.width() / 2 * cos(startAngle)
            }
            piePath.moveTo(xInner, yInner)
            val angle = (item.second.toFloat()/total.toFloat()) * 360f
            piePath.arcTo(innerRect, startAngle, angle, true)
            totalAngle += angle
            val xOuter = outerRect.centerX() + outerRect.width()/2 * sin(angle)
            val yOuter = outerRect.centerY() + outerRect.width()/2 * cos(angle)
            piePath.lineTo(xOuter, yOuter)
            piePath.arcTo(outerRect, angle, -startAngle, true)
            piePath.lineTo(xInner, yInner)
            piePath.close()
            canvas?.drawPath(piePath, piePaint)
        }
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