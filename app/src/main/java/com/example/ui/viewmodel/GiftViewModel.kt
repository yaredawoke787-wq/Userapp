package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.GiftProduct
import com.example.data.repository.GiftRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GiftViewModel(private val repository: GiftRepository) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Unfiltered raw products list for administrative management
    val allProducts: StateFlow<List<GiftProduct>> = repository.allProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Real-time reactive combination of products, selected category, and search query
    val filteredProducts: StateFlow<List<GiftProduct>> = combine(
        repository.allProducts,
        _selectedCategory,
        _searchQuery
    ) { products, category, query ->
        products.filter { product ->
            val matchesCategory = if (category == "All") true else product.category.equals(category, ignoreCase = true)
            val matchesQuery = if (query.isEmpty()) true else {
                product.title.contains(query, ignoreCase = true) ||
                product.subtitle.contains(query, ignoreCase = true) ||
                product.description.contains(query, ignoreCase = true)
            }
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Highlighted products (items with high rating >= 4.8) for carousel
    val heroProducts: StateFlow<List<GiftProduct>> = repository.allProducts
        .map { products -> products.filter { it.rating >= 4.8f } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteProducts: StateFlow<List<GiftProduct>> = repository.favoriteProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val cartProducts: StateFlow<List<GiftProduct>> = repository.cartProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Cart details calculations (Reactive)
    val cartSubtotal: StateFlow<Double> = repository.cartProducts
        .map { products -> products.sumOf { it.price * it.cartQuantity } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val selectedProduct = MutableStateFlow<GiftProduct?>(null)

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncStatus = MutableSharedFlow<Boolean>()
    val syncStatus: SharedFlow<Boolean> = _syncStatus.asSharedFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _currentLanguage = MutableStateFlow("EN")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    fun setDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(product: GiftProduct) {
        viewModelScope.launch {
            repository.toggleFavorite(product)
            // Synchronize selected product if open
            if (selectedProduct.value?.id == product.id) {
                selectedProduct.value = selectedProduct.value?.copy(isFavorite = !product.isFavorite)
            }
        }
    }

    fun addToCart(product: GiftProduct) {
        viewModelScope.launch {
            repository.addToCart(product)
            // Synchronize selected product if open
            if (selectedProduct.value?.id == product.id) {
                selectedProduct.value = selectedProduct.value?.copy(
                    isInCart = true,
                    cartQuantity = (selectedProduct.value?.cartQuantity ?: 0) + 1
                )
            }
        }
    }

    fun decreaseCartQuantity(product: GiftProduct) {
        viewModelScope.launch {
            repository.decreaseCartQuantity(product)
            if (selectedProduct.value?.id == product.id) {
                val currentQty = selectedProduct.value?.cartQuantity ?: 0
                selectedProduct.value = selectedProduct.value?.copy(
                    isInCart = currentQty > 1,
                    cartQuantity = if (currentQty > 1) currentQty - 1 else 0
                )
            }
        }
    }

    fun removeFromCart(product: GiftProduct) {
        viewModelScope.launch {
            repository.removeFromCart(product)
            if (selectedProduct.value?.id == product.id) {
                selectedProduct.value = selectedProduct.value?.copy(isInCart = false, cartQuantity = 0)
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun addProduct(context: android.content.Context, product: GiftProduct) {
        viewModelScope.launch {
            _isSyncing.value = true
            repository.addProduct(context, product)
            _isSyncing.value = false
        }
    }

    fun updateProduct(context: android.content.Context, product: GiftProduct) {
        viewModelScope.launch {
            _isSyncing.value = true
            repository.updateProduct(context, product)
            if (selectedProduct.value?.id == product.id) {
                selectedProduct.value = product
            }
            _isSyncing.value = false
        }
    }

    fun syncWithCloud(context: android.content.Context) {
        viewModelScope.launch {
            _isSyncing.value = true
            val success = repository.syncWithCloud(context)
            _isSyncing.value = false
            _syncStatus.emit(success)
        }
    }

    fun addProduct(product: GiftProduct) {
        viewModelScope.launch {
            repository.addProduct(product)
        }
    }

    fun updateProduct(product: GiftProduct) {
        viewModelScope.launch {
            repository.updateProduct(product)
            if (selectedProduct.value?.id == product.id) {
                selectedProduct.value = product
            }
        }
    }

    fun selectProductById(productId: Int) {
        viewModelScope.launch {
            repository.getProductById(productId).collect { product ->
                selectedProduct.value = product
            }
        }
    }
}

class GiftViewModelFactory(private val repository: GiftRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GiftViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GiftViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
