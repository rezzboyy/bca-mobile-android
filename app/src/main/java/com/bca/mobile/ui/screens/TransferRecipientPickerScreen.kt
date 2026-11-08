package com.bca.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bca.mobile.data.model.Account
import com.bca.mobile.data.model.TransferRecipient
import com.bca.mobile.ui.components.*

enum class RecipientTab {
    REKENING_SENDIRI,
    DAFTAR_TRANSFER
}

/**
 * Authentic BCA m-Transfer Destination Account Picker matching:
 * WhatsApp Image 2026-11-08 at 18.00.20.jpeg
 */
@Composable
fun TransferRecipientPickerScreen(
    account: Account,
    recipients: List<TransferRecipient>,
    bankFilter: String? = null,
    onSelectRecipient: (TransferRecipient) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(RecipientTab.DAFTAR_TRANSFER) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredRecipients = remember(recipients, searchQuery, bankFilter) {
        recipients.filter { rec ->
            val matchesBank = bankFilter == null || rec.bankName.equals(bankFilter, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() ||
                    rec.name.contains(searchQuery, ignoreCase = true) ||
                    rec.accountNumber.contains(searchQuery, ignoreCase = true)
            matchesBank && matchesQuery
        }.sortedBy { it.name }
    }

    BcaMetallicBackground(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Bar: "m-Transfer" with Green Indicator (No back button, matching Image 2)
            BcaSubMenuTopBar(
                title = "m-Transfer",
                onBackClick = onBack,
                showBackButton = false
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 14.dp)
            ) {
                // 2. Segmented Toggle Buttons: "Rekening Sendiri" | "Daftar Transfer"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    // Left Tab: Rekening Sendiri
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
                            .background(
                                if (selectedTab == RecipientTab.REKENING_SENDIRI) Color(0xFF005CA9) else Color(0xFFB2BAC2)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(color = Color.White.copy(alpha = 0.3f)),
                                onClick = { selectedTab = RecipientTab.REKENING_SENDIRI }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Rekening Sendiri",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = if (selectedTab == RecipientTab.REKENING_SENDIRI) FontWeight.Bold else FontWeight.SemiBold
                        )
                    }

                    // Right Tab: Daftar Transfer
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp))
                            .background(
                                if (selectedTab == RecipientTab.DAFTAR_TRANSFER) Color(0xFF005CA9) else Color(0xFFB2BAC2)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(color = Color.White.copy(alpha = 0.3f)),
                                onClick = { selectedTab = RecipientTab.DAFTAR_TRANSFER }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Daftar Transfer",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = if (selectedTab == RecipientTab.DAFTAR_TRANSFER) FontWeight.Bold else FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Search Box with Search Icon
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            textStyle = TextStyle(
                                color = Color(0xFF1E293B),
                                fontSize = 16.sp
                            ),
                            cursorBrush = SolidColor(Color(0xFF005CA9)),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 16.sp
                                    )
                                }
                                innerTextField()
                            }
                        )

                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. White Card Container for Accounts
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                ) {
                    if (selectedTab == RecipientTab.DAFTAR_TRANSFER) {
                        if (filteredRecipients.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Tidak ada rekening terdaftar.",
                                    color = Color(0xFF64748B),
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                itemsIndexed(filteredRecipients) { index, recipient ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = rememberRipple(bounded = true),
                                                onClick = { onSelectRecipient(recipient) }
                                            )
                                            .padding(horizontal = 16.dp, vertical = 14.dp)
                                    ) {
                                        Text(
                                            text = recipient.name.uppercase(),
                                            color = Color(0xFF005CA9),
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = recipient.accountNumber,
                                            color = Color(0xFF000000),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                    }

                                    if (index < filteredRecipients.size - 1) {
                                        HorizontalDivider(
                                            color = Color(0xFFE2E8F0),
                                            thickness = 0.8.dp
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Tab: Rekening Sendiri
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true),
                                    onClick = {
                                        onSelectRecipient(
                                            TransferRecipient(
                                                id = "OWN-ACCOUNT",
                                                accountNumber = account.accountNumber,
                                                name = account.accountHolder,
                                                bankName = "BCA"
                                            )
                                        )
                                    }
                                )
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Text(
                                text = account.accountHolder.uppercase(),
                                color = Color(0xFF005CA9),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = account.accountNumber,
                                color = Color(0xFF000000),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
