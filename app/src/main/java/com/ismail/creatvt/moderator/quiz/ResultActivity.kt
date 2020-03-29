package com.ismail.creatvt.moderator.quiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.creatvt.interact.setTextColorRes
import com.app.creatvt.interact.setTint
import com.ismail.creatvt.moderator.R
import com.ismail.creatvt.moderator.home.categories.SelectCategoryActivity.Companion.SELECTED_CATEGORY
import com.ismail.creatvt.moderator.utility.CORRECT_ANSWERS
import com.ismail.creatvt.moderator.utility.IS_WIN
import com.ismail.creatvt.moderator.utility.TOTAL_QUESTIONS
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val totalQuestions = intent?.getIntExtra(TOTAL_QUESTIONS, 0)
        val correctAnswers = intent?.getIntExtra(CORRECT_ANSWERS, 0)
        val category = intent?.getStringExtra(SELECTED_CATEGORY)
        val isWin = intent?.getBooleanExtra(IS_WIN, false) ?: false

        if (isWin) {
            face_image.setImageResource(R.drawable.ic_smiley_face)
            result_greetings.setText(R.string.hurray_you_won)
            result_greetings.setTextColorRes(R.color.colorLightGreen)
            score_text.setTextColorRes(R.color.textColorDark)
            category_name.setTextColorRes(R.color.textColorDark)
            close_icon.setTint(R.color.textColorDark)
            result_root.setBackgroundResource(R.color.lightWhite)
        } else {
            face_image.setImageResource(R.drawable.ic_sad__face)
            result_greetings.setText(R.string.oops_you_lost)
            result_greetings.setTextColorRes(R.color.colorRed)
            score_text.setTextColorRes(R.color.textColorWhite)
            category_name.setTextColorRes(R.color.textColorWhite)
            close_icon.setTint(R.color.textColorWhite)
            result_root.setBackgroundResource(R.color.lostBackgroundColor)
        }
        close_icon.setOnClickListener {
            finish()
        }
        score_text.text = getString(R.string.ratio, correctAnswers, totalQuestions)
        category_name.text = category
    }
}
