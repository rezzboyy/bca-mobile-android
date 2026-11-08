package com.bca.mobile.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bca.mobile.data.model.FlazzCard
import com.bca.mobile.data.model.FlazzTransaction
import com.bca.mobile.data.model.TransactionType
import com.bca.mobile.data.repository.BcaRepository
import com.bca.mobile.ui.components.BcaAlertResultDialog
import com.bca.mobile.ui.components.BcaSubMenuTopBar

@Composable
fun FlazzScreen(
    flazzCard: FlazzCard,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isCardScanned by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf<String?>(null) }

    // Pulsing radar animation
    val infiniteTransition = rememberInfiniteTransition(label = "nfc_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val cardGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0072CE),
            Color(0xFF003882),
            Color(0xFF001F4D)
        )
    )

    val buttonGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0091EA),
            Color(0xFF015DB5),
            Color(0xFF003789)
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
    ) {
        BcaSubMenuTopBar(
            title = "Flazz",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isCardScanned) {
                // NFC Radar Scanning View
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "TEMPELKAN KARTU FLAZZ",
                    color = Color(0xFF002D72),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Dekatkan kartu Flazz Anda di bagian belakang smartphone untuk membaca saldo dan riwayat transaksi.",
                    color = Color(0xFF64748B),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Radar Pulse Rings
                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(Color(0xFF0072CE).copy(alpha = 0.12f))
                    )
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .scale(pulseScale * 0.9f)
                            .clip(CircleShape)
                            .background(Color(0xFF0072CE).copy(alpha = 0.2f))
                    )
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF005BAA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NFC",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Simulate Tap Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(buttonGradient)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = true),
                            onClick = {
                                BcaRepository.getInstance().triggerProcessingPulse(700)
                                isCardScanned = true
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Simulasikan Tempel Kartu Flazz",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Card Display & History View
                // Realistic Flazz Card Visual
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .shadow(12.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(cardGradient)
                        .padding(18.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Flazz",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "BCA",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column {
                            Text(
                                text = "SALDO AKTIF",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = BcaRepository.formatRupiah(flazzCard.balance),
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = flazzCard.cardNumber,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "NFC ACTIVE",
                                color = Color(0xFF00E676),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Update Saldo / Pindai Ulang
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0xFF005BAA), RoundedCornerShape(6.dp))
                            .background(Color.White)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true),
                                onClick = {
                                    BcaRepository.getInstance().triggerProcessingPulse(800)
                                    alertMessage = "Update Saldo Flazz Berhasil. Saldo kartu telah disinkronisasikan ke sistem pusat BCA."
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Update Saldo",
                            color = Color(0xFF005BAA),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(buttonGradient)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true),
                                onClick = { isCardScanned = false }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Pindai Ulang",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // History Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RIWAYAT TRANSAKSI TERAKHIR",
                        color = Color(0xFF475569),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Flazz History List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(flazzCard.history) { item ->
                        FlazzHistoryCard(item)
                    }
                }
            }
        }
    }

    if (alertMessage != null) {
        BcaAlertResultDialog(
            title = "Flazz Info",
            message = alertMessage ?: "",
            onDismiss = { alertMessage = null },
            confirmText = "OK"
        )
    }
}

@Composable
private fun FlazzHistoryCard(item: FlazzTransaction) {
    val isCredit = item.type == TransactionType.CR
    val typeColor = if (isCredit) Color(0xFF00897B) else Color(0xFFE53935)
    val sign = if (isCredit) "+" else "-"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.merchant,
                    color = Color(0xFF1E293B),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.date,
                    color = Color(0xFF64748B),
                    fontSize = 11.sp
                )
            }

            Text(
                text = "$sign ${BcaRepository.formatRupiah(item.amount)}",
                color = typeColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
