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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.bca.mobile.data.model.Account
import com.bca.mobile.data.model.NetworkSignalState
import com.bca.mobile.data.model.TransferReceipt
import com.bca.mobile.data.model.TransferRecipient
import com.bca.mobile.data.repository.BcaRepository
import com.bca.mobile.ui.components.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Authentic BCA m-Transfer Antar Rekening Screen matching:
 * WhatsApp Image 2026-11-08 at 18.00.21 (1).jpeg
 */
@Composable
fun TransferAntarRekeningScreen(
    account: Account,
    recipients: List<TransferRecipient>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bcaRecipients = remember(recipients) { recipients.filter { it.bankName == "BCA" } }
    var selectedRecipient by remember { mutableStateOf<TransferRecipient?>(null) }
    var showRecipientPicker by remember { mutableStateOf(false) }

    var amountText by remember { mutableStateOf("") }
    var showAmountDialog by remember { mutableStateOf(false) }

    var noteText by remember { mutableStateOf("") }
    var showNoteDialog by remember { mutableStateOf(false) }
    var tempNote by remember { mutableStateOf("") }

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
        if (selectedRecipient == null) {
            errorMessage = "Pilih rekening tujuan transfer."
            return
        }
        val amount = amountText.toLongOrNull() ?: 0L
        if (amount <= 0) {
            errorMessage = "Masukkan jumlah uang transfer terlebih dahulu."
            return
        }
        if (amount > account.balance) {
            errorMessage = "Saldo tidak mencukupi untuk melakukan transfer ini."
            return
        }
        errorMessage = null
        showConfirmation = true
    }

    fun executeTransfer() {
        val rec = selectedRecipient ?: return
        val amount = amountText.toLongOrNull() ?: 0L
        val res = repository.transferAntarRekening(rec, amount, noteText)
        res.onSuccess { receipt ->
            receiptResult = receipt
        }.onFailure { err ->
            errorMessage = err.message ?: "Transfer gagal diproses."
        }
    }

    if (showRecipientPicker) {
        TransferRecipientPickerScreen(
            account = account,
            recipients = bcaRecipients,
            bankFilter = "BCA",
            onSelectRecipient = { rec ->
                selectedRecipient = rec
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

            // 2. Main Content Form Card
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
                        // Row 1: Dari Rekening
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

                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)

                        // Row 2: Ke Rekening (- PILIH - >)
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
                                    text = "Ke Rekening",
                                    color = Color(0xFF005CA9),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (selectedRecipient != null)
                                        "${selectedRecipient?.accountNumber} - ${selectedRecipient?.name}"
                                    else
                                        "- PILIH -",
                                    color = if (selectedRecipient != null) Color(0xFF1E293B) else Color(0xFF707A8A),
                                    fontSize = 15.sp,
                                    fontWeight = if (selectedRecipient != null) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }

                            BcaChevron(size = 20.dp)
                        }

                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)

                        // Row 3: Jumlah Uang (Split: Rp > | >)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Text(
                                text = "Jumlah Uang",
                                color = Color(0xFF005CA9),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Split Box matching WhatsApp Image 2026-11-08 at 18.00.21 (1).jpeg
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = rememberRipple(bounded = true),
                                        onClick = { showAmountDialog = true }
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left part: Gray Box with "Rp" and ">"
                                Row(
                                    modifier = Modifier
                                        .width(90.dp)
                                        .fillMaxHeight()
                                        .background(Color(0xFFE5E7EB))
                                        .padding(horizontal = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Rp",
                                        color = Color(0xFF005CA9),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    BcaChevron(size = 16.dp)
                                }

                                VerticalDivider(color = Color(0xFFCBD5E1), thickness = 1.dp)

                                // Right part: Amount value and right chevron
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(Color.White)
                                        .padding(horizontal = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (amountText.isNotEmpty()) {
                                            val num = amountText.toLongOrNull() ?: 0L
                                            BcaRepository.formatNumberOnly(num)
                                        } else {
                                            ""
                                        },
                                        color = Color(0xFF1E293B),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    BcaChevron(size = 16.dp)
                                }
                            }
                        }

                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)

                        // Row 4: Berita (Berita >)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = {
                                        tempNote = noteText
                                        showNoteDialog = true
                                    }
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
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            BcaChevron(size = 20.dp)
                        }
                    }
                }
            }
        }
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

    // Transfer Note Dialog
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

    // Confirmation Dialog before PIN matching uploaded_media_3_1794157095212.jpg
    if (showConfirmation) {
        val rec = selectedRecipient
        val amt = amountText.toLongOrNull() ?: 0L
        val formattedAmt = String.format(Locale.GERMANY, "Rp %,d,00", amt)
        val currentTime = remember {
            SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        }

        BcaConfirmationDialog(
            onDismissRequest = { showConfirmation = false },
            title = "m-Transfer",
            confirmText = "OK",
            cancelText = "Cancel",
            onConfirm = {
                showConfirmation = false
                showPinDialog = true
            },
            onCancel = { showConfirmation = false },
            dialogWidth = 260.dp,
            minHeight = 365.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(text = "m-Transfer:", color = Color(0xFF004B87), fontSize = 14.sp, fontWeight = FontWeight.Normal)
                Text(text = currentTime, color = Color(0xFF004B87), fontSize = 14.sp, fontWeight = FontWeight.Normal)
                Text(text = "Dari ${account.accountNumber}", color = Color(0xFF004B87), fontSize = 14.sp, fontWeight = FontWeight.Normal)
                Text(text = "Ke ${rec?.accountNumber ?: ""}", color = Color(0xFF004B87), fontSize = 14.sp, fontWeight = FontWeight.Normal)
                Text(text = rec?.name ?: "", color = Color(0xFF004B87), fontSize = 14.sp, fontWeight = FontWeight.Normal)
                Text(text = formattedAmt, color = Color(0xFF004B87), fontSize = 14.sp, fontWeight = FontWeight.Normal)

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Pastikan nominal dan nomor rekening yang dimasukkan sudah sesuai.\nJika terdapat kesalahan data, maka sepenuhnya menjadi tanggung jawab nasabah.",
                    color = Color(0xFF004B87),
                    fontSize = 13.5.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Normal
                )
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
