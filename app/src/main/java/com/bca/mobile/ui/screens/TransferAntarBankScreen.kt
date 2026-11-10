package com.bca.mobile.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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

    // Authentic BCA logic: Layanan Transfer & Biaya appear immediately when Bank and Ke Rekening Tujuan are selected
    val canShowTransferService = selectedBank != "- PILIH -" && selectedRecipient != null

    var selectedService by remember { mutableStateOf("BI FAST") }
    var adminFee by remember { mutableLongStateOf(2500L) }
    var showLayananScreen by remember { mutableStateOf(false) }

    var noteText by remember { mutableStateOf("") }
    var showBeritaDialog by remember { mutableStateOf(false) }

    var selectedPurpose by remember { mutableStateOf("") }
    var showPurposeDialog by remember { mutableStateOf(false) }

    var showConfirmation by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var receiptResult by remember { mutableStateOf<TransferReceipt?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val repository = remember { BcaRepository.getInstance() }

    fun onSendClicked() {
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

            // 2. Main Content Form Card matching uploaded_media_1794213286323.jpg 100%
            val formCardMarginHorizontalDp = 16.dp // Exact 32px on 720px baseline (656px card width on 720px)
            val formCardMarginTopDp = 16.dp // Exact 32px gap below top bar
            val rowHeightDp = 50.dp // Exact 100px uniform row height
            val scrollState = rememberScrollState()

            LaunchedEffect(showAmountDialog, showBankPicker, showRecipientPicker, showBeritaDialog, showPurposeDialog) {
                if (!showAmountDialog && !showBankPicker && !showRecipientPicker && !showBeritaDialog && !showPurposeDialog) {
                    scrollState.scrollTo(0)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = formCardMarginHorizontalDp)
            ) {
                Spacer(modifier = Modifier.height(formCardMarginTopDp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Row 1: Bank (- PILIH - > / SEABANK >)
                        FormCardRow(
                            title = "Bank",
                            subtitle = selectedBank,
                            subtitleColor = if (selectedBank != "- PILIH -") Color(0xFF64748B) else Color(0xFF707A8A),
                            hasChevron = true,
                            rowHeight = rowHeightDp,
                            onClick = { showBankPicker = true }
                        )

                        HorizontalDivider(color = Color(0xFFCBD5E1), thickness = 0.8.dp)

                        // Row 2: Ke Rekening Tujuan (901717125251 - BENI SURYAWAN >)
                        FormCardRow(
                            title = "Ke Rekening Tujuan",
                            subtitle = selectedRecipient?.let { "${it.accountNumber} - ${it.name}" },
                            hasChevron = selectedBank != "- PILIH -" || selectedRecipient != null,
                            rowHeight = rowHeightDp,
                            onClick = if (selectedBank != "- PILIH -") { { showRecipientPicker = true } } else null
                        )

                        HorizontalDivider(color = Color(0xFFCBD5E1), thickness = 0.8.dp)

                        // Row 3: Jumlah Uang (200.000 >)
                        FormCardRow(
                            title = "Jumlah Uang",
                            subtitle = if (amountText.isNotEmpty()) {
                                val num = amountText.toLongOrNull() ?: 0L
                                String.format(Locale.GERMANY, "%,d", num)
                            } else null,
                            hasChevron = true,
                            rowHeight = rowHeightDp,
                            onClick = { showAmountDialog = true }
                        )

                        HorizontalDivider(color = Color(0xFFCBD5E1), thickness = 0.8.dp)

                        // Rows 4, 5, 6, 7: Layanan Transfer, Biaya, Berita, Tujuan Transaksi
                        // Muncul secara dinamis ("nyusul muncul") ketika Bank dan Ke Rekening Tujuan sudah dipilih
                        AnimatedVisibility(
                            visible = canShowTransferService,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Row 4: Layanan Transfer (BI FAST >)
                                FormCardRow(
                                    title = "Layanan Transfer",
                                    subtitle = selectedService,
                                    hasChevron = true,
                                    rowHeight = rowHeightDp,
                                    onClick = { showLayananScreen = true }
                                )

                                HorizontalDivider(color = Color(0xFFCBD5E1), thickness = 0.8.dp)

                                // Row 5: Biaya (Format: 2.500 tanpa chevron)
                                FormCardRow(
                                    title = "Biaya",
                                    subtitle = String.format(Locale.GERMANY, "%,d", adminFee),
                                    hasChevron = false,
                                    rowHeight = rowHeightDp,
                                    onClick = null
                                )

                                HorizontalDivider(color = Color(0xFFCBD5E1), thickness = 0.8.dp)

                                // Row 6: Berita (Opsi: Investasi, Pemindahan Dana, Pembelian, Lainnya)
                                FormCardRow(
                                    title = "Berita",
                                    subtitle = if (noteText.isNotEmpty()) noteText else null,
                                    hasChevron = true,
                                    rowHeight = rowHeightDp,
                                    onClick = { showBeritaDialog = true }
                                )

                                HorizontalDivider(color = Color(0xFFCBD5E1), thickness = 0.8.dp)

                                // Row 7: Tujuan Transaksi
                                FormCardRow(
                                    title = "Tujuan Transaksi",
                                    subtitle = if (selectedPurpose.isNotEmpty()) selectedPurpose else null,
                                    hasChevron = true,
                                    rowHeight = rowHeightDp,
                                    onClick = { showPurposeDialog = true }
                                )

                                HorizontalDivider(color = Color(0xFFCBD5E1), thickness = 0.8.dp)
                            }
                        }

                        // Row 8: Dari Rekening (0060387737 tanpa chevron)
                        FormCardRow(
                            title = "Dari Rekening",
                            subtitle = account.accountNumber,
                            hasChevron = false,
                            rowHeight = rowHeightDp,
                            onClick = null
                        )
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
            initialValue = if (amountText.isEmpty()) "200000" else amountText,
            onDismiss = { showAmountDialog = false },
            onConfirm = { newAmount ->
                amountText = newAmount
                showAmountDialog = false
            }
        )
    }

    // Berita Selection Dialog matching uploaded_media_1794212355871 exactly
    if (showBeritaDialog) {
        val beritaOptions = listOf("Investasi", "Pemindahan Dana", "Pembelian", "Lainnya")
        BcaSelectionDialog(
            title = null,
            options = beritaOptions,
            selectedOption = noteText,
            onSelect = { option ->
                noteText = option
                showBeritaDialog = false
            },
            onDismiss = { showBeritaDialog = false }
        )
    }

    // Purpose Selection Dialog
    if (showPurposeDialog) {
        val purposeOptions = listOf("Investasi", "Pemindahan Dana", "Pembelian", "Lainnya")
        BcaSelectionDialog(
            title = null,
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
            title = "m-Transfer",
            confirmText = "OK",
            cancelText = "Cancel",
            onConfirm = {
                showConfirmation = false
                showPinDialog = true
            },
            onCancel = { showConfirmation = false },
            dialogWidth = 280.dp,
            dialogHeightFraction = 0f,
            minHeight = 440.dp,
            buttonSpacing = 6.dp,
            buttonHeight = 42.dp,
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            contentAlignment = Alignment.Start
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "m-Transfer:", color = Color(0xFF004B87), fontSize = 14.5.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal)
                Text(text = "Dari ${account.accountNumber}", color = Color(0xFF004B87), fontSize = 14.5.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal)
                Text(text = "Ke Rekening Tujuan", color = Color(0xFF004B87), fontSize = 14.5.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal)
                Text(text = selectedBank, color = Color(0xFF004B87), fontSize = 14.5.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal)
                Text(text = rec?.accountNumber ?: "", color = Color(0xFF004B87), fontSize = 14.5.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal)
                Text(text = rec?.name ?: "", color = Color(0xFF004B87), fontSize = 14.5.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal)

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "NOMINAL TRANSFER Rp", color = Color(0xFF004B87), fontSize = 14.5.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal)
                Text(text = formattedAmt, color = Color(0xFF004B87), fontSize = 14.5.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal)
                Text(text = "BIAYA $formattedFee", color = Color(0xFF004B87), fontSize = 14.5.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal)
                Text(text = "LAYANAN $selectedService", color = Color(0xFF004B87), fontSize = 14.5.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal)
                if (noteText.isNotBlank()) {
                    Text(text = "BERITA $noteText", color = Color(0xFF004B87), fontSize = 14.5.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal)
                }
                if (selectedPurpose.isNotBlank()) {
                    Text(text = "TUJUAN TRANSAKSI $selectedPurpose", color = Color(0xFF004B87), fontSize = 14.5.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal)
                }
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

    // Digital Receipt Dialog (Tanpa Nomor Referensi saat transaksi langsung)
    receiptResult?.let { receipt ->
        BcaReceiptDialog(
            receipt = receipt,
            onDismiss = {
                receiptResult = null
                onBack()
            },
            showReference = false
        )
    }
}

/**
 * Authentic BCA Transfer Form Row
 * Matches uploaded_media_1794213286323.jpg:
 * - Exactly 100px uniform row height on all rows
 * - Exactly 12px left indent for title & subtitle
 * - Exactly 14px right indent for chevron
 * - Exactly 20px top indent for title text
 * - Subtitle font 14sp #64748B
 * - Chevron vertically centered in row
 */
@Composable
private fun FormCardRow(
    title: String,
    subtitle: String?,
    hasChevron: Boolean,
    rowHeight: Dp,
    subtitleColor: Color = Color(0xFF64748B),
    onClick: (() -> Unit)? = null
) {
    val startPadding = 8.dp
    val endPadding = 14.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = true),
                        onClick = onClick
                    )
                } else Modifier
            )
            .padding(start = startPadding, end = endPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                color = Color(0xFF005CA9),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!subtitle.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = subtitleColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (hasChevron) {
            BcaChevron(size = 18.dp, color = Color(0xFF0081C6))
        }
    }
}
