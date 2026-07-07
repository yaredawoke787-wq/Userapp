package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.viewmodel.GiftViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Personalization Option Data
data class PersonalizationOption(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val glowColor: Color
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    viewModel: GiftViewModel,
    onOnboardingComplete: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Page state
    val pagerState = rememberPagerState(pageCount = { 4 })
    val currentPage = pagerState.currentPage
    
    // Personalization category selection
    var selectedOption by remember { mutableStateOf<String?>(null) }
    
    // Interactive 3D touch offsets
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    
    // Portal animation trigger
    var portalTriggered by remember { mutableStateOf(false) }
    
    // Options definition
    val options = listOf(
        PersonalizationOption("Birthday Gifts", "Unwrap joy & golden celebrations", Icons.Default.Cake, Color(0xFFF2994A), Color(0xFFFFE0B2)),
        PersonalizationOption("Wedding Gifts", "Eternal tokens for modern romance", Icons.Default.Favorite, Color(0xFFEB5757), Color(0xFFFFCDD2)),
        PersonalizationOption("Corporate Gifts", "Distinguished executive statements", Icons.Default.BusinessCenter, Color(0xFF2F80ED), Color(0xFFBBDEFB)),
        PersonalizationOption("Festival Gifts", "Bespoke gratitude for seasons", Icons.Default.Celebration, Color(0xFF9B51E0), Color(0xFFE1BEE7)),
        PersonalizationOption("Luxury Gifts", "Royal curations, gold engravings", Icons.Default.Star, Color(0xFFF2B705), Color(0xFFFFF9C4))
    )
    
    // Ambient colors computed based on selection
    val activeOption = options.find { it.title == selectedOption }
    val ambientBaseGlow = activeOption?.primaryColor ?: Color(0xFFF2B705)
    
    // Infinite ambient animations (lighting, floating particles)
    val infiniteTransition = rememberInfiniteTransition(label = "AmbientPulse")
    val ambientPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AmbientPulse"
    )
    
    val globalFloatOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlobalFloat"
    )

    // Animated portal scale/alpha on "Begin Journey"
    val portalScale by animateFloatAsState(
        targetValue = if (portalTriggered) 6f else 1f,
        animationSpec = tween(1200, easing = CubicBezierEasing(0.6f, 0f, 0.4f, 1f)),
        label = "PortalScale"
    )
    
    val portalAlpha by animateFloatAsState(
        targetValue = if (portalTriggered) 0f else 1f,
        animationSpec = tween(1000, easing = EaseOutExpo),
        label = "PortalAlpha"
    )

    // Complete Onboarding Action
    val completeOnboarding: () -> Unit = {
        if (!portalTriggered) {
            portalTriggered = true
            coroutineScope.launch {
                // Save state to SharedPrefs
                val sharedPrefs = context.getSharedPreferences("teke_prefs", Context.MODE_PRIVATE)
                sharedPrefs.edit().putBoolean("onboarding_completed", true).apply()
                
                // Keep selected category if personalized
                selectedOption?.let { opt ->
                    val catName = when (opt) {
                        "Birthday Gifts" -> "Birthday"
                        "Wedding Gifts" -> "Wedding"
                        "Corporate Gifts" -> "Business"
                        "Festival Gifts" -> "Luxury" // Map appropriately
                        else -> "Luxury"
                    }
                    viewModel.selectCategory(catName)
                }
                
                delay(1200)
                onOnboardingComplete()
            }
        }
    }

    // Pure White Luxury Backgroud with delicate ambient soft light projections
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .graphicsLayer {
                scaleX = portalScale
                scaleY = portalScale
                alpha = portalAlpha
            },
        contentAlignment = Alignment.Center
    ) {
        // 1. Subtle Radial Ambient Lighting (Glow Projections)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.35f)
            val glowRadius = size.width * 0.9f * ambientPulse
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ambientBaseGlow.copy(alpha = 0.08f),
                        ambientBaseGlow.copy(alpha = 0.03f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = glowRadius
                ),
                center = center,
                radius = glowRadius
            )
            
            // Draw subtle luxury light rays
            for (i in 0..5) {
                val angle = (i * 30f + (ambientPulse * 15f)) * (Math.PI / 180f).toFloat()
                val lineLength = size.width * 0.7f
                val endPoint = Offset(
                    center.x + cos(angle) * lineLength,
                    center.y + sin(angle) * lineLength
                )
                drawLine(
                    color = ambientBaseGlow.copy(alpha = 0.02f),
                    start = center,
                    end = endPoint,
                    strokeWidth = 3f
                )
            }
        }

        // 2. Micro Particle system representing floating crystal glass dust
        OnboardingParticles(ambientBaseGlow, currentPage)

        // 3. Skip Button (Top Right)
        if (currentPage < 3) {
            Text(
                text = "Skip",
                color = Color(0xFF88888C),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .clickable { completeOnboarding() }
                    .padding(8.dp)
            )
        }

        // 4. Core Horizontal Pager representing onboarding pages
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Main 3D / Illustrative Canvas Slider Section
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                    ) {
                        // 3D Scene / Visual Container (Floating & Rotatable via Drag)
                        Box(
                            modifier = Modifier
                                .weight(1.2f)
                                .fillMaxWidth()
                                .offset(y = globalFloatOffset.dp)
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            dragOffset = Offset(
                                                x = (dragOffset.x + dragAmount.x).coerceIn(-180f, 180f),
                                                y = (dragOffset.y + dragAmount.y).coerceIn(-180f, 180f)
                                            )
                                        },
                                        onDragEnd = {
                                            coroutineScope.launch {
                                                // Bounce back to center smoothly
                                                animate(
                                                    initialValue = dragOffset.x,
                                                    targetValue = 0f,
                                                    animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                                                ) { valX, _ -> dragOffset = dragOffset.copy(x = valX) }
                                            }
                                            coroutineScope.launch {
                                                animate(
                                                    initialValue = dragOffset.y,
                                                    targetValue = 0f,
                                                    animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                                                ) { valY, _ -> dragOffset = dragOffset.copy(y = valY) }
                                            }
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            when (page) {
                                0 -> PageOneLogoScene(dragOffset, ambientBaseGlow, context)
                                1 -> PageTwoCityScene(dragOffset, ambientBaseGlow)
                                2 -> PageThreeGlobeScene(dragOffset, ambientBaseGlow)
                                3 -> PageFourBrainScene(dragOffset, ambientBaseGlow)
                            }
                        }

                        // Text & Description Container (Light Luxury Styling)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(0.8f)
                                .fillMaxWidth()
                        ) {
                            when (page) {
                                0 -> {
                                    Text(
                                        text = "Welcome to",
                                        color = Color(0xFF88888C),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 4.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Teke Man Promotion",
                                        color = Color(0xFF16161A),
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        textAlign = TextAlign.Center,
                                        fontFamily = FontFamily.Serif
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Discover beautiful, personalized gifts custom-crafted for high-profile celebrations and cherished memories.",
                                        color = Color(0xFF66666C),
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 22.sp,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                                1 -> {
                                    Text(
                                        text = "THOUSANDS OF GIFTS",
                                        color = Color(0xFF88888C),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 3.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Curate the Perfect Gesture",
                                        color = Color(0xFF16161A),
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        fontFamily = FontFamily.Serif
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "From high-profile wedding hampers and custom wax-sealed birthday boxes to premium corporate awards, handselected for elite standards.",
                                        color = Color(0xFF66666C),
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 22.sp,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                                2 -> {
                                    Text(
                                        text = "SECURE & SWIFT LOGISTICS",
                                        color = Color(0xFF88888C),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 3.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Fast. Trusted. Delivered.",
                                        color = Color(0xFF16161A),
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        fontFamily = FontFamily.Serif
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Our boutique logistics fleet ensures white-glove hand delivery in Addis Ababa and premium secure shipping across the globe.",
                                        color = Color(0xFF66666C),
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 22.sp,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                                3 -> {
                                    Text(
                                        text = "SELECT YOUR DESIRED AMBIENCE",
                                        color = Color(0xFF88888C),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 2.5.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "What Brings You Here?",
                                        color = Color(0xFF16161A),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        fontFamily = FontFamily.Serif
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    // Personalization grid choices
                                    PersonalizationGrid(
                                        options = options,
                                        selectedOption = selectedOption,
                                        onSelected = { selectedOption = it }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Navigation and interactive Controls Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Next/Submit Core Navigation button with gorgeous liquid-glass ripple animation
                if (currentPage < 3) {
                    // Energy progress ring (Left aligned)
                    EnergyRingProgress(currentPage)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(32.dp))
                            .border(1.dp, ambientBaseGlow.copy(alpha = 0.5f), RoundedCornerShape(32.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color.White,
                                        Color(0xFFF9F9FA)
                                    )
                                )
                            )
                            .clickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(currentPage + 1)
                                }
                            }
                            .padding(horizontal = 28.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Next Screen",
                                color = Color(0xFF16161A),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Next Arrow",
                                tint = ambientBaseGlow,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else {
                    // Page 4: Grand Final Begin Journey Button - Ultra Cinematic & Expanding
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .border(1.dp, ambientBaseGlow.copy(alpha = 0.6f), RoundedCornerShape(30.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF16161A),
                                        Color(0xFF222228)
                                    )
                                )
                            )
                            .clickable { completeOnboarding() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (selectedOption != null) "Begin Bespoke Experience" else "Begin Journey",
                            color = ambientBaseGlow,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }
        }
    }
}

// Custom procedural particle background system
@Composable
fun OnboardingParticles(accentColor: Color, page: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "Particles")
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Time"
    )

    // Pre-calculate randomized particles
    val particlesList = remember {
        val r = Random(123)
        List(40) {
            Offset(r.nextFloat(), r.nextFloat()) to (4f + r.nextFloat() * 10f)
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particlesList.forEachIndexed { i, (pos, pSize) ->
            // Apply floating physics based on trigonometric cycles
            val xOffset = sin(animTime + i) * 35f
            val yOffset = cos(animTime * 1.5f + i) * 45f
            
            val drawX = pos.x * this.size.width + xOffset
            val drawY = pos.y * this.size.height + yOffset
            
            // Check boundaries
            if (drawX in 0f..this.size.width && drawY in 0f..this.size.height) {
                val alpha = (0.2f + 0.3f * sin(animTime + i)).coerceIn(0.1f, 0.7f)
                val color = if (i % 3 == 0) accentColor else Color(0xFFE5E9F0)
                
                // Draw luxury crystal hexagon/diamond particle
                val path = Path().apply {
                    moveTo(drawX, drawY - pSize)
                    lineTo(drawX + pSize / 2f, drawY)
                    lineTo(drawX, drawY + pSize)
                    lineTo(drawX - pSize / 2f, drawY)
                    close()
                }
                drawPath(path, color.copy(alpha = alpha))
            }
        }
    }
}

// 1. PAGE ONE: Luxury Crystal/Glass Floating Logo with Ribbon Orbit
@Composable
fun PageOneLogoScene(dragOffset: Offset, accentColor: Color, context: Context) {
    val infiniteTransition = rememberInfiniteTransition(label = "LogoSceneAnims")
    val orbitRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Orbit"
    )
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    Box(
        modifier = Modifier
            .size(240.dp)
            .graphicsLayer {
                rotationY = dragOffset.x / 4f
                rotationX = -dragOffset.y / 4f
                cameraDistance = 12f * density
            },
        contentAlignment = Alignment.Center
    ) {
        // High-end glassmorphic circular pedestal behind logo
        Canvas(modifier = Modifier.size(190.dp)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.08f),
                        Color.White.copy(alpha = 0f)
                    )
                )
            )
            drawCircle(
                color = accentColor.copy(alpha = 0.15f),
                style = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f)))
            )
        }

        // Orbiting luxury crystal particles / gift ribbons
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            rotate(orbitRotation) {
                // Orbit path 1
                drawOval(
                    color = accentColor.copy(alpha = 0.2f),
                    topLeft = Offset(center.x - 110.dp.toPx(), center.y - 35.dp.toPx()),
                    size = Size(220.dp.toPx(), 70.dp.toPx()),
                    style = Stroke(width = 1.5f)
                )
                
                // Floating gift ribbon nodes on orbit
                drawCircle(
                    color = accentColor,
                    radius = 5.dp.toPx(),
                    center = Offset(center.x - 110.dp.toPx(), center.y)
                )
                drawCircle(
                    color = Color(0xFFC0C0C0), // silver
                    radius = 4.dp.toPx(),
                    center = Offset(center.x + 110.dp.toPx(), center.y)
                )
            }
        }

        // Central High-Fidelity Floating Glass Logo Brand Identity
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("https://res.cloudinary.com/dnmgvjg3h/image/upload/v1783010583/IMG_20260702_192242_814_absnox.png")
                .crossfade(true)
                .build(),
            contentDescription = "Teke Man Logo Emblem",
            modifier = Modifier
                .size(150.dp)
                .scale(pulseScale)
                .alpha(0.95f),
            contentScale = ContentScale.Fit
        )
    }
}

