package com.bca.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bca.mobile.data.model.Account
import com.bca.mobile.data.model.NetworkSignalState
import com.bca.mobile.data.model.TransferReceipt
import com.bca.mobile.data.model.TransferRecipient
import com.bca.mobile.data.repository.BcaRepository
import com.bca.mobile.ui.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Authentic BCA m-Transfer Antar Bank Screen matching:
 * - Screenshot_2026-10-28-09-13-28-992_com.bca.jpg.jpeg (Bank Selection Popup)
 * - Screenshot_2026-10-28-09-13-25-578_com.bca.jpg.jpeg (Jumlah Uang Dialog)
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

    val otherBankRecipients = remember(recipients) { recipients.filter { it.bankName != "BCA" } }
    var selectedRecipient by remember { mutableStateOf(otherBankRecipients.firstOrNull()) }
    var showRecipientPicker by remember { mutableStateOf(false) }

    var amountText by remember { mutableStateOf("") }
    var showAmountDialog by remember { mutableStateOf(false) }
    var noteText by remember { mutableStateOf("") }

    var showConfirmation by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var receiptResult by remember { mutableStateOf<TransferReceipt?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

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
        val amount = amountText.toLongOrNull() ?: 0L
        if (selectedRecipient == null) {
            errorMessage = "Pilih rekening tujuan transfer."
            return
        }
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE5E7EB))
    ) {
        // 1. Top Bar: "m-Transfer" + Green Indicator + Glossy "Send" Button
        BcaSubMenuTopBar(
            title = "m-Transfer",
            onBackClick = onBack,
            showBackButton = true,
            trailingAction = {
                BcaTopBarActionButton(
                    text = "Send",
                    onClick = { onSendClicked() }
                )
            }
        )

        // 2. Main Form Card matching Screenshot_2026-10-28-09-13-28-992_com.bca.jpg.jpeg
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
                    // Row 1: Bank Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showBankPicker = true },
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
                                color = if (selectedBank != "- PILIH -") Color(0xFF1E293B) else Color(0xFF64748B),
                                fontSize = 15.sp,
                                fontWeight = if (selectedBank != "- PILIH -") FontWeight.SemiBold else FontWeight.Normal
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
                    Spacer(modifier = Modifier.height(10.dp))

                    // Row 2: Ke Rekening Tujuan
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true),
                                onClick = { showRecipientPicker = true }
                            ),
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
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (selectedRecipient != null)
                                    "${selectedRecipient?.accountNumber} - ${selectedRecipient?.name}"
                                else
                                    "- PILIH REKENING -",
                                color = if (selectedRecipient != null) Color(0xFF1E293B) else Color(0xFF64748B),
                                fontSize = 15.sp,
                                fontWeight = if (selectedRecipient != null) FontWeight.SemiBold else FontWeight.Normal
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
                    Spacer(modifier = Modifier.height(10.dp))

                    // Row 3: Jumlah Uang (Opens black dialog)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true),
                                onClick = { showAmountDialog = true }
                            ),
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
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (amountText.isNotEmpty()) {
                                    val num = amountText.toLongOrNull() ?: 0L
                                    BcaRepository.formatRupiah(num)
                                } else {
                                    "0"
                                },
                                color = if (amountText.isNotEmpty()) Color(0xFF1E293B) else Color(0xFF94A3B8),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
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
                    Spacer(modifier = Modifier.height(10.dp))

                    // Row 4: Dari Rekening
                    Text(
                        text = "Dari Rekening",
                        color = Color(0xFF005CA9),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${account.accountNumber} - ${BcaRepository.formatRupiah(account.balance)}",
                        color = Color(0xFF64748B),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Row 5: Berita Transfer
                    Text(
                        text = "Berita:",
                        color = Color(0xFF005CA9),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    BasicTextField(
                        value = noteText,
                        onValueChange = { if (it.length <= 40) noteText = it },
                        textStyle = TextStyle(color = Color(0xFF1E293B), fontSize = 14.sp),
                        cursorBrush = SolidColor(Color(0xFF005CA9)),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(4.dp))
                            .border(0.8.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }

    // Authentic Black Bank Picker Dialog matching Screenshot_2026-10-28-09-13-28-992_com.bca.jpg.jpeg
    if (showBankPicker) {
        BcaBankSelectionDialog(
            onDismiss = { showBankPicker = false },
            onSelectBank = { bankName ->
                selectedBank = bankName
                showBankPicker = false
            }
        )
    }

    // Black "Jumlah Uang:" Dialog
    if (showAmountDialog) {
        BcaAmountInputDialog(
            initialValue = amountText,
            onDismiss = { showAmountDialog = false },
            onConfirm = { valInput ->
                amountText = valInput
                showAmountDialog = false
            }
        )
    }

    // Recipient Picker Alert
    if (showRecipientPicker) {
        BcaAlertResultDialog(
            title = "Pilih Rekening Tujuan",
            message = "Pilih dari daftar transfer tersimpan:",
            onDismiss = { showRecipientPicker = false },
            confirmText = "Pilih"
        )
    }

    // Confirmation Alert
    if (showConfirmation) {
        val amount = amountText.toLongOrNull() ?: 0L
        val confMessage = """
m-Transfer ANTAR BANK:
BANK: $selectedBank
DARI: ${account.accountNumber}
KE: ${selectedRecipient?.accountNumber}
NAMA: ${selectedRecipient?.name}
JUMLAH: ${BcaRepository.formatRupiah(amount)}
BIAYA: Rp 2.500
BERITA: ${noteText.ifEmpty { "-" }}
        """.trimIndent()

        BcaAlertResultDialog(
            title = "Konfirmasi Transfer",
            message = confMessage,
            onDismiss = { showConfirmation = false },
            confirmText = "Lanjut",
            cancelText = "Batal",
            onConfirm = {
                showConfirmation = false
                showPinDialog = true
            }
        )
    }

    // PIN Verification Dialog
    if (showPinDialog) {
        BcaPinDialog(
            onDismiss = { showPinDialog = false },
            onSuccess = {
                showPinDialog = false
                val amount = amountText.toLongOrNull() ?: 0L
                val recipient = selectedRecipient ?: otherBankRecipients.first()

                coroutineScope.launch {
                    repository.setNetworkSignal(NetworkSignalState.PROCESSING)
                    delay(1000)
                    repository.setNetworkSignal(NetworkSignalState.READY)
                    val result = repository.transferAntarBank(
                        recipientBank = selectedBank,
                        recipientAccount = recipient.accountNumber,
                        recipientName = recipient.name,
                        amount = amount,
                        service = "BI-FAST",
                        note = noteText
                    )
                    receiptResult = result.getOrNull()
                    amountText = ""
                    noteText = ""
                }
            },
            onVerifyPin = { repository.verifyPin(it) }
        )
    }

    // Receipt Dialog
    receiptResult?.let { receipt ->
        BcaReceiptDialog(
            receipt = receipt,
            onDismiss = { receiptResult = null }
        )
    }

    if (errorMessage != null) {
        BcaAlertResultDialog(
            title = "m-Transfer",
            message = errorMessage ?: "",
            onDismiss = { errorMessage = null },
            confirmText = "OK"
        )
    }
}
