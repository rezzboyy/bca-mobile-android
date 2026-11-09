package com.bca.mobile.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BcaSubMenuTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = onBackClick != null,
    trailingAction: (@Composable () -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .padding(horizontal = 14.dp)
            ) {
                // Left: Optional Back button
                if (showBackButton && onBackClick != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = false, radius = 20.dp),
                                onClick = onBackClick
                            ),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "❮",
                            color = Color(0xFF005CA9),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Center: Screen Title (BCA Signature Blue)
                Text(
                    text = title,
                    color = Color(0xFF005CA9),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )

                // Right: Status indicator (Hijau/Biru/Merah) & optional trailing action
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BcaConnectionIndicator()

                    trailingAction?.invoke()
                }
            }

            HorizontalDivider(
                color = Color(0xFFE5E7EB),
                thickness = 0.8.dp
            )
        }
    }
}

/**
 * Authentic BCA Top Bar Action Button (e.g. "Send", "Edit")
 */
@Composable
fun BcaTopBarActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val buttonBrush = androidx.compose.ui.graphics.Brush.verticalGradient(
        colors = listOf(
            Color(0xFF6F8FB8),
            Color(0xFF4A6D9B),
            Color(0xFF32537E),
            Color(0xFF223E62)
        )
    )

    Box(
        modifier = modifier
            .height(36.dp)
            .width(68.dp)
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, Color(0xFF1B324E), RoundedCornerShape(6.dp))
            .background(if (enabled) buttonBrush else androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFF94A3B8), Color(0xFF64748B))))
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = Color.White.copy(alpha = 0.3f)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Authentic BCA Blue Chevron Right (>) matching WhatsApp screenshots
 */
@Composable
fun BcaChevron(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF005CA9),
    size: Dp = 18.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val strokePx = 2.4.dp.toPx()
        val path = Path().apply {
            moveTo(this@Canvas.size.width * 0.32f, this@Canvas.size.height * 0.18f)
            lineTo(this@Canvas.size.width * 0.68f, this@Canvas.size.height * 0.50f)
            lineTo(this@Canvas.size.width * 0.32f, this@Canvas.size.height * 0.82f)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = strokePx,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

/**
 * Authentic BCA Signature Diagonal Metallic Background matching WhatsApp screenshots
 * Features the signature upward-sloping metallic facet ("jaret") with authentic BCA gradients.
 */
@Composable
fun BcaMetallicBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                val canvasW = size.width
                val canvasH = size.height

                // 1. Base upper plane: Light silver metallic gradient
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE4E7EB),
                            Color(0xFFDCDFE3),
                            Color(0xFFD6D9DD)
                        ),
                        startY = 0f,
                        endY = canvasH * 0.52f
                    )
                )

                // 2. Lower facet polygon: Darker silver plane
                // Authentic BCA signature diagonal streak ("jaret") slopes UPWARDS from bottom-left to top-right
                // Measured exactly from authentic 720x1600 & 1080x2400 BCA Mobile screenshots:
                // Left edge (x = 0): y = canvasH * 0.516f
                // Right edge (x = canvasW): y = canvasH * 0.267f
                val path = Path().apply {
                    moveTo(0f, canvasH * 0.516f)
                    lineTo(canvasW, canvasH * 0.267f)
                    lineTo(canvasW, canvasH)
                    lineTo(0f, canvasH)
                    close()
                }

                drawPath(
                    path = path,
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0xFFC7CBD0),
                            0.45f to Color(0xFFC4C8CC),
                            1.0f to Color(0xFFD8DCDF)
                        ),
                        startY = canvasH * 0.267f,
                        endY = canvasH
                    )
                )
            },
        content = content
    )
}
