package com.bca.mobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bca.mobile.R
import kotlinx.coroutines.delay

/**
 * Authentic BCA Mobile Opening / Splash Screen.
 * Matches 100% parity with user reference screenshot (uploaded_media_1794346161708.jpg):
 * - Clean white background #FAFAFA / #FFFFFF
 * - Centered BCA Mobile official app icon logo (width ~36.5% of screen width, ~142dp)
 * - Display duration: exactly 4 seconds (4000ms) before transitioning to main login screen (Screen.Hub)
 */
@Composable
fun BcaSplashScreen(
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier,
    durationMs: Long = 4000L
) {
    LaunchedEffect(Unit) {
        delay(durationMs)
        onTimeout()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_bca_splash_logo),
            contentDescription = "BCA mobile",
            modifier = Modifier
                .width(142.dp)
                .aspectRatio(348f / 336f),
            contentScale = ContentScale.Fit
        )
    }
}
