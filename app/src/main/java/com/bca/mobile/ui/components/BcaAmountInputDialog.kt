package com.bca.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Authentic BCA Signature Black Input Modal Dialog matching BCA Mobile UI
 * Features:
 * - Solid Black background with thin border
 * - White title label (e.g. "Jumlah Uang:", "No. Rekening Tujuan:", "Berita:")
 * - White rectangular text input field
 * - Glossy Blue "Cancel" and "OK" action buttons
 */
@Composable
fun BcaBlackInputDialog(
    title: String,
    initialValue: String = "",
    isNumericOnly: Boolean = false,
    maxLength: Int = 40,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var inputText by remember { mutableStateOf(initialValue) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val buttonGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0091EA),
            Color(0xFF015DB5),
            Color(0xFF003789)
        )
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 380.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black)
                    .border(width = 1.dp, color = Color(0xFF555555), shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 22.dp, vertical = 20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // White input box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .background(Color.White, RoundedCornerShape(2.dp))
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        BasicTextField(
                            value = inputText,
                            onValueChange = { input ->
                                val isValid = if (isNumericOnly) input.all { it.isDigit() } else true
                                if (isValid && input.length <= maxLength) {
                                    inputText = input
                                }
                            },
                            textStyle = TextStyle(
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            cursorBrush = SolidColor(Color.Black),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = if (isNumericOnly) KeyboardType.Number else KeyboardType.Text
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Buttons Row: Cancel and OK
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Cancel Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(buttonGradient)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(color = Color.White.copy(alpha = 0.3f)),
                                    onClick = onDismiss
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cancel",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // OK Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(buttonGradient)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(color = Color.White.copy(alpha = 0.3f)),
                                    onClick = { onConfirm(inputText) }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "OK",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Authentic BCA "Jumlah Uang:" Input Dialog matching Screenshot_2026-10-28-09-13-25-578_com.bca.jpg.jpeg
 */
@Composable
fun BcaAmountInputDialog(
    initialValue: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    BcaBlackInputDialog(
        title = "Jumlah Uang:",
        initialValue = initialValue,
        isNumericOnly = true,
        maxLength = 15,
        onDismiss = onDismiss,
        onConfirm = onConfirm
    )
}
