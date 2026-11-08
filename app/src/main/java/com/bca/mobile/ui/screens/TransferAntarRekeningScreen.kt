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
 * Authentic BCA m-Transfer Antar Rekening Screen matching:
 * - Screenshot_2026-10-28-09-13-09-327_com.bca.jpg.jpeg (No.Rekening Tujuan List)
 * - Screenshot_2026-10-28-09-13-25-578_com.bca.jpg.jpeg (Jumlah Uang Dialog)
 */
@Composable
fun TransferAntarRekeningScreen(
    account: Account,
    recipients: List<TransferRecipient>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bcaRecipients = remember(recipients) { recipients.filter { it.bankName == "BCA" } }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var selectedRecipient by remember { mutableStateOf<TransferRecipient?>(bcaRecipients.firstOrNull()) }

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
        val amount = amountText.toLongOrNull() ?: 0L
        if (selectedRecipient == null) {
            errorMessage = "Pilih rekening tujuan transfer."
            return
        }
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE5E7EB)) // Subtle light background matching authentic BCA
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

        // 2. Main Content Layout
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            // Label: "No.Rekening Tujuan" (Exact match to Screenshot_2026-10-28-09-13-09-327_com.bca.jpg.jpeg)
            Text(
                text = "No.Rekening Tujuan",
                color = Color(0xFF005CA9),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Rekening 1, Rekening 2, Rekening 3 Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val displayList = listOf(
                        "Rekening 1" to (bcaRecipients.getOrNull(0) ?: TransferRecipient("1", "SUTISNA WIJAYA", "5371829012", "BCA")),
                        "Rekening 2" to (bcaRecipients.getOrNull(1) ?: TransferRecipient("2", "RATNA SARI", "2891047123", "BCA")),
                        "Rekening 3" to (bcaRecipients.getOrNull(2) ?: TransferRecipient("3", "AGUS SETIAWAN", "8492018374", "BCA"))
                    )

                    displayList.forEachIndexed { index, (label, recipient) ->
                        val isSelected = selectedIndex == index || (selectedIndex == null && index == 0)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isSelected) Color(0xFFEFF6FF) else Color.White)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = {
                                        selectedIndex = index
                                        selectedRecipient = recipient
                                    }
                                )
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color(0xFF005CA9) else Color(0xFF64748B),
                                    fontSize = 16.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${recipient.accountNumber} - ${recipient.name}",
                                        color = Color(0xFF1E293B),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Text(
                                text = "›",
                                color = Color(0xFF005CA9),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (index < displayList.size - 1) {
                            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Form Card: Jumlah Uang & Dari Rekening
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Jumlah Uang Row (Tapping opens the black Jumlah Uang popup dialog)
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

                    // Dari Rekening Row
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

                    // Berita Transfer
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

    // Black "Jumlah Uang:" Dialog matching Screenshot_2026-10-28-09-13-25-578_com.bca.jpg.jpeg
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

    // Confirmation Alert
    if (showConfirmation) {
        val amount = amountText.toLongOrNull() ?: 0L
        val confMessage = """
m-Transfer:
DARI: ${account.accountNumber}
KE: ${selectedRecipient?.accountNumber}
NAMA: ${selectedRecipient?.name}
JUMLAH: ${BcaRepository.formatRupiah(amount)}
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
                val recipient = selectedRecipient ?: bcaRecipients.first()

                coroutineScope.launch {
                    repository.setNetworkSignal(NetworkSignalState.PROCESSING)
                    delay(1000)
                    repository.setNetworkSignal(NetworkSignalState.READY)
                    val result = repository.transferAntarRekening(
                        recipient = recipient,
                        amount = amount,
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

    // Official Receipt Dialog
    receiptResult?.let { receipt ->
        BcaReceiptDialog(
            receipt = receipt,
            onDismiss = { receiptResult = null }
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
}
