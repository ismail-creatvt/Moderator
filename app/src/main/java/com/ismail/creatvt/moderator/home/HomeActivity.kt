package com.ismail.creatvt.moderator.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.ismail.creatvt.moderator.BaseActivity
import com.ismail.creatvt.moderator.R
import com.ismail.creatvt.moderator.SplashActivity
import com.ismail.creatvt.moderator.customviews.data.BarData
import com.ismail.creatvt.moderator.customviews.data.PieData
import com.ismail.creatvt.moderator.login.LoginActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.stats_bottom_view_layout.*


class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.ismail.creatvt.moderator.R.layout.activity_home)

        val user = FirebaseAuth.getInstance().currentUser

        if(user != null){
            display_name.text = user.displayName
            var photoUrl: String
            val provider = user.providerData[0]
            when {
                provider.photoUrl.toString().contains("facebook.com") -> photoUrl = user.photoUrl!!.toString() + "?height=500"
                provider.photoUrl.toString().contains("google.com") -> {
                    photoUrl = user.photoUrl!!.toString()
                    //Remove thumbnail url and replace the original part of the Url with the new part
                    photoUrl = photoUrl.substring(0, photoUrl.length - 15) + "s400-c/photo.jpg"
                }
                else -> photoUrl = user.photoUrl!!.toString()
            }
            Glide.with(profile_image)
                .load(photoUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(com.ismail.creatvt.moderator.R.drawable.dummy_profile_image)
                .into(profile_image)
        }

        val data = listOf(
            BarData(10, 30),
            BarData(20, 50),
            BarData(30, 33),
            BarData(45, 10),
            BarData(20, 20),
            BarData(15, 15),
            BarData(5, 36)
        )
        bar_graph.setData(data)

        val pieData = listOf(
            PieData(0xffff0000.toInt(), 10),
            PieData(0xff00ff00.toInt(), 30),
            PieData(0xff0000ff.toInt(), 50)
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

        more_options.setOnClickListener {
            val moreOptionsPopup = PopupMenu(this, more_options)
            moreOptionsPopup.inflate(R.menu.more_option_menu)
            moreOptionsPopup.show()
            moreOptionsPopup.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.logout ->{
                        FirebaseAuth.getInstance().signOut()
                        startActivity(Intent(this@HomeActivity, LoginActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        })
                        return@setOnMenuItemClickListener true
                    }
                    R.id.settings ->{
                        return@setOnMenuItemClickListener true
                    }
                    else -> {
                        return@setOnMenuItemClickListener false
                    }
                }
            }
        }
    }

}