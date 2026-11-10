package com.bca.mobile.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bca.mobile.R
import com.bca.mobile.data.model.Account
import com.bca.mobile.data.repository.BcaRepository
import com.bca.mobile.ui.components.BcaAlertResultDialog
import com.bca.mobile.ui.components.BcaBottomNavBar
import com.bca.mobile.ui.components.BcaConnectionIndicator
import com.bca.mobile.ui.components.BcaNavTab

/**
 * Authentic BCA "Akun Saya" Screen matching Screenshot_2026-10-28-09-12-38-889_com.bca.jpg.jpeg
 * Precision crafted to match 100% of reference dimensions, fonts, colors, and layout.
 * Includes interactive Custom Account Data editor (Saldo, Nama, No Rekening, No Kartu, Jenis Rekening).
 */
@Composable
fun AkunSayaScreen(
    account: Account,
    onBack: () -> Unit,
    onTabSelected: (BcaNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { BcaRepository.getInstance() }

    var alertMessage by remember { mutableStateOf<String?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }

    // Dialog state for custom account fields
    var editDialogField by remember { mutableStateOf<EditFieldType?>(null) }

    val cardNumberFormatted = account.cardNumber.ifEmpty { "5307 9521 1830 4782" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 1. Top Bar: "Akun Saya" + Green Dot (Exact match to reference screenshot)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            color = Color(0xFFF7F7F7),
            shadowElevation = 0.5.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "Akun Saya",
                        color = Color(0xFF0C557F),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )

                    // Right status indicator light
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 4.dp)
                    ) {
                        BcaConnectionIndicator()
                    }
                }

                HorizontalDivider(
                    color = Color(0xFFE4E4E4),
                    thickness = 0.8.dp
                )
            }
        }

        // 2. Main Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 14.dp)
            ) {
                // Label Jenis Kartu
            Text(
                text = "Jenis Kartu",
                color = Color(0xFF64748B),
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = account.cardType.ifEmpty { "Paspor BCA Mastercard Platinum" },
                color = Color(0xFF1E293B),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Authentic Paspor BCA Mastercard Platinum Debit Card (1595x986 ratio)
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1595f / 986f)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
            ) {
                val cardWidth = maxWidth
                val cardHeight = maxHeight

                Image(
                    painter = painterResource(id = R.drawable.card_paspor_platinum_exact),
                    contentDescription = "Paspor BCA Platinum Debit Card",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )

                // Dynamic Account Holder Name - always rendered cleanly directly on the authentic card surface!
                // 100% transparent background so the card texture remains fully visible without any black box!
                if (account.accountHolder.isNotEmpty()) {
                    Text(
                        text = account.accountHolder.trim().uppercase(),
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color(0xCC000000),
                                offset = Offset(1.5f, 1.5f),
                                blurRadius = 2.5f
                            )
                        ),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(
                                start = cardWidth * 0.125f,
                                bottom = cardHeight * 0.130f
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // "Lihat Detail Kartu" Outlined Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White)
                    .border(1.2.dp, Color(0xFF0C557F), RoundedCornerShape(6.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(color = Color(0xFF0C557F).copy(alpha = 0.2f)),
                        onClick = {
                            repository.triggerProcessingPulse(400)
                            showDetailDialog = true
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Lihat Detail Kartu",
                    color = Color(0xFF0C557F),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Nomor Kartu Container with Copy Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F5F9))
                    .border(0.8.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Nomor Kartu",
                            color = Color(0xFF0C557F),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = cardNumberFormatted,
                            color = Color(0xFF334155),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }

                    // Authentic Copy Icon Button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = false, radius = 20.dp),
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Nomor Kartu", cardNumberFormatted.replace(" ", ""))
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Nomor kartu disalin", Toast.LENGTH_SHORT).show()
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_copy_exact),
                            contentDescription = "Salin Nomor Kartu",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // 4 Authentic Squircle Action Buttons (Kontrol, Atur Limit, Blokir, Ganti Kartu)
            // Precision padded to 12.dp with 4 equal weight(1f) columns (84.dp each)
            // Exactly matching centers at 108px, 276px, 444px, 612px in 720p reference screenshot
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                AkunSquircleActionButton(
                    modifier = Modifier.weight(1f),
                    drawableRes = R.drawable.ic_akun_kontrol_squircle,
                    label = "Kontrol",
                    onClick = {
                        repository.triggerProcessingPulse(350)
                        alertMessage = "Layanan Kontrol Transaksi Kartu Debit Paspor BCA aktif."
                    }
                )
                AkunSquircleActionButton(
                    modifier = Modifier.weight(1f),
                    drawableRes = R.drawable.ic_akun_atur_limit_squircle,
                    label = "Atur Limit",
                    onClick = {
                        repository.triggerProcessingPulse(350)
                        alertMessage = "Pengaturan Limit Transaksi Harian Debit Paspor BCA aktif."
                    }
                )
                AkunSquircleActionButton(
                    modifier = Modifier.weight(1f),
                    drawableRes = R.drawable.ic_akun_blokir_squircle,
                    label = "Blokir",
                    onClick = {
                        repository.triggerProcessingPulse(350)
                        alertMessage = "Status Kartu: AKTIF. Anda dapat memblokir sementara kartu jika diperlukan."
                    }
                )
                AkunSquircleActionButton(
                    modifier = Modifier.weight(1f),
                    drawableRes = R.drawable.ic_akun_ganti_kartu_squircle,
                    label = "Ganti Kartu",
                    onClick = {
                        repository.triggerProcessingPulse(350)
                        alertMessage = "Layanan Penggantian Kartu Paspor BCA via CS Digital atau Kantor Cabang."
                    }
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 5. Custom Data Rekening Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
            ) {
                Text(
                    text = "KELOLA DATA REKENING",
                    color = Color(0xFF64748B),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Ubah saldo, nama, nomor rekening & jenis kartu secara instan",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Settings Card Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Row 1: Custom Saldo
                        AccountSettingRow(
                            title = "Saldo Rekening",
                            value = BcaRepository.formatRupiah(account.balance),
                            highlightValue = true,
                            onClick = { editDialogField = EditFieldType.SALDO }
                        )

                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                        // Row 2: Custom Nama Rekening
                        AccountSettingRow(
                            title = "Nama Pemilik Rekening",
                            value = account.accountHolder,
                            onClick = { editDialogField = EditFieldType.NAMA }
                        )

                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                        // Row 3: Custom No Rekening
                        AccountSettingRow(
                            title = "Nomor Rekening",
                            value = account.accountNumber,
                            onClick = { editDialogField = EditFieldType.NOMOR_REKENING }
                        )

                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                        // Row 4: Custom Nomor Kartu
                        AccountSettingRow(
                            title = "Nomor Kartu",
                            value = cardNumberFormatted,
                            onClick = { editDialogField = EditFieldType.NOMOR_KARTU }
                        )

                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                        // Row 5: Custom Jenis Rekening / Kartu
                        AccountSettingRow(
                            title = "Jenis Rekening / Kartu",
                            value = account.cardType,
                            onClick = { editDialogField = EditFieldType.JENIS_REKENING }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }

        // 3. Bottom Navigation Bar: Akun Saya Active
        BcaBottomNavBar(
            selectedTab = BcaNavTab.AKUN_SAYA,
            onTabSelected = onTabSelected
        )
    }

    // Detail Kartu Dialog
    if (showDetailDialog) {
        BcaAlertResultDialog(
            title = "Detail Kartu",
            message = "Jenis Kartu: ${account.cardType}\nNomor Kartu: $cardNumberFormatted\nNama: ${account.accountHolder}\nNo Rekening: ${account.accountNumber}\nValid Thru: 09/29\nCVV: ***\nStatus: AKTIF",
            onDismiss = { showDetailDialog = false },
            confirmText = "OK"
        )
    }

    // Generic Info Dialog
    if (alertMessage != null) {
        BcaAlertResultDialog(
            title = "Akun Saya",
            message = alertMessage ?: "",
            onDismiss = { alertMessage = null },
            confirmText = "OK"
        )
    }

    // Custom Account Field Editor Dialog
    editDialogField?.let { fieldType ->
        AccountFieldEditorDialog(
            fieldType = fieldType,
            currentAccount = account,
            onDismiss = { editDialogField = null },
            onSave = { updatedAccount ->
                repository.updateAccountCustom(
                    accountHolder = updatedAccount.accountHolder,
                    accountNumber = updatedAccount.accountNumber,
                    balance = updatedAccount.balance,
                    cardType = updatedAccount.cardType,
                    cardNumber = updatedAccount.cardNumber,
                    accountType = updatedAccount.accountType
                )
                repository.triggerProcessingPulse(400)
                editDialogField = null
                Toast.makeText(context, "${fieldType.label} berhasil disimpan", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

/**
 * Authentic Squircle Action Button for Akun Saya matching uploaded_media_1793994776746.png
 */
@Composable
private fun AkunSquircleActionButton(
    modifier: Modifier = Modifier,
    drawableRes: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = label,
            modifier = Modifier.size(62.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF2C5888),
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center,
            softWrap = false,
            maxLines = 1
        )
    }
}

/**
 * Account Setting Row for Custom Data Rekening
 */
@Composable
private fun AccountSettingRow(
    title: String,
    value: String,
    highlightValue: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = Color(0xFF0C557F).copy(alpha = 0.1f)),
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color(0xFF64748B),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = if (highlightValue) Color(0xFF0C557F) else Color(0xFF1E293B),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Edit Chip Button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFE0F2FE))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Ubah",
                color = Color(0xFF0284C7),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

enum class EditFieldType(val label: String) {
    SALDO("Saldo Rekening"),
    NAMA("Nama Pemilik Rekening"),
    NOMOR_REKENING("Nomor Rekening"),
    NOMOR_KARTU("Nomor Kartu"),
    JENIS_REKENING("Jenis Rekening / Kartu")
}

/**
 * Smart parsing for saldo supporting all formats:
 * - 260240.16
 * - 260.240,16
 * - 260,240.16
 * - 50000000
 * - Rp 1.000.000
 */
private fun parseFlexibleSaldo(raw: String): Double? {
    val trimmed = raw.trim().replace("Rp", "", ignoreCase = true).replace(" ", "")
    if (trimmed.isEmpty()) return null

    val cleaned = if (trimmed.contains(".") && trimmed.contains(",")) {
        if (trimmed.lastIndexOf(",") > trimmed.lastIndexOf(".")) {
            // Indonesian format: 1.000.000,50
            trimmed.replace(".", "").replace(",", ".")
        } else {
            // US format: 1,000,000.50
            trimmed.replace(",", "")
        }
    } else if (trimmed.contains(",")) {
        trimmed.replace(",", ".")
    } else if (trimmed.contains(".")) {
        val parts = trimmed.split(".")
        if (parts.size > 2 || (parts.size == 2 && parts[1].length == 3)) {
            trimmed.replace(".", "")
        } else {
            trimmed
        }
    } else {
        trimmed
    }

    return cleaned.toDoubleOrNull()
}

/**
 * Modal Dialog for editing individual account fields with automatic keyboard management
 */
@Composable
private fun AccountFieldEditorDialog(
    fieldType: EditFieldType,
    currentAccount: Account,
    onDismiss: () -> Unit,
    onSave: (Account) -> Unit
) {
    val initialText = remember(fieldType, currentAccount) {
        when (fieldType) {
            EditFieldType.SALDO -> {
                val b = currentAccount.balance
                if (b % 1.0 == 0.0) b.toLong().toString() else b.toString()
            }
            EditFieldType.NAMA -> currentAccount.accountHolder
            EditFieldType.NOMOR_REKENING -> currentAccount.accountNumber
            EditFieldType.NOMOR_KARTU -> currentAccount.cardNumber
            EditFieldType.JENIS_REKENING -> currentAccount.cardType
        }
    }

    var textFieldValue by remember(fieldType) {
        mutableStateOf(
            TextFieldValue(
                text = initialText,
                selection = TextRange(initialText.length)
            )
        )
    }

    var errorMessage by remember(fieldType) { mutableStateOf<String?>(null) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(fieldType) {
        focusRequester.requestFocus()
    }

    val buttonGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0091EA),
            Color(0xFF015DB5),
            Color(0xFF003789)
        )
    )

    val cardPresets = listOf(
        "Paspor BCA Gold",
        "Paspor BCA Platinum",
        "Paspor BCA Blue",
        "Tahapan BCA",
        "BCA Xpresi",
        "BCA Prioritas"
    )

    val saldoPresets = listOf(
        100_000L,
        500_000L,
        1_000_000L,
        5_000_000L,
        25_000_000L,
        100_000_000L
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 350.dp)
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ubah ${fieldType.label}",
                        color = Color(0xFF1E293B),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Input Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .border(width = 1.2.dp, color = Color(0xFFCBD5E1), shape = RoundedCornerShape(6.dp))
                            .background(Color(0xFFF8FAFC))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        BasicTextField(
                            value = textFieldValue,
                            onValueChange = { newValue ->
                                val filteredText = when (fieldType) {
                                    EditFieldType.SALDO -> newValue.text.filter { it.isDigit() || it == '.' || it == ',' }
                                    EditFieldType.NOMOR_REKENING -> newValue.text.filter { it.isDigit() }.take(15)
                                    EditFieldType.NOMOR_KARTU -> newValue.text.filter { it.isDigit() || it == ' ' }.take(23)
                                    EditFieldType.NAMA -> newValue.text.uppercase().take(30)
                                    EditFieldType.JENIS_REKENING -> newValue.text.take(30)
                                }
                                textFieldValue = newValue.copy(text = filteredText)
                                errorMessage = null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            singleLine = true,
                            textStyle = TextStyle(
                                color = Color(0xFF0F172A),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            cursorBrush = SolidColor(Color(0xFF0C557F)),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = when (fieldType) {
                                    EditFieldType.SALDO -> KeyboardType.Decimal
                                    EditFieldType.NOMOR_REKENING, EditFieldType.NOMOR_KARTU -> KeyboardType.Number
                                    EditFieldType.NAMA -> KeyboardType.Text
                                    EditFieldType.JENIS_REKENING -> KeyboardType.Text
                                },
                                capitalization = when (fieldType) {
                                    EditFieldType.NAMA -> KeyboardCapitalization.Characters
                                    else -> KeyboardCapitalization.Words
                                },
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                }
                            )
                        )
                    }

                    // Live preview for balance
                    if (fieldType == EditFieldType.SALDO) {
                        val parsedVal = parseFlexibleSaldo(textFieldValue.text) ?: 0.0
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Pratinjau: ${BcaRepository.formatRupiah(parsedVal)}",
                            color = Color(0xFF0284C7),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }

                    // Quick presets for Card Type
                    if (fieldType == EditFieldType.JENIS_REKENING) {
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(cardPresets) { preset ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (textFieldValue.text == preset) Color(0xFF0C557F) else Color(0xFFF1F5F9))
                                        .clickable {
                                            textFieldValue = TextFieldValue(preset, TextRange(preset.length))
                                        }
                                        .padding(horizontal = 8.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = preset,
                                        color = if (textFieldValue.text == preset) Color.White else Color(0xFF334155),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Quick presets for Saldo
                    if (fieldType == EditFieldType.SALDO) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(saldoPresets) { amount ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .clickable {
                                            val str = amount.toString()
                                            textFieldValue = TextFieldValue(str, TextRange(str.length))
                                        }
                                        .padding(horizontal = 8.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = BcaRepository.formatNumberOnly(amount),
                                        color = Color(0xFF334155),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFD32F2F),
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons (Batal & Simpan)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Cancel Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(buttonGradient)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(color = Color.White),
                                    onClick = onDismiss
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Batal",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Save Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(buttonGradient)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(color = Color.White),
                                    onClick = {
                                        val rawText = textFieldValue.text.trim()
                                        if (rawText.isEmpty()) {
                                            errorMessage = "Nilai tidak boleh kosong"
                                            return@clickable
                                        }

                                        when (fieldType) {
                                            EditFieldType.SALDO -> {
                                                val parsed = parseFlexibleSaldo(rawText)
                                                if (parsed == null || parsed < 0) {
                                                    errorMessage = "Format saldo tidak valid"
                                                    return@clickable
                                                }
                                                onSave(currentAccount.copy(balance = parsed))
                                            }
                                            EditFieldType.NAMA -> {
                                                onSave(currentAccount.copy(accountHolder = rawText.uppercase()))
                                            }
                                            EditFieldType.NOMOR_REKENING -> {
                                                if (rawText.length < 5) {
                                                    errorMessage = "Nomor rekening minimal 5 digit"
                                                    return@clickable
                                                }
                                                onSave(currentAccount.copy(accountNumber = rawText))
                                            }
                                            EditFieldType.NOMOR_KARTU -> {
                                                if (rawText.replace(" ", "").length < 10) {
                                                    errorMessage = "Nomor kartu minimal 10 digit"
                                                    return@clickable
                                                }
                                                onSave(currentAccount.copy(cardNumber = rawText))
                                            }
                                            EditFieldType.JENIS_REKENING -> {
                                                onSave(currentAccount.copy(cardType = rawText, accountType = rawText))
                                            }
                                        }
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Simpan",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
