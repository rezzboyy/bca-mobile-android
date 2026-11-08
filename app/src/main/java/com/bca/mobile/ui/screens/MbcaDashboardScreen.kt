package com.bca.mobile.ui.screens

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bca.mobile.ui.components.BcaConfirmationDialog
import com.bca.mobile.R
import com.bca.mobile.ui.components.BcaAlertResultDialog
import com.bca.mobile.ui.components.BcaBottomNavBar
import com.bca.mobile.ui.components.BcaConnectionIndicator
import com.bca.mobile.ui.components.BcaNavTab
import kotlinx.coroutines.delay

data class DashboardMenuItem(
    val id: String,
    val title: String,
    @DrawableRes val iconRes: Int
)

@Composable
fun MbcaDashboardScreen(
    userName: String = "DADANG MUBAROK",
    onLogout: () -> Unit,
    onNavigateToMInfo: () -> Unit = {},
    onNavigateToMTransfer: () -> Unit = {},
    onNavigateToFlazz: () -> Unit = {},
    onNavigateToCardless: () -> Unit = {},
    onTabSelected: (BcaNavTab) -> Unit = {}
) {
    var placeholderAlert by remember { mutableStateOf<String?>(null) }
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    // Intercept back button on Dashboard to display official Logout confirmation
    BackHandler {
        showLogoutConfirmation = true
    }

    // Alternating promotional foreground content (3.5 seconds interval)
    val bannerFgList = remember {
        listOf(
            R.drawable.banner_fg_gacha,
            R.drawable.banner_fg_600k
        )
    }
    var currentBannerIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3500L)
            currentBannerIndex = (currentBannerIndex + 1) % bannerFgList.size
        }
    }

    // 12 Core Menu Items matching the 4-column x 3-row layout from screenshot
    val menuItems = listOf(
        // Row 1
        DashboardMenuItem("minfo", "m-Info", R.drawable.tile_minfo),
        DashboardMenuItem("mtransfer", "m-Transfer", R.drawable.tile_mtransfer),
        DashboardMenuItem("mpayment", "m-Payment", R.drawable.tile_mpayment),
        DashboardMenuItem("mcommerce", "m-Commerce", R.drawable.tile_mcommerce),
        // Row 2
        DashboardMenuItem("cardless", "Cardless", R.drawable.tile_cardless),
        DashboardMenuItem("madmin", "m-Admin", R.drawable.tile_madmin),
        DashboardMenuItem("bcakeyboard", "BCA\nKeyboard", R.drawable.tile_bcakeyboard),
        DashboardMenuItem("flazz", "Flazz", R.drawable.tile_flazz),
        // Row 3
        DashboardMenuItem("bagibagi", "BagiBagi", R.drawable.tile_bagibagi),
        DashboardMenuItem("lifestyle", "Lifestyle", R.drawable.tile_lifestyle),
        DashboardMenuItem("financialdiary", "Financial\nDiary", R.drawable.tile_financialdiary),
        DashboardMenuItem("logout", "Logout", R.drawable.tile_logout)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF002245)) // Deep Navy background from screenshot
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Top Bar: Pure White background with BCA mobile logo, Green indicator & Logout
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: BCA mobile clean brand logo (enlarged to match example)
                    Image(
                        painter = painterResource(id = R.drawable.bca_top_logo_clean),
                        contentDescription = "BCA mobile",
                        modifier = Modifier
                            .height(34.dp)
                            .widthIn(max = 165.dp),
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.CenterStart
                    )

                    // Right: Network status indicator (Hijau/Biru/Merah) & Logout button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        BcaConnectionIndicator()

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = false, radius = 18.dp),
                                    onClick = { showLogoutConfirmation = true }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_top_logout_clean),
                                contentDescription = "Logout",
                                modifier = Modifier.size(24.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }

            // 2. Main Content Area (Greeting, Banner, 4-Column Grid)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(18.dp))

                // Greeting section
                Text(
                    text = "Selamat datang,",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = userName,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Promotional Banner Container (Static Base + Zoom In/Out Animated Foreground)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(636f / 84f),
                    contentAlignment = Alignment.Center
                ) {
                    // Layer 1: Static background banner + merged locked button "Main & Dapatkan Hadiah >"
                    Image(
                        painter = painterResource(id = R.drawable.banner_base_static),
                        contentDescription = "Promotional Banner Background",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )

                    // Layer 2: Animated zoom in/out stickers
                    AnimatedContent(
                        targetState = currentBannerIndex,
                        transitionSpec = {
                            (scaleIn(
                                initialScale = 0.82f,
                                animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
                            ) + fadeIn(
                                animationSpec = tween(durationMillis = 650)
                            )).togetherWith(
                                scaleOut(
                                    targetScale = 0.82f,
                                    animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
                                ) + fadeOut(
                                    animationSpec = tween(durationMillis = 650)
                                )
                            )
                        },
                        label = "BannerForegroundZoomAnimation",
                        modifier = Modifier.fillMaxSize()
                    ) { targetIndex ->
                        Image(
                            painter = painterResource(id = bannerFgList[targetIndex]),
                            contentDescription = if (targetIndex == 0) "The New Gacha Berhadiah" else "Total 600.000+ Hadiah Siap Dimenangkan",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // 4-Column Grid for the 11 Menu Tiles
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(menuItems) { item ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = false, radius = 36.dp),
                                    onClick = {
                                        when (item.id) {
                                            "minfo" -> onNavigateToMInfo()
                                            "mtransfer" -> onNavigateToMTransfer()
                                            "flazz" -> onNavigateToFlazz()
                                            "cardless" -> onNavigateToCardless()
                                            "logout" -> {
                                                showLogoutConfirmation = true
                                            }
                                            else -> {
                                                placeholderAlert = "Fitur ${item.title.replace("\n", " ")} saat ini sedang dalam pemeliharaan sistem berkala."
                                            }
                                        }
                                    }
                                )
                        ) {
                            // Authentic squircle blue tile
                            Image(
                                painter = painterResource(id = item.iconRes),
                                contentDescription = item.title,
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(RoundedCornerShape(14.dp)),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Label below icon (11sp so 'm-Commerce' fits on a single line)
                            Text(
                                text = item.title,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                lineHeight = 14.sp,
                                maxLines = 2,
                                modifier = Modifier.heightIn(min = 30.dp)
                            )
                        }
                    }
                }
            }

            // 3. Unified Bottom Navigation Bar with Floating Elevated QRIS Button
            BcaBottomNavBar(
                selectedTab = BcaNavTab.HOME,
                onTabSelected = onTabSelected
            )
        }

        if (placeholderAlert != null) {
            BcaAlertResultDialog(
                title = "m-BCA Informasi",
                message = placeholderAlert ?: "",
                onDismiss = { placeholderAlert = null },
                confirmText = "OK"
            )
        }

        if (showLogoutConfirmation) {
            BcaConfirmationDialog(
                onDismissRequest = { showLogoutConfirmation = false },
                title = "Logout",
                confirmText = "Logout",
                cancelText = "Cancel",
                onConfirm = {
                    showLogoutConfirmation = false
                    onLogout()
                },
                onCancel = { showLogoutConfirmation = false },
                dialogWidth = 280.dp,
                minHeight = 320.dp
            ) {
                Text(
                    text = "Anda akan logout dari BCA mobile",
                    color = Color(0xFF004B87),
                    fontSize = 14.5.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                )
            }
        }
    }
}
