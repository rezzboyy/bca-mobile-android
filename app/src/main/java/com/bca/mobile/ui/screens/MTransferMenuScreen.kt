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
                showBackButton = false
            )

            // 2. Main Scrollable Content: Floating White Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                // Outer Rounded Card Container
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                ) {
                    // Card Header with m-Transfer Tile & Title
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF6F8FA))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.tile_mtransfer),
                            contentDescription = "m-Transfer",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "m-Transfer",
                            color = Color(0xFF005CA9),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Section 1: Daftar Transfer
                    BcaMenuSectionBanner(title = "Daftar Transfer")

                    BcaMenuItemRow(
                        title = "Antar Rekening",
                        onClick = onNavigateDaftarTransferBca
                    )
                    HorizontalDivider(color = Color(0xFFF0F2F5), thickness = 0.8.dp)

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
                    HorizontalDivider(color = Color(0xFFF0F2F5), thickness = 0.8.dp)

                    BcaMenuItemRow(
                        title = "Antar Bank",
                        onClick = onNavigateTransferBank
                    )
                    HorizontalDivider(color = Color(0xFFF0F2F5), thickness = 0.8.dp)

                    BcaMenuItemRow(
                        title = "BCA Virtual Account",
                        onClick = {
                            alertMessage = "Layanan BCA Virtual Account: Masukkan kode perusahaan dan nomor pelanggan untuk melanjutkan pembayaran."
                        }
                    )
                    HorizontalDivider(color = Color(0xFFF0F2F5), thickness = 0.8.dp)

                    BcaMenuItemRow(
                        title = "Sakuku",
                        onClick = {
                            alertMessage = "Layanan Transfer ke Sakuku: Kirim saldo langsung ke nomor ponsel terdaftar."
                        }
                    )
                    HorizontalDivider(color = Color(0xFFF0F2F5), thickness = 0.8.dp)

                    BcaMenuItemRow(
                        title = "Status Transaksi Sakuku",
                        onClick = {
                            alertMessage = "Status Transaksi Sakuku: Semua transaksi Sakuku sebelumnya telah berhasil diproses."
                        }
                    )
                    HorizontalDivider(color = Color(0xFFF0F2F5), thickness = 0.8.dp)

                    BcaMenuItemRow(
                        title = "Inbox",
                        onClick = onNavigateToInbox
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. Bottom Navigation Bar: Transaksi Active (Blue)
            BcaBottomNavBar(
                selectedTab = BcaNavTab.TRANSAKSI,
                onTabSelected = onTabSelected
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
