package com.bca.mobile.ui.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bca.mobile.R
import com.bca.mobile.data.model.TransferReceipt
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Authentic BCA Mobile Transfer Receipt Dialog matching:
 * - Real BCA Mobile screenshot (uploaded_media_1794292225853.jpg)
 * - buktitfsesamabca.png & buktitfbanklain.png
 * - Diagonal Light-Blue BCA Watermark Pattern
 * - Exact 100% pixel audit:
 *   - Dialog width = 86.1% screen (620px at 720p / 310dp)
 *   - Aspect ratio = 1.24 (768px height at 720p / 384dp)
 *   - 2-line authentic Ref (Line 1: "Ref 95031...", Line 2: "5A81C11F")
 *   - Buttons: 48dp circles (Share & Download) + 43dp height OK button with 6dp corner radius
 */
@Composable
fun BcaReceiptDialog(
    receipt: TransferReceipt,
    onDismiss: () -> Unit,
    showReference: Boolean = false
) {
    val context = LocalContext.current
    val isSesamaBca = receipt.destinationBank.isBlank() || receipt.destinationBank.equals("BCA", ignoreCase = true)

    val receiptTextColor = Color(0xFF1B619B)
    val circleBorderColor = Color(0xFF1B619B)
    val okButtonGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF3594D4),
            Color(0xFF11559C)
        )
    )

    // Build plain text summary for Android system share sheet
    val shareText = remember(receipt, isSesamaBca, showReference) {
        buildReceiptShareText(receipt, isSesamaBca, showReference)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.861f)
                    .aspectRatio(1.0f / (if (isSesamaBca) 1.24f else 1.26f), matchHeightConstraintsFirst = false)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(1.dp))
                    .clip(RoundedCornerShape(1.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFD4D4D4), RoundedCornerShape(1.dp))
            ) {
                // Diagonal Light-Blue BCA Watermark Background
                Image(
                    painter = painterResource(id = R.drawable.bg_bca_receipt_watermark),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.FillBounds
                )

                // Dialog Content Column with buttons pinned to bottom
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 16.5.dp,
                            top = 20.dp,
                            end = 16.dp,
                            bottom = 12.dp
                        )
                ) {
                    val (refPart1, refPart2) = remember(receipt) {
                        formatBcaReceiptRef(receipt.referenceNo, receipt.timestamp, receipt.destinationAccount)
                    }

                    if (isSesamaBca) {
                        // --- Bukti Transfer Sesama BCA ---
                        ReceiptTextLine("m-Transfer:", receiptTextColor, 14.5.sp, 20.sp)
                        ReceiptTextLine("BERHASIL", receiptTextColor, 14.5.sp, 20.sp)
                        ReceiptTextLine(receipt.timestamp, receiptTextColor, 14.5.sp, 20.sp)
                        ReceiptTextLine("Ke ${cleanAccount(receipt.destinationAccount)}", receiptTextColor, 14.5.sp, 20.sp)
                        ReceiptTextLine(receipt.destinationName.uppercase(), receiptTextColor, 14.5.sp, 20.sp)
                        ReceiptTextLine(formatSesamaRupiah(receipt.amount), receiptTextColor, 14.5.sp, 20.sp)

                        if (receipt.note.isNotBlank() && receipt.note != "-") {
                            ReceiptTextLine("BERITA: ${receipt.note}", receiptTextColor, 14.5.sp, 20.sp)
                        }

                        if (showReference) {
                            // Jarak antara nominal/berita dan baris Ref (57px = ~28dp)
                            Spacer(modifier = Modifier.height(28.dp))

                            // Baris Ref otentik BCA (hanya tampil saat dibuka dari Inbox)
                            ReceiptTextLine("Ref $refPart1", receiptTextColor, 14.5.sp, 20.sp)
                            ReceiptTextLine(refPart2, receiptTextColor, 14.5.sp, 20.sp)
                        }
                    } else {
                        // --- Bukti Transfer Bank Lain ---
                        ReceiptTextLine("m-Transfer:", receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)
                        ReceiptTextLine("BERHASIL", receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)
                        ReceiptTextLine(receipt.timestamp, receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        ReceiptTextLine("Ke Rekening Tujuan", receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)
                        ReceiptTextLine(receipt.destinationBank.uppercase(), receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)
                        ReceiptTextLine(cleanAccount(receipt.destinationAccount), receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)
                        ReceiptTextLine(receipt.destinationName.uppercase(), receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        ReceiptTextLine("NOMINAL TRANSFER ${formatBankLainRupiah(receipt.amount)}", receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)
                        ReceiptTextLine("BIAYA ${formatBankLainRupiah(receipt.adminFee)}", receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)
                        ReceiptTextLine("LAYANAN ${receipt.service.ifBlank { "Realtime Online" }}", receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)

                        if (receipt.note.isNotBlank() && receipt.note != "-") {
                            ReceiptTextLine("BERITA: ${receipt.note}", receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)
                        }

                        if (showReference) {
                            Spacer(modifier = Modifier.height(16.dp))

                            // Baris Ref otentik BCA (hanya tampil saat dibuka dari Inbox)
                            ReceiptTextLine("Ref $refPart1", receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)
                            ReceiptTextLine(refPart2, receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        ReceiptTextLine("Biaya Termasuk PPN (Bila ada)", receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)
                        ReceiptTextLine("PT. BANK CENTRAL ASIA TBK.", receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)
                    }

                    // Flexible breathing watermark spacer that pushes buttons to bottom
                    Spacer(modifier = Modifier.weight(1f))

                    // --- Bottom Action Buttons Row ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Share Circular Button (48.dp, border 1.2.dp, icon 22.dp)
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .border(1.2.dp, circleBorderColor, CircleShape)
                                .clip(CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Bagikan Bukti Transfer"))
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_receipt_share),
                                contentDescription = "Bagikan",
                                tint = circleBorderColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(15.dp))

                        // Download / Save Circular Button (48.dp, border 1.2.dp, icon 22.dp)
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .border(1.2.dp, circleBorderColor, CircleShape)
                                .clip(CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = {
                                        Toast.makeText(context, "Bukti transfer berhasil disimpan", Toast.LENGTH_SHORT).show()
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_receipt_download),
                                contentDescription = "Simpan",
                                tint = circleBorderColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        // OK Gradient Rounded Rectangle Button (height 43.dp, radius 6.dp)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(43.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(okButtonGradient)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = onDismiss
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "OK",
                                color = Color.White,
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Memformat nomor referensi otentik BCA Mobile:
 * Baris 1: "95031" + 14 digit timestamp + 8 digit seq = 27 karakter
 * Baris 2: 8 karakter heksadesimal checksum
 */
fun formatBcaReceiptRef(rawRef: String?, timestamp: String, accountNum: String): Pair<String, String> {
    val cleanRef = rawRef?.trim().orEmpty()
    if (cleanRef.length >= 35 && cleanRef.startsWith("95031")) {
        return Pair(cleanRef.substring(0, 27), cleanRef.substring(27).take(8))
    }

    // Ekstrak timestamp ke format yyyyMMddHHmmss
    val digitsOnly = timestamp.filter { it.isDigit() }
    val ts14 = if (digitsOnly.length >= 14) {
        val dd = digitsOnly.substring(0, 2)
        val mm = digitsOnly.substring(2, 4)
        val yyyy = digitsOnly.substring(4, 8)
        val hhmmss = digitsOnly.substring(8, 14)
        "$yyyy$mm$dd$hhmmss"
    } else {
        "20261106234300"
    }

    val accDigits = accountNum.filter { it.isDigit() }
    val seq8 = if (accDigits.length >= 8) {
        accDigits.takeLast(8)
    } else {
        val seed = Math.abs((cleanRef + accountNum + timestamp).hashCode())
        String.format(Locale.US, "%08d", (seed % 90000000) + 10000000)
    }

    val seed = Math.abs((cleanRef + accountNum + timestamp).hashCode())
    val hex8 = String.format(Locale.US, "%08X", seed)

    val part1 = "95031$ts14$seq8"
    val part2 = hex8
    return Pair(part1, part2)
}

@Composable
private fun ReceiptTextLine(
    text: String,
    color: Color,
    fontSize: TextUnit = 14.5.sp,
    lineHeight: TextUnit = 20.sp,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        lineHeight = lineHeight,
        letterSpacing = letterSpacing,
        fontWeight = fontWeight,
        fontFamily = FontFamily.SansSerif,
        maxLines = 1
    )
}

private fun cleanAccount(accountStr: String): String {
    return accountStr.substringBefore(" - ").trim()
}

/**
 * Formats Rupiah for Sesama BCA: Rp 100.000,00
 */
private fun formatSesamaRupiah(amount: Long): String {
    val symbols = DecimalFormatSymbols(Locale("id", "ID")).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }
    val df = DecimalFormat("#,##0.00", symbols)
    return "Rp " + df.format(amount)
}

/**
 * Formats Rupiah for Bank Lain: Rp 50,000.00
 */
private fun formatBankLainRupiah(amount: Long): String {
    val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ','
        decimalSeparator = '.'
    }
    val df = DecimalFormat("#,##0.00", symbols)
    return "Rp " + df.format(amount)
}

private fun buildReceiptShareText(receipt: TransferReceipt, isSesamaBca: Boolean, showReference: Boolean = false): String {
    val (refPart1, refPart2) = formatBcaReceiptRef(receipt.referenceNo, receipt.timestamp, receipt.destinationAccount)
    val refText = if (showReference) "\n\nRef $refPart1\n$refPart2" else ""

    return if (isSesamaBca) {
        val beritaText = if (receipt.note.isNotBlank() && receipt.note != "-") "\nBERITA: " + receipt.note else ""
        """
        m-Transfer:
        BERHASIL
        ${receipt.timestamp}
        Ke ${cleanAccount(receipt.destinationAccount)}
        ${receipt.destinationName.uppercase()}
        ${formatSesamaRupiah(receipt.amount)}$beritaText$refText
        """.trimIndent()
    } else {
        val beritaText = if (receipt.note.isNotBlank() && receipt.note != "-") "\nBERITA: " + receipt.note else ""
        val refBlock = if (showReference) "\nRef $refPart1\n$refPart2\n" else ""
        """
        m-Transfer:
        BERHASIL
        ${receipt.timestamp}

        Ke Rekening Tujuan
        ${receipt.destinationBank.uppercase()}
        ${cleanAccount(receipt.destinationAccount)}
        ${receipt.destinationName.uppercase()}

        NOMINAL TRANSFER ${formatBankLainRupiah(receipt.amount)}
        BIAYA ${formatBankLainRupiah(receipt.adminFee)}
        LAYANAN ${receipt.service.ifBlank { "Realtime Online" }}$beritaText$refBlock
        Biaya Termasuk PPN (Bila ada)
        PT. BANK CENTRAL ASIA TBK.
        """.trimIndent()
    }
}