// 2. PAGE TWO: Miniature Luxury Gift City Scene
@Composable
fun PageTwoCityScene(dragOffset: Offset, accentColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "CitySceneAnims")
    val bounceY by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CityBounce"
    )

    Canvas(
        modifier = Modifier
            .size(240.dp)
            .graphicsLayer {
                rotationY = 15f + dragOffset.x / 4f
                rotationX = -10f - dragOffset.y / 4f
                cameraDistance = 15f * density
            }
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f + bounceY.dp.toPx()

        // Draw shadow pedestal
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color.Black.copy(alpha = 0.06f), Color.Transparent)
            ),
            topLeft = Offset(cx - 90.dp.toPx(), cy + 60.dp.toPx()),
            size = Size(180.dp.toPx(), 40.dp.toPx())
        )

        // Draw transparent isometric grids
        val pathGrid = Path().apply {
            moveTo(cx, cy - 30.dp.toPx())
            lineTo(cx + 100.dp.toPx(), cy + 20.dp.toPx())
            lineTo(cx, cy + 70.dp.toPx())
            lineTo(cx - 100.dp.toPx(), cy + 20.dp.toPx())
            close()
        }
        drawPath(
            path = pathGrid,
            color = Color.Black.copy(alpha = 0.015f)
        )
        drawPath(
            path = pathGrid,
            color = accentColor.copy(alpha = 0.15f),
            style = Stroke(width = 1.5f)
        )

        // Draw isometric luxury gift towers (frosted glass boxes)
        // Main Tower Center
        drawIsometricBox(
            center = Offset(cx, cy + 10.dp.toPx()),
            w = 34.dp.toPx(), h = 75.dp.toPx(),
            accentColor = accentColor,
            isMain = true
        )

        // Left Tower
        drawIsometricBox(
            center = Offset(cx - 42.dp.toPx(), cy + 15.dp.toPx()),
            w = 26.dp.toPx(), h = 50.dp.toPx(),
            accentColor = Color(0xFFC0C0C0), // silver
            isMain = false
        )

        // Right Tower
        drawIsometricBox(
            center = Offset(cx + 42.dp.toPx(), cy + 20.dp.toPx()),
            w = 24.dp.toPx(), h = 42.dp.toPx(),
            accentColor = Color(0xFFE5E9F0),
            isMain = false
        )

        // Cute glowing logistics delivery drone hovering above
        val droneY = cy - 65.dp.toPx()
        val droneX = cx - 20.dp.toPx() + sin(bounceY * 0.1f) * 20f
        
        // Drone body
        drawRoundRect(
            color = Color(0xFF16161A),
            topLeft = Offset(droneX - 10.dp.toPx(), droneY - 4.dp.toPx()),
            size = Size(20.dp.toPx(), 8.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx())
        )
        // Drone glowing engine ring
        drawCircle(
            color = accentColor,
            radius = 3.dp.toPx(),
            center = Offset(droneX, droneY)
        )
        // Drone blades paths
        drawLine(
            color = Color.Gray.copy(alpha = 0.5f),
            start = Offset(droneX - 25.dp.toPx(), droneY),
            end = Offset(droneX + 25.dp.toPx(), droneY),
            strokeWidth = 2f
        )
    }
}

