package com.ismail.creatvt.moderator.home.categories

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ismail.creatvt.moderator.R
import com.ismail.creatvt.moderator.quiz.QuizActivity
import com.ismail.creatvt.moderator.utility.CATEGORIES_KEY
import com.ismail.creatvt.moderator.utility.CATEGORY_ALL
import kotlinx.android.synthetic.main.activity_select_category.*


class SelectCategoryActivity : AppCompatActivity(), CategoryAdapter.ItemClickListener {

    private var isTakeQuiz: Boolean = false

    companion object {
        const val TAKE_QUIZ: String = "TAKE_QUIZ"
        const val SELECTED_CATEGORY = "SELECTED_CATEGORY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.QuestionActivityStyle)
        setContentView(R.layout.activity_select_category)

        isTakeQuiz = intent.getBooleanExtra(TAKE_QUIZ, false)

        loading_group.visibility = View.VISIBLE

        val categoriesRef = FirebaseDatabase.getInstance().getReference(CATEGORIES_KEY)

        val categoryAdapter = CategoryAdapter().apply {
            onItemClickListener = this@SelectCategoryActivity
        }

        categoriesRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(categoriesSnapshot: DataSnapshot) {
                val categories = arrayListOf<String>()
                if (!isTakeQuiz) {
                    categories.add(CATEGORY_ALL)
                }
                for (categorySnapshot in categoriesSnapshot.children) {
                    categories.add(categorySnapshot.getValue(String::class.java) ?: "")
                }
                categoryAdapter.categoryList = categories
                loading_group.visibility = View.GONE
            }
        })

        category_list.adapter = categoryAdapter
        category_list.layoutManager = LinearLayoutManager(this)

        cross_icon.setOnClickListener {
            finish()
        }
    }

    override fun onItemClick(category: String) {
        if (isTakeQuiz) {
            startActivity(Intent(this, QuizActivity::class.java).apply {
                putExtra(SELECTED_CATEGORY, category)
            })
            finish()
        } else {
            val data = Intent()
            data.putExtra(SELECTED_CATEGORY, category)
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

}
