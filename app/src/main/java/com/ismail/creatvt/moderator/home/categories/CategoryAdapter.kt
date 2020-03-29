package com.ismail.creatvt.moderator.home.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.creatvt.interact.applyUnit
import com.ismail.creatvt.moderator.R
import kotlinx.android.synthetic.main.category_item.view.*

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    var categoryList: ArrayList<String>? = null
        set(value) {
            val oldList = categoryList
            field = value
            DiffUtil.calculateDiff(
                CategoryDiffCallback(
                    oldList ?: arrayListOf(),
                    value ?: arrayListOf()
                )
            )
                .dispatchUpdatesTo(this)
        }
    var onItemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CategoryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        )

    override fun getItemCount() = categoryList?.size ?: 0

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) = holder.itemView.applyUnit{
        category_name.text = categoryList?.get(position)
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(categoryList?.get(holder.adapterPosition) ?: "")
        }
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface ItemClickListener{
        fun onItemClick(category: String)
    }
}