@file:Suppress("unused")
@file:OptIn(ExperimentalComposeUiApi::class)

package com.estholon.firebaseauthentication.ui.core.components.authentication.otp

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * OTP/One-Time Password input component for Jetpack Compose.
 *
 * Diseño y principios:
 * - API limpia y testeable (stateless) + wrapper stateful opcional.
 * - Filtrado defensivo (solo dígitos), manejo de pegar, y límite de longitud.
 * - Accesible: descripciones por celda y un único foco real.
 * - Estado de error y estilo personalizable.
 * - Autofill de Android correcto: registro de boundingBox antes de solicitarlo.
 *
 * Uso rápido:
 * ```kotlin
 * var otp by rememberSaveable { mutableStateOf("") }
 * OtpCodeInput(
 *     value = otp,
 *     onValueChange = { otp = it },
 *     onFilled = { code -> /* submit */ },
 * )
 * ```
 */
@Composable
fun OtpCodeInput(
    modifier: Modifier = Modifier,
    length: Int = 6,
    value: String,
    onValueChange: (String) -> Unit,
    onFilled: (String) -> Unit = {},
    isError: Boolean = false,
    cellSize: Dp = 48.dp,
    cellSpacing: Dp = 8.dp,
    cornerRadius: Dp = 12.dp,
    textStyle: TextStyle = MaterialTheme.typography.headlineMedium.copy(letterSpacing = 0.5.sp),
    visualTransformation: VisualTransformation = PasswordVisualTransformation('.'),
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.NumberPassword,
        imeAction = ImeAction.Done
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    require(length > 0) { "length must be > 0" }

    val filtered = remember(value, length) { value.filter(Char::isDigit).take(length) }

    // Mantén la fuente externa como verdad
    LaunchedEffect(filtered) { if (filtered != value) onValueChange(filtered) }

    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    // --- Autofill: registra boundingBox antes de solicitarlo ---
    val autofill = LocalAutofill.current
    val autofillNode = remember {
        AutofillNode(
            onFill = { filled ->
                val digits = filled.filter(Char::isDigit).take(length)
                onValueChange(digits)
                if (digits.length == length) onFilled(digits)
            },
            // En Android NO existe OneTimeCode. Usa Password o elimínalo.
            autofillTypes = listOf(AutofillType.Password)
        )
    }
    LocalAutofillTree.current += autofillNode

    var positioned by remember { mutableStateOf(false) }

    val nextIndex = (filtered.length).coerceIn(0, length - 1)

    val cursorAlpha by rememberInfiniteTransition(label = "cursor")
        .animateFloat(
            initialValue = 1f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(animation = tween(700), repeatMode = RepeatMode.Reverse),
            label = "cursorAlpha"
        )

    val borderColor: Color = when {
        isError -> MaterialTheme.colorScheme.error
        isFocused -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }

    BasicTextField(
        modifier = modifier
            .focusRequester(focusRequester)
            .onGloballyPositioned { coordinates ->
                autofillNode.boundingBox = coordinates.boundsInWindow()
                positioned = true
            }
            .onFocusChanged { state ->
                isFocused = state.isFocused
            }
            .semantics { /*this[ContentDescriptionKey] = "OTP input" */},
        value = filtered,
        onValueChange = { new ->
            val digits = new.filter(Char::isDigit).take(length)
            onValueChange(digits)
            if (digits.length == length) onFilled(digits)
        },
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onSurface),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        decorationBox = { innerField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(cellSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(length) { index ->
                    val char = filtered.getOrNull(index)?.toString() ?: ""
                    val isCurrent = isFocused && index == nextIndex && filtered.length < length

                    Cell(
                        value = char,
                        size = cellSize,
                        cornerRadius = cornerRadius,
                        borderColor = borderColor,
                        textStyle = textStyle,
                        drawCursor = isCurrent,
                        cursorAlpha = cursorAlpha,
                        visualTransformation = visualTransformation,
                        isFilled = char.isNotEmpty(),
                        indexForSemantics = index + 1,
                        totalForSemantics = length
                    )
                }
            }
            // Mantener el campo invisible en el árbol para IME/foco
            Box(Modifier.size(0.dp)) { innerField() }
        },
    )

    // Solicitar autofill SOLO cuando hay foco y boundingBox listo
    LaunchedEffect(isFocused, positioned) {
        if (isFocused && positioned) {
            autofill?.requestAutofillForNode(autofillNode)
        }
    }
}

@Composable
private fun Cell(
    value: String,
    size: Dp,
    cornerRadius: Dp,
    borderColor: Color,
    textStyle: TextStyle,
    drawCursor: Boolean,
    cursorAlpha: Float,
    visualTransformation: VisualTransformation,
    isFilled: Boolean,
    indexForSemantics: Int,
    totalForSemantics: Int,
) {
    val shape = RoundedCornerShape(cornerRadius)

    val displayText = if (isFilled) {
        visualTransformation.filter(AnnotatedString(value)).text
    } else AnnotatedString("")

    Box(
        modifier = Modifier
            .size(size)
            .border(1.dp, borderColor, shape)
            .semantics { /*this[ContentDescriptionKey] = "OTP cell $indexForSemantics of $totalForSemantics"*/ },
        contentAlignment = Alignment.Center
    ) {
        if (isFilled) {
            Text(
                text = displayText,
                style = textStyle,
                color = MaterialTheme.colorScheme.onSurface
            )
        } else if (drawCursor) {
            // Caret simple centrado
            val caretHeightPx = with(LocalDensity.current) { textStyle.fontSize.toPx() * 1.2f }
            Canvas(Modifier.height(size * 0.6f)) {
                val caretHeight = minOf(caretHeightPx, size.toPx() * 0.6f)
                val x = size.toPx() / 2f
                val centerY = size.toPx() / 2f
                drawLine(
                    color = Color.Black,
                    start = Offset(x, centerY - caretHeight / 2f),
                    end = Offset(x, centerY + caretHeight / 2f),
                    strokeWidth = 2f
                )
            }
        }
    }
}

// ---------- Wrapper con estado interno (opcional) ----------
@Composable
fun OtpCode(
    modifier: Modifier = Modifier,
    length: Int = 6,
    isError: Boolean = false,
    onFilled: (String) -> Unit = {},
) {
    var otp by rememberSaveable { mutableStateOf("") }

    OtpCodeInput(
        modifier = modifier,
        length = length,
        value = otp,
        onValueChange = { otp = it },
        onFilled = onFilled,
        isError = isError
    )
}

// ---------- Semantics helper ----------

// ---------- Preview de ejemplo (sin Hilt) ----------
//@Preview
//@Composable
//fun OtpSamplePreview() {
//    MaterialTheme {
//        var code by rememberSaveable { mutableStateOf("") }
//        Row(Modifier.padding(16.dp)) {
//            OtpCodeInput(
//                value = code,
//                onValueChange = { code = it },
//                onFilled = { /* Submit code */ },
//                isError = false,
//                cellSize = 52.dp,
//                cellSpacing = 10.dp,
//                textStyle = MaterialTheme.typography.headlineSmall
//            )
//        }
//    }
//}
