package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GiftProduct
import com.example.ui.theme.*
import com.example.ui.viewmodel.GiftViewModel
import kotlinx.coroutines.launch

@Composable
fun ContactScreen(
    viewModel: GiftViewModel,
    onProductClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val products by viewModel.filteredProducts.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val cartProducts by viewModel.cartProducts.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isDarkTheme = true
    val coroutineScope = rememberCoroutineScope()

    val categories = listOf(
        com.example.ui.localization.TekeLocalization.getString("cat_all", currentLanguage),
        com.example.ui.localization.TekeLocalization.getString("cat_watch", currentLanguage),
        com.example.ui.localization.TekeLocalization.getString("cat_chocolate", currentLanguage),
        com.example.ui.localization.TekeLocalization.getString("cat_shine_board", currentLanguage),
        com.example.ui.localization.TekeLocalization.getString("cat_photo_glob", currentLanguage),
        com.example.ui.localization.TekeLocalization.getString("cat_mag", currentLanguage)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Aesthetic light aura matching the active primary gold shade
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .blur(90.dp)
                .alpha(if (isDarkTheme) 0.12f else 0.05f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary, Color.Transparent)
                    )
                )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 110.dp)
        ) {
            // --- HEADER ---
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "EXCLUSIVE SELECTION",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Bespoke Gift Studio",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "Browse our luxury collection categories below",
                        color = WarmGray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // --- CATEGORIES HORIZONTAL CHIPS ---
            item {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(categories) { category ->
                            val categoryIndex = categories.indexOf(category)
                            val active = when (categoryIndex) {
                                0 -> selectedCategory == "All"
                                1 -> selectedCategory == "Luxury"
                                2 -> selectedCategory == "Business"
                                3 -> selectedCategory == "Wedding"
                                4 -> selectedCategory == "Graduation"
                                5 -> selectedCategory == "Birthday"
                                else -> false
                            }

                            val bgColor = if (active) Color(0xFF2A2000) else Color(0xFF16161A)
                            val borderModifier = if (active) {
                                Modifier.border(1.dp, Color(0xFFF2B705), RoundedCornerShape(20.dp))
                            } else {
                                Modifier.border(0.5.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(bgColor)
                                    .then(borderModifier)
                                    .clickable {
                                        val targetCategory = when (categoryIndex) {
                                            0 -> "All"
                                            1 -> "Luxury"
                                            2 -> "Business"
                                            3 -> "Wedding"
                                            4 -> "Graduation"
                                            5 -> "Birthday"
                                            else -> "All"
                                        }
                                        viewModel.selectCategory(targetCategory)
                                    }
                                    .padding(horizontal = 18.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = category,
                                    color = if (active) Color(0xFFF2B705) else Color(0xFF88888C),
                                    fontSize = 12.sp,
                                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }

            // --- PRODUCT COUNT ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FILTERED MASTERPIECES",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${products.size} Products",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // --- 2-COLUMN PRODUCTS GRID ---
            if (products.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Inbox,
                                contentDescription = "No products found",
                                tint = WarmGray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No bespoke products match this category",
                                color = WarmGray,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Left Column (Even Indexes)
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            val leftProducts = products.filterIndexed { idx, _ -> idx % 2 == 0 }
                            leftProducts.forEach { product ->
                                CategoryProductCard(
                                    product = product,
                                    aspectRatio = 0.85f,
                                    onProductClick = { onProductClick(product.id) },
                                    onFavoriteClick = { viewModel.toggleFavorite(product) },
                                    onAddToCartClick = { viewModel.addToCart(product) }
                                )
                            }
                        }

                        // Right Column (Odd Indexes)
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            val rightProducts = products.filterIndexed { idx, _ -> idx % 2 != 0 }
                            rightProducts.forEach { product ->
                                CategoryProductCard(
                                    product = product,
                                    aspectRatio = 1.15f,
                                    onProductClick = { onProductClick(product.id) },
                                    onFavoriteClick = { viewModel.toggleFavorite(product) },
                                    onAddToCartClick = { viewModel.addToCart(product) }
                                )
                            }
                        }
                    }
                }
            }

            // --- BESPOKE CONCIERGE DIVIDER ---
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Divider(color = Color.White.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "DIRECT CONCIERGE",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Arrange Custom Gifts",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "Need personal arrangements, corporate deals, or custom wax seals?",
                        color = WarmGray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // --- CONCIERGE CONTACT CARDS ---
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ContactCard(
                        title = "Primary Concierge Line",
                        subtitle = "Click to Call Direct",
                        value = "+251 911 518 012",
                        icon = Icons.Default.Call,
                        isDarkTheme = isDarkTheme,
                        onCardClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+251911518012"))
                            context.startActivity(intent)
                        }
                    )

                    ContactCard(
                        title = "Secondary Concierge Line",
                        subtitle = "Click to Call Direct",
                        value = "+251 983 838 309",
                        icon = Icons.Default.Call,
                        isDarkTheme = isDarkTheme,
                        onCardClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+251983838309"))
                            context.startActivity(intent)
                        }
                    )

                    ContactCard(
                        title = "Official Telegram Channel",
                        subtitle = "Join our luxury gift catalogue",
                        value = "@Teke_Man_Promotion",
                        icon = Icons.Default.Send,
                        isDarkTheme = isDarkTheme,
                        onCardClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Teke_Man_Promotion"))
                            context.startActivity(intent)
                        }
                    )

                    ContactCard(
                        title = "Bespoke TikTok Account",
                        subtitle = "Watch cinematic gift reviews",
                        value = "@teke_man_promotion",
                        icon = Icons.Default.VideoLibrary,
                        isDarkTheme = isDarkTheme,
                        onCardClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@teke_man_promotion"))
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryProductCard(
    product: GiftProduct,
    aspectRatio: Float = 1.0f,
    onProductClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onAddToCartClick: () -> Unit
) {
    val isDarkTheme = true

    // Dynamic interactive hover scale
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "CardHover"
    )

    // Dynamic ambient glow color based on the product name/category
    val glowColor = remember(product) {
        val title = product.title.lowercase()
        when {
            title.contains("rose") || title.contains("red") || title.contains("love") || title.contains("bloom") -> Color(0xFF4A0010) // burgundy
            title.contains("gold") || title.contains("watch") || title.contains("royal") || title.contains("honey") || title.contains("imperial") -> Color(0xFF3A2A00) // gold/amber
            title.contains("chocolate") || title.contains("coffee") || title.contains("cake") || title.contains("sweet") -> Color(0xFF2E1A05) // cacao
            else -> Color(0xFF0F1B3F) // royal sapphire blue
        }
    }

    // Determine if there is a discount badge
    val discountText = remember(product.id) {
        when (product.id % 3) {
            1 -> "-10%"
            2 -> "-15%"
            else -> null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = 0.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false
            )
            .border(
                width = 0.5.dp,
                color = Color(0xFF333333),
                shape = RoundedCornerShape(24.dp)
            )
            .drawBehind {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(glowColor.copy(alpha = 0.45f), Color(0xFF16161A)),
                        radius = size.width * 1.3f
                    )
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onProductClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF16161A)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Suspended Product Image Frame with aspect ratio and Quick Favorite Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White.copy(alpha = 0.05f))
            ) {
                com.example.ui.components.ProductImage(
                    product = product,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Translucent Overlay at the top for Category Badge & Favorite
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color.Black.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = product.category.uppercase(),
                                color = Color(0xFFF2B705),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        // Luxury discount badge in Soft Coral/Red (#E57373)
                        if (discountText != null) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFFE57373),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = discountText,
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                    
                    // Direct Favorite toggle
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (product.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (product.isFavorite) Color(0xFFE57373) else Color(0xFFF2B705),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Info Details Panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Text(
                    text = product.title,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = product.subtitle,
                    color = WarmGray,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ETB ${product.price}",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        // Rating indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating star",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = product.rating.toString(),
                                color = WarmGray,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Suspended Gold Add to Cart FAB Inside Card
                    IconButton(
                        onClick = onAddToCartClick,
                        modifier = Modifier
                            .size(36.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = CircleShape,
                                clip = false,
                                ambientColor = GoldAccent,
                                spotColor = GoldAccent
                            )
                            .background(
                                Brush.horizontalGradient(
                                    AccentGoldGradient
                                ),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Add to Cart",
                            tint = SoftBlack,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContactCard(
    title: String,
    subtitle: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDarkTheme: Boolean,
    onCardClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "ContactCardPress"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = if (isDarkTheme) 0.dp else 4.dp,
                shape = RoundedCornerShape(20.dp),
                clip = false
            )
            .border(
                width = 1.dp,
                color = if (isDarkTheme) CardBorderColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onCardClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) PremiumGray.copy(alpha = 0.4f) else Color.White
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Frame
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkTheme) 0.15f else 0.08f),
                        shape = CircleShape
                    )
                    .border(0.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = value,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = subtitle,
                    color = WarmGray,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
