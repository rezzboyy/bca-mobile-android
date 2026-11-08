package com.bca.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bca.mobile.data.updater.UpdateInfo

@Composable
fun BcaUpdateDialog(
    updateInfo: UpdateInfo,
    isDownloading: Boolean,
    downloadProgress: Float,
    onDismiss: () -> Unit,
    onConfirmUpdate: () -> Unit
) {
    // Official BCA Alert Button Gradient
    val buttonGradient = Brush.verticalGradient(
        0.0f to Color(0xFF3FA5E5),
        0.35f to Color(0xFF2D80C4),
        0.75f to Color(0xFF1A65A9),
        1.0f to Color(0xFF053F88)
    )

    // Inner dialog subtle silver gradient matching official BCA client
    val innerPanelGradient = Brush.verticalGradient(
        0.0f to Color(0xFFF3F7FA),
        0.08f to Color(0xFFFAFAFA),
        0.7f to Color(0xFFF6F6F6),
        1.0f to Color(0xFFEFEFEF)
    )

    val sizeMb = if (updateInfo.apkSize > 0) {
        String.format("%.1f MB", updateInfo.apkSize / (1024.0 * 1024.0))
    } else {
        "~5 MB"
    }

    Dialog(
        onDismissRequest = { if (!isDownloading) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = !isDownloading,
            dismissOnClickOutside = !isDownloading,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Clean Dialog Card without dark silver inner outline
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .heightIn(min = 280.dp, max = 380.dp)
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(4.dp))
                    .clip(RoundedCornerShape(4.dp))
                    .background(innerPanelGradient)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        // Header Title
                        Text(
                            text = if (isDownloading) "Mengunduh Pembaruan" else "Pembaruan Tersedia",
                            color = Color(0xFF01547B),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        if (isDownloading) {
                            // Downloading Progress Screen
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(horizontal = 18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Mengunduh pembaruan BCA Mobile...",
                                    color = Color(0xFF002245),
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                LinearProgressIndicator(
                                    progress = { downloadProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = Color(0xFF005CA9),
                                    trackColor = Color(0xFFD6E4F0)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "${(downloadProgress * 100).toInt()}% selesai ($sizeMb)",
                                    color = Color(0xFF64748B),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Layar penginstalan akan muncul otomatis setelah unduhan selesai.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.5.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            // Update Information Screen
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp)
                            ) {
                                Text(
                                    text = "Versi baru m-BCA telah dirilis:",
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFE8F2FA), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = updateInfo.tagName,
                                        color = Color(0xFF005CA9),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = sizeMb,
                                        color = Color(0xFF005CA9),
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Catatan Pembaruan:",
                                    color = Color(0xFF334155),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = updateInfo.changelog.ifBlank { "• Peningkatan performa dan pembaruan sistem." },
                                    color = Color(0xFF1E293B),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    fontFamily = FontFamily.SansSerif
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Bottom Buttons: "Nanti" & "Perbarui Sekarang"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 7.dp)
                                    .height(39.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(3.dp))
                                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(3.dp))
                                        .background(Color(0xFFEFEFEF))
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = rememberRipple(bounded = true),
                                            onClick = onDismiss
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Nanti",
                                        color = Color(0xFF475569),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(3.dp))
                                        .border(1.dp, Color(0xFF053F88), RoundedCornerShape(3.dp))
                                        .background(buttonGradient)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = rememberRipple(bounded = true),
                                            onClick = onConfirmUpdate
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Perbarui",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
