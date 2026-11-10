package com.bca.mobile.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.bca.mobile.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * =========================================================================================
 * BcaPromoBanner - Komponen Banner Iklan Animasi Dashboard m-BCA
 * =========================================================================================
 * Komponen banner 1 kolom mandiri (reusable) berisi 2 iklan bergantian:
 * 1. The New Gacha Berhadiah (banner_fg_gacha)
 * 2. Total 600.000+ Hadiah Siap Dimenangkan! (banner_fg_600k)
 *
 * Struktur Layer:
 * - Layer 1 (Statis): banner_base_static (Frame Biru BCA & Tombol "Main & Dapatkan Hadiah >")
 * - Layer 2 (Outgoing Transisi): Sticker iklan lama mundur & menciut mengecil ke belakang (1.00 -> 0.50)
 * - Layer 3 (Incoming / Aktif): Sticker iklan baru masuk dari kecil/menciut (0.50), lalu mengembang menyembul ke depan (1.025 -> 1.00)
 *
 * Siklus Animasi per Iklan:
 * - Masuk dari kecil & mengembang menyembul ke depan (scale 0.50 -> 1.025 -> 1.00)
 * - Sembulan ke-1: Zoom In 1.00 -> 1.035 (2000 ms) & Zoom Out 1.035 -> 1.00 (2000 ms)
 * - Sembulan ke-2: Zoom In 1.00 -> 1.035 (2000 ms) & Zoom Out 1.035 -> 1.00 (2000 ms)
 * - Transisi: Mundur menciut mengecil ke belakang secara mulus seraya iklan baru masuk mengembang menyembul ke depan
 * =========================================================================================
 */
@Composable
fun BcaPromoBanner(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val bannerFgList = remember {
        listOf(
            R.drawable.banner_fg_gacha,
            R.drawable.banner_fg_600k
        )
    }
    var currentBannerIndex by remember { mutableIntStateOf(0) }
    var outgoingBannerIndex by remember { mutableIntStateOf(-1) }

    val currentScale = remember { Animatable(1.00f) }
    val currentAlpha = remember { Animatable(1.00f) }

    val outgoingScale = remember { Animatable(1.00f) }
    val outgoingAlpha = remember { Animatable(0.00f) }

    LaunchedEffect(Unit) {
        // Inisialisasi awal saat layar pertama kali dibuka: Masuk dari kecil mengembang menyembul ke depan
        currentScale.snapTo(0.50f)
        currentAlpha.snapTo(0.0f)
        coroutineScope {
            launch {
                currentAlpha.animateTo(1.0f, tween(380, easing = FastOutSlowInEasing))
            }
            currentScale.animateTo(1.025f, tween(380, easing = CubicBezierEasing(0.12f, 0.8f, 0.32f, 1.0f)))
            currentScale.animateTo(1.00f, tween(160, easing = FastOutSlowInEasing))
        }

        while (isActive) {
            // 1. Berdenyut sangat halus & kalem (2 kali sembulan zoom in zoom out berturut-turut pada setiap iklan)
            // Sembulan ke-1
            currentScale.animateTo(1.035f, tween(2000, easing = FastOutSlowInEasing))
            currentScale.animateTo(1.00f, tween(2000, easing = FastOutSlowInEasing))

            // Sembulan ke-2
            currentScale.animateTo(1.035f, tween(2000, easing = FastOutSlowInEasing))
            currentScale.animateTo(1.00f, tween(2000, easing = FastOutSlowInEasing))

            // 2. Transisi Pergantian Iklan:
            // Iklan lama mundur mengecil ke belakang (1.00 -> 0.50)
            // Iklan baru masuk dari kecil mengembang menyembul ke depan (0.50 -> 1.025 -> 1.00)
            outgoingBannerIndex = currentBannerIndex
            outgoingScale.snapTo(1.00f)
            outgoingAlpha.snapTo(1.00f)

            val nextIndex = (currentBannerIndex + 1) % bannerFgList.size
            currentBannerIndex = nextIndex
            currentScale.snapTo(0.50f)
            currentAlpha.snapTo(0.00f)

            coroutineScope {
                // Outgoing: Mundur mengecil ke belakang lebih santai & kalem (kecepatan dikurangi, durasi 750ms)
                launch {
                    outgoingScale.animateTo(0.50f, tween(750, easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)))
                }
                launch {
                    outgoingAlpha.animateTo(0.0f, tween(700, easing = FastOutSlowInEasing))
                }

                // Incoming: Masuk dari kecil lalu mengembang menyembul ke depan secara selaras
                launch {
                    delay(180L) // Jeda transisi agar pergerakan mundur ke belakang terlihat jelas & elegan
                    launch {
                        currentAlpha.animateTo(1.0f, tween(480, easing = FastOutSlowInEasing))
                    }
                    currentScale.animateTo(1.025f, tween(480, easing = CubicBezierEasing(0.12f, 0.8f, 0.32f, 1.0f)))
                    currentScale.animateTo(1.00f, tween(200, easing = FastOutSlowInEasing))
                }
            }

            // Bersihkan outgoing setelah animasi transisi selesai
            outgoingBannerIndex = -1
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(636f / 84f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Layer 1: Static background banner + merged locked button "Main & Dapatkan Hadiah >"
        Image(
            painter = painterResource(id = R.drawable.banner_base_static),
            contentDescription = "Promotional Banner Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        // Layer 2: Outgoing sticker (mundur menciut mengecil ke belakang)
        if (outgoingBannerIndex in bannerFgList.indices) {
            Image(
                painter = painterResource(id = bannerFgList[outgoingBannerIndex]),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = outgoingScale.value
                        scaleY = outgoingScale.value
                        alpha = outgoingAlpha.value
                    },
                contentScale = ContentScale.Fit
            )
        }

        // Layer 3: Incoming / Current active sticker (mengembang menyembul ke depan)
        Image(
            painter = painterResource(id = bannerFgList[currentBannerIndex]),
            contentDescription = if (currentBannerIndex == 0) "The New Gacha Berhadiah" else "Total 600.000+ Hadiah Siap Dimenangkan",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = currentScale.value
                    scaleY = currentScale.value
                    alpha = currentAlpha.value
                },
            contentScale = ContentScale.Fit
        )
    }
}
