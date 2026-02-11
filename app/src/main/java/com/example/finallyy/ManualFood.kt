package com.example.finallyy

data class ManualFood(
    var key: String? = null,   // ✅ THIS WAS MISSING
    var name: String = "",
    var calories: Double = 0.0,
    var protein: Double = 0.0,
    var fat: Double = 0.0,
    var carbs: Double = 0.0,
    var isSelected: Boolean = false
)
