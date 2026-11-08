package com.bca.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bca.mobile.data.repository.BcaRepository
import com.bca.mobile.ui.components.BcaAlertResultDialog
import com.bca.mobile.ui.components.BcaBottomNavBar
import com.bca.mobile.ui.components.BcaNavTab
import com.bca.mobile.ui.components.BcaSubMenuTopBar

data class BcaNotificationItem(
    val id: String,
    val title: String,
    val dateText: String,
    val detailMessage: String,
    val category: String = "Semua"
)

/**
 * Authentic BCA Notifikasi Screen matching Screenshot_2026-10-28-09-12-33-299_com.bca.jpg.jpeg
 */
@Composable
fun NotifikasiScreen(
    onBack: () -> Unit,
    onTabSelected: (BcaNavTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("Semua") }
    var activeDetailItem by remember { mutableStateOf<BcaNotificationItem?>(null) }
    var alertMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedFilter) {
        BcaRepository.getInstance().triggerProcessingPulse(400)
    }

    val filterChips = listOf("Semua", "OTP & Persetujuan", "Transaksi")

    val allNotifications = remember {
        mutableStateListOf(
            BcaNotificationItem(
                id = "1",
                title = "Financial Diary: Pemasukan sebe...",
                dateText = "24/10 10:05:45",
                detailMessage = "Financial Diary: Pemasukan sebesar Rp 2.500.000,00 tercatat dari TRANSFER BCA."
            ),
            BcaNotificationItem(
                id = "2",
                title = "Financial Diary: Pengeluaran seb...",
                dateText = "16/10 21:18:50",
                detailMessage = "Financial Diary: Pengeluaran sebesar Rp 150.000,00 tercatat untuk QRIS BCA."
            ),
            BcaNotificationItem(
                id = "3",
                title = "Financial Diary: Pengeluaran seb...",
                dateText = "16/10 16:44:07",
                detailMessage = "Financial Diary: Pengeluaran sebesar Rp 85.000,00 tercatat untuk SHOPEEPAY."
            ),
            BcaNotificationItem(
                id = "4",
                title = "Financial Diary: Pengeluaran seb...",
                dateText = "16/10 12:51:46",
                detailMessage = "Financial Diary: Pengeluaran sebesar Rp 45.000,00 tercatat untuk TRANSFER ANTAR REK."
            ),
            BcaNotificationItem(
                id = "5",
                title = "Financial Diary: Pemasukan sebe...",
                dateText = "16/10 12:30:49",
                detailMessage = "Financial Diary: Pemasukan sebesar Rp 500.000,00 tercatat dari TRANSFER ANTAR BANK."
            ),
            BcaNotificationItem(
                id = "6",
                title = "Financial Diary: Pengeluaran seb...",
                dateText = "15/10 15:44:58",
                detailMessage = "Financial Diary: Pengeluaran sebesar Rp 220.000,00 tercatat untuk TIKET.COM."
            ),
            BcaNotificationItem(
                id = "7",
                title = "Financial Diary: Pengeluaran seb...",
                dateText = "15/10 12:56:48",
                detailMessage = "Financial Diary: Pengeluaran sebesar Rp 35.000,00 tercatat untuk DANA TOPUP."
            ),
            BcaNotificationItem(
                id = "8",
                title = "Financial Diary: Pengeluaran seb...",
                dateText = "14/10 18:03:06",
                detailMessage = "Financial Diary: Pengeluaran sebesar Rp 120.000,00 tercatat untuk TRAVELOKA."
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 1. Top Bar: Pure White with "Notifikasi" & Green Indicator
        BcaSubMenuTopBar(
            title = "Notifikasi",
            onBackClick = onBack,
            showBackButton = false
        )

        // 2. Filter Chips Row: "Semua", "OTP & Persetujuan", "Transaksi"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            filterChips.forEach { chipName ->
                val isSelected = selectedFilter == chipName
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFFD6E4F0) else Color.White)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color(0xFF679ECB) else Color(0xFFD0D7DE),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = true),
                            onClick = { selectedFilter = chipName }
                        )
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = chipName,
                        color = if (isSelected) Color(0xFF16598E) else Color(0xFF333333),
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }

        // 3. Action Links Row: "Hapus Semua" (Bold Navy Blue) & "Tandai Terbaca Semua" (Bold Navy Blue)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hapus Semua",
                color = Color(0xFF0C568E),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        allNotifications.clear()
                        alertMessage = "Semua notifikasi telah dihapus."
                    }
                )
            )

            Text(
                text = "Tandai Terbaca Semua",
                color = Color(0xFF0C568E),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        alertMessage = "Semua notifikasi ditandai sebagai terbaca."
                    }
                )
            )
        }

        HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 0.8.dp)

        // 4. Center Notification List matching screenshot exactly
        if (allNotifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tidak ada notifikasi baru",
                    color = Color(0xFF64748B),
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(allNotifications, key = { it.id }) { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true),
                                onClick = { activeDetailItem = item }
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    color = Color(0xFF005CA9),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.dateText,
                                    color = Color(0xFF64748B),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }

                            Text(
                                text = "›",
                                color = Color(0xFF005CA9),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 0.8.dp)
                    }
                }
            }
        }

        // 5. Bottom Navigation Bar: Notifikasi Active (Blue)
        BcaBottomNavBar(
            selectedTab = BcaNavTab.NOTIFIKASI,
            onTabSelected = onTabSelected
        )
    }

    if (activeDetailItem != null) {
        BcaAlertResultDialog(
            title = "Detail Notifikasi",
            message = "${activeDetailItem?.dateText}\n\n${activeDetailItem?.detailMessage}",
            onDismiss = { activeDetailItem = null },
            confirmText = "Tutup"
        )
    }

    if (alertMessage != null) {
        BcaAlertResultDialog(
            title = "Notifikasi",
            message = alertMessage ?: "",
            onDismiss = { alertMessage = null },
            confirmText = "OK"
        )
    }
}
