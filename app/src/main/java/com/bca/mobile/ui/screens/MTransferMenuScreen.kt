package com.bca.mobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bca.mobile.R
import com.bca.mobile.data.repository.BcaRepository
import com.bca.mobile.ui.components.BcaAlertResultDialog
import com.bca.mobile.ui.components.BcaBottomNavBar
import com.bca.mobile.ui.components.BcaNavTab
import com.bca.mobile.ui.components.BcaSubMenuTopBar

@Composable
fun MTransferMenuScreen(
    onBack: () -> Unit,
    onNavigateTransferBca: () -> Unit,
    onNavigateTransferBank: () -> Unit,
    onNavigateDaftarTransferBca: () -> Unit = {},
    onNavigateDaftarTransferBank: () -> Unit = {},
    onNavigateToInbox: () -> Unit = {},
    onTabSelected: (BcaNavTab) -> Unit = {},
    modifier: Modifier = Modifier,
    repository: BcaRepository = remember { BcaRepository.getInstance() }
) {
    var alertMessage by remember { mutableStateOf<String?>(null) }
    val inboxUnreadCount by repository.inboxUnreadCount.collectAsState()
    val inboxTitle = if (inboxUnreadCount > 0) "Inbox ($inboxUnreadCount)" else "Inbox"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF002245)) // Deep Navy matching BCA screenshot
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Top Bar: Pure White with "m-Transfer" & Green Indicator (no back arrow)
            BcaSubMenuTopBar(
                title = "m-Transfer",
                onBackClick = onBack,
                showBackButton = false,
                contentHeight = 62.dp
            )

            // 2. Main Content: Floating White Card with Locked m-Transfer Header Badge
            // Card bottom extends down until it touches QRIS dome directly ("tempelkan kaya gitu", 0dp clearance, zero gap)!
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .offset(y = 7.dp)
                    .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 0.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
            ) {
                // Card Header with m-Transfer Tile & Title (Locked / Sticky - Tidak ikut kescroll!)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFCFDFE),
                                    Color(0xFFF1F4F7),
                                    Color(0xFFE8ECEF)
                                )
                            )
                        )
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.tile_mtransfer),
                        contentDescription = "m-Transfer",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "m-Transfer",
                        color = Color(0xFF005CA9),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider(color = Color.White, thickness = 1.dp)

                // Scrollable Menu List Items (Meluncur mulus di bawah locked badge) - Posisi menu mengikuti referensi asli
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Section 1: Daftar Transfer
                    BcaMenuSectionBanner(title = "Daftar Transfer")

                    BcaMenuItemRow(
                        title = "Antar Rekening",
                        onClick = onNavigateDaftarTransferBca
                    )
                    HorizontalDivider(color = Color(0xFFE8ECEF), thickness = 0.8.dp)

                    BcaMenuItemRow(
                        title = "Antar Bank",
                        onClick = onNavigateDaftarTransferBank
                    )

                    // Section 2: Transfer
                    BcaMenuSectionBanner(title = "Transfer")

                    BcaMenuItemRow(
                        title = "Antar Rekening",
                        onClick = onNavigateTransferBca
                    )
                    HorizontalDivider(color = Color(0xFFE8ECEF), thickness = 0.8.dp)

                    BcaMenuItemRow(
                        title = "Antar Bank",
                        onClick = onNavigateTransferBank
                    )
                    HorizontalDivider(color = Color(0xFFE8ECEF), thickness = 0.8.dp)

                    BcaMenuItemRow(
                        title = "BCA Virtual Account",
                        onClick = {
                            alertMessage = "Layanan BCA Virtual Account: Masukkan kode perusahaan dan nomor pelanggan untuk melanjutkan pembayaran."
                        }
                    )
                    HorizontalDivider(color = Color(0xFFE8ECEF), thickness = 0.8.dp)

                    BcaMenuItemRow(
                        title = "Sakuku",
                        onClick = {
                            alertMessage = "Layanan Transfer ke Sakuku: Kirim saldo langsung ke nomor ponsel terdaftar."
                        }
                    )
                    HorizontalDivider(color = Color(0xFFE8ECEF), thickness = 0.8.dp)

                    BcaMenuItemRow(
                        title = "Status Transaksi Sakuku",
                        onClick = {
                            alertMessage = "Status Transaksi Sakuku: Semua transaksi Sakuku sebelumnya telah berhasil diproses."
                        }
                    )
                    HorizontalDivider(color = Color(0xFFE8ECEF), thickness = 0.8.dp)

                    BcaMenuItemRow(
                        title = inboxTitle,
                        onClick = onNavigateToInbox
                    )
                }
            }

            // 3. Bottom Navigation Bar: Transaksi Active (Blue)
            BcaBottomNavBar(
                selectedTab = BcaNavTab.TRANSAKSI,
                onTabSelected = onTabSelected,
                tightDome = true,
                modifier = Modifier.zIndex(1f)
            )
        }

        if (alertMessage != null) {
            BcaAlertResultDialog(
                title = "m-Transfer",
                message = alertMessage ?: "",
                onDismiss = { alertMessage = null },
                confirmText = "OK"
            )
        }
    }
}
