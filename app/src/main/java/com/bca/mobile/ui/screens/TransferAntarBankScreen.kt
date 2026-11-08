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
import java.util.Locale

/**
 * Authentic BCA m-Transfer Antar Bank Screen matching:
 * - Form & Layout: WhatsApp Image 2026-11-08 at 18.00.20 (1).jpeg & uploaded_media_2_1794157095212.jpg
 * - Confirmation Dialog: uploaded_media_2_1794157095212.jpg
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

    var selectedService by remember { mutableStateOf("BI FAST") }
    var adminFee by remember { mutableLongStateOf(2500L) }
    var showLayananScreen by remember { mutableStateOf(false) }

    var noteText by remember { mutableStateOf("") }
    var showNoteDialog by remember { mutableStateOf(false) }

    var selectedPurpose by remember { mutableStateOf("Lainnya") }
    var showPurposeDialog by remember { mutableStateOf(false) }

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
            note = noteText,
            service = selectedService
        )
        res.onSuccess { receipt ->
            receiptResult = receipt
        }.onFailure { err ->
            errorMessage = err.message ?: "Transfer antar bank gagal."
        }
    }

    // Sub-screen: Recipient Picker
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

    // Sub-screen: Layanan Transfer Picker (Image 1)
    if (showLayananScreen) {
        LayananTransferScreen(
            onSelectService = { service, fee ->
                selectedService = service
                adminFee = fee
                showLayananScreen = false
            },
            onBack = { showLayananScreen = false }
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

            // 2. Main Content Form Card matching uploaded_media_2_1794157095212.jpg
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

                        // Row 2: Ke Rekening Tujuan
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
                                        text = String.format(Locale.GERMANY, "%,d,00", num),
                                        color = Color(0xFF1E293B),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            BcaChevron(size = 20.dp)
                        }

                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)

                        // Row 4: Layanan Transfer (BI FAST >)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = { showLayananScreen = true }
                                )
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Layanan Transfer",
                                    color = Color(0xFF005CA9),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = selectedService,
                                    color = Color(0xFF1E293B),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            BcaChevron(size = 20.dp)
                        }

                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)

                        // Row 5: Biaya
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Text(
                                text = "Biaya",
                                color = Color(0xFF005CA9),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = String.format(Locale.GERMANY, "%,d,00", adminFee),
                                color = Color(0xFF1E293B),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }

                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)

                        // Row 6: Berita (optional note)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = { showNoteDialog = true }
                                )
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Berita",
                                    color = Color(0xFF005CA9),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (noteText.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = noteText,
                                        color = Color(0xFF1E293B),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                            }

                            BcaChevron(size = 20.dp)
                        }

                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)

                        // Row 7: Tujuan Transaksi (default "Lainnya" >)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = { showPurposeDialog = true }
                                )
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Tujuan Transaksi",
                                    color = Color(0xFF005CA9),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = selectedPurpose,
                                    color = Color(0xFF1E293B),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            BcaChevron(size = 20.dp)
                        }

                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)

                        // Row 8: Dari Rekening (0060387737)
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

    // Note Input Dialog
    if (showNoteDialog) {
        BcaBlackInputDialog(
            title = "Berita:",
            initialValue = noteText,
            isNumericOnly = false,
            maxLength = 40,
            onDismiss = { showNoteDialog = false },
            onConfirm = { note ->
                noteText = note
                showNoteDialog = false
            }
        )
    }

    // Purpose Selection Dialog
    if (showPurposeDialog) {
        val purposeOptions = listOf("Lainnya", "Investasi", "Bisnis", "Pembelian", "Biaya Hidup / Keluarga")
        BcaSelectionDialog(
            title = "Tujuan Transaksi",
            options = purposeOptions,
            selectedOption = selectedPurpose,
            onSelect = { purpose ->
                selectedPurpose = purpose
                showPurposeDialog = false
            },
            onDismiss = { showPurposeDialog = false }
        )
    }

    // Confirmation Dialog matching uploaded_media_2_1794157095212.jpg
    if (showConfirmation) {
        val rec = selectedRecipient
        val amt = amountText.toLongOrNull() ?: 0L
        val formattedAmt = String.format(Locale.US, "%,d.00", amt)
        val formattedFee = String.format(Locale.US, "Rp %,d.00", adminFee)

        BcaConfirmationDialog(
            onDismissRequest = { showConfirmation = false },
            title = null,
            confirmText = "OK",
            cancelText = "Cancel",
            onConfirm = {
                showConfirmation = false
                showPinDialog = true
            },
            onCancel = { showConfirmation = false },
            dialogWidth = 280.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(text = "m-Transfer:", color = Color(0xFF004B87), fontSize = 15.sp, fontWeight = FontWeight.Normal)
                Text(text = "Dari ${account.accountNumber}", color = Color(0xFF004B87), fontSize = 15.sp, fontWeight = FontWeight.Normal)
                Text(text = "Ke Rekening Tujuan", color = Color(0xFF004B87), fontSize = 15.sp, fontWeight = FontWeight.Normal)
                Text(text = selectedBank, color = Color(0xFF004B87), fontSize = 15.sp, fontWeight = FontWeight.Normal)
                Text(text = rec?.accountNumber ?: "", color = Color(0xFF004B87), fontSize = 15.sp, fontWeight = FontWeight.Normal)
                Text(text = rec?.name ?: "", color = Color(0xFF004B87), fontSize = 15.sp, fontWeight = FontWeight.Normal)

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "NOMINAL TRANSFER Rp", color = Color(0xFF004B87), fontSize = 15.sp, fontWeight = FontWeight.Normal)
                Text(text = formattedAmt, color = Color(0xFF004B87), fontSize = 15.sp, fontWeight = FontWeight.Normal)
                Text(text = "BIAYA $formattedFee", color = Color(0xFF004B87), fontSize = 15.sp, fontWeight = FontWeight.Normal)
                Text(text = "LAYANAN $selectedService", color = Color(0xFF004B87), fontSize = 15.sp, fontWeight = FontWeight.Normal)
                Text(text = "TUJUAN TRANSAKSI $selectedPurpose", color = Color(0xFF004B87), fontSize = 15.sp, fontWeight = FontWeight.Normal)
            }
        }
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
