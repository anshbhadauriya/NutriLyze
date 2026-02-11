package com.example.finallyy

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class ManualFoodAdapter(
    private val foods: MutableList<ManualFood>,
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<ManualFoodAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.cardRoot)
        val name: TextView = view.findViewById(R.id.tvFoodName)
        val calories: TextView = view.findViewById(R.id.tvCalories)
        val macros: TextView = view.findViewById(R.id.tvMacros)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manual_food, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val food = foods[position]

        holder.name.text = food.name
        holder.calories.text = "Calories: ${food.calories} kcal"
        holder.macros.text =
            "P:${food.protein}g  F:${food.fat}g  C:${food.carbs}g"

        holder.card.setCardBackgroundColor(
            if (food.isSelected) Color.LTGRAY else Color.WHITE
        )

        holder.itemView.setOnLongClickListener {
            toggleSelection(position)
            true
        }

        holder.itemView.setOnClickListener {
            if (hasSelection()) toggleSelection(position)
        }
    }

    private fun toggleSelection(position: Int) {
        foods[position].isSelected = !foods[position].isSelected
        notifyItemChanged(position)
        onSelectionChanged()
    }

    fun hasSelection(): Boolean = foods.any { it.isSelected }

    fun getSelectedItems(): List<ManualFood> =
        foods.filter { it.isSelected }

    fun clearSelection() {
        foods.forEach { it.isSelected = false }
        notifyDataSetChanged()
        onSelectionChanged()
    }

    override fun getItemCount(): Int = foods.size
}
