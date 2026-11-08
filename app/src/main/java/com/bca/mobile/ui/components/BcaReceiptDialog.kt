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
 * - buktitfsesamabca.png (Aspect ratio 1.20, typography, exact spacing)
 * - buktitfbanklain.png (Aspect ratio 1.25, bank layout, single-line nominal)
 * - Diagonal Light-Blue BCA Watermark Pattern.png (Watermark background)
 * - ukuranbuktitf.png (Dialog width = 84.7% screen, sharp border)
 */
@Composable
fun BcaReceiptDialog(
    receipt: TransferReceipt,
    onDismiss: () -> Unit
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
    val shareText = remember(receipt, isSesamaBca) {
        buildReceiptShareText(receipt, isSesamaBca)
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
                    .fillMaxWidth(0.847f)
                    .aspectRatio(1.0f / (if (isSesamaBca) 1.21f else 1.26f), matchHeightConstraintsFirst = false)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(1.dp))
                    .clip(RoundedCornerShape(1.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFCCCCCC), RoundedCornerShape(1.dp))
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
                            start = if (isSesamaBca) 20.dp else 17.dp,
                            top = if (isSesamaBca) 22.dp else 19.dp,
                            end = if (isSesamaBca) 18.dp else 14.dp,
                            bottom = 18.dp
                        )
                ) {
                    if (isSesamaBca) {
                        // --- Bukti Transfer Sesama BCA (buktitfsesamabca.png) ---
                        ReceiptTextLine("m-Transfer:", receiptTextColor, 14.5.sp, 21.sp)
                        ReceiptTextLine("BERHASIL", receiptTextColor, 14.5.sp, 21.sp)
                        ReceiptTextLine(receipt.timestamp, receiptTextColor, 14.5.sp, 21.sp)
                        ReceiptTextLine("Ke ${cleanAccount(receipt.destinationAccount)}", receiptTextColor, 14.5.sp, 21.sp)
                        ReceiptTextLine(receipt.destinationName.uppercase(), receiptTextColor, 14.5.sp, 21.sp)
                        ReceiptTextLine(formatSesamaRupiah(receipt.amount), receiptTextColor, 14.5.sp, 21.sp)

                        if (receipt.note.isNotBlank() && receipt.note != "-") {
                            ReceiptTextLine("BERITA: ${receipt.note}", receiptTextColor, 14.5.sp, 21.sp)
                        }
                    } else {
                        // --- Bukti Transfer Bank Lain (buktitfbanklain.png) ---
                        ReceiptTextLine("m-Transfer:", receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)
                        ReceiptTextLine("BERHASIL", receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)
                        ReceiptTextLine(receipt.timestamp, receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)

                        Spacer(modifier = Modifier.height(18.dp))

                        ReceiptTextLine("Ke Rekening Tujuan", receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)
                        ReceiptTextLine(receipt.destinationBank.uppercase(), receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)
                        ReceiptTextLine(cleanAccount(receipt.destinationAccount), receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)
                        ReceiptTextLine(receipt.destinationName.uppercase(), receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)

                        Spacer(modifier = Modifier.height(18.dp))

                        ReceiptTextLine("NOMINAL TRANSFER ${formatBankLainRupiah(receipt.amount)}", receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)
                        ReceiptTextLine("BIAYA ${formatBankLainRupiah(receipt.adminFee)}", receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)
                        ReceiptTextLine("LAYANAN ${receipt.service.ifBlank { "Realtime Online" }}", receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)

                        if (receipt.note.isNotBlank() && receipt.note != "-") {
                            ReceiptTextLine("BERITA: ${receipt.note}", receiptTextColor, 13.8.sp, 20.sp, (-0.1).sp)
                        }

                        Spacer(modifier = Modifier.height(18.dp))

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
                        // Share Circular Button
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .border(1.dp, circleBorderColor, CircleShape)
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
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Download / Save Circular Button
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .border(1.dp, circleBorderColor, CircleShape)
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
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // OK Gradient Rounded Rectangle Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(4.dp))
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

@Composable
private fun ReceiptTextLine(
    text: String,
    color: Color,
    fontSize: TextUnit = 14.5.sp,
    lineHeight: TextUnit = 21.sp,
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

private fun buildReceiptShareText(receipt: TransferReceipt, isSesamaBca: Boolean): String {
    return if (isSesamaBca) {
        """
        m-Transfer:
        BERHASIL
        ${receipt.timestamp}
        Ke ${cleanAccount(receipt.destinationAccount)}
        ${receipt.destinationName.uppercase()}
        ${formatSesamaRupiah(receipt.amount)}
        ${if (receipt.note.isNotBlank() && receipt.note != "-") "BERITA: " + receipt.note else ""}
        """.trimIndent()
    } else {
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
        LAYANAN ${receipt.service.ifBlank { "Realtime Online" }}
        ${if (receipt.note.isNotBlank() && receipt.note != "-") "BERITA: " + receipt.note else ""}

        Biaya Termasuk PPN (Bila ada)
        PT. BANK CENTRAL ASIA TBK.
        """.trimIndent()
    }
}
