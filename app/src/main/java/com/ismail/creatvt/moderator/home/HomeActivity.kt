package com.ismail.creatvt.moderator.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.app.creatvt.interact.dpToPx
import com.app.creatvt.interact.getColorCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ismail.creatvt.moderator.BaseActivity
import com.ismail.creatvt.moderator.R
import com.ismail.creatvt.moderator.customviews.data.BarData
import com.ismail.creatvt.moderator.customviews.data.PieData
import com.ismail.creatvt.moderator.home.categories.SelectCategoryActivity
import com.ismail.creatvt.moderator.home.categories.SelectCategoryActivity.Companion.SELECTED_CATEGORY
import com.ismail.creatvt.moderator.home.categories.SelectCategoryActivity.Companion.TAKE_QUIZ
import com.ismail.creatvt.moderator.login.LoginActivity
import com.ismail.creatvt.moderator.utility.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.stats_bottom_view_layout.*
import java.util.*


class HomeActivity : BaseActivity() {

    private var pieListener: ValueEventListener? = null
    private var barEventListeners: HashMap<String, ValueEventListener>? = null
    private var oldCategory: String? = null
    private var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>? = null
    private var category: String = ""

    companion object {
        private const val SELECT_CATEGORY_REQUEST = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val user = FirebaseAuth.getInstance().currentUser

        val prefs = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        category = prefs.getString(SELECTED_CATEGORY, CATEGORY_ALL) ?: ""

        category_button.text = category

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
                .placeholder(R.drawable.dummy_profile_image)
                .into(profile_image)
        }

        val data = listOf(
            BarData(0, 0),
            BarData(0, 0),
            BarData(0, 0),
            BarData(0, 0),
            BarData(0, 0),
            BarData(0, 0),
            BarData(0, 0)
        )
        bar_graph.setData(data)
        setPieValues(Pair(0, 0))
        setBarGraphData()
        setPieData()
        bottomSheetBehavior = BottomSheetBehavior.from(stats_root)
        bottomSheetBehavior?.peekHeight = (windowManager.getScreenHeight() * 0.6f).toInt()
        bottomSheetBehavior?.addBottomSheetCallback(object:
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                take_quiz_button.alpha = 1f - slideOffset
                take_quiz_button.translationY = dpToPx(this@HomeActivity, 120f) * slideOffset
                pie_chart.alpha = slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

        })

        FirebaseDatabase.getInstance().getReference(SCORES_KEY)
            .child(FirebaseAuth.getInstance().currentUser?.uid ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(scoreSnapshot: DataSnapshot) {
                    val score = scoreSnapshot.getValue(Int::class.java) ?: 0
                    level_value.text = getString(R.string.level_1, (score / 1000))
                    score_value.text = score.toString()
                }
            })

        stats_root.setBottomSheetBehavior(bottomSheetBehavior!!)

        logout_button.setOnClickListener {
            showYesNoDialog(getString(R.string.do_you_want_to_logout)) {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                })
            }
        }

        take_quiz_button.setOnClickListener{
            startActivity(Intent(this, SelectCategoryActivity::class.java).apply {
                putExtra(TAKE_QUIZ, true)
            })
        }

        edit_category_button.setOnClickListener {
            startActivityForResult(
                Intent(this, SelectCategoryActivity::class.java),
                SELECT_CATEGORY_REQUEST
            )
        }

    }

    private fun setPieValues(pair: Pair<Int, Int>) {
        val pieData = listOf(
            PieData(getColorCompat(PIE_COLOR_WINS), pair.first),
            PieData(getColorCompat(PIE_COLOR_LOSSES), pair.second)
        )
        pie_chart.setData(pieData)
    }

    private fun setPieData() {
        pieListener = getPieValues(category, {
            setPieValues(it)
        }, oldCategory, pieListener)
    }

    private fun setBarGraphData() {
        barEventListeners = getLastSevenDayValues(this, category, {
            runOnUiThread {
                bar_graph.setData(it)
            }
        }, oldCategory, barEventListeners)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_CATEGORY_REQUEST && resultCode == Activity.RESULT_OK) {
            oldCategory = category
            category = data?.getStringExtra(SELECTED_CATEGORY) ?: ""
            category_button.text = category
            getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
                .edit().putString(SELECTED_CATEGORY, category).apply()
            setBarGraphData()
            setPieData()
        }
    }

    override fun onBackPressed() {
        if(bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        } else{
            super.onBackPressed()
        }
    }

}