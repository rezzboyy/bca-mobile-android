package com.bca.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bca.mobile.ui.components.BcaConnectionIndicator
import com.bca.mobile.ui.components.BcaMetallicBackground
import com.bca.mobile.ui.components.BcaSubMenuTopBar

/**
 * Authentic BCA Layanan Transfer Selection Screen
 * 100% pixel-perfect matching uploaded_media_1_1794157095212.jpg
 */
@Composable
fun LayananTransferScreen(
    onSelectService: (serviceName: String, fee: Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BcaMetallicBackground(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar: "m-Transfer" (centered blue) + Green network indicator at right
            BcaSubMenuTopBar(
                title = "m-Transfer",
                onBackClick = onBack,
                showBackButton = false,
                trailingAction = {
                    BcaConnectionIndicator()
                }
            )

            val density = androidx.compose.ui.platform.LocalDensity.current
            val marginHorizontalDp = (30f / density.density).dp // 660px card width matching crop_card1_bifast.png

            // Content Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = marginHorizontalDp, vertical = 14.dp)
            ) {
                // Header: "Layanan Transfer"
                Text(
                    text = "Layanan Transfer",
                    color = Color(0xFF005CA9),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 2.dp, bottom = 12.dp)
                )

                // Card 1: BI FAST
                TransferServiceCard(
                    title = "BI FAST",
                    minTransaction = "Rp 10,000.00",
                    fee = "Rp 2,500.00",
                    serviceHours = "24 Jam",
                    onClick = {
                        onSelectService("BI FAST", 2500L)
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Card 2: Realtime Online
                TransferServiceCard(
                    title = "Realtime Online",
                    minTransaction = "Rp 10,000.00",
                    fee = "Rp 6,500.00",
                    serviceHours = "24 Jam",
                    onClick = {
                        onSelectService("Realtime Online", 6500L)
                    }
                )
            }
        }
    }
}

@Composable
private fun TransferServiceCard(
    title: String,
    minTransaction: String,
    fee: String,
    serviceHours: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                color = Color(0xFF005CA9),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Row 1: Minimal Transaksi
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Minimal Transaksi",
                    color = Color(0xFF64748B),
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(1.08f)
                )
                Text(
                    text = minTransaction,
                    color = Color(0xFF005CA9),
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            // Row 2: Biaya
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Biaya",
                    color = Color(0xFF64748B),
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(1.08f)
                )
                Text(
                    text = fee,
                    color = Color(0xFF005CA9),
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            // Row 3: Waktu Layanan
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Waktu Layanan",
                    color = Color(0xFF64748B),
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(1.08f)
                )
                Text(
                    text = serviceHours,
                    color = Color(0xFF005CA9),
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
