package com.bca.mobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bca.mobile.R
import com.bca.mobile.data.model.Account
import com.bca.mobile.data.model.NetworkSignalState
import com.bca.mobile.data.repository.BcaRepository
import com.bca.mobile.ui.components.BcaAlertResultDialog
import com.bca.mobile.ui.components.BcaBottomNavBar
import com.bca.mobile.ui.components.BcaChevron
import com.bca.mobile.ui.components.BcaNavTab
import com.bca.mobile.ui.components.BcaSubMenuTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MInfoMenuScreen(
    account: Account,
    onBack: () -> Unit,
    onNavigateToMutasi: () -> Unit,
    onNavigateToKurs: () -> Unit,
    onTabSelected: (BcaNavTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showSaldoDialog by remember { mutableStateOf(false) }
    var alertInfoMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val repository = remember { BcaRepository.getInstance() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF002446)) // Authentic Deep Navy matching BCA screenshots
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Top Bar: Pure White with "m-Info" & Green Indicator (no back arrow)
            BcaSubMenuTopBar(
                title = "m-Info",
                onBackClick = onBack,
                showBackButton = false
            )

            // 2. Main Scrollable Content: Floating White Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Outer Rounded Card Container (14dp corner radius, 656px on 720p / 1312px on 1440p)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                ) {
                    // Card Header with m-Info Tile & Title (authentic vertical gradient)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
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
                            painter = painterResource(id = R.drawable.tile_minfo),
                            contentDescription = "m-Info",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(13.dp)),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "m-Info",
                            color = Color(0xFF005CA9),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(color = Color(0xFFD2D8DF), thickness = 0.8.dp)

                    // Group 1: General Info (Directly under Header, no section banner)
                    BcaMenuItemRow(
                        title = "Info Saldo",
                        onClick = {
                            coroutineScope.launch {
                                repository.setNetworkSignal(NetworkSignalState.PROCESSING)
                                delay(300)
                                repository.setNetworkSignal(NetworkSignalState.READY)
                                showSaldoDialog = true
                            }
                        }
                    )
                    HorizontalDivider(color = Color(0xFFE8ECEF), thickness = 0.8.dp)

                    BcaMenuItemRow(
                        title = "Mutasi Rekening",
                        onClick = onNavigateToMutasi
                    )
                    HorizontalDivider(color = Color(0xFFE8ECEF), thickness = 0.8.dp)

                    BcaMenuItemRow(
                        title = "Rekening Deposito",
                        onClick = {
                            alertInfoMessage = "Layanan Rekening Deposito BCA: Anda memiliki 1 bilyet deposito berjangka aktif."
                        }
                    )
                    HorizontalDivider(color = Color(0xFFE8ECEF), thickness = 0.8.dp)

                    BcaMenuItemRow(
                        title = "Info Reward BCA",
                        onClick = {
                            alertInfoMessage = "Total Reward BCA Anda saat ini: 12.450 Poin."
                        }
                    )

                    // Group 2: Info Reksadana
                    BcaMenuSectionBanner(title = "Info Reksadana")

                    BcaMenuItemRow(
                        title = "NAB Reksadana",
                        onClick = {
                            alertInfoMessage = "Informasi Nilai Aktiva Bersih (NAB) Reksadana dapat diakses lengkap via Welma by BCA."
                        }
                    )
                    HorizontalDivider(color = Color(0xFFE8ECEF), thickness = 0.8.dp)

                    BcaMenuItemRow(
                        title = "Saldo Reksadana",
                        onClick = {
                            alertInfoMessage = "Portofolio Saldo Reksadana: Tidak ada transaksi aktif."
                        }
                    )
                    HorizontalDivider(color = Color(0xFFE8ECEF), thickness = 0.8.dp)

                    BcaMenuItemRow(
                        title = "Info Kurs",
                        onClick = onNavigateToKurs
                    )

                    // Group 3: Info RDN
                    BcaMenuSectionBanner(title = "Info RDN")

                    BcaMenuItemRow(
                        title = "Info Saldo",
                        onClick = {
                            alertInfoMessage = "Info Saldo RDN (Rekening Dana Nasabah): Rp 0,00."
                        }
                    )
                    HorizontalDivider(color = Color(0xFFE8ECEF), thickness = 0.8.dp)

                    BcaMenuItemRow(
                        title = "Mutasi Rekening",
                        onClick = {
                            alertInfoMessage = "Mutasi Rekening Dana Nasabah (RDN): Tidak ada mutasi transaksi dalam 30 hari terakhir."
                        }
                    )

                    // Group 4: Info KPR
                    BcaMenuSectionBanner(title = "Info KPR")

                    BcaMenuItemRow(
                        title = "Inquiry Pinjaman",
                        onClick = {
                            alertInfoMessage = "Inquiry Pinjaman KPR: Fasilitas KPR BCA Anda aktif dengan status pembayaran lancar."
                        }
                    )

                    // Group 5: Info Kartu Kredit (Semua 4 Item Lengkap Sesuai Referensi Asli)
                    BcaMenuSectionBanner(title = "Info Kartu Kredit")

                    BcaMenuItemRow(
                        title = "Saldo",
                        onClick = {
                            alertInfoMessage = "Kartu Kredit BCA Everyday Card: Sisa limit Rp 18.500.000,00."
                        }
                    )
                    HorizontalDivider(color = Color(0xFFE8ECEF), thickness = 0.8.dp)

                    BcaMenuItemRow(
                        title = "Transaksi dan Tagihan",
                        onClick = {
                            alertInfoMessage = "Tagihan Kartu Kredit BCA: Tidak ada tagihan baru yang jatuh tempo."
                        }
                    )
                    HorizontalDivider(color = Color(0xFFE8ECEF), thickness = 0.8.dp)

                    BcaMenuItemRow(
                        title = "Lainnya",
                        onClick = {
                            alertInfoMessage = "Layanan Kartu Kredit Lainnya: Informasi aktivasi PIN dan e-Statement dapat diakses via Halo BCA."
                        }
                    )
                    HorizontalDivider(color = Color(0xFFE8ECEF), thickness = 0.8.dp)

                    BcaMenuItemRow(
                        title = "Inbox",
                        onClick = {
                            alertInfoMessage = "Inbox Kartu Kredit: Tidak ada pesan baru."
                        }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            // 3. Unified Bottom Navigation Bar with Floating Elevated QRIS Button
            BcaBottomNavBar(
                selectedTab = BcaNavTab.NONE,
                onTabSelected = onTabSelected
            )
        }

        // Official Saldo Result Alert
        if (showSaldoDialog) {
            val now = java.text.SimpleDateFormat("dd/MM HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
            val saldoFormatted = java.text.DecimalFormat("#,##0.00", java.text.DecimalFormatSymbols(java.util.Locale.US)).format(account.balance)
            val saldoText = "m-Info:\n$now\n\n${account.accountNumber} Rp. $saldoFormatted"

            BcaAlertResultDialog(
                title = "m-Info",
                message = saldoText,
                onDismiss = { showSaldoDialog = false },
                confirmText = "OK"
            )
        }

        // Generic Info Dialog
        if (alertInfoMessage != null) {
            BcaAlertResultDialog(
                title = "m-Info",
                message = alertInfoMessage ?: "",
                onDismiss = { alertInfoMessage = null },
                confirmText = "OK"
            )
        }
    }
}

@Composable
fun BcaMenuSectionBanner(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(Color(0xFF555555))
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun BcaMenuItemRow(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = onClick
            )
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = Color(0xFF0C568E),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )
        BcaChevron(
            size = 18.dp
        )
    }
}
