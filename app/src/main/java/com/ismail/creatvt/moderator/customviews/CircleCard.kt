package com.ismail.creatvt.moderator.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.ismail.creatvt.moderator.R
import kotlin.math.min

class CircleCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CircleCard, defStyleAttr, 0)

        val backgroundColor = a.getColor(R.styleable.CircleCard_cc_backgroundColor, 0xffffffff.toInt())

        paint.color = backgroundColor
        paint.style = Paint.Style.FILL

        a.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val radius = min(height, width)/2f

        paint.setShadowLayer(radius * 0.9f, width/2f, height/2f, 0x023920)

        canvas?.drawCircle(width/2f, height/2f, radius * 0.9f, paint)

    }
}