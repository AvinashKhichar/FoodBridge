package com.example.foodbridge.data

import com.google.firebase.Timestamp

data class Product (
    val id: String,
    val name: String,
    val category: String,
    val date: String,
    val description: String? = null,
    val images: List<String>,
    val location: Map<String, Any?>? = null,
    val timestamp: Timestamp? = null
) {
    constructor() : this("0", "", "", "", images = emptyList())
}
