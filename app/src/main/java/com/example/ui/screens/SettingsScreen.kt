package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.GiftViewModel
import com.example.ui.localization.TekeLocalization

@Composable
fun SettingsScreen(
    viewModel: GiftViewModel,
    onNavigateToAdmin: () -> Unit
) {
    val context = LocalContext.current
    val isDarkThemeEnabled by viewModel.isDarkTheme.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 100.dp)
    ) {
        // Decorative background aura that matches primary accent color
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .blur(80.dp)
                .alpha(if (isDarkThemeEnabled) 0.08f else 0.05f)
                .background(Brush.radialGradient(colors = listOf(MaterialTheme.colorScheme.primary, Color.Transparent)))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            // Secret click counter for admin portal access
            var adminClickCount by remember { mutableStateOf(0) }

            Spacer(modifier = Modifier.height(20.dp))

            // Brand Header
            Text(
                text = TekeLocalization.getString("boutique_settings", currentLanguage),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = if (currentLanguage == "AM") "ምርጫዎች እና ቅርስ" else "Preferences & Heritage",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.clickable {
                    adminClickCount++
                    if (adminClickCount >= 5) {
                        adminClickCount = 0
                        onNavigateToAdmin()
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- ABOUT APP SECTION ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = if (isDarkThemeEnabled) 0.dp else 4.dp,
                        shape = RoundedCornerShape(20.dp),
                        clip = false
                    )
                    .border(
                        width = 1.dp,
                        color = if (isDarkThemeEnabled) CardBorderColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(20.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkThemeEnabled) PremiumGray.copy(alpha = 0.3f) else Color.White
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = TekeLocalization.getString("exclusive_studio", currentLanguage),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = TekeLocalization.getString("brand_desc", currentLanguage),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Justify
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- PREFERENCES SECTION ---
            Text(
                text = TekeLocalization.getString("system_settings", currentLanguage),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // Language picker item
            SettingsListItem(
                title = TekeLocalization.getString("language_setting", currentLanguage),
                subtitle = TekeLocalization.getString("language_setting_desc", currentLanguage),
                icon = Icons.Default.Translate,
                isDarkTheme = isDarkThemeEnabled,
                trailingContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        TextButton(
                            onClick = { viewModel.setLanguage("EN") },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
                        ) {
                            Text(
                                text = "EN",
                                color = if (currentLanguage == "EN") GoldAccent else Color.Gray,
                                fontWeight = if (currentLanguage == "EN") FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                        Text("/", color = Color.Gray.copy(alpha = 0.4f), fontSize = 12.sp)
                        TextButton(
                            onClick = { viewModel.setLanguage("AM") },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
                        ) {
                            Text(
                                text = "አማርኛ",
                                color = if (currentLanguage == "AM") GoldAccent else Color.Gray,
                                fontWeight = if (currentLanguage == "AM") FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dynamic customizable admin button
            val prefs = remember { context.getSharedPreferences("teke_admin_prefs", android.content.Context.MODE_PRIVATE) }
            val adminButtonName = prefs.getString("admin_button_name", "teke man") ?: "teke man"

            SettingsListItem(
                title = adminButtonName,
                subtitle = if (currentLanguage == "AM") "ምርቶችን ለመጨመር እና ለማሻሻል እዚህ ይጫኑ" else "Upload & edit products inside the boutique",
                icon = Icons.Default.Build,
                isDarkTheme = isDarkThemeEnabled,
                onClick = onNavigateToAdmin
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Share App
            SettingsListItem(
                title = TekeLocalization.getString("share_boutique", currentLanguage),
                subtitle = TekeLocalization.getString("share_boutique_desc", currentLanguage),
                icon = Icons.Default.Share,
                isDarkTheme = isDarkThemeEnabled,
                onClick = {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Discover Ethiopia's premier luxury gift curators at Teke Man Promotion! Join our Telegram: https://t.me/Teke_Man_Promotion")
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Rate App
            SettingsListItem(
                title = TekeLocalization.getString("rate_boutique", currentLanguage),
                subtitle = TekeLocalization.getString("rate_boutique_desc", currentLanguage),
                icon = Icons.Default.Star,
                isDarkTheme = isDarkThemeEnabled,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Teke_Man_Promotion"))
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Version Indicator
            Text(
                text = TekeLocalization.getString("footer_text", currentLanguage),
                color = WarmGray,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SettingsListItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDarkTheme: Boolean,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
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
            )
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) PremiumGray.copy(alpha = 0.4f) else Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
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
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        color = WarmGray,
                        fontSize = 11.sp
                    )
                }
            }

            if (trailingContent != null) {
                trailingContent()
            } else if (onClick != null) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Navigate",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
