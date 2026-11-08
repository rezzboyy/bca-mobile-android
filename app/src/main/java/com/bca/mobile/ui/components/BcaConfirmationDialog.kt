package com.bca.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Authentic BCA Confirmation Modal Dialog
 * Features:
 * - Clean white card with subtle grey border
 * - Blue typography matching official BCA client
 * - Dual glossy blue action buttons for both Cancel and Confirm/OK/Logout
 */
@Composable
fun BcaConfirmationDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    confirmText: String = "OK",
    onConfirm: () -> Unit,
    cancelText: String? = "Cancel",
    onCancel: () -> Unit = onDismissRequest,
    dialogWidth: Dp = 280.dp,
    minHeight: Dp? = 320.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    // Official BCA Action Button Gradient (verified from authentic screenshot telemetry)
    val buttonGradient = Brush.verticalGradient(
        0.0f to Color(0xFF48A5E0),
        0.25f to Color(0xFF3B9DDE),
        0.55f to Color(0xFF277ABE),
        0.85f to Color(0xFF1962A7),
        1.0f to Color(0xFF0C4C93)
    )

    // Inner panel background
    val panelGradient = Brush.verticalGradient(
        0.0f to Color(0xFFFCFDFE),
        0.5f to Color(0xFFF9FAFB),
        1.0f to Color(0xFFF4F6F8)
    )

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(dialogWidth)
                    .then(
                        if (minHeight != null) Modifier.defaultMinSize(minHeight = minHeight)
                        else Modifier
                    )
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(panelGradient)
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (minHeight != null) Modifier.defaultMinSize(minHeight = minHeight)
                            else Modifier
                        )
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!title.isNullOrBlank()) {
                        Text(
                            text = title,
                            color = Color(0xFF004B87),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Dialog Content Slot
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = true),
                        horizontalAlignment = Alignment.Start
                    ) {
                        content()
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bottom Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (cancelText != null) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .border(1.dp, Color(0xFF0C4C93), RoundedCornerShape(4.dp))
                                    .background(buttonGradient)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = rememberRipple(bounded = true),
                                        onClick = onCancel
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cancelText,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .border(1.dp, Color(0xFF0C4C93), RoundedCornerShape(4.dp))
                                .background(buttonGradient)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = onConfirm
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = confirmText,
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