fun DrawScope.drawIsometricBox(
    center: Offset,
    w: Float,
    h: Float,
    accentColor: Color,
    isMain: Boolean
) {
    // Top diamond
    val pathTop = Path().apply {
        moveTo(center.x, center.y - h)
        lineTo(center.x + w, center.y - h + w * 0.5f)
        lineTo(center.x, center.y - h + w)
        lineTo(center.x - w, center.y - h + w * 0.5f)
        close()
    }
    
    // Left side
    val pathLeft = Path().apply {
        moveTo(center.x - w, center.y - h + w * 0.5f)
        lineTo(center.x, center.y - h + w)
        lineTo(center.x, center.y + w)
        lineTo(center.x - w, center.y + w * 0.5f)
        close()
    }

    // Right side
    val pathRight = Path().apply {
        moveTo(center.x, center.y - h + w)
        lineTo(center.x + w, center.y - h + w * 0.5f)
        lineTo(center.x + w, center.y + w * 0.5f)
        lineTo(center.x, center.y + w)
        close()
    }

    // Draw with translucent premium colors
    drawPath(pathLeft, Color.White.copy(alpha = 0.82f))
    drawPath(pathLeft, accentColor.copy(alpha = 0.12f))
    drawPath(pathLeft, Color.LightGray.copy(alpha = 0.15f), style = Stroke(width = 1f))

    drawPath(pathRight, Color.White.copy(alpha = 0.7f))
    drawPath(pathRight, accentColor.copy(alpha = 0.18f))
    drawPath(pathRight, Color.LightGray.copy(alpha = 0.15f), style = Stroke(width = 1f))

    drawPath(pathTop, Color.White.copy(alpha = 0.9f))
    drawPath(pathTop, accentColor.copy(alpha = 0.08f))
    drawPath(pathTop, accentColor.copy(alpha = 0.4f), style = Stroke(width = 1f))

    // If main, draw a glowing golden decorative cross-ribbon
    if (isMain) {
        drawLine(
            color = accentColor,
            start = Offset(center.x, center.y - h),
            end = Offset(center.x, center.y + w),
            strokeWidth = 2.5f
        )
    }
}

