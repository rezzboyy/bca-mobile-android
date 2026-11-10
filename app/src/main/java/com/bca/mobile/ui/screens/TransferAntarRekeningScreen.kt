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
        mutableStateOf<TransferRecipient?>(null)
    }
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
                // Baseline 720px width reference = 360dp standard Android canvas (Scale = 2.0x)
                fun refPx(px: Float): androidx.compose.ui.unit.Dp = (px * 0.5f).dp

                // 2. Gray Horizontal Spacing Band below Top Bar (matching uploaded_media_0_1794222646465.jpg)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(refPx(47f))
                        .background(Color(0xFFD9D9D9))
                        .drawBehind {
                            val stroke = refPx(1f).toPx()
                            drawLine(Color(0xFFCCCCCC), Offset(0f, 0f), Offset(size.width, 0f), stroke)
                            drawLine(Color(0xFFCCCCCC), Offset(0f, size.height), Offset(size.width, size.height), stroke)
                        }
                )

                // 3. White Form Section (Full width 720px matching authentic BCA mobile)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    // Row 1: Dari Rekening (height: 113px, margin: 40px, start text: 55px, NO CHEVRON)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(refPx(113f))
                            .padding(start = refPx(55f), end = refPx(40f)),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Dari Rekening",
                            color = Color(0xFF004B87),
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(refPx(6f)))
                        Text(
                            text = account.accountNumber,
                            color = Color(0xFF64748B),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = refPx(40f)),
                        color = Color(0xFFCBD5E1),
                        thickness = refPx(1f)
                    )

                    // Row 2: Ke Rekening (height: 99px, start text: 55px, right chevron at x=641)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(refPx(99f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true),
                                onClick = { showRecipientPicker = true }
                            )
                            .padding(start = refPx(55f), end = refPx(40f)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ke Rekening",
                                color = Color(0xFF004B87),
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(refPx(6f)))
                            Text(
                                text = if (selectedRecipient != null)
                                    "${selectedRecipient?.accountNumber} - ${selectedRecipient?.name}"
                                else
                                    "- PILIH -",
                                color = if (selectedRecipient != null) Color(0xFF64748B) else Color(0xFF707A8A),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }

                        Box(modifier = Modifier.padding(end = refPx(19f))) {
                            BcaChevron(size = 18.dp)
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = refPx(40f)),
                        color = Color(0xFFCBD5E1),
                        thickness = refPx(1f)
                    )

                    // Row 3: Jumlah Uang (Composite box height: 100px, left width: 170px)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = refPx(22f), bottom = refPx(0f))
                    ) {
                        Text(
                            text = "Jumlah Uang",
                            color = Color(0xFF004B87),
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = refPx(55f))
                        )
                        Spacer(modifier = Modifier.height(refPx(10f)))

                        // Composite Input Box
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(refPx(100f))
                                .padding(horizontal = refPx(40f))
                                .border(width = refPx(1f), color = Color(0xFFCBD5E1))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = { showAmountDialog = true }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left part: Light Gray Box with "Rp" and vibrant cyan chevron
                            Row(
                                modifier = Modifier
                                    .width(refPx(170f))
                                    .fillMaxHeight()
                                    .background(Color(0xFFE9E9E9))
                                    .padding(start = refPx(15f), end = refPx(19f)),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Rp",
                                    color = Color(0xFF004B87),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                BcaChevron(size = 18.dp)
                            }

                            // Vertical divider
                            VerticalDivider(color = Color(0xFFCBD5E1), thickness = refPx(1f))

                            // Right part: Amount value and right chevron
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(Color.White)
                                    .padding(start = refPx(14f), end = refPx(19f)),
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
                                    color = Color(0xFF64748B),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal
                                )

                                BcaChevron(size = 18.dp)
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = refPx(40f)),
                        color = Color(0xFFCBD5E1),
                        thickness = refPx(1f)
                    )

                    // Row 4: Berita (height: 114px, start text: 55px, right chevron at x=640)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(refPx(114f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true),
                                onClick = {
                                    tempNote = noteText
                                    showNoteDialog = true
                                }
                            )
                            .padding(start = refPx(55f), end = refPx(40f)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Berita",
                                color = Color(0xFF004B87),
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (noteText.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(refPx(8f)))
                                Text(
                                    text = noteText,
                                    color = Color(0xFF64748B),
                                    fontSize = 14.5.sp
                                )
                            }
                        }

                        Box(modifier = Modifier.padding(end = refPx(19f))) {
                            BcaChevron(size = 18.dp)
                        }
                    }

                    // Bottom card border (full width across x=0..719)
                    HorizontalDivider(
                        color = Color(0xFFDDDDDD),
                        thickness = refPx(1f)
                    )
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

    // Confirmation Dialog before PIN matching uploaded_media_1_1794222646465.jpg
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
            dialogWidth = 266.dp,
            dialogHeightFraction = 0f,
            minHeight = 374.dp,
            buttonSpacing = 4.dp,
            buttonHeight = 39.dp,
            titleBottomSpacing = 16.dp,
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 7.dp),
            contentAlignment = Alignment.Start
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp)
            ) {
                Text(
                    text = "m-Transfer:",
                    color = Color(0xFF004B87),
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = currentTime,
                    color = Color(0xFF004B87),
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "Dari ${account.accountNumber}",
                    color = Color(0xFF004B87),
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "Ke ${rec?.accountNumber ?: ""}",
                    color = Color(0xFF004B87),
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = rec?.name ?: "",
                    color = Color(0xFF004B87),
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = formattedAmt,
                    color = Color(0xFF004B87),
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Normal
                )
                if (noteText.isNotBlank()) {
                    Text(
                        text = "Berita: $noteText",
                        color = Color(0xFF004B87),
                        fontSize = 14.sp,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Pastikan nominal dan nomor\nrekening yang dimasukkan\nsudah sesuai.\nJika terdapat kesalahan data,\nmaka sepenuhnya menjadi\ntanggung jawab nasabah.",
                    color = Color(0xFF004B87),
                    fontSize = 13.sp,
                    lineHeight = 17.5.sp,
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
