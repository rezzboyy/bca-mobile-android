package com.bca.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.bca.mobile.data.model.NetworkSignalState
import com.bca.mobile.data.repository.BcaRepository
import com.bca.mobile.ui.components.*

/**
 * Authentic BCA m-Transfer Daftar Transfer Antar Bank Screen
 */
@Composable
fun DaftarTransferAntarBankScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var destinationAccount by remember { mutableStateOf("") }
    var showAccountInputDialog by remember { mutableStateOf(false) }
    var tempAccountInput by remember { mutableStateOf("") }

    var selectedBank by remember { mutableStateOf("- PILIH -") }
    var showBankPicker by remember { mutableStateOf(false) }

    var showConfirmation by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val repository = remember { BcaRepository.getInstance() }

    fun onSendClicked() {
        if (repository.networkSignal.value == NetworkSignalState.OFFLINE) {
            errorMessage = "205 - Silakan tunggu beberapa saat sampai lampu indikator di pojok kanan atas berwarna hijau sebelum melanjutkan transaksi."
            return
        }
        if (destinationAccount.isBlank()) {
            errorMessage = "Silakan masukkan nomor rekening tujuan terlebih dahulu."
            return
        }
        if (selectedBank == "- PILIH -") {
            errorMessage = "Silakan pilih Bank tujuan terlebih dahulu."
            return
        }
        errorMessage = null
        showConfirmation = true
    }

    fun handlePinSuccess() {
        showPinDialog = false
        val holderName = repository.lookupBcaAccountHolder(destinationAccount)
        repository.addRecipient(
            accountNumber = destinationAccount.trim(),
            name = holderName,
            bankName = selectedBank
        )
        successMessage = "Nomor rekening berhasil didaftarkan ke Daftar Transfer:\n$destinationAccount - $holderName ($selectedBank)"
    }

    BcaMetallicBackground(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Bar: "m-Transfer" + Green Indicator + Glossy "Send" Button
            BcaSubMenuTopBar(
                title = "m-Transfer",
                onBackClick = onBack,
                showBackButton = false,
                trailingAction = {
                    BcaTopBarActionButton(
                        text = "Send",
                        onClick = { onSendClicked() }
                    )
                }
            )

            // 2. Main Content Form Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Pendaftaran Rekening Bank Lain",
                    color = Color(0xFF005CA9),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Row 1: No.Rekening Tujuan
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = {
                                        tempAccountInput = destinationAccount
                                        showAccountInputDialog = true
                                    }
                                )
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "No. Rekening Tujuan",
                                    color = Color(0xFF005CA9),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = destinationAccount.ifEmpty { "- INPUT REKENING -" },
                                    color = if (destinationAccount.isNotEmpty()) Color(0xFF1E293B) else Color(0xFF707A8A),
                                    fontSize = 15.sp,
                                    fontWeight = if (destinationAccount.isNotEmpty()) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }

                            BcaChevron(size = 20.dp)
                        }

                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)

                        // Row 2: Bank
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = { showBankPicker = true }
                                )
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Bank",
                                    color = Color(0xFF005CA9),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = selectedBank,
                                    color = if (selectedBank != "- PILIH -") Color(0xFF1E293B) else Color(0xFF707A8A),
                                    fontSize = 15.sp,
                                    fontWeight = if (selectedBank != "- PILIH -") FontWeight.SemiBold else FontWeight.Normal
                                )
                            }

                            BcaChevron(size = 20.dp)
                        }
                    }
                }
            }
        }
    }

    // Account Input Dialog
    if (showAccountInputDialog) {
        BcaBlackInputDialog(
            title = "No. Rekening Tujuan:",
            initialValue = destinationAccount,
            isNumericOnly = true,
            maxLength = 18,
            onDismiss = { showAccountInputDialog = false },
            onConfirm = { input ->
                destinationAccount = input
                showAccountInputDialog = false
            }
        )
    }

    // Bank Selection Dialog
    if (showBankPicker) {
        BcaBankSelectionDialog(
            onDismiss = { showBankPicker = false },
            onSelectBank = { bank ->
                selectedBank = bank
                showBankPicker = false
            }
        )
    }

    // Confirmation Dialog before PIN
    if (showConfirmation) {
        val holderName = repository.lookupBcaAccountHolder(destinationAccount)
        BcaAlertResultDialog(
            title = "m-Transfer",
            message = "Daftar Transfer Antar Bank:\n" +
                    "Bank Tujuan : $selectedBank\n" +
                    "No. Rekening : $destinationAccount\n" +
                    "Nama Penerima : $holderName\n\n" +
                    "Lanjutkan pendaftaran?",
            onDismiss = { showConfirmation = false },
            confirmText = "OK",
            onConfirm = {
                showConfirmation = false
                showPinDialog = true
            },
            cancelText = "Batal"
        )
    }

    // PIN Authentication Dialog
    if (showPinDialog) {
        BcaPinDialog(
            onDismiss = { showPinDialog = false },
            onVerifyPin = { pin -> repository.verifyPin(pin) },
            onSuccess = {
                handlePinSuccess()
            }
        )
    }

    // Error Alert
    if (errorMessage != null) {
        BcaAlertResultDialog(
            title = "m-Transfer",
            message = errorMessage ?: "",
            onDismiss = { errorMessage = null },
            confirmText = "OK"
        )
    }

    // Success Alert
    if (successMessage != null) {
        BcaAlertResultDialog(
            title = "m-Transfer",
            message = successMessage ?: "",
            onDismiss = {
                successMessage = null
                onBack()
            },
            confirmText = "OK"
        )
    }
}
