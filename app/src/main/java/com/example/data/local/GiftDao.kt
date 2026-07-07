package com.example.data.local

import androidx.room.*
import com.example.data.model.GiftProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface GiftDao {
    @Query("SELECT * FROM gift_products ORDER BY id ASC")
    fun getAllProducts(): Flow<List<GiftProduct>>

    @Query("SELECT * FROM gift_products WHERE category = :category ORDER BY id ASC")
    fun getProductsByCategory(category: String): Flow<List<GiftProduct>>

    @Query("SELECT * FROM gift_products WHERE isFavorite = 1")
    fun getFavoriteProducts(): Flow<List<GiftProduct>>

    @Query("SELECT * FROM gift_products WHERE isInCart = 1")
    fun getCartProducts(): Flow<List<GiftProduct>>

    @Query("SELECT * FROM gift_products WHERE id = :productId")
    fun getProductById(productId: Int): Flow<GiftProduct?>

    @Query("SELECT COUNT(*) FROM gift_products")
    suspend fun getProductCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<GiftProduct>)

    @Update
    suspend fun updateProduct(product: GiftProduct)

    @Query("SELECT * FROM gift_products ORDER BY id ASC")
    suspend fun getAllProductsDirect(): List<GiftProduct>

    @Delete
    suspend fun deleteProducts(products: List<GiftProduct>)

    @Query("UPDATE gift_products SET isInCart = 0, cartQuantity = 0")
    suspend fun clearCart()
}
