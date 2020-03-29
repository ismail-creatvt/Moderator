package com.ismail.creatvt.moderator.utility

import android.util.DisplayMetrics
import android.view.WindowManager


fun WindowManager.getScreenHeight(): Int {
    val displayMetrics = DisplayMetrics()
    getDefaultDisplay().getMetrics(displayMetrics)
    return displayMetrics.heightPixels
}

fun WindowManager.getScreenWidth(): Int {
    val displayMetrics = DisplayMetrics()
    getDefaultDisplay().getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}