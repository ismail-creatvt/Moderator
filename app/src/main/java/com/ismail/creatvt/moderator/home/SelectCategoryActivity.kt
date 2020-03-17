package com.ismail.creatvt.moderator.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ismail.creatvt.moderator.R
import com.ismail.creatvt.moderator.models.Category
import kotlinx.android.synthetic.main.activity_select_category.*


class SelectCategoryActivity : AppCompatActivity(), CategoryAdapter.ItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.QuestionActivityStyle)
        setContentView(R.layout.activity_select_category)

        category_list.adapter = CategoryAdapter(listOf(
            Category("Computer Science"),
            Category("Mechanical Engineering"),
            Category("Chemical Engineering")
        )).apply {
            onItemClickListener = this@SelectCategoryActivity
        }
        category_list.layoutManager = LinearLayoutManager(this)

        cross_icon.setOnClickListener {
            finish()
        }
    }

    override fun onItemClick(category: Category) {

    }

}
