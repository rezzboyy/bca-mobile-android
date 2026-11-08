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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Authentic BCA Bank Picker Popup matching Screenshot_2026-10-28-09-13-28-992_com.bca.jpg.jpeg
 */
@Composable
fun BcaBankSelectionDialog(
    banks: List<String> = listOf(
        "BANK MANDIRI",
        "SEABANK",
        "BANK BRI",
        "BNI",
        "BANK SMBC INDONESIA",
        "PERMATA BANK",
        "BANK CIMB NIAGA",
        "BANK DANAMON",
        "BANK BSI"
    ),
    onDismiss: () -> Unit,
    onSelectBank: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 340.dp)
                    .shadow(16.dp, RoundedCornerShape(2.dp))
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.Black)
                    .border(0.8.dp, Color(0xFF444444), RoundedCornerShape(2.dp))
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    banks.forEach { bankName ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(color = Color.White.copy(alpha = 0.2f)),
                                    onClick = { onSelectBank(bankName) }
                                )
                                .padding(horizontal = 18.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = bankName,
                                color = Color.White,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Normal,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
