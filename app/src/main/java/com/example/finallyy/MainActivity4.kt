package com.example.finallyy

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
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
                    calories /= 4.184 // Convert kJ to kcal
                    val fat = product?.nutriments?.fat ?: 0.0
                    val sugars = product?.nutriments?.sugars ?: 0.0
                    val proteins = product?.nutriments?.proteins ?: 0.0
                    var sodiumServing = product?.nutriments?.sodium_serving ?: 0.0
                    sodiumServing *= 1000 // Convert g to mg
                    val nutriScore = product?.nutrition_grades?.uppercase() ?: "--"

                    val nutriScoreExplanation = when (nutriScore) {
                        "A" -> "Best (सबसे अच्छा)"
                        "B" -> "Good (अच्छा)"
                        "C" -> "Average (मध्यम)"
                        "D" -> "Poor (खराब)"
                        "E" -> "Worst (सबसे खराब)"
                        else -> ""
                    }

                    runOnUiThread {
                        findViewById<TextView>(R.id.tvProductName).text = productName
                        findViewById<TextView>(R.id.tvBrand).text = brand
                        findViewById<TextView>(R.id.tvCalories).text = "Calories: %.2f kcal".format(calories)
                        findViewById<TextView>(R.id.tvFat).text = "Fat: $fat g"
                        findViewById<TextView>(R.id.tvSugars).text = "Sugars: $sugars g"
                        findViewById<TextView>(R.id.tvProteins).text = "Proteins: $proteins g"
                        findViewById<TextView>(R.id.tvNutriScore).text = nutriScore
                        findViewById<TextView>(R.id.tvSodiumServing).text = "Sodium: $sodiumServing mg"
                        findViewById<TextView>(R.id.tvNutriScoreExplanation).text = nutriScoreExplanation
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        findViewById<TextView>(R.id.tvProductName).text = "Error parsing product data"
                    }
                }
            }
        })
    }
}

data class ProductResponse(
    val product: Product?
)

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
    val nutrition_grades: String?
)
