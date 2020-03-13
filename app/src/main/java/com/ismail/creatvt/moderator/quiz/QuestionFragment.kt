package com.ismail.creatvt.moderator.quiz

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ismail.creatvt.moderator.R

class QuestionFragment : Fragment() {

    private var questionId: String = ""

    private var listener: OnQuizInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            questionId = it.getString(QUESTION_ID)?:""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_question, container, false)
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
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        private const val QUESTION_ID = "com.ismail.creatvt.moderator.quiz.QUESTION_ID"

        @JvmStatic
        fun newInstance(questionId: String) =
            QuestionFragment().apply {
                arguments = Bundle().apply {
                    putString(QUESTION_ID, questionId)
                }
            }
    }
}
