package com.bca.mobile.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bca.mobile.data.model.NetworkSignalState
import com.bca.mobile.data.repository.BcaRepository

/**
 * Authentic BCA Mobile Network Signal Status Indicator
 * 🟢 Hijau  : Ready / Siap Transaksi (koneksi stabil)
 * 🔵 Biru   : Processing / Sedang Proses kirim-terima data transaksi (dengan efek denyut)
 * 🔴 Merah  : Offline / Gangguan sinyal atau server tidak terjangkau
 */
@Composable
fun BcaConnectionIndicator(
    modifier: Modifier = Modifier,
    signalState: NetworkSignalState? = null
) {
    val repository = remember { BcaRepository.getInstance() }
    val repoSignal by repository.indicatorSignal.collectAsState()
    val activeSignal = signalState ?: repoSignal

    var showDialog by remember { mutableStateOf(false) }

    val (solidColor, borderColor) = when (activeSignal) {
        NetworkSignalState.READY -> Color(0xFF8CC63E) to Color(0xFF75A932)      // Authentic BCA Lime Green #8CC63E
        NetworkSignalState.PROCESSING -> Color(0xFF0070BA) to Color(0xFF005CA9) // Authentic BCA Blue #0070BA
        NetworkSignalState.OFFLINE -> Color(0xFFE53935) to Color(0xFFC62828)    // Authentic BCA Red #E53935
    }

    val animatedColor by animateColorAsState(
        targetValue = solidColor,
        animationSpec = tween(durationMillis = 250),
        label = "indicatorColor"
    )

    val animatedBorder by animateColorAsState(
        targetValue = borderColor,
        animationSpec = tween(durationMillis = 250),
        label = "indicatorBorder"
    )

    // Outer touch area (36dp) with crisp inner indicator (16dp rounded 3dp)
    Box(
        modifier = modifier
            .size(36.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false, radius = 20.dp),
                onClick = { showDialog = true }
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(3.dp))
                .clip(RoundedCornerShape(3.dp))
                .background(animatedColor)
                .border(width = 0.8.dp, color = animatedBorder, shape = RoundedCornerShape(3.dp))
        )
    }

    if (showDialog) {
        BcaConnectionStatusDialog(
            currentState = activeSignal,
            onDismiss = { showDialog = false }
        )
    }
}

/**
 * Authentic BCA Alert Dialog detailing connection indicator status
 */
@Composable
fun BcaConnectionStatusDialog(
    currentState: NetworkSignalState,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 340.dp)
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Title
                    Text(
                        text = "Status Indikator Jaringan",
                        color = Color(0xFF0C568E),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // 3 Status Rows
                    StatusInfoRow(
                        color = Color(0xFF8CC63E),
                        title = "🟢 Hijau - Ready / Siap Transaksi",
                        desc = "Koneksi internet di HP Anda stabil dan terhubung penuh ke server BCA. Transaksi aman dan siap diproses.",
                        isActive = currentState == NetworkSignalState.READY
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    StatusInfoRow(
                        color = Color(0xFF0070BA),
                        title = "🔵 Biru - Processing / Sedang Proses",
                        desc = "Aplikasi sedang aktif mengirim data transaksi ke server BCA atau sedang menunggu respon dari bank.",
                        isActive = currentState == NetworkSignalState.PROCESSING
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    StatusInfoRow(
                        color = Color(0xFFE53935),
                        title = "🔴 Merah - Offline / Gangguan",
                        desc = "Tidak ada koneksi internet di HP atau server BCA sedang gangguan/offline. Transaksi ditolak.",
                        isActive = currentState == NetworkSignalState.OFFLINE
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Close Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF0091EA),
                                        Color(0xFF015DB5),
                                        Color(0xFF003789)
                                    )
                                )
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(color = Color.White.copy(alpha = 0.3f)),
                                onClick = onDismiss
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "OK",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusInfoRow(
    color: Color,
    title: String,
    desc: String,
    isActive: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(if (isActive) color.copy(alpha = 0.12f) else Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 3.dp)
                .size(13.dp)
                .clip(RoundedCornerShape(2.5.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(9.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isActive) Color(0xFF002245) else Color(0xFF444444)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 11.5.sp,
                lineHeight = 15.sp,
                color = Color(0xFF666666)
            )
        }
    }
}
