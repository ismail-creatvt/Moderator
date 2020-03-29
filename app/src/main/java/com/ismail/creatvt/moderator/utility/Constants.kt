package com.ismail.creatvt.moderator.utility

import android.content.res.Resources
import androidx.core.os.ConfigurationCompat
import com.ismail.creatvt.moderator.R
import java.text.SimpleDateFormat

const val CATEGORY_ALL: String = "All"
const val CORRECT_ANSWERS: String = "CORRECT_ANSWERS"
const val DATE_FORMAT: String = "ddMMyyyy"
const val QUESTION_ID = "com.ismail.creatvt.moderator.quiz.QUESTION_ID"
const val QUESTION_NUMBER = "com.ismail.creatvt.moderator.quiz.QUESTION_NUMBER"
const val TOTAL_QUESTIONS = "com.ismail.creatvt.moderator.quiz.TOTAL_QUESTIONS"
const val IS_WIN = "IS_WIN"
const val CORRECT_OPTION_BACKGROUND = R.drawable.option_box_background_green
const val INCORRECT_OPTION_BACKGROUND = R.drawable.option_box_background_red
const val SECONDS: Long = 1000
const val DEFAULT_COUNTDOWN_DURATION: Long = 60 * SECONDS

const val QUESTIONS_KEY = "questions"
const val STATS_KEY = "stats"
const val SCORES_KEY = "scores"
const val CATEGORIES_KEY = "categories"

const val PIE_COLOR_WINS = R.color.colorAccent
const val PIE_COLOR_LOSSES = R.color.colorRed

const val SHARED_PREFS = "ModeratorPrefs"

fun Resources.getDateFormatter(): SimpleDateFormat {
    return SimpleDateFormat(DATE_FORMAT, ConfigurationCompat.getLocales(configuration).get(0))
}