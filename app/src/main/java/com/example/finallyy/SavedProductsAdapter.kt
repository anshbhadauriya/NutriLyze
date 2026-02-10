package com.example.finallyy

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class SavedProductsAdapter(
    private val products: MutableList<SavedProduct>,
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<SavedProductsAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.cardRoot)
        val tvName: TextView = view.findViewById(R.id.tvSavedProductName)
        val tvBrand: TextView = view.findViewById(R.id.tvSavedBrand)
        val tvCalories: TextView = view.findViewById(R.id.tvSavedCalories)
        val tvNutriScore: TextView = view.findViewById(R.id.tvSavedNutriScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        holder.tvName.text = product.productName
        holder.tvBrand.text = product.brand
        holder.tvCalories.text = product.calories
        holder.tvNutriScore.text = product.nutriScore

        holder.card.setCardBackgroundColor(
            if (product.isSelected) Color.LTGRAY else Color.WHITE
        )

        holder.itemView.setOnLongClickListener {
            toggleSelection(position)
            true
        }

        holder.itemView.setOnClickListener {
            if (hasSelection()) {
                toggleSelection(position)
            }
        }
    }

    private fun toggleSelection(position: Int) {
        products[position].isSelected = !products[position].isSelected
        notifyItemChanged(position)
        onSelectionChanged()
    }

    fun hasSelection(): Boolean =
        products.any { it.isSelected }

    fun getSelectedItems(): List<SavedProduct> =
        products.filter { it.isSelected }

    fun clearSelection() {
        products.forEach { it.isSelected = false }
        notifyDataSetChanged()
        onSelectionChanged()
    }

    override fun getItemCount(): Int = products.size
}
