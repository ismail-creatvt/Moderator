package com.ismail.creatvt.moderator.home

import android.os.Bundle
import com.ismail.creatvt.moderator.BaseActivity
import com.ismail.creatvt.moderator.R
import kotlinx.android.synthetic.main.stats_bottom_view_layout.*

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val data = listOf(
            Pair(10, 30),
            Pair(20, 50),
            Pair(30, 60),
            Pair(50, 10),
            Pair(80, 20),
            Pair(60, 15),
            Pair(70, 36)
        )
        bar_graph.setData(data)
    }

}