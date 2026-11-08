package com.bca.mobile.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object BcaColors {
    val NavyBackground = Color(0xFF000518)
    val NavyDeep = Color(0xFF000921)
    val NavyRadialCenter = Color(0xFF001438)
    
    val CardBorderWhite = Color(0xFFFFFFFF)
    val DividerWhite = Color(0xFFFFFFFF)
    
    val BadgeBlue = Color(0xFF004FA8)
    val TextDisclaimer = Color(0xF2FFFFFF)
    val ModalBackground = Color(0xFF002D72)
    val ModalBorder = Color(0x66448AFF)
    
    // 3-Row Menu Gradient (Vertical 180 deg)
    val MenuRowGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0196F2), // Top light metallic blue
            Color(0xFF015DB5), // Mid classic blue
            Color(0xFF003789)  // Bottom deep royal navy
        )
    )

    // 3D Glossy Action Buttons (5-stop vertical metallic gradient)
    val GlossyButtonGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1899ED), // Top gloss highlight
            Color(0xFF008DE5),
            Color(0xFF0167BC),
            Color(0xFF013F92),
            Color(0xFF02368A)  // Deep metallic base
        )
    )
}
