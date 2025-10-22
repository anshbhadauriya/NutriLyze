package com.example.finallyy

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import okhttp3.*
import java.io.IOException

class MainActivity4 : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        val scannedId = intent.getStringExtra("SCANNED_ID")
        Log.d("MainActivity4", "Scanned barcode received: $scannedId")

        if (scannedId.isNullOrEmpty()) {
            findViewById<TextView>(R.id.tvProductName).text = "No barcode scanned"
        } else {
            val url = "https://world.openfoodfacts.org/api/v2/product/$scannedId.json"
            fetchProductData(url)
        }

        val btnSaveProduct = findViewById<Button>(R.id.btnSaveProduct)
        btnSaveProduct.setOnClickListener {
            saveProduct(
                productName = findViewById<TextView>(R.id.tvProductName).text.toString(),
                brand = findViewById<TextView>(R.id.tvBrand).text.toString(),
                calories = findViewById<TextView>(R.id.tvCalories).text.toString(),
                fat = findViewById<TextView>(R.id.tvFat).text.toString(),
                sugars = findViewById<TextView>(R.id.tvSugars).text.toString(),
                proteins = findViewById<TextView>(R.id.tvProteins).text.toString(),
                sodium = findViewById<TextView>(R.id.tvSodiumServing).text.toString(),
                nutriScore = findViewById<TextView>(R.id.tvNutriScore).text.toString()
            )
        }
    }

    private fun fetchProductData(url: String) {
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity4, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                    findViewById<TextView>(R.id.tvProductName).text = "Failed to fetch product data"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string()
                Log.d("MainActivity4", "Response JSON: $bodyString")

                if (bodyString.isNullOrEmpty()) {
                    runOnUiThread {
                        findViewById<TextView>(R.id.tvProductName).text = "Empty response"
                    }
                    return
                }

                try {
                    val gson = Gson()
                    val productResponse = gson.fromJson(bodyString, ProductResponse::class.java)
                    val product = productResponse.product

                    val productName = product?.product_name ?: "Unknown"
                    val brand = product?.brands ?: "Unknown"
                    var calories = product?.nutriments?.energy ?: 0.0
                    calories /= 4.184
                    val fat = product?.nutriments?.fat ?: 0.0
                    val sugars = product?.nutriments?.sugars ?: 0.0
                    val proteins = product?.nutriments?.proteins ?: 0.0
                    var sodiumServing = product?.nutriments?.sodium_serving ?: 0.0
                    sodiumServing *= 1000
                    val nutriScore = product?.nutrition_grades?.uppercase() ?: "--"

                    val nutriScoreExplanation = when (nutriScore) {
                        "A" -> "Best (सबसे अच्छा)"
                        "B" -> "Good (अच्छा)"
                        "C" -> "Average (मध्यम)"
                        "D" -> "Poor (खराब)"
                        "E" -> "Worst (सबसे खराब)"
                        else -> ""
                    }
                    val productImageUrl = product?.image_front_url

                    runOnUiThread {
                        val ivProductImage = findViewById<ImageView>(R.id.ivProductImage)
                        val tvLoading = findViewById<TextView>(R.id.tvLoading)

                        tvLoading.visibility = TextView.VISIBLE
                        Glide.with(this@MainActivity4)
                            .load(productImageUrl)
                            .into(ivProductImage)
                        tvLoading.visibility = TextView.GONE

                        findViewById<TextView>(R.id.tvProductName).text = productName
                        findViewById<TextView>(R.id.tvBrand).text = brand
                        findViewById<TextView>(R.id.tvCalories).text = "Calories: %.2f kcal".format(calories)
                        findViewById<TextView>(R.id.tvFat).text = "Fat: $fat g"
                        findViewById<TextView>(R.id.tvSugars).text = "Sugars: $sugars g"
                        findViewById<TextView>(R.id.tvProteins).text = "Proteins: $proteins g"
                        findViewById<TextView>(R.id.tvSodiumServing).text = "Sodium: $sodiumServing mg"
                        findViewById<TextView>(R.id.tvNutriScore).text = nutriScore
                        findViewById<TextView>(R.id.tvNutriScoreExplanation).text = nutriScoreExplanation
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        findViewById<TextView>(R.id.tvProductName).text = "Error parsing product data"
                        findViewById<TextView>(R.id.tvLoading).text = "No image found"
                    }
                }
            }
        })
    }

    private fun saveProduct(
        productName: String,
        brand: String,
        calories: String,
        fat: String,
        sugars: String,
        proteins: String,
        sodium: String,
        nutriScore: String
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "Please login to save products", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = user.uid
        val userEmail = user.email ?: "unknown_email"

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val firstName = sharedPref.getString("FirstName", "User") ?: "User"

        val productData = mapOf(
            "firstName" to firstName,
            "email" to userEmail,  // Add email here
            "productName" to productName,
            "brand" to brand,
            "calories" to calories,
            "fat" to fat,
            "sugars" to sugars,
            "proteins" to proteins,
            "sodium" to sodium,
            "nutriScore" to nutriScore,
            "timestamp" to System.currentTimeMillis()
        )

        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("users").child(userId).child("savedProducts")

        ref.push().setValue(productData)
            .addOnSuccessListener {
                Toast.makeText(this, "Product saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save product: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}

// Data classes for Gson
data class ProductResponse(val product: Product?)

data class Nutriments(
    val energy: Double?,
    val fat: Double?,
    val sugars: Double?,
    val proteins: Double?,
    val sodium_serving: Double?
)

data class Product(
    val product_name: String?,
    val brands: String?,
    val nutriments: Nutriments?,
    val nutrition_grades: String?,
    val image_front_url: String?
)
