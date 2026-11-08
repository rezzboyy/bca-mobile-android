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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bca.mobile.data.model.Account
import com.bca.mobile.data.model.NetworkSignalState
import com.bca.mobile.data.model.TransferReceipt
import com.bca.mobile.data.model.TransferRecipient
import com.bca.mobile.data.repository.BcaRepository
import com.bca.mobile.ui.components.*

/**
 * Authentic BCA m-Transfer Antar Bank Screen matching:
 * WhatsApp Image 2026-11-08 at 18.00.20 (1).jpeg & WhatsApp Image 2026-11-08 at 18.00.21 (2).jpeg
 */
@Composable
fun TransferAntarBankScreen(
    account: Account,
    recipients: List<TransferRecipient>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedBank by remember { mutableStateOf("- PILIH -") }
    var showBankPicker by remember { mutableStateOf(false) }

    val otherBankRecipients = remember(recipients, selectedBank) {
        if (selectedBank != "- PILIH -") {
            recipients.filter { it.bankName.equals(selectedBank, ignoreCase = true) }
        } else {
            recipients.filter { it.bankName != "BCA" }
        }
    }
    var selectedRecipient by remember { mutableStateOf<TransferRecipient?>(null) }
    var showRecipientPicker by remember { mutableStateOf(false) }

    var amountText by remember { mutableStateOf("") }
    var showAmountDialog by remember { mutableStateOf(false) }

    var showConfirmation by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var receiptResult by remember { mutableStateOf<TransferReceipt?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val repository = remember { BcaRepository.getInstance() }

    fun onSendClicked() {
        if (repository.networkSignal.value == NetworkSignalState.OFFLINE) {
            errorMessage = "205 - Silakan tunggu beberapa saat sampai lampu indikator di pojok kanan atas berwarna hijau sebelum melanjutkan transaksi."
            return
        }
        if (selectedBank == "- PILIH -") {
            errorMessage = "Silakan pilih Bank tujuan terlebih dahulu."
            return
        }
        if (selectedRecipient == null) {
            errorMessage = "Silakan pilih rekening tujuan transfer."
            return
        }
        val amount = amountText.toLongOrNull() ?: 0L
        if (amount <= 0) {
            errorMessage = "Masukkan jumlah uang transfer terlebih dahulu."
            return
        }
        val adminFee = 2500L
        if (amount + adminFee > account.balance) {
            errorMessage = "Saldo tidak mencukupi untuk transfer dan biaya admin."
            return
        }
        errorMessage = null
        showConfirmation = true
    }

    fun executeTransfer() {
        val rec = selectedRecipient ?: return
        val amount = amountText.toLongOrNull() ?: 0L
        val res = repository.transferAntarBank(
            recipientBank = selectedBank,
            recipientAccount = rec.accountNumber,
            recipientName = rec.name,
            amount = amount,
            note = "",
            service = "BI-FAST"
        )
        res.onSuccess { receipt ->
            receiptResult = receipt
        }.onFailure { err ->
            errorMessage = err.message ?: "Transfer antar bank gagal."
        }
    }

    if (showRecipientPicker) {
        TransferRecipientPickerScreen(
            account = account,
            recipients = otherBankRecipients.ifEmpty { recipients.filter { it.bankName != "BCA" } },
            bankFilter = if (selectedBank != "- PILIH -") selectedBank else null,
            onSelectRecipient = { rec ->
                selectedRecipient = rec
                if (selectedBank == "- PILIH -") {
                    selectedBank = rec.bankName
                }
                showRecipientPicker = false
            },
            onBack = { showRecipientPicker = false }
        )
        return
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

            // 2. Main Content Form Card matching WhatsApp Image 2026-11-08 at 18.00.20 (1).jpeg
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
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Row 1: Bank (- PILIH - >)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = { showBankPicker = true }
                                )
                                .padding(horizontal = 16.dp, vertical = 14.dp),
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

                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)

                        // Row 2: Ke Rekening Tujuan (matching screenshot - no chevron if unselected)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = { showRecipientPicker = true }
                                )
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Ke Rekening Tujuan",
                                    color = Color(0xFF005CA9),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (selectedRecipient != null) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${selectedRecipient?.accountNumber} - ${selectedRecipient?.name}",
                                        color = Color(0xFF1E293B),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            if (selectedRecipient != null) {
                                BcaChevron(size = 20.dp)
                            }
                        }

                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)

                        // Row 3: Jumlah Uang >
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = { showAmountDialog = true }
                                )
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Jumlah Uang",
                                    color = Color(0xFF005CA9),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (amountText.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    val num = amountText.toLongOrNull() ?: 0L
                                    Text(
                                        text = BcaRepository.formatRupiah(num),
                                        color = Color(0xFF1E293B),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            BcaChevron(size = 20.dp)
                        }

                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)

                        // Row 4: Dari Rekening (0060387737)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Text(
                                text = "Dari Rekening",
                                color = Color(0xFF005CA9),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = account.accountNumber,
                                color = Color(0xFF1E293B),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }

    // Bank Selection Dialog
    if (showBankPicker) {
        BcaBankSelectionDialog(
            onDismiss = { showBankPicker = false },
            onSelectBank = { bankName ->
                selectedBank = bankName
                showBankPicker = false
                selectedRecipient = null
            }
        )
    }

    // Amount Input Dialog
    if (showAmountDialog) {
        BcaAmountInputDialog(
            initialValue = amountText,
            onDismiss = { showAmountDialog = false },
            onConfirm = { newAmount ->
                amountText = newAmount
                showAmountDialog = false
            }
        )
    }

    // Confirmation Dialog before PIN
    if (showConfirmation) {
        val rec = selectedRecipient
        val amt = amountText.toLongOrNull() ?: 0L
        BcaAlertResultDialog(
            title = "m-Transfer Antar Bank",
            message = "Transfer Antar Bank:\n" +
                    "Bank Tujuan : $selectedBank\n" +
                    "No. Rekening : ${rec?.accountNumber}\n" +
                    "Nama : ${rec?.name}\n" +
                    "Jumlah : ${BcaRepository.formatRupiah(amt)}\n" +
                    "Biaya Admin : Rp 2.500,00 (BI-FAST)\n\n" +
                    "Lanjutkan transaksi?",
            onDismiss = { showConfirmation = false },
            confirmText = "OK",
            onConfirm = {
                showConfirmation = false
                showPinDialog = true
            },
            cancelText = "Cancel"
        )
    }

    // PIN Authentication Dialog
    if (showPinDialog) {
        BcaPinDialog(
            onDismiss = { showPinDialog = false },
            onVerifyPin = { pin -> repository.verifyPin(pin) },
            onSuccess = {
                showPinDialog = false
                executeTransfer()
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

    // Digital Receipt Dialog
    receiptResult?.let { receipt ->
        BcaReceiptDialog(
            receipt = receipt,
            onDismiss = {
                receiptResult = null
                onBack()
            }
        )
    }
}
