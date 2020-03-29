package com.ismail.creatvt.moderator.home.categories

import androidx.recyclerview.widget.DiffUtil

class CategoryDiffCallback(
    private val oldCategories: ArrayList<String>,
    private val newCategories: ArrayList<String>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldCategories[oldItemPosition] == newCategories[newItemPosition]
    }

    override fun getOldListSize(): Int {
        return oldCategories.size
    }

    override fun getNewListSize(): Int {
        return newCategories.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldCategories[oldItemPosition] == newCategories[newItemPosition]
    }
}
