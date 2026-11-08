package com.bca.mobile.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bca.mobile.R
import kotlinx.coroutines.delay

/**
 * Authentic BCA Full-Screen PIN Entry Screen matching user reference image (Galaxy Z Fold 4)
 * - Header: "m-Transfer" with online connection status lamp
 * - Padlock Icon + "Masukkan PIN m-BCA"
 * - 6 Circular PIN indicators (hollow when empty, solid dark blue when filled)
 * - 3x4 Custom Numeric Keypad with authentic bevel gradients and backspace button
 */
@Composable
fun BcaPinDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    title: String = "m-Transfer",
    subtitle: String = "Masukkan PIN m-BCA",
    onVerifyPin: (String) -> Boolean = { it == "123456" || it == "000000" }
) {
    var pinText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isChecking by remember { mutableStateOf(false) }

    // When 6 digits are typed, verify PIN automatically
    LaunchedEffect(pinText) {
        if (pinText.length == 6 && !isChecking) {
            isChecking = true
            delay(120) // Small visual delay so the 6th dot is clearly seen
            if (onVerifyPin(pinText)) {
                errorMessage = null
                onSuccess()
            } else {
                errorMessage = "PIN m-BCA yang Anda masukkan salah. Silakan coba lagi."
                delay(500)
                pinText = ""
                isChecking = false
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // 1. Top Bar matching authentic m-Transfer style (no back button, title centered, status indicator right)
            BcaSubMenuTopBar(
                title = title,
                onBackClick = onDismiss,
                showBackButton = false
            )

            // 2. Middle Body: Lock + "Masukkan PIN m-BCA" + 6 Circles positioned in upper-third
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                // Padlock Icon
                Image(
                    painter = painterResource(id = R.drawable.ic_bca_pin_lock),
                    contentDescription = "Lock",
                    modifier = Modifier.size(26.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Instruction Text
                if (subtitle == "Masukkan PIN m-BCA") {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Masukkan ",
                            fontSize = 15.sp,
                            color = Color(0xFF0C557F),
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.SansSerif
                        )
                        Text(
                            text = "PIN m-BCA",
                            fontSize = 15.sp,
                            color = Color(0xFF0C557F),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                } else {
                    Text(
                        text = subtitle,
                        fontSize = 15.sp,
                        color = Color(0xFF0C557F),
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 6 PIN Circle Indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 6) {
                        val isFilled = i < pinText.length
                        Box(
                            modifier = Modifier
                                .size(15.dp)
                                .clip(CircleShape)
                                .then(
                                    if (isFilled) {
                                        Modifier.background(Color(0xFF0C557F))
                                    } else {
                                        Modifier
                                            .background(Color.White)
                                            .border(1.4.dp, Color(0xFF8C98A5), CircleShape)
                                    }
                                )
                        )
                    }
                }

                // Error Message (if any)
                AnimatedVisibility(
                    visible = errorMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFDC2626),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Empty white space pushing keypad to the bottom
                Spacer(modifier = Modifier.weight(1f))
            }

            // 3. Authentic 3x4 Numeric Keypad with Soft 3D Bevel Keys
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(Color.White)
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            ) {
                val keyRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("", "0", "DEL")
                )

                for (row in keyRows) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (key in row) {
                            when (key) {
                                "" -> {
                                    // Empty slot on bottom-left
                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp)
                                    )
                                }
                                "DEL" -> {
                                    // Backspace / Delete button
                                    BcaKeypadButton(
                                        modifier = Modifier.weight(1f),
                                        isBackspace = true,
                                        onClick = {
                                            if (pinText.isNotEmpty() && !isChecking) {
                                                pinText = pinText.dropLast(1)
                                                errorMessage = null
                                            }
                                        }
                                    )
                                }
                                else -> {
                                    // Number key '0'-'9'
                                    BcaKeypadButton(
                                        modifier = Modifier.weight(1f),
                                        label = key,
                                        onClick = {
                                            if (pinText.length < 6 && !isChecking) {
                                                pinText += key
                                                errorMessage = null
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Authentic Keypad Button with Soft 3D Bevel/Gradient matching reference photo
 */
@Composable
private fun BcaKeypadButton(
    modifier: Modifier = Modifier,
    label: String? = null,
    isBackspace: Boolean = false,
    onClick: () -> Unit
) {
    val buttonGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF0EBEA),
            Color(0xFFE2DCDB),
            Color(0xFFD3CCCA)
        )
    )

    Box(
        modifier = modifier
            .height(56.dp)
            .shadow(elevation = 0.5.dp, shape = RoundedCornerShape(3.dp))
            .clip(RoundedCornerShape(3.dp))
            .background(buttonGradient)
            .border(0.8.dp, Color(0xFFC0B8B6), RoundedCornerShape(3.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = Color(0xFF0C557F).copy(alpha = 0.2f)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isBackspace) {
            Image(
                painter = painterResource(id = R.drawable.ic_bca_keypad_backspace),
                contentDescription = "Hapus",
                modifier = Modifier.size(26.dp)
            )
        } else if (label != null) {
            Text(
                text = label,
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif,
                color = Color(0xFF4A5568),
                textAlign = TextAlign.Center
            )
        }
    }
}
