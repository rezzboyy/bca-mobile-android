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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bca.mobile.R
import com.bca.mobile.data.model.TransferReceipt
import com.bca.mobile.ui.components.BcaAlertResultDialog
import com.bca.mobile.ui.components.BcaReceiptDialog
import com.bca.mobile.ui.components.BcaSubMenuTopBar
import com.bca.mobile.ui.components.BcaTopBarActionButton

data class BcaInboxItem(
    val id: String,
    val typeTitle: String,
    val line1: String,
    val line2: String? = null,
    val fullReceiptMessage: String,
    val isYellow: Boolean = false
)

/**
 * Authentic BCA m-Transfer Inbox Screen matching Screenshot_2026-10-28-09-13-41-915_com.bca.jpg.jpeg
 */
@Composable
fun TransferInboxScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditMode by remember { mutableStateOf(false) }
    var selectedItemForDetail by remember { mutableStateOf<BcaInboxItem?>(null) }
    var alertMessage by remember { mutableStateOf<String?>(null) }

    val inboxItems = remember {
        mutableStateListOf(
            BcaInboxItem(
                id = "1",
                typeTitle = "Transfer BCA Virtual Account",
                line1 = "16/10/2026 16.44.06 SHOPEE...",
                line2 = null,
                fullReceiptMessage = "TRANSFER BCA VIRTUAL ACCOUNT\nTanggal: 16/10/2026 16:44:06\nNo. VA: 12208123456789\nNama: SHOPEEPAY\nJumlah: Rp 85.000\nSTATUS: BERHASIL"
            ),
            BcaInboxItem(
                id = "2",
                typeTitle = "Transfer Antar Rekening",
                line1 = "16/10/2026 12.51:44 SINDI",
                line2 = "APRILIA",
                fullReceiptMessage = "TRANSFER ANTAR REKENING\nTanggal: 16/10/2026 12:51:44\nKe: 5371829012\nNama: SINDI APRILIA\nJumlah: Rp 45.000\nSTATUS: BERHASIL"
            ),
            BcaInboxItem(
                id = "3",
                typeTitle = "Transfer BCA Virtual Account",
                line1 = "15/10/2026 15.44.56 TIKET....",
                line2 = null,
                fullReceiptMessage = "TRANSFER BCA VIRTUAL ACCOUNT\nTanggal: 15/10/2026 15:44:56\nNo. VA: 010293847561\nNama: TIKET.COM\nJumlah: Rp 220.000\nSTATUS: BERHASIL"
            ),
            BcaInboxItem(
                id = "4",
                typeTitle = "Transfer BCA Virtual Account",
                line1 = "14/10/2026 18.03.04 TRAVEL...",
                line2 = null,
                fullReceiptMessage = "TRANSFER BCA VIRTUAL ACCOUNT\nTanggal: 14/10/2026 18:03:04\nNo. VA: 081293847562\nNama: TRAVELOKA\nJumlah: Rp 120.000\nSTATUS: BERHASIL"
            ),
            BcaInboxItem(
                id = "5",
                typeTitle = "Transfer BCA Virtual Account",
                line1 = "11/10/2026 17.13.30 TRIP.C...",
                line2 = null,
                fullReceiptMessage = "TRANSFER BCA VIRTUAL ACCOUNT\nTanggal: 11/10/2026 17:13:30\nNo. VA: 098123456781\nNama: TRIP.COM\nJumlah: Rp 350.000\nSTATUS: BERHASIL"
            ),
            BcaInboxItem(
                id = "6",
                typeTitle = "Transfer Antar Bank",
                line1 = "11/10/2026 15.19.51 SONY P...",
                line2 = null,
                fullReceiptMessage = "TRANSFER ANTAR BANK\nTanggal: 11/10/2026 15:19:51\nBank: BANK MANDIRI\nKe: 1370018294012\nNama: SONY PRATAMA\nJumlah: Rp 500.000\nSTATUS: BERHASIL"
            ),
            BcaInboxItem(
                id = "7",
                typeTitle = "Transfer BCA Virtual Account",
                line1 = "10/10/2026 03.34.37 DANA",
                line2 = null,
                fullReceiptMessage = "TRANSFER BCA VIRTUAL ACCOUNT\nTanggal: 10/10/2026 03:34:37\nNo. VA: 39010812345678\nNama: DANA TOPUP\nJumlah: Rp 50.000\nSTATUS: BERHASIL"
            ),
            BcaInboxItem(
                id = "8",
                typeTitle = "Transfer BCA Virtual Account",
                line1 = "09/10/2026 23.30.49 DANA",
                line2 = null,
                fullReceiptMessage = "TRANSFER BCA VIRTUAL ACCOUNT\nTanggal: 09/10/2026 23:30:49\nNo. VA: 39010812345678\nNama: DANA TOPUP\nJumlah: Rp 25.000\nSTATUS: BERHASIL"
            ),
            BcaInboxItem(
                id = "9",
                typeTitle = "Transfer Antar Bank",
                line1 = "09/10/2026 21.45.49 RESTU ...",
                line2 = null,
                fullReceiptMessage = "TRANSFER ANTAR BANK\nTanggal: 09/10/2026 21:45:49\nBank: BANK BRI\nKe: 020601058392501\nNama: RESTU ADI\nJumlah: Rp 150.000\nSTATUS: BERHASIL",
                isYellow = true
            ),
            BcaInboxItem(
                id = "10",
                typeTitle = "Transfer Antar Rekening",
                line1 = "09/10/2026 21.38.48 RISKI",
                line2 = "NASRULLOH",
                fullReceiptMessage = "TRANSFER ANTAR REKENING\nTanggal: 09/10/2026 21:38:48\nKe: 2891047123\nNama: RISKI NASRULLOH\nJumlah: Rp 100.000\nSTATUS: BERHASIL"
            ),
            BcaInboxItem(
                id = "11",
                typeTitle = "Transfer Antar Rekening",
                line1 = "03/10/2026 21.22.26",
                line2 = "MUHAMMAD KHOIRUL ANWAR",
                fullReceiptMessage = "TRANSFER ANTAR REKENING\nTanggal: 03/10/2026 21:22:26\nKe: 8492018374\nNama: MUHAMMAD KHOIRUL ANWAR\nJumlah: Rp 200.000\nSTATUS: BERHASIL"
            )
        )
    }

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

        // 2. Transaction Records List matching Screenshot_2026-10-28-09-13-41-915_com.bca.jpg.jpeg
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
                                    inboxItems.remove(item)
                                } else {
                                    selectedItemForDetail = item
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
                            Text(
                                text = "✕",
                                color = Color(0xFFE53935),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 8.dp)
                            )
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

    // Receipt Detail Alert
    selectedItemForDetail?.let { item ->
        val receipt = remember(item) {
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

    return TransferReceipt(
        referenceNo = "INB" + System.currentTimeMillis().toString().takeLast(8),
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
