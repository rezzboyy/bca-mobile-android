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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
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
 * Reference 1: 9d0ecc1e-d6a3-4e44-a349-30cd4daf8058.jpg (Formulir Transfer)
 * Reference 2: 70b28587-900d-4216-82a6-04d7d28f6d41.jpg (Konfirmasi Dialog m-Transfer)
 */
@Composable
fun TransferAntarRekeningScreen(
    account: Account,
    recipients: List<TransferRecipient>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bcaRecipients = remember(recipients) { recipients.filter { it.bankName == "BCA" } }
    var selectedRecipient by remember {
        mutableStateOf<TransferRecipient?>(
            bcaRecipients.find { it.accountNumber == "3741720467" } ?: bcaRecipients.firstOrNull()
        )
    }
    var showRecipientPicker by remember { mutableStateOf(false) }

    var amountText by remember { mutableStateOf("100000") }
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // 2. Gray Horizontal Spacing Band below Top Bar (matching Image 9d0ecc1e-d6a3-4e44-a349-30cd4daf8058.jpg)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(26.dp)
                        .background(Color(0xFFD6D9DE))
                        .drawBehind {
                            val stroke = 0.8.dp.toPx()
                            drawLine(Color(0xFFC4C8CE), Offset(0f, 0f), Offset(size.width, 0f), stroke)
                            drawLine(Color(0xFFC4C8CE), Offset(0f, size.height), Offset(size.width, size.height), stroke)
                        }
                )

                // 3. White Form Section (Full width without floating card margins)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    // Row 1: Dari Rekening
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        Text(
                            text = "Dari Rekening",
                            color = Color(0xFF005CA9),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = account.accountNumber,
                            color = Color(0xFF555555),
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = Color(0xFFCBD5E1),
                        thickness = 1.dp
                    )

                    // Row 2: Ke Rekening (- PILIH - or Selected Recipient >)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true),
                                onClick = { showRecipientPicker = true }
                            )
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ke Rekening",
                                color = Color(0xFF005CA9),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (selectedRecipient != null)
                                    "${selectedRecipient?.accountNumber} - ${selectedRecipient?.name}"
                                else
                                    "- PILIH -",
                                color = if (selectedRecipient != null) Color(0xFF555555) else Color(0xFF707A8A),
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }

                        BcaChevron(size = 18.dp)
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = Color(0xFFCBD5E1),
                        thickness = 1.dp
                    )

                    // Row 3: Jumlah Uang (Split: [Rp > | Amount >])
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 14.dp)
                    ) {
                        Text(
                            text = "Jumlah Uang",
                            color = Color(0xFF005CA9),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Composite Input Box
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .drawBehind {
                                    val stroke = 1.dp.toPx()
                                    drawLine(Color(0xFFCBD5E1), Offset(0f, 0f), Offset(size.width, 0f), stroke)
                                    drawLine(Color(0xFFCBD5E1), Offset(0f, size.height), Offset(size.width, size.height), stroke)
                                }
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = { showAmountDialog = true }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left part: Light Gray Box with "Rp" and ">"
                            Row(
                                modifier = Modifier
                                    .width(88.dp)
                                    .fillMaxHeight()
                                    .background(Color(0xFFE9ECEF))
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

                            // Vertical divider
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
                                    color = Color(0xFF555555),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal
                                )

                                BcaChevron(size = 18.dp)
                            }
                        }
                    }

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
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Berita",
                                color = Color(0xFF005CA9),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (noteText.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = noteText,
                                    color = Color(0xFF555555),
                                    fontSize = 14.sp
                                )
                            }
                        }

                        BcaChevron(size = 18.dp)
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = Color(0xFFCBD5E1),
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 4. Remaining space showing authentic metallic upward-sloping facet
                Spacer(modifier = Modifier.weight(1f))
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

    // Confirmation Dialog before PIN matching 70b28587-900d-4216-82a6-04d7d28f6d41.jpg
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
            minHeight = 365.dp,
            buttonSpacing = 8.dp,
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "m-Transfer:",
                    color = Color(0xFF004B87),
                    fontSize = 14.5.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = currentTime,
                    color = Color(0xFF004B87),
                    fontSize = 14.5.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "Dari ${account.accountNumber}",
                    color = Color(0xFF004B87),
                    fontSize = 14.5.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "Ke ${rec?.accountNumber ?: ""}",
                    color = Color(0xFF004B87),
                    fontSize = 14.5.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = rec?.name ?: "",
                    color = Color(0xFF004B87),
                    fontSize = 14.5.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = formattedAmt,
                    color = Color(0xFF004B87),
                    fontSize = 14.5.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Normal
                )
                if (noteText.isNotBlank()) {
                    Text(
                        text = "Berita: $noteText",
                        color = Color(0xFF004B87),
                        fontSize = 14.5.sp,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "Pastikan nominal dan nomor rekening yang dimasukkan sudah sesuai.\nJika terdapat kesalahan data, maka sepenuhnya menjadi tanggung jawab nasabah.",
                    color = Color(0xFF004B87),
                    fontSize = 13.5.sp,
                    lineHeight = 18.5.sp,
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
