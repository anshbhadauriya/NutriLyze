package com.example.finallyy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SavedProductsAdapter(
    private val products: List<SavedProduct>
) : RecyclerView.Adapter<SavedProductsAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
    }

    override fun getItemCount(): Int = products.size
}