// 3. PAGE THREE: Luxury Digital Globe Scene
@Composable
fun PageThreeGlobeScene(dragOffset: Offset, accentColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "GlobeSceneAnims")
    val globeRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "GlobeSpin"
    )

    Canvas(
        modifier = Modifier
            .size(240.dp)
            .graphicsLayer {
                rotationY = dragOffset.x / 5f
                rotationX = -dragOffset.y / 5f
                cameraDistance = 14f * density
            }
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f

        // 1. Globe outer atmosphere glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.12f),
                    accentColor.copy(alpha = 0.03f),
                    Color.Transparent
                )
            ),
            radius = 100.dp.toPx()
        )

        // 2. Main spherical border (Glass silhouette)
        drawCircle(
            color = accentColor.copy(alpha = 0.25f),
            radius = 80.dp.toPx(),
            style = Stroke(width = 2f)
        )

        // 3. Latitude and Longitude curved lines
        rotate(globeRotation) {
            drawOval(
                color = accentColor.copy(alpha = 0.08f),
                topLeft = Offset(cx - 80.dp.toPx(), cy - 30.dp.toPx()),
                size = Size(160.dp.toPx(), 60.dp.toPx()),
                style = Stroke(width = 1f)
            )
            drawOval(
                color = accentColor.copy(alpha = 0.08f),
                topLeft = Offset(cx - 30.dp.toPx(), cy - 80.dp.toPx()),
                size = Size(60.dp.toPx(), 160.dp.toPx()),
                style = Stroke(width = 1f)
            )
            
            // Render country node lights (gold dots representing delivery areas)
            val continentPoints = listOf(
                Offset(cx - 40.dp.toPx(), cy - 20.dp.toPx()),
                Offset(cx + 20.dp.toPx(), cy - 40.dp.toPx()),
                Offset(cx - 10.dp.toPx(), cy + 30.dp.toPx()),
                Offset(cx + 35.dp.toPx(), cy + 25.dp.toPx()),
                Offset(cx, cy - 50.dp.toPx())
            )
            continentPoints.forEach { pt ->
                drawCircle(
                    color = accentColor,
                    radius = 3.dp.toPx(),
                    center = pt
                )
                drawCircle(
                    color = accentColor.copy(alpha = 0.4f),
                    radius = 6.dp.toPx(),
                    center = pt,
                    style = Stroke(width = 1f)
                )
            }
        }

        // 4. Curved Delivery Route with a flying premium package
        val pathArc = Path().apply {
            moveTo(cx - 60.dp.toPx(), cy - 10.dp.toPx())
            quadraticTo(
                cx, cy - 90.dp.toPx(),
                cx + 60.dp.toPx(), cy - 10.dp.toPx()
            )
        }
        drawPath(
            path = pathArc,
            color = accentColor,
            style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f)))
        )

        // Draw custom delivery locator pin
        drawCircle(
            color = Color(0xFF16161A),
            radius = 4.dp.toPx(),
            center = Offset(cx - 60.dp.toPx(), cy - 10.dp.toPx())
        )
    }
}

