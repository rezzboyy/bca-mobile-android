package com.bca.mobile.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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
            Color(0xFF5B7FA9),
            Color(0xFF3B5D86),
            Color(0xFF223E62)
        )
    )

    Box(
        modifier = modifier
            .height(34.dp)
            .widthIn(min = 62.dp)
            .clip(RoundedCornerShape(4.dp))
            .border(0.8.dp, Color(0xFF1B324E), RoundedCornerShape(4.dp))
            .background(if (enabled) buttonBrush else androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFF94A3B8), Color(0xFF64748B))))
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = Color.White.copy(alpha = 0.3f)),
                onClick = onClick
            )
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
