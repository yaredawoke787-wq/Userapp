package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GiftProduct
import com.example.ui.theme.*
import com.example.ui.viewmodel.GiftViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CartScreen(viewModel: GiftViewModel) {
    val context = LocalContext.current
    val cartProducts by viewModel.cartProducts.collectAsState()
    val subtotal by viewModel.cartSubtotal.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var showSuccessOverlay by remember { mutableStateOf(false) }

    // Delivery and Service fees
    val deliveryFee = if (subtotal > 0) 250.0 else 0.0
    val totalAmount = subtotal + deliveryFee

    val isDarkTheme = true

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 100.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Brand Header
            Text(
                text = "YOUR SELECTION BAG",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = "Bespoke Bag Curation",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (cartProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Empty Bag",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Your Selection Bag is empty",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Browse our luxury galleries to add custom elements.",
                            color = WarmGray,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                // Cart Items list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(cartProducts) { product ->
                        CartItemRow(
                            product = product,
                            onIncrease = { viewModel.addToCart(product) },
                            onDecrease = { viewModel.decreaseCartQuantity(product) },
                            onRemove = { viewModel.removeFromCart(product) }
                        )
                    }
                }

                // Subtotal Summary Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = if (isDarkTheme) 0.dp else 12.dp,
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                            clip = false
                        )
                        .background(if (isDarkTheme) PremiumGray.copy(alpha = 0.4f) else Color.White)
                        .border(
                            width = 1.dp,
                            color = if (isDarkTheme) CardBorderColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal Value", color = WarmGray, fontSize = 14.sp)
                        Text("ETB $subtotal", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Bespoke White-Glove Delivery", color = WarmGray, fontSize = 14.sp)
                        Text("ETB $deliveryFee", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = if (isDarkTheme) Color.White.copy(alpha = 0.08f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Boutique Grand Total", color = MaterialTheme.colorScheme.primary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "ETB $totalAmount",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Checkout Button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                showSuccessOverlay = true
                                delay(2200) // Beautiful delay for success state appreciation
                                viewModel.clearCart()
                                showSuccessOverlay = false
                                // Action dialer trigger
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:+251921935862")
                                }
                                context.startActivity(intent)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        if (isDarkTheme) AccentGoldGradient else listOf(SleekSatinGold, SleekBrightGold)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PhoneInTalk,
                                    contentDescription = "Place order",
                                    tint = if (isDarkTheme) SoftBlack else Color.White
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "PLACE CONCIERGE ORDER NOW",
                                    color = if (isDarkTheme) SoftBlack else Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- SUCCESS CONFETTI OVERLAY ---
        AnimatedVisibility(
            visible = showSuccessOverlay,
            enter = fadeIn() + expandIn(),
            exit = fadeOut() + shrinkOut()
        ) {
            SuccessOverlayState()
        }
    }
}

@Composable
fun CartItemRow(
    product: GiftProduct,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    val isDarkTheme = true

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDarkTheme) 0.dp else 2.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .border(
                width = 1.dp,
                color = if (isDarkTheme) CardBorderColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) PremiumGray.copy(alpha = 0.3f) else Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini image thumbnail
            com.example.ui.components.ProductImage(
                product = product,
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.title,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = product.subtitle,
                    color = WarmGray,
                    fontSize = 11.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ETB ${product.price}",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = Color.Red.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Multiplier counter
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = if (isDarkTheme) Color.White.copy(alpha = 0.05f) else SleekLightGray,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onDecrease() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("-", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text(
                        text = product.cartQuantity.toString(),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onIncrease() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

// Custom High-End Confetti & Success Overlay
@Composable
fun SuccessOverlayState() {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "ConfettiLoop")

    // Animate particles falling down
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ConfettiProgress"
    )

    // Confetti particles generation
    val particles = remember {
        List(40) {
            ConfettiParticle(
                x = Random.nextFloat(),
                yOffset = Random.nextFloat(),
                color = when (Random.nextInt(4)) {
                    0 -> GoldAccent
                    1 -> LightGold
                    2 -> Color.White
                    else -> Color(0xFFFFD700)
                },
                speed = 150f + Random.nextFloat() * 100f,
                size = 6f + Random.nextFloat() * 10f,
                rotationSpeed = 30f + Random.nextFloat() * 120f
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBlack.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        // Continuous confetti render on canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.forEach { p ->
                val currentY = ((p.yOffset * size.height) + (progress * p.speed)) % size.height
                val currentX = p.x * size.width
                val currentRot = progress * p.rotationSpeed
                
                translate(left = currentX, top = currentY) {
                    drawRect(
                        color = p.color,
                        size = androidx.compose.ui.geometry.Size(p.size, p.size * 1.5f),
                        alpha = 0.75f
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Glowing Check icon
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success check",
                tint = GoldAccent,
                modifier = Modifier
                    .size(80.dp)
                    .shadow(16.dp, CircleShape)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ORDER RESERVED",
                color = GoldAccent,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bespoke Curation Secured",
                color = LuxuryWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Opening direct boutique concierge lines now. Simply click dial to confirm your delivery logistics with Teke Man specialists.",
                color = WarmGray,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

data class ConfettiParticle(
    val x: Float,
    val yOffset: Float,
    val color: Color,
    val speed: Float,
    val size: Float,
    val rotationSpeed: Float
)
