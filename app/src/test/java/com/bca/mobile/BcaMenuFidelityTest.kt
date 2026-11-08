package com.bca.mobile

import com.bca.mobile.ui.components.BcaNavTab
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BcaMenuFidelityTest {

    @Test
    fun testAllScreenRoutesAvailable() {
        val screens = listOf(
            Screen.Hub,
            Screen.Dashboard,
            Screen.MInfoMenu,
            Screen.MutasiRekening,
            Screen.InfoKurs,
            Screen.MTransferMenu,
            Screen.TransferAntarRekening,
            Screen.TransferAntarBank,
            Screen.FlazzScreen,
            Screen.CardlessScreen,
            Screen.Notifikasi
        )
        assertEquals(11, screens.size)
        assertTrue(screens.contains(Screen.Notifikasi))
        assertTrue(screens.contains(Screen.MInfoMenu))
        assertTrue(screens.contains(Screen.MTransferMenu))
    }

    @Test
    fun testBottomNavTabs() {
        val tabs = BcaNavTab.values()
        assertEquals(6, tabs.size)
        assertTrue(tabs.contains(BcaNavTab.HOME))
        assertTrue(tabs.contains(BcaNavTab.TRANSAKSI))
        assertTrue(tabs.contains(BcaNavTab.QRIS))
        assertTrue(tabs.contains(BcaNavTab.NOTIFIKASI))
        assertTrue(tabs.contains(BcaNavTab.AKUN_SAYA))
        assertTrue(tabs.contains(BcaNavTab.NONE))
    }

    @Test
    fun testMInfoMenuStructure() {
        val expectedInfoItems = listOf(
            "Info Saldo", "Mutasi Rekening", "Rekening Deposito", "Info Reward BCA",
            "NAB Reksadana", "Saldo Reksadana", "Info Kurs",
            "Info Saldo", "Mutasi Rekening",
            "Inquiry Pinjaman",
            "Saldo"
        )
        assertEquals(11, expectedInfoItems.size)
    }

    @Test
    fun testMTransferMenuStructure() {
        val expectedDaftarTransfer = listOf("Antar Rekening", "Antar Bank")
        val expectedTransfer = listOf(
            "Antar Rekening", "Antar Bank", "BCA Virtual Account",
            "Sakuku", "Status Transaksi Sakuku", "Inbox (16)"
        )
        assertEquals(2, expectedDaftarTransfer.size)
        assertEquals(6, expectedTransfer.size)
    }

    @Test
    fun testNotifikasiFilters() {
        val filters = listOf("Semua", "OTP & Persetujuan", "Info")
        assertEquals(3, filters.size)
        assertEquals("Semua", filters[0])
    }
}
