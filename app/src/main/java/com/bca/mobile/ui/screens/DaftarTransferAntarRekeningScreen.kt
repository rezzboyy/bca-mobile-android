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
 * Authentic BCA m-Transfer Daftar Transfer Antar Rekening Screen matching:
 * WhatsApp Image 2026-11-08 at 18.00.21.jpeg
 */
@Composable
fun DaftarTransferAntarRekeningScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var acc1 by remember { mutableStateOf("") }
    var acc2 by remember { mutableStateOf("") }
    var acc3 by remember { mutableStateOf("") }

    var editingSlot by remember { mutableStateOf<Int?>(null) } // 1, 2, or 3
    var tempInputAcc by remember { mutableStateOf("") }

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
        val validList = listOf(acc1.trim(), acc2.trim(), acc3.trim()).filter { it.isNotEmpty() }
        if (validList.isEmpty()) {
            errorMessage = "Silakan masukkan setidaknya satu nomor rekening tujuan."
            return
        }
        errorMessage = null
        showConfirmation = true
    }

    fun handlePinSuccess() {
        showPinDialog = false
        val validList = listOf(acc1.trim(), acc2.trim(), acc3.trim()).filter { it.isNotEmpty() }
        val registered = repository.registerBcaRecipients(validList)
        val namesText = registered.joinToString(separator = "\n") { "${it.accountNumber} - ${it.name}" }
        successMessage = "Nomor rekening berhasil didaftarkan ke Daftar Transfer:\n$namesText"
    }

    BcaMetallicBackground(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Bar: "m-Transfer" + Green Indicator + Glossy "Send" Button (No back arrow, exactly like screenshot)
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

            // 2. Main Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 14.dp)
            ) {
                // Label Outside Card: "No.Rekening Tujuan" (Exact match to WhatsApp Image)
                Text(
                    text = "No.Rekening Tujuan",
                    color = Color(0xFF005CA9),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Card Container with 3 Rows: Rekening 1, Rekening 2, Rekening 3
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Row 1: Rekening 1
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = {
                                        editingSlot = 1
                                        tempInputAcc = acc1
                                    }
                                )
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Rekening 1",
                                    color = if (acc1.isNotEmpty()) Color(0xFF005CA9) else Color(0xFF707A8A),
                                    fontSize = 17.sp,
                                    fontWeight = if (acc1.isNotEmpty()) FontWeight.Bold else FontWeight.Normal
                                )
                                if (acc1.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = acc1,
                                        color = Color(0xFF1E293B),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            BcaChevron(size = 20.dp)
                        }

                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)

                        // Row 2: Rekening 2
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = {
                                        editingSlot = 2
                                        tempInputAcc = acc2
                                    }
                                )
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Rekening 2",
                                    color = if (acc2.isNotEmpty()) Color(0xFF005CA9) else Color(0xFF707A8A),
                                    fontSize = 17.sp,
                                    fontWeight = if (acc2.isNotEmpty()) FontWeight.Bold else FontWeight.Normal
                                )
                                if (acc2.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = acc2,
                                        color = Color(0xFF1E293B),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            BcaChevron(size = 20.dp)
                        }

                        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)

                        // Row 3: Rekening 3
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = {
                                        editingSlot = 3
                                        tempInputAcc = acc3
                                    }
                                )
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Rekening 3",
                                    color = if (acc3.isNotEmpty()) Color(0xFF005CA9) else Color(0xFF707A8A),
                                    fontSize = 17.sp,
                                    fontWeight = if (acc3.isNotEmpty()) FontWeight.Bold else FontWeight.Normal
                                )
                                if (acc3.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = acc3,
                                        color = Color(0xFF1E293B),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
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

    // Authentic BCA Black Input Dialog for Rekening Slot
    if (editingSlot != null) {
        BcaBlackInputDialog(
            title = "No. Rekening Tujuan:",
            initialValue = tempInputAcc,
            isNumericOnly = true,
            maxLength = 12,
            onDismiss = { editingSlot = null },
            onConfirm = { input ->
                when (editingSlot) {
                    1 -> acc1 = input
                    2 -> acc2 = input
                    3 -> acc3 = input
                }
                editingSlot = null
            }
        )
    }

    // Confirmation Dialog before PIN
    if (showConfirmation) {
        val validList = listOf(acc1.trim(), acc2.trim(), acc3.trim()).filter { it.isNotEmpty() }
        val details = validList.mapIndexed { idx, acc ->
            val name = repository.lookupBcaAccountHolder(acc)
            "Rekening ${idx + 1}: $acc\nNama: $name"
        }.joinToString(separator = "\n\n")

        BcaAlertResultDialog(
            title = "m-Transfer",
            message = "Apakah Anda yakin ingin mendaftarkan nomor rekening tujuan berikut ke Daftar Transfer?\n\n$details",
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
