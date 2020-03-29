package com.ismail.creatvt.moderator.quiz

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ismail.creatvt.moderator.BaseActivity
import com.ismail.creatvt.moderator.R
import com.ismail.creatvt.moderator.home.categories.SelectCategoryActivity.Companion.SELECTED_CATEGORY
import com.ismail.creatvt.moderator.models.QuestionObject
import com.ismail.creatvt.moderator.models.UserStats
import com.ismail.creatvt.moderator.utility.*
import kotlinx.android.synthetic.main.activity_quiz.*
import java.util.*

class QuizActivity : BaseActivity(), QuestionFragment.OnQuizInteractionListener {

    companion object {
        private const val SCORE_PER_QUESTION: Int = 10
        private const val WIN_CRITERIA: Float = 50.0f
    }

    private var totalScore: Int = 0
    private val currentQuestions = arrayListOf<QuestionObject>()
    private var category = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.QuestionActivityStyle)
        setContentView(R.layout.activity_quiz)

        category = intent.getStringExtra(SELECTED_CATEGORY) ?: ""

        val questionsRef =
            FirebaseDatabase.getInstance().getReference(QUESTIONS_KEY).child(category)
        val questions = arrayListOf<QuestionObject>()


        questionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                runOnUiThread { showToast("Error fetching questions!") }
                finish()
            }

            override fun onDataChange(questionsSnapshot: DataSnapshot) {
                for (question in questionsSnapshot.children) {
                    val questionObject = question.getValue(QuestionObject::class.java)
                    if (questionObject != null) {
                        questions.add(questionObject)
                    }
                }

                currentQuestions.clear()
                while (currentQuestions.size < 10) {
                    val random = (Math.random() * questions.size - 1).toInt()
                    val question = questions[random]
                    if (!currentQuestions.contains(question)) {
                        currentQuestions.add(question)
                    }
                }
                setUpQuestionList()
            }
        })

    }

    private fun setUpQuestionList() {
        questions_list.isUserInputEnabled = false
        questions_list.adapter = object : FragmentStateAdapter(this) {

            override fun getItemCount() = currentQuestions.size

            override fun createFragment(position: Int): Fragment {
                return QuestionFragment.newInstance(
                    currentQuestions[position].id,
                    category,
                    position + 1,
                    currentQuestions.size
                )
            }

        }
    }

    override fun onOptionClick(correct: Boolean) {
        if (correct) {
            totalScore += SCORE_PER_QUESTION
        }
        Handler().postDelayed({
            val currentItem = questions_list.currentItem
            if (currentItem == (currentQuestions.size - 1)) {
                saveUserScore {
                    startActivity(Intent(this@QuizActivity, ResultActivity::class.java).apply {
                        putExtra(TOTAL_QUESTIONS, currentQuestions.size)
                        putExtra(CORRECT_ANSWERS, totalScore / SCORE_PER_QUESTION)
                        putExtra(SELECTED_CATEGORY, category)
                        putExtra(IS_WIN, it)
                    })
                    finish()
                }
            } else {
                questions_list.currentItem = questions_list.currentItem + 1
            }
        }, 1000)
    }

    private fun showSavingScoreDialog() = Dialog(this).apply {
        setContentView(R.layout.saving_score_layout)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawableResource(R.color.transparentGray)
        window?.setLayout(MATCH_PARENT, MATCH_PARENT)
        show()
    }

    private fun saveUserScore(onCompleteListener: (Boolean) -> Unit) {
        val dialog = showSavingScoreDialog()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val maxScore = currentQuestions.size * SCORE_PER_QUESTION
            val totalPercentage = (totalScore / maxScore.toFloat()) * 100.0
            val isWin = totalPercentage >= WIN_CRITERIA
            saveStats(currentUser.uid, dialog, isWin) {
                saveScore(currentUser.uid, dialog, totalScore, it) {
                    onCompleteListener(isWin)
                }
            }
        }
    }

    private fun saveScore(
        uid: String,
        dialog: Dialog,
        totalScore: Int,
        isWin: Boolean,
        onCompleteListener: (Boolean) -> Unit
    ) {
        val scoreRef = FirebaseDatabase.getInstance().getReference(SCORES_KEY).child(uid)
        scoreRef.runTransaction(object : Transaction.Handler {
            override fun onComplete(error: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                if (error != null) {
                    showError(error, dialog)
                    finish()
                } else {
                    onCompleteListener(isWin)
                }
            }

            override fun doTransaction(scoreData: MutableData): Transaction.Result {
                var score = scoreData.getValue(Int::class.java) ?: 0
                score += totalScore
                scoreData.value = score
                return Transaction.success(scoreData)
            }
        })
    }

    private fun saveStats(
        uid: String,
        dialog: Dialog,
        isWin: Boolean,
        onCompleteListener: (Boolean) -> Unit
    ) {
        val statsRef = FirebaseDatabase.getInstance().getReference(STATS_KEY).child(uid)
        statsRef.runTransaction(object : Transaction.Handler {
            override fun onComplete(error: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                if (error != null) {
                    showError(error, dialog)
                    finish()
                } else {
                    onCompleteListener(isWin)
                }
            }

            override fun doTransaction(userData: MutableData): Transaction.Result {
                val user = userData.getValue(UserStats::class.java) ?: UserStats()
                val date = resources.getDateFormatter().format(Date())
                user.incrementData(isWin, category, date)
                userData.value = user
                return Transaction.success(userData)
            }

        })
    }

    private fun showError(error: DatabaseError, dialog: Dialog? = null) {
        dialog?.dismiss()
        Log.d("FirebaseError", error.details)
        Log.d("FirebaseError", error.message)
        Snackbar.make(quiz_root, R.string.error_while_saving_data, Snackbar.LENGTH_SHORT)
    }

    override fun onBackPressed() = showYesNoDialog(getString(R.string.do_you_want_to_quit)) {
        super.onBackPressed()
    }

}