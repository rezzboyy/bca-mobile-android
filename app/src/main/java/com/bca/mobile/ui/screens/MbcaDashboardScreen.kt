package com.bca.mobile.ui.screens

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.ui.graphics.graphicsLayer
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

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

    // Alternating promotional foreground content with steady 2-second display & smooth zoom in/out
    val bannerFgList = remember {
        listOf(
            R.drawable.banner_fg_gacha,
            R.drawable.banner_fg_600k
        )
    }
    var currentBannerIndex by remember { mutableIntStateOf(0) }
    val bannerScale = remember { Animatable(0.88f) }
    val bannerAlpha = remember { Animatable(0.0f) }

    LaunchedEffect(Unit) {
        while (isActive) {
            // 1. Muncul halus & kalem (scale 0.88 -> 1.00, alpha 0.0 -> 1.0 dalam 500ms)
            coroutineScope {
                launch {
                    bannerScale.snapTo(0.88f)
                    bannerScale.animateTo(1.00f, tween(500, easing = FastOutSlowInEasing))
                }
                launch {
                    bannerAlpha.snapTo(0.0f)
                    bannerAlpha.animateTo(1.0f, tween(500, easing = FastOutSlowInEasing))
                }
            }

            // 2. Berdenyut sangat halus & kalem selama 2 detik (2.000 ms)
            // Zoom In lembut (+3.5%): 1.00 -> 1.035 dalam 1.000 ms
            bannerScale.animateTo(1.035f, tween(1000, easing = FastOutSlowInEasing))
            // Zoom Out lembut: 1.035 -> 1.00 dalam 1.000 ms
            bannerScale.animateTo(1.00f, tween(1000, easing = FastOutSlowInEasing))

            // 3. Menghilang halus & kalem untuk berganti iklan (scale 1.00 -> 0.88, alpha 1.0 -> 0.0 dalam 500ms)
            coroutineScope {
                launch {
                    bannerScale.animateTo(0.88f, tween(500, easing = FastOutSlowInEasing))
                }
                launch {
                    bannerAlpha.animateTo(0.0f, tween(500, easing = FastOutSlowInEasing))
                }
            }

            // Jeda singkat yang elegan (150ms) sebelum iklan berikutnya muncul
            delay(150L)

            // 4. Gantian ke iklan promo berikutnya dan berulang terus menerus (looping)
            currentBannerIndex = (currentBannerIndex + 1) % bannerFgList.size
        }
    }

    // 11 Core Menu Items (menu logout di samping Financial Diary telah dihapus sesuai instruksi)
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
        DashboardMenuItem("financialdiary", "Financial\nDiary", R.drawable.tile_financialdiary)
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

                    // Layer 2: Zoom in/out animated foreground sticker (tetap 2 detik, zoom in/out, bergantian)
                    Image(
                        painter = painterResource(id = bannerFgList[currentBannerIndex]),
                        contentDescription = if (currentBannerIndex == 0) "The New Gacha Berhadiah" else "Total 600.000+ Hadiah Siap Dimenangkan",
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = bannerScale.value
                                scaleY = bannerScale.value
                                alpha = bannerAlpha.value
                            },
                        contentScale = ContentScale.Fit
                    )
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
            val logoutDensity = androidx.compose.ui.platform.LocalDensity.current
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
                dialogWidth = (560f / logoutDensity.density).dp, // Exactly 560px matching uploaded_media_1794211976818
                dialogHeight = (640f / logoutDensity.density).dp, // Exactly 640px matching uploaded_media_1794211976818
                buttonSpacing = 15.dp, // 28px gap matching authentic reference
                buttonHeight = 40.dp, // 75px button height matching authentic reference
                contentPadding = PaddingValues(start = 7.dp, end = 7.dp, top = 6.dp, bottom = 10.dp),
                contentAlignment = Alignment.Start
            ) {
                Text(
                    text = "Anda akan logout dari BCA mobile",
                    color = Color(0xFF004B87),
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 8.dp)
                )
            }
        }
    }
}
