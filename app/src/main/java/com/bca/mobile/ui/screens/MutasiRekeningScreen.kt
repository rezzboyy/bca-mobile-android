package com.bca.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bca.mobile.data.model.Account
import com.bca.mobile.data.model.NetworkSignalState
import com.bca.mobile.data.model.Transaction
import com.bca.mobile.data.model.TransactionType
import com.bca.mobile.data.repository.BcaRepository
import com.bca.mobile.ui.components.BcaAlertResultDialog
import com.bca.mobile.ui.components.BcaPinDialog
import com.bca.mobile.ui.components.BcaSubMenuTopBar
import com.bca.mobile.ui.components.BcaTopBarActionButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Authentic BCA Mutasi Rekening Screen matching Screenshot_2026-10-28-09-12-54-686_com.bca.jpg.jpeg
 */
@Composable
fun MutasiRekeningScreen(
    account: Account,
    mutations: List<Transaction>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = remember { BcaRepository.getInstance() }
    val coroutineScope = rememberCoroutineScope()

    var showResultList by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var selectedJenisTransaksi by remember { mutableStateOf("Semua") } // "Semua", "Debet (DB)", "Kredit (CR)"
    var showJenisPicker by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf<String?>(null) }

    val todayFormatted = remember {
        SimpleDateFormat("dd - MM - yyyy", Locale.getDefault()).format(Date())
    }

    val filteredList = remember(mutations, selectedJenisTransaksi) {
        when (selectedJenisTransaksi) {
            "Kredit (CR)" -> mutations.filter { it.type == TransactionType.CR }
            "Debet (DB)" -> mutations.filter { it.type == TransactionType.DB }
            else -> mutations
        }
    }

    fun onSendClicked() {
        if (repository.networkSignal.value == NetworkSignalState.OFFLINE) {
            alertMessage = "205 - Silakan tunggu beberapa saat sampai lampu indikator di pojok kanan atas berwarna hijau sebelum melanjutkan transaksi."
            return
        }
        showPinDialog = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
    ) {
        // 1. Top Bar: "m-Info" + Green Indicator + Glossy "Send" Button
        BcaSubMenuTopBar(
            title = "m-Info",
            onBackClick = onBack,
            showBackButton = true,
            trailingAction = {
                BcaTopBarActionButton(
                    text = "Send",
                    onClick = { onSendClicked() }
                )
            }
        )

        if (!showResultList) {
            // 2. Mutasi Inquiry Form matching Screenshot_2026-10-28-09-12-54-686_com.bca.jpg.jpeg
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Nomor Rekening Row
                        Text(
                            text = "Nomor Rekening:",
                            color = Color(0xFF005CA9),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = account.accountNumber.ifEmpty { "2270216440" },
                            color = Color(0xFF64748B),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Jenis Transaksi Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = { showJenisPicker = true }
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Jenis Transaksi:",
                                    color = Color(0xFF005CA9),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = selectedJenisTransaksi,
                                    color = Color(0xFF64748B),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }

                            Text(
                                text = "›",
                                color = Color(0xFF005CA9),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Periode Mutasi Header
                        Text(
                            text = "Periode Mutasi",
                            color = Color(0xFF005CA9),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dari Tanggal Row
                        Text(
                            text = "Dari Tanggal:",
                            color = Color(0xFF005CA9),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = todayFormatted,
                                color = Color(0xFF005CA9),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sampai Tanggal Row
                        Text(
                            text = "Sampai Tanggal:",
                            color = Color(0xFF005CA9),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = todayFormatted,
                                color = Color(0xFF005CA9),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Catatan Red Text
                        Text(
                            text = "Catatan.",
                            color = Color(0xFFE53935),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "• Periode mutasi yang dapat dipilih 7 hari.\n• Mutasi rekening maksimum 31 hari yang lalu.",
                            color = Color(0xFFE53935),
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        } else {
            // 3. Mutasi Results List View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hasil Mutasi Rekening",
                        color = Color(0xFF005CA9),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ubah Parameter",
                        color = Color(0xFF0070BA),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showResultList = false }
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(10.dp))
                ) {
                    items(filteredList) { tx ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = tx.date,
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                Text(
                                    text = "${if (tx.type == TransactionType.CR) "+ " else "- "}${BcaRepository.formatRupiah(tx.amount)}",
                                    color = if (tx.type == TransactionType.CR) Color(0xFF16A34A) else Color(0xFFDC2626),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = tx.description,
                                color = Color(0xFF1E293B),
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)
                    }
                }
            }
        }
    }

    // Jenis Transaksi Dialog
    if (showJenisPicker) {
        BcaAlertResultDialog(
            title = "Pilih Jenis Transaksi",
            message = "Pilih jenis filter mutasi yang ingin ditampilkan:",
            onDismiss = { showJenisPicker = false },
            confirmText = "Semua"
        )
    }

    // PIN Verification Dialog
    if (showPinDialog) {
        BcaPinDialog(
            onDismiss = { showPinDialog = false },
            onSuccess = {
                showPinDialog = false
                coroutineScope.launch {
                    repository.setNetworkSignal(NetworkSignalState.PROCESSING)
                    delay(800)
                    repository.setNetworkSignal(NetworkSignalState.READY)
                    showResultList = true
                }
            },
            onVerifyPin = { repository.verifyPin(it) }
        )
    }

    if (alertMessage != null) {
        BcaAlertResultDialog(
            title = "m-Info",
            message = alertMessage ?: "",
            onDismiss = { alertMessage = null },
            confirmText = "OK"
        )
    }
}
