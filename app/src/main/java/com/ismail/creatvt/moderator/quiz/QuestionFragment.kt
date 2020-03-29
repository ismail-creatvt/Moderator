package com.ismail.creatvt.moderator.quiz

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.app.creatvt.interact.setTextColorRes
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ismail.creatvt.moderator.R
import com.ismail.creatvt.moderator.home.categories.SelectCategoryActivity.Companion.SELECTED_CATEGORY
import com.ismail.creatvt.moderator.models.QuestionObject
import com.ismail.creatvt.moderator.utility.*
import kotlinx.android.synthetic.main.fragment_question.*

class QuestionFragment : Fragment(), View.OnClickListener {

    private var optionTexts: ArrayList<TextView> = arrayListOf()
    private var questionNumber: Int = 0
    private var totalQuestions: Int = 0
    private var questionCountDownTimer: QuestionCountDownTimer? = null
    private var category: String = ""
    private var questionId: String = ""

    private var listener: OnQuizInteractionListener? = null
    private var questionObject: QuestionObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            category = it.getString(SELECTED_CATEGORY) ?: ""
            questionId = it.getString(QUESTION_ID)?:""
            questionNumber = it.getInt(QUESTION_NUMBER)
            totalQuestions = it.getInt(TOTAL_QUESTIONS)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        questionCountDownTimer?.cancel()
    }

    class QuestionCountDownTimer(
        private val onTickListener: (Long) -> Unit,
        val onFinishListener: () -> Unit,
        duration: Long
    ) : CountDownTimer(duration, 100) {
        override fun onFinish() {
            onFinishListener()
        }

        override fun onTick(millisUntilFinished: Long) {
            onTickListener(millisUntilFinished)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val questionRef = FirebaseDatabase.getInstance().getReference(QUESTIONS_KEY).child(category)
            .child(questionId)

        optionTexts = arrayListOf(option1, option2, option3, option4)

        question_counter.text = getString(R.string.ratio, questionNumber, totalQuestions)

        questionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(questionSnapshot: DataSnapshot) {
                questionObject = questionSnapshot.getValue(QuestionObject::class.java)
                question.text = questionObject?.question?.text ?: ""
                val isTextLong = question.text.length > 150
                var duration = DEFAULT_COUNTDOWN_DURATION
                if (isTextLong) {
                    duration *= 2
                }
                startCountDown(duration)
                optionTexts.forEachIndexed { index, option ->
                    option.text = questionObject?.getOption(index) ?: ""
                    if (option.text.length > 40) {
                        val padding = resources.getDimensionPixelSize(R.dimen.margin_medium)
                        val margin = resources.getDimensionPixelSize(R.dimen.margin_small)
                        option.setPadding(padding, padding, padding, padding)
                        val lp = option.layoutParams as ConstraintLayout.LayoutParams
                        lp.marginStart = margin
                        lp.marginEnd = margin
                        lp.topMargin = margin
                        lp.bottomMargin = margin
                        option.layoutParams = lp
                    }
                    option.setOnClickListener(this@QuestionFragment)
                }
            }

        })
    }

    private fun startCountDown(duration: Long) {
        questionCountDownTimer = QuestionCountDownTimer({
            timer.progress = ((it / duration.toFloat()) * 600.0).toInt()
        }, {
            Snackbar.make(question_root, R.string.timeout, Snackbar.LENGTH_SHORT).show()
            listener?.onOptionClick(false)
        }, duration)

        questionCountDownTimer?.start()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnQuizInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnQuizInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnQuizInteractionListener {
        fun onOptionClick(correct: Boolean)
    }

    companion object {

        @JvmStatic
        fun newInstance(
            questionId: String,
            category: String,
            questionNumber: Int,
            totalQuestions: Int
        ) =
            QuestionFragment().apply {
                arguments = Bundle().apply {
                    putString(QUESTION_ID, questionId)
                    putString(SELECTED_CATEGORY, category)
                    putInt(QUESTION_NUMBER, questionNumber)
                    putInt(TOTAL_QUESTIONS, totalQuestions)
                }
            }
    }

    private fun soundFailure() {

    }

    private fun soundSuccess() {

    }

    override fun onClick(v: View?) {
        if (v == null) return
        val textView = v as TextView
        textView.setTextColorRes(android.R.color.white)
        val isCorrect = textView.text.toString() == questionObject?.correct_option
        if (isCorrect) {
            v.setBackgroundResource(CORRECT_OPTION_BACKGROUND)
            soundSuccess()
        } else {
            v.setBackgroundResource(INCORRECT_OPTION_BACKGROUND)
            soundFailure()
            optionTexts.forEachIndexed { index, option ->
                option.setOnClickListener(null)
                if (questionObject?.getOption(index) == questionObject?.correct_option) {
                    option.setTextColorRes(android.R.color.white)
                    option.setBackgroundResource(CORRECT_OPTION_BACKGROUND)
                    option.animate().scaleX(1.1f).scaleY(1.1f).alpha(0.5f).start()
                    Handler().postDelayed({
                        option.animate().scaleX(1f).scaleY(1f).alpha(1f).start()
                    }, 150)
                }
            }
        }

        listener?.onOptionClick(isCorrect)
        listener = null
    }

}
