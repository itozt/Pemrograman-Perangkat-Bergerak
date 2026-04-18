package com.example.todolistpersonal

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * CategoryAdapter - Adapter untuk menampilkan horizontal list kategori/grup.
 * Digunakan di TasksFragment untuk filter tasks berdasarkan kategori.
 */
class CategoryAdapter(
    private val categories: List<Category>,
    private val selectedCategoryId: String,
    private val onCategorySelected: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryTextView = itemView.findViewById<TextView>(R.id.tv_category_chip)

        fun bind(category: Category, isSelected: Boolean) {
            categoryTextView.text = category.name

            if (isSelected) {
                categoryTextView.setBackgroundResource(R.drawable.rounded_category_background)
                categoryTextView.setTextColor(Color.WHITE)
            } else {
                categoryTextView.setBackgroundResource(R.drawable.rounded_category_background_inactive)
                categoryTextView.setTextColor(Color.parseColor("#666666"))
            }

            itemView.setOnClickListener {
                onCategorySelected(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_chip, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        val isSelected = category.id == selectedCategoryId
        holder.bind(category, isSelected)
    }

    override fun getItemCount() = categories.size
}
