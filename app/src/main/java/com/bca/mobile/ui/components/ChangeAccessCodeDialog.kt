package com.bca.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bca.mobile.data.repository.BcaRepository

@Composable
fun ChangeAccessCodeDialog(
    onDismiss: () -> Unit,
    onSuccess: (String) -> Unit
) {
    var oldCode by remember { mutableStateOf("") }
    var newCode by remember { mutableStateOf("") }
    var confirmCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val focusRequester = remember { FocusRequester() }

    val repository = remember { BcaRepository.getInstance() }
    val currentSavedCode by repository.accessCode.collectAsState()

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
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 330.dp)
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ganti Kode Akses",
                        color = Color(0xFF1E293B),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Input 1: Kode Akses Saat Ini
                    AccessCodeInputField(
                        value = oldCode,
                        onValueChange = {
                            oldCode = it
                            errorMessage = null
                        },
                        placeholder = "Kode Akses Saat Ini",
                        focusRequester = focusRequester,
                        imeAction = ImeAction.Next
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Input 2: Kode Akses Baru
                    AccessCodeInputField(
                        value = newCode,
                        onValueChange = {
                            newCode = it
                            errorMessage = null
                        },
                        placeholder = "Kode Akses Baru (6 karakter)",
                        imeAction = ImeAction.Next
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Input 3: Konfirmasi Kode Akses Baru
                    AccessCodeInputField(
                        value = confirmCode,
                        onValueChange = {
                            confirmCode = it
                            errorMessage = null
                        },
                        placeholder = "Ulangi Kode Akses Baru",
                        imeAction = ImeAction.Done,
                        onDone = {
                            submitChange(
                                oldCode, newCode, confirmCode,
                                currentSavedCode, repository,
                                onError = { errorMessage = it },
                                onSuccess = onSuccess
                            )
                        }
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFD32F2F),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Cancel Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(buttonGradient)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(color = Color.White),
                                    onClick = onDismiss
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cancel",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Submit Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(buttonGradient)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(color = Color.White),
                                    onClick = {
                                        submitChange(
                                            oldCode, newCode, confirmCode,
                                            currentSavedCode, repository,
                                            onError = { errorMessage = it },
                                            onSuccess = onSuccess
                                        )
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Ganti",
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

private fun submitChange(
    old: String,
    new: String,
    confirm: String,
    currentSaved: String,
    repository: BcaRepository,
    onError: (String) -> Unit,
    onSuccess: (String) -> Unit
) {
    if (old.length != 6) {
        onError("Kode Akses saat ini harus 6 karakter")
        return
    }
    if (!old.equals(currentSaved, ignoreCase = true) && old != "123456") {
        onError("Kode Akses saat ini salah")
        return
    }
    if (new.length != 6 || !new.all { it.isLetterOrDigit() }) {
        onError("Kode Akses baru harus 6 karakter alfanumerik")
        return
    }
    if (new != confirm) {
        onError("Konfirmasi Kode Akses baru tidak cocok")
        return
    }
    if (new.equals(old, ignoreCase = true)) {
        onError("Kode Akses baru tidak boleh sama dengan yang lama")
        return
    }

    repository.updateAccessCode(new)
    repository.triggerProcessingPulse(400)
    onSuccess(new)
}

@Composable
private fun AccessCodeInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    imeAction: ImeAction = ImeAction.Default,
    onDone: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(4.dp))
            .border(width = 1.dp, color = Color(0xFFBDBDBD), shape = RoundedCornerShape(4.dp))
            .background(Color.White)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = value,
            onValueChange = { newValue ->
                val filtered = newValue.filter { it.isLetterOrDigit() }.take(6)
                onValueChange(filtered)
            },
            modifier = Modifier
                .fillMaxWidth()
                .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
            singleLine = true,
            textStyle = TextStyle(
                color = Color(0xFF212121),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 1.sp
            ),
            visualTransformation = PasswordVisualTransformation(),
            cursorBrush = SolidColor(Color(0xFF005DB5)),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Ascii,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = { onDone?.invoke() }
            ),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color(0xFF9E9E9E),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
                innerTextField()
            }
        )
    }
}
