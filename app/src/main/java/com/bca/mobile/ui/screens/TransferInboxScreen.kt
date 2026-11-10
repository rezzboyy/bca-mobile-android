package com.bca.mobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bca.mobile.R
import com.bca.mobile.data.model.BcaInboxItem
import com.bca.mobile.data.model.TransferReceipt
import com.bca.mobile.data.repository.BcaRepository
import com.bca.mobile.ui.components.BcaAlertResultDialog
import com.bca.mobile.ui.components.BcaReceiptDialog
import com.bca.mobile.ui.components.BcaSubMenuTopBar
import com.bca.mobile.ui.components.BcaTopBarActionButton

/**
 * Authentic BCA m-Transfer Inbox Screen matching Screenshot_2026-10-28-09-13-41-915_com.bca.jpg.jpeg
 * Connected to BcaRepository: automatically records transactions, supports item deletion and clear all.
 */
@Composable
fun TransferInboxScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    repository: BcaRepository = remember { BcaRepository.getInstance() }
) {
    var isEditMode by remember { mutableStateOf(false) }
    var selectedItemForDetail by remember { mutableStateOf<BcaInboxItem?>(null) }
    var alertMessage by remember { mutableStateOf<String?>(null) }

    val inboxItems by repository.inboxItems.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 1. Top Bar: "m-Transfer" (Centered) + Green Indicator + Glossy "Edit" Button (No back button)
        BcaSubMenuTopBar(
            title = "m-Transfer",
            onBackClick = onBack,
            showBackButton = false,
            trailingAction = {
                if (isEditMode) {
                    BcaTopBarActionButton(
                        text = "Done",
                        onClick = {
                            isEditMode = false
                            alertMessage = "Perubahan tersimpan."
                        }
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.btn_topbar_edit_exact),
                        contentDescription = "Edit",
                        modifier = Modifier
                            .size(width = 64.dp, height = 38.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = false),
                                onClick = { isEditMode = true }
                            ),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        )

        // Banner Hapus Semua saat Edit Mode
        if (isEditMode && inboxItems.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF1F0))
                    .clickable {
                        repository.clearInbox()
                        isEditMode = false
                        alertMessage = "Semua bukti transaksi di Inbox telah dihapus."
                    }
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hapus Semua (${inboxItems.size} item)",
                    color = Color(0xFFD32F2F),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Hapus",
                    color = Color(0xFFD32F2F),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            HorizontalDivider(color = Color(0xFFFFCDD2), thickness = 0.8.dp)
        }

        // 2. Main Content: Empty State vs Transaction Records List
        if (inboxItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_inbox_envelope_blue),
                        contentDescription = null,
                        modifier = Modifier.size(width = 44.dp, height = 40.dp),
                        alpha = 0.4f,
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Belum ada bukti transaksi di Inbox",
                        color = Color(0xFF6B8096),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Transaction Records List matching Screenshot_2026-10-28-09-13-41-915_com.bca.jpg.jpeg
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(inboxItems, key = { it.id }) { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true),
                                onClick = {
                                    if (isEditMode) {
                                        repository.deleteInboxItem(item.id)
                                    } else {
                                        repository.markInboxItemAsRead(item.id)
                                        selectedItemForDetail = item.copy(isYellow = true)
                                    }
                                }
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 18.dp, end = 15.dp, top = 10.dp, bottom = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Official cropped SVG/PNG envelope icon (56x53px at 720p = 28x26.5dp)
                            Image(
                                painter = painterResource(
                                    id = if (item.isYellow) R.drawable.ic_inbox_envelope_yellow else R.drawable.ic_inbox_envelope_blue
                                ),
                                contentDescription = "Inbox Status",
                                modifier = Modifier.size(width = 28.dp, height = 26.dp),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = item.typeTitle,
                                    color = Color(0xFF0C557F),
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Normal,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = item.line1,
                                    color = Color(0xFF0C557F),
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Normal,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (item.line2 != null) {
                                    Text(
                                        text = item.line2,
                                        color = Color(0xFF0C557F),
                                        fontSize = 15.sp,
                                        lineHeight = 22.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            if (isEditMode) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = rememberRipple(bounded = false, radius = 18.dp),
                                            onClick = { repository.deleteInboxItem(item.id) }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "✕",
                                        color = Color(0xFFE53935),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_inbox_chevron),
                                    contentDescription = "Open item",
                                    modifier = Modifier
                                        .padding(start = 6.dp)
                                        .size(width = 6.dp, height = 11.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFFD1D1D1), thickness = 0.8.dp)
                    }
                }
            }
        }
    }

    // Receipt Detail Alert
    selectedItemForDetail?.let { item ->
        val receipt = item.receipt ?: remember(item) {
            parseInboxToReceipt(item)
        }
        BcaReceiptDialog(
            receipt = receipt,
            onDismiss = { selectedItemForDetail = null }
        )
    }

    if (alertMessage != null) {
        BcaAlertResultDialog(
            title = "m-Transfer",
            message = alertMessage ?: "",
            onDismiss = { alertMessage = null },
            confirmText = "OK"
        )
    }
}

private fun parseInboxToReceipt(item: BcaInboxItem): TransferReceipt {
    val msg = item.fullReceiptMessage
    val dateRegex = Regex("""(?:Tanggal|TGL):\s*([^\n]+)""", RegexOption.IGNORE_CASE)
    val accRegex = Regex("""(?:Ke|No\. VA):\s*([^\n]+)""", RegexOption.IGNORE_CASE)
    val nameRegex = Regex("""Nama:\s*([^\n]+)""", RegexOption.IGNORE_CASE)
    val amtRegex = Regex("""Jumlah:\s*Rp\s*([\d\.]+)""", RegexOption.IGNORE_CASE)
    val bankRegex = Regex("""Bank:\s*([^\n]+)""", RegexOption.IGNORE_CASE)

    val timestamp = dateRegex.find(msg)?.groupValues?.get(1)?.trim() ?: "16/10/2026 12:00:00"
    val acc = accRegex.find(msg)?.groupValues?.get(1)?.trim() ?: item.line1.substringBefore(" ")
    val name = nameRegex.find(msg)?.groupValues?.get(1)?.trim() ?: "PENERIMA"
    val amtRaw = amtRegex.find(msg)?.groupValues?.get(1)?.replace(".", "")?.toLongOrNull() ?: 50000L
    val bank = bankRegex.find(msg)?.groupValues?.get(1)?.trim() ?: if (item.typeTitle.contains("BANK", ignoreCase = true)) "BANK LAIN" else "BCA"

    val refNo = if (item.id.length >= 35 && item.id.startsWith("95031")) {
        item.id
    } else {
        BcaRepository.getInstance().generateBcaReferenceNumber(accountNum = acc)
    }

    return TransferReceipt(
        referenceNo = refNo,
        timestamp = timestamp,
        sourceAccount = "2270216440",
        destinationAccount = acc,
        destinationName = name,
        destinationBank = bank,
        amount = amtRaw,
        adminFee = if (bank.equals("BCA", ignoreCase = true)) 0L else 6500L,
        note = "",
        status = "BERHASIL",
        service = "Realtime Online"
    )
}
