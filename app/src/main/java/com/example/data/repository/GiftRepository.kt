package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.local.GiftDao
import com.example.data.model.GiftProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class GiftRepository(private val giftDao: GiftDao) {

    val allProducts: Flow<List<GiftProduct>> = giftDao.getAllProducts()
    val favoriteProducts: Flow<List<GiftProduct>> = giftDao.getFavoriteProducts()
    val cartProducts: Flow<List<GiftProduct>> = giftDao.getCartProducts()

    fun getProductsByCategory(category: String): Flow<List<GiftProduct>> {
        return giftDao.getProductsByCategory(category)
    }

    fun getProductById(productId: Int): Flow<GiftProduct?> {
        return giftDao.getProductById(productId)
    }

    init {
        // Automatically pre-populate default luxury items on startup if DB is empty
        CoroutineScope(Dispatchers.IO).launch {
            if (giftDao.getProductCount() == 0) {
                giftDao.insertProducts(defaultProducts)
            }
        }
    }

    suspend fun toggleFavorite(product: GiftProduct) {
        giftDao.updateProduct(product.copy(isFavorite = !product.isFavorite))
    }

    suspend fun addToCart(product: GiftProduct) {
        val qty = if (product.isInCart) product.cartQuantity + 1 else 1
        giftDao.updateProduct(product.copy(isInCart = true, cartQuantity = qty))
    }

    suspend fun decreaseCartQuantity(product: GiftProduct) {
        if (product.cartQuantity <= 1) {
            giftDao.updateProduct(product.copy(isInCart = false, cartQuantity = 0))
        } else {
            giftDao.updateProduct(product.copy(cartQuantity = product.cartQuantity - 1))
        }
    }

    suspend fun removeFromCart(product: GiftProduct) {
        giftDao.updateProduct(product.copy(isInCart = false, cartQuantity = 0))
    }

    suspend fun clearCart() {
        giftDao.clearCart()
    }

    suspend fun syncWithCloud(context: Context): Boolean {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                val cloudProducts = com.example.data.remote.CloudDatabaseSync.fetchProducts(context)
                if (cloudProducts != null) {
                    val localProducts = giftDao.getAllProductsDirect()
                    val localStates = localProducts.associateBy { it.id }
                    
                    val productsToInsert = cloudProducts.map { cloudProduct ->
                        val local = localStates[cloudProduct.id]
                        if (local != null) {
                            cloudProduct.copy(
                                isFavorite = local.isFavorite,
                                isInCart = local.isInCart,
                                cartQuantity = local.cartQuantity
                            )
                        } else {
                            cloudProduct
                        }
                    }
                    
                    if (productsToInsert.isNotEmpty()) {
                        val cloudIds = productsToInsert.map { it.id }.toSet()
                        val itemsToDelete = localProducts.filter { it.id !in cloudIds }
                        
                        giftDao.insertProducts(productsToInsert)
                        if (itemsToDelete.isNotEmpty()) {
                            giftDao.deleteProducts(itemsToDelete)
                        }
                    }
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                android.util.Log.e("GiftRepository", "Failed to sync with cloud", e)
                false
            }
        }
    }

    suspend fun pushLocalToCloud(context: Context): Boolean {
        return kotlinx.coroutines.withContext(Dispatchers.IO) {
            try {
                val localProducts = giftDao.getAllProductsDirect()
                com.example.data.remote.CloudDatabaseSync.pushProducts(context, localProducts)
            } catch (e: Exception) {
                android.util.Log.e("GiftRepository", "Failed to push local to cloud", e)
                false
            }
        }
    }

    suspend fun addProduct(context: Context, product: GiftProduct) {
        // Since we are adding, let's find the max ID locally first to assign a unique sequential ID
        val localProducts = giftDao.getAllProductsDirect()
        val nextId = (localProducts.maxOfOrNull { it.id } ?: 0) + 1
        val assignedProduct = product.copy(id = nextId)
        
        giftDao.insertProducts(listOf(assignedProduct))
        pushLocalToCloud(context)
    }

    suspend fun updateProduct(context: Context, product: GiftProduct) {
        giftDao.updateProduct(product)
        pushLocalToCloud(context)
    }

    suspend fun addProduct(product: GiftProduct) {
        giftDao.insertProducts(listOf(product))
    }

    suspend fun updateProduct(product: GiftProduct) {
        giftDao.updateProduct(product)
    }

    companion object {
        private val defaultProducts = listOf(
            GiftProduct(
                id = 1,
                title = "Imperial Gold Crown Watch Box",
                subtitle = "Limited Edition Collector's Piece",
                price = 12500.00,
                rating = 4.9f,
                description = "An absolute masterpiece of craftsmanship. Handcrafted with gold leaf accents and deep black velvet interior lining. A royal gift that symbolizes timeless success, perfect for high-profile business partners or wedding anniversaries.",
                imageResId = R.drawable.img_luxury_box_1783249841023,
                category = "Luxury",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
            ),
            GiftProduct(
                id = 2,
                title = "Royal Crystal Champagne Flutes",
                subtitle = "Matrimonial Gold Plated Toasting Set",
                price = 6200.00,
                rating = 4.9f,
                description = "Indulge in pure matrimonial romance. Features hand-blown lead-free crystal flutes with custom gold-plated handles, perfectly placed within a high-end silk keepsake carrier box. Designed to capture timeless memories.",
                imageResId = R.drawable.img_wedding_gift_1783249854737,
                category = "Wedding",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
            ),
            GiftProduct(
                id = 3,
                title = "Rose Gold Surprise Package",
                subtitle = "Bespoke Sweet & Sparkle Collection",
                price = 4500.00,
                rating = 4.8f,
                description = "Celebrate in luxurious elegance. Includes handcrafted gold-dusted artisan chocolate truffles, a premium soy scented candle in a gold metal jar, and a custom laser-engraved maple wood greeting card.",
                imageResId = R.drawable.img_birthday_gift_1783249866279,
                category = "Birthday",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"
            ),
            GiftProduct(
                id = 4,
                title = "Academic Executive Portfolio Set",
                subtitle = "Full-Grain Leather & Gold Nib Pen",
                price = 7400.00,
                rating = 4.9f,
                description = "Commemorate their monumental academic milestones. Pairing a heavy brass gold-nibbed fountain pen with a premium full-grain double-layered leather portfolio for certificates and digital tablets.",
                imageResId = R.drawable.img_graduation_gift_1783249879106,
                category = "Graduation",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"
            ),
            GiftProduct(
                id = 5,
                title = "Executive Obsidian Work Companion",
                subtitle = "Matte Black Journal & Brass Card Holder",
                price = 9500.00,
                rating = 4.9f,
                description = "For modern visionaries and executive leadership. Includes our flagship minimalist matte black journal, solid brass weighted gel pen, and a matching double-sided high-polish card safe.",
                imageResId = R.drawable.img_business_gift_1783249891507,
                category = "Business",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4"
            ),
            GiftProduct(
                id = 6,
                title = "Golden Bloom Pearl Necklace Set",
                subtitle = "Akoya Pearls with 18K Gold Ribbon",
                price = 22000.00,
                rating = 4.8f,
                description = "The ultimate jewelry statement of elegance. Fine-grade Akoya white pearls matched seamlessly with an exquisite 18k golden ribbon clasp. Comes inside our hand-polished black lacquer wooden chest.",
                imageResId = R.drawable.img_luxury_box_1783249841023,
                category = "Luxury"
            ),
            GiftProduct(
                id = 7,
                title = "Golden Blossom Embroidered Quilt",
                subtitle = "1000TC Royal Egyptian Cotton",
                price = 14200.00,
                rating = 4.7f,
                description = "The quintessential premium wedding keepsake. Created with extremely high-density, pure Egyptian cotton threads adorned with intricate silk gold floral stitching, making every sleep feel like a five-star hotel.",
                imageResId = R.drawable.img_wedding_gift_1783249854737,
                category = "Wedding"
            ),
            GiftProduct(
                id = 8,
                title = "Celestial Solara 3D Projection Sphere",
                subtitle = "Laser-Etched Pure Quartz on Brass Stand",
                price = 3800.00,
                rating = 4.7f,
                description = "Bring the magic of the cosmos indoors. A flawless, high-purity quartz crystal sphere beautifully laser-etched with high-resolution astronomical detail. It rests upon a premium gold-brushed metal base with halo light ring.",
                imageResId = R.drawable.img_birthday_gift_1783249866279,
                category = "Birthday"
            ),
            GiftProduct(
                id = 9,
                title = "Vintage Maritime Hourglass Desk Sculpture",
                subtitle = "Fine Sand & Nautical Brass Clockwork",
                price = 5100.00,
                rating = 4.6f,
                description = "A gorgeous scholarly keepsake. Constructed with fine heavy-weight nautical marine brass framing and pristine white sand. Symbolizes graduation achievements and the infinite paths of success.",
                imageResId = R.drawable.img_graduation_gift_1783249879106,
                category = "Graduation"
            ),
            GiftProduct(
                id = 10,
                title = "Prestige Spanish Cedar Desk Humidor",
                subtitle = "Glossy Piano Finish & Digital Humidistat",
                price = 11800.00,
                rating = 4.8f,
                description = "Make an unforgettable corporate statement. Handcrafted in aromatic Spanish cedar wood with high-security magnetic double locks, a sleek digital hygrometer, and a premium glossy mirror-polished outer finish.",
                imageResId = R.drawable.img_business_gift_1783249891507,
                category = "Business"
            )
        )
    }
}
