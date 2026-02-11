package com.example.finallyy

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HealthSummaryActivity : AppCompatActivity() {

    private lateinit var tvTotalProducts: TextView
    private lateinit var tvTotalCalories: TextView
    private lateinit var tvAverageScore: TextView
    private lateinit var tvHealthRating: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_summary)

        tvTotalProducts = findViewById(R.id.tvTotalProducts)
        tvTotalCalories = findViewById(R.id.tvTotalCalories)
        tvAverageScore = findViewById(R.id.tvAverageScore)
        tvHealthRating = findViewById(R.id.tvHealthRating)

        calculateHealthSummary()
    }

    private fun calculateHealthSummary() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val database = FirebaseDatabase.getInstance()

        var totalProducts = 0
        var totalCalories = 0.0
        var totalProtein = 0.0
        var totalFat = 0.0
        var totalCarbs = 0.0
        var scoreSum = 0

        // 🔹 1️⃣ Fetch saved packaged products
        val savedRef = database.getReference("users")
            .child(userId)
            .child("savedProducts")

        savedRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (child in snapshot.children) {
                    val product = child.getValue(SavedProduct::class.java)
                    if (product != null) {
                        totalProducts++

                        val caloriesNumber =
                            product.calories.replace("[^0-9.]".toRegex(), "")
                                .toDoubleOrNull() ?: 0.0

                        totalCalories += caloriesNumber
                        scoreSum += scoreToNumber(product.nutriScore)
                    }
                }

                // 🔹 2️⃣ Fetch today's manual logs
                val date = java.text.SimpleDateFormat("yyyy-MM-dd",
                    java.util.Locale.getDefault()).format(java.util.Date())

                val manualRef = database.getReference("users")
                    .child(userId)
                    .child("dailyLogs")
                    .child(date)
                    .child("foods")

                manualRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot2: DataSnapshot) {

                        for (child in snapshot2.children) {
                            val calories =
                                child.child("calories").getValue(Double::class.java) ?: 0.0
                            val protein =
                                child.child("protein").getValue(Double::class.java) ?: 0.0
                            val fat =
                                child.child("fat").getValue(Double::class.java) ?: 0.0
                            val carbs =
                                child.child("carbs").getValue(Double::class.java) ?: 0.0

                            totalCalories += calories
                            totalProtein += protein
                            totalFat += fat
                            totalCarbs += carbs
                        }

                        val averageScore =
                            if (totalProducts > 0)
                                scoreSum.toDouble() / totalProducts
                            else 0.0

                        val avgScoreLetter = numberToScore(averageScore)

                        tvTotalProducts.text = "Total Packaged Products: $totalProducts"
                        tvTotalCalories.text =
                            "Total Calories: %.2f kcal".format(totalCalories)
                        tvAverageScore.text =
                            "Average NutriScore: $avgScoreLetter"
                        tvHealthRating.text =
                            "Health Rating: ${healthLabel(avgScoreLetter)}"

                        // You can add these if you want
                        // tvProtein.text = "Protein: %.2f g".format(totalProtein)
                        // tvFat.text = "Fat: %.2f g".format(totalFat)
                        // tvCarbs.text = "Carbs: %.2f g".format(totalCarbs)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    private fun scoreToNumber(score: String): Int {
        return when (score.uppercase()) {
            "A" -> 5
            "B" -> 4
            "C" -> 3
            "D" -> 2
            "E" -> 1
            else -> 0
        }
    }

    private fun numberToScore(value: Double): String {
        return when {
            value >= 4.5 -> "A"
            value >= 3.5 -> "B"
            value >= 2.5 -> "C"
            value >= 1.5 -> "D"
            else -> "E"
        }
    }

    private fun healthLabel(score: String): String {
        return when (score) {
            "A" -> "Excellent 🔥"
            "B" -> "Very Good 💪"
            "C" -> "Moderate ⚖️"
            "D" -> "Needs Improvement ⚠️"
            "E" -> "Poor ❌"
            else -> "--"
        }
    }
}
