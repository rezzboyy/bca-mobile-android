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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bca.mobile.data.model.Account
import com.bca.mobile.data.repository.BcaRepository
import com.bca.mobile.ui.components.BcaPinDialog
import com.bca.mobile.ui.components.BcaSubMenuTopBar

@Composable
fun CardlessScreen(
    account: Account,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nominalOptions = listOf(100_000L, 200_000L, 500_000L, 1_000_000L)
    var selectedNominal by remember { mutableStateOf(nominalOptions[0]) }

    var showPinDialog by remember { mutableStateOf(false) }
    var generatedCode by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val repository = remember { BcaRepository.getInstance() }

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
            title = "Cardless (Tarik Tunai)",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (generatedCode == null) {
                // Source Account Card
                FormSectionCard(title = "DARI REKENING") {
                    Text(
                        text = "${account.accountNumber} - ${account.accountHolder}",
                        color = Color(0xFF002D72),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Saldo: ${BcaRepository.formatRupiah(account.balance)}",
                        color = Color(0xFF00897B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Nominal Picker
                FormSectionCard(title = "PILIH NOMINAL TARIK TUNAI") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        nominalOptions.chunked(2).forEach { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                pair.forEach { nom ->
                                    val isSelected = selectedNominal == nom
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) Color(0xFF005BAA) else Color(0xFFF8FAFC))
                                            .border(
                                                1.5.dp,
                                                if (isSelected) Color(0xFF005BAA) else Color(0xFFCBD5E1),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = rememberRipple(bounded = true),
                                                onClick = { selectedNominal = nom }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = BcaRepository.formatRupiah(nom),
                                            color = if (isSelected) Color.White else Color(0xFF002D72),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFE53935),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

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
                                if (selectedNominal > account.balance) {
                                    errorMessage = "Saldo tidak mencukupi untuk nominal penarikan ini."
                                } else {
                                    errorMessage = null
                                    showPinDialog = true
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LANJUTKAN",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Generated Code View
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(10.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "KODE TARIK TUNAI ANDA",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = generatedCode ?: "",
                            color = Color(0xFF005BAA),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 6.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Berlaku hingga: 60 Menit ke depan",
                            color = Color(0xFFE53935),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Nominal: ${BcaRepository.formatRupiah(selectedNominal)}",
                            color = Color(0xFF1E293B),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Instructions Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE0F2FE))
                        .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "Petunjuk di Mesin ATM BCA:",
                            color = Color(0xFF0369A1),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "1. Pilih menu 'Transaksi Tanpa Kartu' di layar ATM.\n" +
                                    "2. Masukkan nomor HP terdaftar Anda.\n" +
                                    "3. Masukkan 6 digit Kode Tarik Tunai di atas.\n" +
                                    "4. Ambil uang Anda.",
                            color = Color(0xFF0C4A6E),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(buttonGradient)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = true),
                            onClick = { generatedCode = null }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SELESAI / TUTUP",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showPinDialog) {
        BcaPinDialog(
            onDismiss = { showPinDialog = false },
            onSuccess = {
                showPinDialog = false
                repository.triggerProcessingPulse(700)
                generatedCode = repository.generateCardlessCode(selectedNominal)
            },
            onVerifyPin = { repository.verifyPin(it) }
        )
    }
}

@Composable
private fun FormSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
            .padding(14.dp)
    ) {
        Text(
            text = title,
            color = Color(0xFF64748B),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}
