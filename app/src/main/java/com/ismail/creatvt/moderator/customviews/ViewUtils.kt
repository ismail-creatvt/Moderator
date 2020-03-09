package com.app.creatvt.interact

import android.content.Context
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