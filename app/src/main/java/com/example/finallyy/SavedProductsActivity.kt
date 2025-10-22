package com.example.finallyy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SavedProductsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SavedProductsAdapter
    private val savedProductsList = mutableListOf<SavedProduct>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_products)

        recyclerView = findViewById(R.id.recyclerViewSaved)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SavedProductsAdapter(savedProductsList)
        recyclerView.adapter = adapter

        fetchSavedProducts()
    }

    private fun fetchSavedProducts() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) return

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(userId)
            .collection("savedProducts")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { documents ->
                savedProductsList.clear()
                for (doc in documents) {
                    val product = SavedProduct(
                        productName = doc.getString("productName") ?: "--",
                        brand = doc.getString("brand") ?: "--",
                        calories = doc.getString("calories") ?: "--",
                        fat = doc.getString("fat") ?: "--",
                        sugars = doc.getString("sugars") ?: "--",
                        proteins = doc.getString("proteins") ?: "--",
                        sodium = doc.getString("sodium") ?: "--",
                        nutriScore = doc.getString("nutriScore") ?: "--"
                    )
                    savedProductsList.add(product)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("SavedProducts", "Failed to fetch saved products", e)
            }
    }
}

data class SavedProduct(
    val productName: String,
    val brand: String,
    val calories: String,
    val fat: String,
    val sugars: String,
    val proteins: String,
    val sodium: String,
    val nutriScore: String
)

class SavedProductsAdapter(private val products: List<SavedProduct>) :
    RecyclerView.Adapter<SavedProductsAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvSavedProductName)
        val tvBrand: TextView = view.findViewById(R.id.tvSavedBrand)
        val tvCalories: TextView = view.findViewById(R.id.tvSavedCalories)
        val tvFat: TextView = view.findViewById(R.id.tvSavedFat)
        val tvSugars: TextView = view.findViewById(R.id.tvSavedSugars)
        val tvProteins: TextView = view.findViewById(R.id.tvSavedProteins)
        val tvSodium: TextView = view.findViewById(R.id.tvSavedSodium)
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
        holder.tvFat.text = product.fat
        holder.tvSugars.text = product.sugars
        holder.tvProteins.text = product.proteins
        holder.tvSodium.text = product.sodium
        holder.tvNutriScore.text = product.nutriScore
    }

    override fun getItemCount(): Int = products.size
}
