package com.example.finallyy

data class SavedProduct(
    var key: String? = null,
    var productName: String = "",
    var brand: String = "",
    var calories: String = "",
    var fat: String = "",
    var sugars: String = "",
    var proteins: String = "",
    var sodium: String = "",
    var nutriScore: String = "",
    var isSelected: Boolean = false
)
