package com.bca.mobile

import androidx.compose.ui.graphics.Color
import com.bca.mobile.ui.theme.BcaColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BcaThemeTest {
    @Test
    fun testBcaBrandColors() {
        assertNotNull(BcaColors.NavyBackground)
        assertNotNull(BcaColors.NavyRadialCenter)
        assertNotNull(BcaColors.MenuRowGradient)
        assertNotNull(BcaColors.GlossyButtonGradient)
        
        assertEquals(Color(0xFF000518), BcaColors.NavyBackground)
        assertEquals(Color(0xFF001438), BcaColors.NavyRadialCenter)
    }
}
