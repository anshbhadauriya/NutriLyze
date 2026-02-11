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

        val ref = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("savedProducts")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var totalProducts = 0
                var totalCalories = 0.0
                var scoreSum = 0

                for (child in snapshot.children) {
                    val product = child.getValue(SavedProduct::class.java)
                    if (product != null) {

                        totalProducts++

                        // Extract calories number
                        val caloriesNumber =
                            product.calories.replace("[^0-9.]".toRegex(), "").toDoubleOrNull() ?: 0.0

                        totalCalories += caloriesNumber

                        scoreSum += scoreToNumber(product.nutriScore)
                    }
                }

                val averageScore = if (totalProducts > 0)
                    scoreSum.toDouble() / totalProducts
                else 0.0

                val avgScoreLetter = numberToScore(averageScore)

                tvTotalProducts.text = "Total Products: $totalProducts"
                tvTotalCalories.text = "Total Calories: %.2f kcal".format(totalCalories)
                tvAverageScore.text = "Average NutriScore: $avgScoreLetter"
                tvHealthRating.text = "Health Rating: ${healthLabel(avgScoreLetter)}"
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
