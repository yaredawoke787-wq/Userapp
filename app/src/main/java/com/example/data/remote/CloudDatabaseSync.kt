package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.data.model.GiftProduct
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

object CloudDatabaseSync {
    private const val TAG = "CloudDatabaseSync"
    
    // Default shared public sandbox Firebase Database URL so it works immediately out-of-the-box!
    const val DEFAULT_FIREBASE_URL = "https://teke-man-promotion-default-rtdb.europe-west1.firebasedatabase.app/"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val listType = Types.newParameterizedType(List::class.java, GiftProduct::class.java)
    private val productListAdapter = moshi.adapter<List<GiftProduct>>(listType)

    fun getFirebaseUrl(context: Context): String {
        val prefs = context.getSharedPreferences("teke_admin_prefs", Context.MODE_PRIVATE)
        var url = prefs.getString("firebase_db_url", DEFAULT_FIREBASE_URL) ?: DEFAULT_FIREBASE_URL
        if (url.trim().isEmpty()) {
            url = DEFAULT_FIREBASE_URL
        }
        if (url.isNotEmpty() && !url.endsWith("/")) {
            url += "/"
        }
        return url
    }

    fun fetchProducts(context: Context): List<GiftProduct>? {
        val baseUrl = getFirebaseUrl(context)
        if (baseUrl.isEmpty()) return null
        val requestUrl = "${baseUrl}products.json"
        
        Log.d(TAG, "Fetching products from: $requestUrl")
        val request = Request.Builder()
            .url(requestUrl)
            .get()
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "HTTP error: ${response.code} ${response.message}")
                    return null
                }
                val bodyString = response.body?.string() ?: return null
                Log.d(TAG, "Fetch body: $bodyString")
                if (bodyString == "null") {
                    return emptyList()
                }
                return try {
                    productListAdapter.fromJson(bodyString)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed list parsing, trying Map fallback", e)
                    parseFromMap(bodyString)
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network exception fetching products", e)
            return null
        }
    }

    private fun parseFromMap(json: String): List<GiftProduct>? {
        return try {
            val mapType = Types.newParameterizedType(Map::class.java, String::class.java, GiftProduct::class.java)
            val adapter = moshi.adapter<Map<String, GiftProduct>>(mapType)
            val map = adapter.fromJson(json)
            map?.values?.toList()?.sortedBy { it.id }
        } catch (e: Exception) {
            Log.e(TAG, "Failed Map parsing fallback", e)
            null
        }
    }

    fun pushProducts(context: Context, products: List<GiftProduct>): Boolean {
        val baseUrl = getFirebaseUrl(context)
        if (baseUrl.isEmpty()) return false
        val requestUrl = "${baseUrl}products.json"

        Log.d(TAG, "Pushing products to: $requestUrl")
        val json = productListAdapter.toJson(products)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        
        val request = Request.Builder()
            .url(requestUrl)
            .put(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "HTTP error pushing: ${response.code} ${response.message}")
                    return false
                }
                Log.d(TAG, "Successfully pushed products to cloud database.")
                return true
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network exception pushing products", e)
            return false
        }
    }
}
