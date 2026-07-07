package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// High-performance particle data class representing crystal/light fragments
data class CinematicParticle(
    val id: Int,
    val angle: Float,
    val initialDistance: Float,
    val targetRadius: Float,
    val size: Float,
    val speedFactor: Float,
    val isGold: Boolean,
    val maxAlpha: Float,
    val rotationSpeed: Float
)

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    
    // Core animation timeline states
    var startAssembly by remember { mutableStateOf(false) }
    var logoRevealed by remember { mutableStateOf(false) }
    var textRevealed by remember { mutableStateOf(false) }
    var sweepActive by remember { mutableStateOf(false) }

    // Total cinematic animation duration is 7.5 seconds (7500ms)
    LaunchedEffect(Unit) {
        // 0ms - 500ms: Silence, pure white
        delay(500)
        // 500ms: Assembly particles begin flowing toward the center
        startAssembly = true
        // 1800ms: Logo begins to materialize and rotate
        delay(1300)
        logoRevealed = true
        // 3500ms: Logo fully formed, text fades in
        delay(1700)
        textRevealed = true
        // 4500ms: Premium diagonal light sweep/shine travels across the logo
        delay(1000)
        sweepActive = true
        // 7500ms: Seamless transition to the main application
        delay(3000)
        onSplashComplete()
    }

    // 1. Particle Assembly Progress (0f -> 1f)
    val assemblyProgress by animateFloatAsState(
        targetValue = if (startAssembly) 1f else 0f,
        animationSpec = tween(durationMillis = 3500, easing = CubicBezierEasing(0.19f, 1f, 0.22f, 1f)),
        label = "AssemblyProgress"
    )

    // 2. Camera Dolly-in (Scale) and 3D Rotation Orbit
    val cameraScale by animateFloatAsState(
        targetValue = if (logoRevealed) 1.05f else 0.85f,
        animationSpec = tween(durationMillis = 5000, easing = EaseOutCubic),
        label = "CameraScale"
    )

    val cameraRotationY by animateFloatAsState(
        targetValue = if (logoRevealed) 0f else -25f,
        animationSpec = tween(durationMillis = 5000, easing = EaseOutCubic),
        label = "CameraRotationY"
    )

    val cameraRotationX by animateFloatAsState(
        targetValue = if (logoRevealed) 0f else 15f,
        animationSpec = tween(durationMillis = 5000, easing = EaseOutCubic),
        label = "CameraRotationX"
    )

    // 3. Logo Materialization Alpha
    val logoAlpha by animateFloatAsState(
        targetValue = if (logoRevealed) 1f else 0f,
        animationSpec = tween(durationMillis = 2000, easing = EaseInOutQuart),
        label = "LogoAlpha"
    )

    // 4. Subtle, luxurious constant floating/levitation motion
    val floatTransition = rememberInfiniteTransition(label = "LuxuryFloat")
    val floatOffset by floatTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatOffset"
    )

    // 5. Luxury light sweep shine animation across the logo
    val sweepProgress by animateFloatAsState(
        targetValue = if (sweepActive) 1.5f else -0.5f,
        animationSpec = tween(durationMillis = 2200, easing = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)),
        label = "SweepProgress"
    )

    // 6. Text reveal anim
    val textAlpha by animateFloatAsState(
        targetValue = if (textRevealed) 1f else 0f,
        animationSpec = tween(durationMillis = 1500, easing = EaseOutQuart),
        label = "TextAlpha"
    )

    val textOffset by animateFloatAsState(
        targetValue = if (textRevealed) 0f else 15f,
        animationSpec = tween(durationMillis = 1500, easing = EaseOutQuart),
        label = "TextOffset"
    )

    // Generate high-performance particles deterministically
    val particles = remember {
        val random = Random(42)
        List(90) { id ->
            val angle = random.nextFloat() * 2f * Math.PI.toFloat()
            val initialDistance = 450f + random.nextFloat() * 400f
            val targetRadius = random.nextFloat() * 30f
            val size = 2f + random.nextFloat() * 10f
            val speedFactor = 0.7f + random.nextFloat() * 0.6f
            val isGold = random.nextFloat() > 0.6f
            val maxAlpha = 0.3f + random.nextFloat() * 0.7f
            val rotationSpeed = (random.nextFloat() - 0.5f) * 150f
            CinematicParticle(id, angle, initialDistance, targetRadius, size, speedFactor, isGold, maxAlpha, rotationSpeed)
        }
    }

    // Pure White seamless studio backdrop
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        
        // --- ASSEMBLY PARTICLES LAYER ---
        // Elegant crystal & gold light fragments flowing intelligently toward the center
        if (assemblyProgress > 0f && assemblyProgress < 0.98f) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(1f - (assemblyProgress * 0.8f)) // gradually fade out as logo forms
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                
                particles.forEach { particle ->
                    // Flow calculation: linearly interpolate position based on assemblyProgress and speedFactor
                    val t = (assemblyProgress * particle.speedFactor).coerceIn(0f, 1f)
                    
                    // Bezier/Spiral path trajectory for organic luxury movement
                    val currentDistance = particle.initialDistance * (1f - t) + particle.targetRadius * t
                    val spiralAngle = particle.angle + (1f - t) * 0.8f // elegant spiral twist
                    
                    val px = center.x + cos(spiralAngle) * currentDistance
                    val py = center.y + sin(spiralAngle) * currentDistance
                    
                    val particleAlpha = (t * (1f - t) * 4f * particle.maxAlpha).coerceIn(0f, 1f)
                    val color = if (particle.isGold) Color(0xFFF2B705) else Color(0xFFE5E9F0)
                    
                    // Draw micro crystal star / diamond shape
                    rotate(degrees = particle.rotationSpeed * t, pivot = Offset(px, py)) {
                        val path = Path().apply {
                            moveTo(px, py - particle.size)
                            lineTo(px + particle.size / 2f, py)
                            lineTo(px, py + particle.size)
                            lineTo(px - particle.size / 2f, py)
                            close()
                        }
                        drawPath(
                            path = path,
                            color = color.copy(alpha = particleAlpha)
                        )
                    }
                }
            }
        }

        // --- HERO LOGO AND TYPOGRAPHY CONTAINER ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.offset(y = floatOffset.dp)
        ) {
            
            // --- 3D TRANSFORMED LOGO FRAME ---
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer {
                        this.scaleX = cameraScale
                        this.scaleY = cameraScale
                        this.rotationY = cameraRotationY
                        this.rotationX = cameraRotationX
                        this.cameraDistance = 16f * density
                        this.compositingStrategy = CompositingStrategy.Offscreen
                    }
                    .alpha(logoAlpha),
                contentAlignment = Alignment.Center
            ) {
                // Luxury Render Image
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data("https://res.cloudinary.com/dnmgvjg3h/image/upload/v1783254388/1783254303291_kpzul9.png")
                        .crossfade(true)
                        .build(),
                    contentDescription = "Teke Man Splash Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                // --- LUXURY LIGHT SWEEP SHINE ---
                // Applying a high-end diagonal specular highlight mask
                if (sweepActive && logoAlpha > 0.1f) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Diagonal sweep direction
                        val sweepWidth = size.width * 0.6f
                        val currentSweepCenter = size.width * sweepProgress
                        
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.0f),
                                    Color.White.copy(alpha = 0.15f),
                                    Color.White.copy(alpha = 0.85f), // High-intensity reflection peak
                                    Color.White.copy(alpha = 0.15f),
                                    Color.White.copy(alpha = 0.0f),
                                    Color.Transparent
                                ),
                                start = Offset(currentSweepCenter - sweepWidth, 0f),
                                end = Offset(currentSweepCenter, size.height)
                            ),
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // --- CINEMATIC BRAND TYPOGRAPHY ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(textAlpha)
                    .offset(y = textOffset.dp)
            ) {
                Text(
                    text = "TEKE MAN PROMOTION",
                    color = Color(0xFF16161A), // Sleek, expensive dark slate
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 8.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = "EXCLUSIVE GIFT STUDIO",
                    color = Color(0xFFF2B705), // Pure Premium Gold Accent
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 5.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.SansSerif
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Elegant minimal luxury motto
                Text(
                    text = "B e s p o k e   L u x u r y   G i f t i n g",
                    color = Color(0xFF88888C),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
