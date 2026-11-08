package com.bca.mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bca.mobile.R

enum class BcaNavTab {
    HOME,
    TRANSAKSI,
    QRIS,
    NOTIFIKASI,
    AKUN_SAYA,
    NONE
}

@Composable
fun BcaBottomNavBar(
    selectedTab: BcaNavTab,
    onTabSelected: (BcaNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val navImageRes = when (selectedTab) {
        BcaNavTab.HOME -> R.drawable.bca_nav_bar_home
        BcaNavTab.TRANSAKSI -> R.drawable.bca_nav_bar_transaksi
        BcaNavTab.NOTIFIKASI -> R.drawable.bca_nav_bar_notifikasi
        BcaNavTab.AKUN_SAYA -> R.drawable.bca_nav_bar_akun_saya
        BcaNavTab.NONE, BcaNavTab.QRIS -> R.drawable.bca_nav_bar_all_grey
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        // Main Navigation Bar matching bca_nav_exact.png 1:1 with elevated QRIS button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(720f / 200f)
        ) {
            // Pixel-Perfect Navbar Image (exact match to bca_nav_exact.png)
            Image(
                painter = painterResource(id = navImageRes),
                contentDescription = "BCA Navigation Bar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillWidth
            )

            // 5 Clickable Touch Target Zones (20% width each)
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // 1. Home Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false, radius = 28.dp),
                            onClick = { onTabSelected(BcaNavTab.HOME) }
                        )
                )

                // 2. Transaksi Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false, radius = 28.dp),
                            onClick = { onTabSelected(BcaNavTab.TRANSAKSI) }
                        )
                )

                // 3. QRIS Tab (Elevated button touch zone)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false, radius = 32.dp),
                            onClick = { onTabSelected(BcaNavTab.QRIS) }
                        )
                )

                // 4. Notifikasi Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false, radius = 28.dp),
                            onClick = { onTabSelected(BcaNavTab.NOTIFIKASI) }
                        )
                )

                // 5. Akun Saya Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false, radius = 28.dp),
                            onClick = { onTabSelected(BcaNavTab.AKUN_SAYA) }
                        )
                )
            }
        }

        // Base extension for system navigation bar (gesture bar or 3-button nav inset)
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsBottomHeight(WindowInsets.navigationBars)
                .background(Color(0xFFF7F7F7))
        )
    }
}
