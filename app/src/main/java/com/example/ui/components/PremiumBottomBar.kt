package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGoldGradient
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SoftBlack

import com.example.ui.localization.TekeLocalization

sealed class Screen(val route: String, val label: String, val activeIcon: ImageVector, val inactiveIcon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Contact : Screen("contact", "Categories", Icons.Filled.GridView, Icons.Outlined.GridView)
    object Favorites : Screen("favorites", "Favorites", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
    object Cart : Screen("cart", "Bag", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart)
    object Settings : Screen("settings", "Premium", Icons.Filled.Star, Icons.Outlined.Star)
}

@Composable
fun PremiumBottomBar(
    currentRoute: String,
    currentLanguage: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        Screen.Home,
        Screen.Contact,
        Screen.Favorites,
        Screen.Cart,
        Screen.Settings
    )

    // Main Floating Pill Box
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp, top = 8.dp)
            .height(64.dp)
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(32.dp))
            .clip(RoundedCornerShape(32.dp))
            .border(0.5.dp, Color(0xFF333333), RoundedCornerShape(32.dp))
            .background(Color(0xCC16161A)) // Glassmorphic high-quality alpha
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEach { screen ->
                val selected = currentRoute == screen.route
                val localizedLabel = remember(screen.route, currentLanguage) {
                    when (screen.route) {
                        "home" -> TekeLocalization.getString("tab_home", currentLanguage)
                        "contact" -> TekeLocalization.getString("tab_categories", currentLanguage)
                        "favorites" -> TekeLocalization.getString("tab_favorites", currentLanguage)
                        "cart" -> TekeLocalization.getString("tab_bag", currentLanguage)
                        "settings" -> TekeLocalization.getString("tab_premium", currentLanguage)
                        else -> screen.label
                    }
                }
                
                // Elastic physical springs for scale reactions
                val scale by animateFloatAsState(
                    targetValue = if (selected) 1.15f else 0.95f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "IconScaleSpring"
                )

                val tintColor by animateColorAsState(
                    targetValue = if (selected) Color(0xFFF2B705) else Color.White.copy(alpha = 0.5f),
                    animationSpec = tween(250),
                    label = "IconTint"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!selected) {
                                onNavigate(screen.route)
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .height(28.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (selected) screen.activeIcon else screen.inactiveIcon,
                            contentDescription = localizedLabel,
                            tint = tintColor,
                            modifier = Modifier
                                .scale(scale)
                                .size(24.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(3.dp))
                    
                    // Small glowing gold dot beneath the active icon (4dp)
                    val dotWidth by animateDpAsState(
                        targetValue = if (selected) 4.dp else 0.dp,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        label = "BottomBarDotWidth"
                    )
                    
                    Box(
                        modifier = Modifier
                            .height(4.dp)
                            .width(dotWidth)
                            .clip(CircleShape)
                            .background(Color(0xFFF2B705))
                    )
                    
                    Spacer(modifier = Modifier.height(1.dp))
                    
                    Text(
                        text = localizedLabel,
                        color = tintColor,
                        fontSize = 8.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
