package com.app.creatvt.interact

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Point
import android.text.TextPaint
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat

fun View.dpToPx(dp:Float):Float{
    return dpToPx(context, dp)
}

fun View.spToPx(sp:Float):Float{
    return spToPx(context, sp)
}

fun dpToPx(context: Context, dp:Float):Float{
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
}

fun spToPx(context:Context, sp:Float):Float{
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics)
}

fun <T> T.applyUnit(lambda:T.()->Unit){
    lambda()
}

fun View.centerX():Float{
    val location = getLocationOnScreen()
    return location.x + width/2f
}

fun View.centerY():Float{
    val location = getLocationOnScreen()
    return location.y + height/2f
}

fun View.getLocationOnScreen(): Point {
    val location = IntArray(2)
    getLocationOnScreen(location)
    return Point(location[0], location[1])
}

fun TextView.setTextColorRes(@ColorRes color: Int) {
    setTextColor(ResourcesCompat.getColor(resources, color, context?.theme))
}

fun ImageView.setTint(@ColorRes color: Int) {
    imageTintList =
        ColorStateList.valueOf(ResourcesCompat.getColor(resources, color, context?.theme))
}

fun Context.getColorCompat(@ColorRes color: Int): Int {
    return ResourcesCompat.getColor(resources, color, theme)
}

fun TextPaint.getHeight(): Float {
    return fontMetrics.descent + fontMetrics.ascent
}