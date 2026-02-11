package com.example.finallyy

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AddManualFoodActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_manual_food)

        val etFoodName = findViewById<EditText>(R.id.etFoodName)
        val etCalories = findViewById<EditText>(R.id.etCalories)
        val etProtein = findViewById<EditText>(R.id.etProtein)
        val etFat = findViewById<EditText>(R.id.etFat)
        val etCarbs = findViewById<EditText>(R.id.etCarbs)
        val btnSave = findViewById<Button>(R.id.btnSaveFood)

        btnSave.setOnClickListener {

            val foodName = etFoodName.text.toString()
            val calories = etCalories.text.toString().toDoubleOrNull() ?: 0.0
            val protein = etProtein.text.toString().toDoubleOrNull() ?: 0.0
            val fat = etFat.text.toString().toDoubleOrNull() ?: 0.0
            val carbs = etCarbs.text.toString().toDoubleOrNull() ?: 0.0

            if (foodName.isEmpty()) {
                Toast.makeText(this, "Enter food name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveFood(foodName, calories, protein, fat, carbs)
        }
    }

    private fun saveFood(
        name: String,
        calories: Double,
        protein: Double,
        fat: Double,
        carbs: Double
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Date())

        val foodData = mapOf(
            "name" to name,
            "calories" to calories,
            "protein" to protein,
            "fat" to fat,
            "carbs" to carbs,
            "timestamp" to System.currentTimeMillis()
        )

        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("dailyLogs")
            .child(date)
            .child("foods")
            .push()
            .setValue(foodData)
            .addOnSuccessListener {
                Toast.makeText(this, "Food added!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
            }
    }
}
