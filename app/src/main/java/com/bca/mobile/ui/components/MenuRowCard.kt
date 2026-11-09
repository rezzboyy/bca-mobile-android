package com.bca.mobile.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bca.mobile.ui.theme.BcaColors

@Composable
fun MenuRowCard(
    title: String,
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
    badgeText: String? = null,
    showDivider: Boolean = true,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = Color.White.copy(alpha = 0.3f)),
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .background(BcaColors.MenuRowGradient)
                .padding(start = 14.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rounded Tile Icon (Matching uploaded_media_1794227768198.jpg)
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Title
            Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            // Optional Capsule Badge
            if (badgeText != null) {
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
                        .background(Color.White, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = BcaColors.BadgeBlue,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }

        if (showDivider) {
            HorizontalDivider(
                color = Color.White.copy(alpha = 0.65f),
                thickness = 1.dp
            )
        }
    }
}