// 4. PAGE FOUR: Interactive Luxury AI Crystal Brain
@Composable
fun PageFourBrainScene(dragOffset: Offset, accentColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "BrainSceneAnims")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseBrain"
    )

    Canvas(
        modifier = Modifier
            .size(240.dp)
            .graphicsLayer {
                rotationY = dragOffset.x / 4f
                rotationX = -dragOffset.y / 4f
                cameraDistance = 12f * density
            }
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f

        // Draw central intelligence core glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.15f),
                    Color.Transparent
                )
            ),
            radius = 80.dp.toPx() * pulse
        )

        // Draw procedural brain synapse structure (nodes & lines)
        val nodes = listOf(
            Offset(cx, cy - 40.dp.toPx()),
            Offset(cx - 40.dp.toPx(), cy - 20.dp.toPx()),
            Offset(cx + 40.dp.toPx(), cy - 20.dp.toPx()),
            Offset(cx - 50.dp.toPx(), cy + 10.dp.toPx()),
            Offset(cx + 50.dp.toPx(), cy + 10.dp.toPx()),
            Offset(cx - 20.dp.toPx(), cy + 40.dp.toPx()),
            Offset(cx + 20.dp.toPx(), cy + 40.dp.toPx()),
            Offset(cx, cy + 15.dp.toPx())
        )

        // Draw synaptic lines
        for (i in nodes.indices) {
            for (j in i + 1 until nodes.size) {
                // Connect if within proximity limit to look organized
                val dist = (nodes[i] - nodes[j]).getDistance()
                if (dist < 80.dp.toPx()) {
                    drawLine(
                        color = accentColor.copy(alpha = 0.2f),
                        start = nodes[i],
                        end = nodes[j],
                        strokeWidth = 1.2f
                    )
                }
            }
        }

        // Draw synaptic core nodes
        nodes.forEachIndexed { idx, pt ->
            val nodePulse = if (idx % 2 == 0) pulse else 1f
            drawCircle(
                color = if (idx % 3 == 0) accentColor else Color(0xFFC0C0C0),
                radius = (4.dp.toPx() + (idx % 3).dp.toPx()) * nodePulse,
                center = pt
            )
            drawCircle(
                color = accentColor.copy(alpha = 0.3f),
                radius = (8.dp.toPx() + (idx % 3).dp.toPx()) * nodePulse,
                center = pt,
                style = Stroke(width = 1f)
            )
        }
    }
}

