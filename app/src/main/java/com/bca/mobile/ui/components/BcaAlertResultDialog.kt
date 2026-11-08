package com.bca.mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bca.mobile.R

/**
 * Authentic BCA Alert/Result Modal Dialog
 * 100% pixel-perfect replica matching Screenshot_2026-10-28-09-12-49-688_com.bca.jpg.jpeg
 */
@Composable
fun BcaAlertResultDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    confirmText: String = "OK",
    onConfirm: () -> Unit = onDismiss,
    cancelText: String? = null
) {
    // Official BCA Alert Button Gradient:
    // Top cyan-blue highlight #3FA5E5 to mid #2D80C4 to royal #1A65A9 to deep #053F88
    val buttonGradient = Brush.verticalGradient(
        0.0f to Color(0xFF3FA5E5),
        0.35f to Color(0xFF2D80C4),
        0.75f to Color(0xFF1A65A9),
        1.0f to Color(0xFF053F88)
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
            // Clean Dialog Card with Authentic Gradient Gray Panel
            Box(
                modifier = Modifier
                    .width(284.dp)
                    .height(314.dp)
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_clean_gradient_gray_panel),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.FillBounds
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(11.dp))

                        // Header Title (e.g. "m-Info" in official BCA Blue #01547B)
                        Text(
                            text = title,
                            color = Color(0xFF01547B),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Message Text Box (Left-aligned, pure black, standard sans-serif)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 14.dp)
                        ) {
                            val lines = message.split("\n")
                            lines.forEach { line ->
                                if (line.isEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                } else if (line.contains("Rp.") || (line.any { it.isDigit() } && line.length > 25)) {
                                    // Guaranteed strictly 1 single line for balance & account numbers
                                    AutoResizeSingleLineText(
                                        text = line,
                                        maxFontSize = 15.sp,
                                        minFontSize = 10.5.sp,
                                        color = Color(0xFF000000),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    Text(
                                        text = line,
                                        color = Color(0xFF000000),
                                        fontSize = 15.sp,
                                        lineHeight = 22.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Normal,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        // Bottom Action Buttons (spanning almost full width, 7dp horizontal margin)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 7.dp, end = 7.dp, bottom = 7.dp)
                                .height(39.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (cancelText != null) {
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
                                        text = cancelText,
                                        color = Color(0xFF475569),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(3.dp))
                                    .border(1.dp, Color(0xFF053F88), RoundedCornerShape(3.dp))
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
 * Text composable that automatically scales down font size so that the content
 * always strictly fits on a single line (maxLines = 1, softWrap = false) without wrapping.
 */
@Composable
private fun AutoResizeSingleLineText(
    text: String,
    modifier: Modifier = Modifier,
    maxFontSize: TextUnit = 15.sp,
    minFontSize: TextUnit = 10.5.sp,
    color: Color = Color.Black,
    fontFamily: FontFamily = FontFamily.SansSerif,
    fontWeight: FontWeight = FontWeight.Normal,
    lineHeight: TextUnit = 22.sp
) {
    val estimatedSize = remember(text) {
        when {
            text.length > 34 -> 12.0.sp
            text.length > 29 -> 13.0.sp
            text.length > 25 -> 14.0.sp
            else -> maxFontSize
        }
    }
    var fontSizeValue by remember(text) { mutableStateOf(estimatedSize.value) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        fontSize = fontSizeValue.sp,
        lineHeight = lineHeight,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Clip,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth && fontSizeValue > minFontSize.value) {
                fontSizeValue = (fontSizeValue - 0.4f).coerceAtLeast(minFontSize.value)
            } else {
                readyToDraw = true
            }
        }
    )
}

