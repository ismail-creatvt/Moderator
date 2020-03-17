package com.ismail.creatvt.moderator.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.creatvt.interact.applyUnit
import com.ismail.creatvt.moderator.R
import com.ismail.creatvt.moderator.models.Category
import kotlinx.android.synthetic.main.category_item.view.*

class CategoryAdapter(var categoryList:List<Category>): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    var onItemClickListener:ItemClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CategoryViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
    )

    override fun getItemCount() = categoryList.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) = holder.itemView.applyUnit{
        category_name.text = categoryList[position].name
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(categoryList[holder.adapterPosition])
        }
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface ItemClickListener{
        fun onItemClick(category:Category)
    }
}