package com.app.creatvt.interact

import android.content.Context
import android.graphics.Point
import android.util.TypedValue
import android.view.View

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