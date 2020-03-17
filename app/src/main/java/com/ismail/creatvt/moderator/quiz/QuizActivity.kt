package com.ismail.creatvt.moderator.quiz

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ismail.creatvt.moderator.R
import kotlinx.android.synthetic.main.activity_quiz.*

class QuizActivity : AppCompatActivity(), QuestionFragment.OnQuizInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.QuestionActivityStyle)
        setContentView(R.layout.activity_quiz)

    }

    override fun onFragmentInteraction(uri: Uri) {

    }
}