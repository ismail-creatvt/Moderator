package com.ismail.creatvt.moderator.home

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ismail.creatvt.moderator.BaseActivity
import com.ismail.creatvt.moderator.R
import com.ismail.creatvt.moderator.customviews.DragPointBottomSheetBehavior
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.stats_bottom_view_layout.*

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val data = listOf(
            Pair(10, 30),
            Pair(20, 50),
            Pair(30, 33),
            Pair(45, 10),
            Pair(20, 20),
            Pair(15, 15),
            Pair(5, 36)
        )
        bar_graph.setData(data)

        val pieData = listOf(
            Pair(0xfff909f0.toInt(), 10),
            Pair(0xff99f9f0.toInt(), 30),
            Pair(0xff9909f9.toInt(), 50)
        )
        pie_chart.setData(pieData)

        val bottomSheetBehavior = BottomSheetBehavior.from(stats_root)
        bottomSheetBehavior.addBottomSheetCallback(object:
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                quiz_options.scaleX = 1f - slideOffset
                quiz_options.scaleY = 1f - slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

        })
    }

}