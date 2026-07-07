package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gift_products")
data class GiftProduct(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subtitle: String,
    val price: Double,
    val rating: Float,
    val description: String,
    val imageResId: Int,
    val category: String,
    val isFavorite: Boolean = false,
    val isInCart: Boolean = false,
    val cartQuantity: Int = 0,
    val videoUrl: String? = null,
    val imageUrl: String? = null
)
