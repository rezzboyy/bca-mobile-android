package com.bca.mobile.ui.screens

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bca.mobile.R
import com.bca.mobile.ui.components.BcaAlertResultDialog
import com.bca.mobile.ui.components.BcaModalDialog
import com.bca.mobile.ui.components.ChangeAccessCodeDialog
import com.bca.mobile.ui.components.GlossyActionButton
import com.bca.mobile.ui.components.MbcaLoginDialog
import com.bca.mobile.ui.components.MenuRowCard
import com.bca.mobile.ui.theme.BcaColors
import com.bca.mobile.ui.theme.BcaMobileTheme

@Composable
fun BcaHubScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit = {},
    onOpenFlazz: () -> Unit = {}
) {
    var activeModal by remember { mutableStateOf<String?>(null) }
    var showMbcaLogin by remember { mutableStateOf(false) }
    var showChangeAccessCodeDialog by remember { mutableStateOf(false) }
    var accessCodeSuccessMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BcaColors.NavyRadialCenter,
                        BcaColors.NavyDeep,
                        Color.Black
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Center Phone Column Container (max width 448dp)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 448.dp)
                .fillMaxWidth()
        ) {
            // Radiant Navy Wallpaper Background
            Image(
                painter = painterResource(id = R.drawable.bca_wallpaper),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )

            // Main UI Content Layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Info Button Header (Authentic BCA Hub has only the (i) Info icon)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp, end = 18.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .shadow(elevation = 4.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = false, radius = 18.dp),
                                onClick = { activeModal = "Info" }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.info_icon_clean),
                            contentDescription = "Info BCA",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // Center Action Section (Logo + 3-Row Card + Action Buttons)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // BCA mobile Brand Logo
                    Image(
                        painter = painterResource(id = R.drawable.bca_mobile_logo),
                        contentDescription = "BCA mobile",
                        modifier = Modifier
                            .width(240.dp)
                            .padding(bottom = 18.dp),
                        contentScale = ContentScale.Fit
                    )

                    // 3-Row Action Menu Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 24.dp,
                                shape = RoundedCornerShape(18.dp),
                                ambientColor = Color(0x80000C28),
                                spotColor = Color(0x80000C28)
                            )
                            .clip(RoundedCornerShape(18.dp))
                            .border(width = 2.dp, color = Color.White, shape = RoundedCornerShape(18.dp))
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Row 1: m-BCA (Opens Authentic Kode Akses Dialog)
                            MenuRowCard(
                                title = "m-BCA",
                                iconRes = R.drawable.mbca_clean,
                                showDivider = true,
                                onClick = { showMbcaLogin = true }
                            )

                            // Row 2: KlikBCA
                            MenuRowCard(
                                title = "KlikBCA",
                                iconRes = R.drawable.klikbca_clean,
                                showDivider = true,
                                onClick = { activeModal = "KlikBCA" }
                            )

                            // Row 3: myBCA + NEW! badge
                            MenuRowCard(
                                title = "myBCA",
                                iconRes = R.drawable.mybca_clean,
                                badgeText = "NEW!",
                                showDivider = false,
                                onClick = { activeModal = "myBCA" }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Button 1: Buka Rekening Baru
                    GlossyActionButton(
                        title = "Buka Rekening Baru",
                        onClick = { activeModal = "Buka Rekening Baru" }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Button 2: Ganti Kode Akses
                    GlossyActionButton(
                        title = "Ganti Kode Akses",
                        onClick = { showChangeAccessCodeDialog = true }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Button 3: Flazz
                    GlossyActionButton(
                        title = "Flazz",
                        onClick = onOpenFlazz
                    )
                }

                // Bottom Regulatory Disclaimer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.disclaimer_line1),
                        color = BcaColors.TextDisclaimer,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(id = R.string.disclaimer_line2),
                        color = BcaColors.TextDisclaimer,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(id = R.string.disclaimer_line3),
                        color = BcaColors.TextDisclaimer,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Authentic m-BCA "Kode Akses" Dialog
        if (showMbcaLogin) {
            MbcaLoginDialog(
                onDismiss = { showMbcaLogin = false },
                onLogin = { code ->
                    showMbcaLogin = false
                    onLoginSuccess()
                }
            )
        }

        // Authentic "Ganti Kode Akses" Dialog
        if (showChangeAccessCodeDialog) {
            ChangeAccessCodeDialog(
                onDismiss = { showChangeAccessCodeDialog = false },
                onSuccess = { newCode ->
                    showChangeAccessCodeDialog = false
                    accessCodeSuccessMessage = "Kode Akses Anda berhasil diubah menjadi: $newCode\nSilakan login menggunakan Kode Akses baru."
                }
            )
        }

        if (accessCodeSuccessMessage != null) {
            BcaAlertResultDialog(
                title = "Ganti Kode Akses",
                message = accessCodeSuccessMessage ?: "",
                onDismiss = { accessCodeSuccessMessage = null },
                confirmText = "OK"
            )
        }

        // Generic Feature Modal Dialog
        activeModal?.let { modalTitle ->
            BcaModalDialog(
                title = modalTitle,
                onDismiss = { activeModal = null }
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=412dp,height=915dp")
@Composable
fun BcaHubScreenPreview() {
    BcaMobileTheme {
        BcaHubScreen()
    }
}