// Personalization Cards Selection Layout
@Composable
fun PersonalizationGrid(
    options: List<PersonalizationOption>,
    selectedOption: String?,
    onSelected: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(options) { item ->
            val active = selectedOption == item.title
            val cardBg by animateColorAsState(
                targetValue = if (active) Color(0xFF16161A) else Color.White,
                animationSpec = tween(250),
                label = "CardBg"
            )
            val cardBorderColor by animateColorAsState(
                targetValue = if (active) item.primaryColor else Color.Black.copy(alpha = 0.08f),
                animationSpec = tween(250),
                label = "CardBorder"
            )
            val textColor by animateColorAsState(
                targetValue = if (active) Color.White else Color(0xFF16161A),
                animationSpec = tween(250),
                label = "TextColor"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardBg)
                    .border(1.dp, cardBorderColor, RoundedCornerShape(16.dp))
                    .clickable { onSelected(item.title) }
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (active) item.primaryColor else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Column {
                        Text(
                            text = item.title,
                            color = textColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = item.description,
                            color = if (active) Color.White.copy(alpha = 0.6f) else Color.Gray,
                            fontSize = 8.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// Elegant Energy progress ring indicating onboarding percentage
@Composable
fun EnergyRingProgress(page: Int) {
    val progress = (page + 1) / 4f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
        label = "EnergyProgress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "RingSpin")
    val spin by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Spin"
    )

    Box(
        modifier = Modifier.size(54.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = 22.dp.toPx()

            // Outer subtle tracking ring
            drawCircle(
                color = Color.Black.copy(alpha = 0.05f),
                radius = radius,
                style = Stroke(width = 3.dp.toPx())
            )

            // Dynamic Energy progress sweep
            rotate(spin) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFFC0C0C0),
                            Color(0xFFF2B705),
                            Color(0xFFC0C0C0)
                        )
                    ),
                    startAngle = 0f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        // Central elegant progress percentage text
        Text(
            text = "${(progress * 100).toInt()}%",
            color = Color(0xFF16161A),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        )
    }
}
