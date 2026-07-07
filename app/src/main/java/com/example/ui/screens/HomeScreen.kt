package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.GiftProduct
import com.example.ui.theme.*
import com.example.ui.viewmodel.GiftViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: GiftViewModel,
    onProductClick: (Int) -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    val context = LocalContext.current
    val products by viewModel.filteredProducts.collectAsState()
    val heroProducts by viewModel.heroProducts.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val cartProducts by viewModel.cartProducts.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var showBrandDialog by remember { mutableStateOf(false) }

    val categories = listOf(
        com.example.ui.localization.TekeLocalization.getString("cat_all", currentLanguage),
        com.example.ui.localization.TekeLocalization.getString("cat_watch", currentLanguage),
        com.example.ui.localization.TekeLocalization.getString("cat_chocolate", currentLanguage),
        com.example.ui.localization.TekeLocalization.getString("cat_shine_board", currentLanguage),
        com.example.ui.localization.TekeLocalization.getString("cat_photo_glob", currentLanguage),
        com.example.ui.localization.TekeLocalization.getString("cat_mag", currentLanguage)
    )

    // Determine Greeting based on current local time
    val greeting = remember {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        when (hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    val background = MaterialTheme.colorScheme.background
    val isDarkTheme = remember(background) {
        with(background) { (red * 0.299f + green * 0.587f + blue * 0.114f) <= 0.5f }
    }

    if (showBrandDialog) {
        AlertDialog(
            onDismissRequest = { showBrandDialog = false },
            confirmButton = {
                TextButton(
                    onClick = { showBrandDialog = false }
                ) {
                    Text(
                        text = com.example.ui.localization.TekeLocalization.getString("close", currentLanguage),
                        color = Color(0xFFF2B705)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showBrandDialog = false
                        onNavigateToOnboarding()
                    }
                ) {
                    Text(
                        text = com.example.ui.localization.TekeLocalization.getString("re_onboarding", currentLanguage),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .border(1.dp, Color(0xFFF2B705), CircleShape)
                            .clip(CircleShape)
                            .background(if (isDarkTheme) Color(0xFF16161A) else Color.White)
                            .padding(4.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://res.cloudinary.com/dnmgvjg3h/image/upload/v1783010583/IMG_20260702_192242_814_absnox.png")
                                .crossfade(true)
                                .build(),
                            contentDescription = "Teke Man Logo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Column {
                        Text(
                            text = com.example.ui.localization.TekeLocalization.getString("teke_man", currentLanguage).trim(),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = com.example.ui.localization.TekeLocalization.getString("premier_studio", currentLanguage),
                            color = Color(0xFFF2B705),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            text = {
                Text(
                    text = com.example.ui.localization.TekeLocalization.getString("brand_desc", currentLanguage),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Justify
                )
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = if (isDarkTheme) Color(0xFF222228) else Color.White,
            tonalElevation = 6.dp
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Decorative glowing background gradients for elite ambiance
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .blur(80.dp)
                .alpha(if (isDarkTheme) 0.12f else 0.05f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary, Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // --- TOP BAR SECTION ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .clickable { showBrandDialog = true }
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = com.example.ui.localization.TekeLocalization.getString("exclusive_studio", currentLanguage),
                            color = Color(0xFFF2B705),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = com.example.ui.localization.TekeLocalization.getString("teke_man", currentLanguage),
                                color = if (isDarkTheme) Color.White else Color(0xFF16161A),
                                fontSize = 21.6.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                            Text(
                                text = com.example.ui.localization.TekeLocalization.getString("promotion", currentLanguage),
                                color = Color(0xFFF2B705),
                                fontSize = 21.6.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }

                // Header Action Buttons + Circular gold-outlined profile logo with soft inner glow
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isSyncing by viewModel.isSyncing.collectAsState()
                    IconButton(
                        onClick = { 
                            viewModel.syncWithCloud(context)
                        },
                        modifier = Modifier
                            .background(if (isDarkTheme) Color.White.copy(alpha = 0.05f) else SleekLightGray, CircleShape)
                            .size(38.dp)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                color = Color(0xFFF2B705),
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Sync",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))

                    Box {
                        IconButton(
                            onClick = onNavigateToCart,
                            modifier = Modifier
                                .background(if (isDarkTheme) Color.White.copy(alpha = 0.05f) else SleekLightGray, CircleShape)
                                .size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingCart,
                                contentDescription = "Cart",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        if (cartProducts.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .align(Alignment.TopEnd)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                                    .border(1.dp, MaterialTheme.colorScheme.background, CircleShape)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    
                    // Circular, gold-outlined profile/star button with soft inner glow
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = CircleShape,
                                clip = false,
                                ambientColor = Color(0xFFF2B705),
                                spotColor = Color(0xFFF2B705)
                            )
                            .border(1.5.dp, Color(0xFFF2B705), CircleShape)
                            .clip(CircleShape)
                            .background(Color(0xFF16161A))
                            .clickable {
                                // Profile action
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Profile Star",
                            tint = Color(0xFFF2B705),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // --- MAIN LIST SCROLLABLE ---
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // --- SEARCH ARCHITECTURE ---
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(if (isDarkTheme) PremiumGray.copy(alpha = 0.6f) else SleekLightGray),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                unfocusedIndicatorColor = if (isDarkTheme) Color.White.copy(alpha = 0.1f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            placeholder = {
                                Text(
                                    com.example.ui.localization.TekeLocalization.getString("search_hint", currentLanguage),
                                    color = WarmGray,
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search icon",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = WarmGray
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(25.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(10.dp))
                        
                        // Detached pill-shaped microphone button for voice search
                        IconButton(
                            onClick = {
                                // Simulated premium voice recognition or direct microphone intent if needed
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .background(if (isDarkTheme) PremiumGray.copy(alpha = 0.6f) else SleekLightGray, CircleShape)
                                .border(1.dp, if (isDarkTheme) Color.White.copy(alpha = 0.1f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice search",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // --- PREMIUM HERO CAROUSEL ---
                if (heroProducts.isNotEmpty() && searchQuery.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 0.dp, bottom = 4.dp)
                        ) {
                            Text(
                                text = "EXCLUSIVE COLLECTIONS",
                                color = Color(0xFFF2B705),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 2.sp,
                                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 8.dp)
                            )
                            
                            val pagerState = rememberPagerState(pageCount = { heroProducts.size })
                            
                            // Smooth auto-slide transitions every 4 seconds
                            LaunchedEffect(key1 = pagerState) {
                                while (true) {
                                    delay(4000)
                                    val nextPage = (pagerState.currentPage + 1) % heroProducts.size
                                    pagerState.animateScrollToPage(nextPage)
                                }
                            }

                            // Banner Card Container (16dp horizontal margin)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(212.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    pageSpacing = 0.dp
                                ) { page ->
                                    val product = heroProducts[page]
                                    
                                    val customTag = when (page % 3) {
                                        0 -> "VIP ACCESS"
                                        1 -> "TRENDING"
                                        else -> "ELITE CHOICE"
                                    }
                                    val customTitle = when (page % 3) {
                                        0 -> "Exclusive Royal Hampers"
                                        1 -> "Imperial Gold Watch Box"
                                        else -> "Matrimonial Toasting Set"
                                    }
                                    val customSubtitle = when (page % 3) {
                                        0 -> "Indulge in Ethiopia's finest custom curations"
                                        1 -> "Bespoke velvet case with royal engraving"
                                        else -> "White-glove delivery, gold-foil cards"
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(26.dp))
                                            .border(0.5.dp, Color(0xFF333333), RoundedCornerShape(26.dp))
                                            .clickable { onProductClick(product.id) }
                                    ) {
                                        // Hero Image background (Elegant Cropping)
                                        com.example.ui.components.ProductImage(
                                            product = product,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        
                                        // Luxurious dark gradient overlay at the bottom for readability
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.verticalGradient(
                                                        colors = listOf(
                                                            Color.Transparent,
                                                            Color.Black.copy(alpha = 0.4f),
                                                            Color.Black.copy(alpha = 0.95f)
                                                        ),
                                                        startY = 150f
                                                    )
                                                )
                                        )
                                        
                                        // Small Premium Badge in top-left
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(top = 20.dp, start = 20.dp)
                                                .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(12.dp))
                                                .border(1.dp, Color(0xFFF2B705), RoundedCornerShape(12.dp))
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = "Premium icon",
                                                    tint = Color(0xFFF2B705),
                                                    modifier = Modifier.size(10.dp)
                                                )
                                                Text(
                                                    text = customTag,
                                                    color = Color(0xFFF2B705),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 1.5.sp,
                                                    fontFamily = FontFamily.SansSerif
                                                )
                                            }
                                        }

                                        // Product title (max 2 lines), subtitle below, and large gold price underneath
                                        Column(
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(horizontal = 22.dp, vertical = 22.dp)
                                        ) {
                                            Text(
                                                text = customTitle,
                                                color = Color.White,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Serif,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = customSubtitle,
                                                color = Color.White.copy(alpha = 0.7f),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Normal,
                                                fontFamily = FontFamily.SansSerif,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "ETB ${product.price}",
                                                color = Color(0xFFF2B705),
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.SansSerif
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Centered page indicator below the banner using animated pill dots with active in gold
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                heroProducts.forEachIndexed { index, _ ->
                                    val active = pagerState.currentPage == index
                                    val width by animateDpAsState(
                                        targetValue = if (active) 20.dp else 6.dp,
                                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                        label = "IndicatorWidth"
                                    )
                                    val color by animateColorAsState(
                                        targetValue = if (active) Color(0xFFF2B705) else Color.White.copy(alpha = 0.3f),
                                        animationSpec = tween(durationMillis = 300),
                                        label = "IndicatorColor"
                                    )
                                    
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .height(6.dp)
                                            .width(width)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(color)
                                    )
                                }
                            }
                        }
                    }
                }

                // --- CATEGORY CHIPS ---
                item {
                    Column(modifier = Modifier.padding(vertical = 12.dp)) {
                        Text(
                            text = if (currentLanguage == "AM") "ምድቦች" else "CATEGORIES",
                            color = GoldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
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

                // --- PRODUCT LIST CARD TITLE ---
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LUXURY GIFTS SELECTION",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 15.sp,
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

                // --- PRODUCT LIST ---
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
                                    text = "No bespoke products match your criteria",
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
                                      ProductCard(
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
                                      ProductCard(
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
            }
        }
    }
}

@Composable
fun ProductCard(
    product: GiftProduct,
    aspectRatio: Float = 1.0f,
    onProductClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onAddToCartClick: () -> Unit
) {
    val background = MaterialTheme.colorScheme.background
    val isDarkTheme = remember(background) {
        with(background) { (red * 0.299f + green * 0.587f + blue * 0.114f) <= 0.5f }
    }

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
                elevation = if (isDarkTheme) 0.dp else 4.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false
            )
            .border(
                width = 0.5.dp,
                color = if (isDarkTheme) Color(0xFF333333) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(24.dp)
            )
            .drawBehind {
                if (isDarkTheme) {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(glowColor.copy(alpha = 0.45f), Color(0xFF16161A)),
                            radius = size.width * 1.3f
                        )
                    )
                }
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onProductClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF16161A) else Color(0xEEF3F5F8)
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
                    .background(Color.White.copy(alpha = if (isDarkTheme) 0.05f else 0.8f))
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
                                    color = if (isDarkTheme) Color.Black.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = product.category.uppercase(),
                                color = if (isDarkTheme) Color(0xFFF2B705) else MaterialTheme.colorScheme.primary,
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
                                color = if (isDarkTheme) Color.Black.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.8f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (product.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (product.isFavorite) Color(0xFFE57373) else (if (isDarkTheme) Color(0xFFF2B705) else MaterialTheme.colorScheme.primary),
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
                                    if (isDarkTheme) AccentGoldGradient else listOf(SleekSatinGold, SleekBrightGold)
                                ),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Add to Cart",
                            tint = if (isDarkTheme) SoftBlack else Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}

// Utility linear interpolation for carousel
private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + fraction * (stop - start)
}
