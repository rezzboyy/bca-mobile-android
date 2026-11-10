package com.bca.mobile

import com.bca.mobile.data.model.NetworkSignalState
import com.bca.mobile.data.model.TransactionType
import com.bca.mobile.data.repository.BcaRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.Date

class BcaRepositoryTest {

    private lateinit var repository: BcaRepository

    @Before
    fun setUp() {
        BcaRepository.resetInstance()
        repository = BcaRepository.getInstance()
    }

    @Test
    fun testInitialAccountState() {
        val account = repository.account.value
        assertEquals("0060387737", account.accountNumber)
        assertEquals("DADANG MUBAROK", account.accountHolder)
        assertEquals("TABUNGAN TAHAPAN", account.accountType)
        assertEquals(261232130240.16, account.balance, 0.001)
        assertEquals("IDR", account.currency)
    }

    @Test
    fun testVerifyAccessCode() {
        assertTrue("6-character code should pass", repository.verifyAccessCode("ABC123"))
        assertTrue("6-character code with spaces trimmed should pass", repository.verifyAccessCode("  654321  "))
        assertFalse("5-character code should fail", repository.verifyAccessCode("12345"))
        assertFalse("7-character code should fail", repository.verifyAccessCode("1234567"))
        assertFalse("Empty code should fail", repository.verifyAccessCode(""))
    }

    @Test
    fun testUpdateAccessCode() {
        assertTrue("Update valid 6-char access code", repository.updateAccessCode("BCA999"))
        assertEquals("BCA999", repository.accessCode.value)
        assertTrue("Verification with new access code should pass", repository.verifyAccessCode("BCA999"))
        assertFalse("Update with invalid length should fail", repository.updateAccessCode("12345"))
        assertFalse("Update with non-alphanumeric should fail", repository.updateAccessCode("ABC!@#"))
    }

    @Test
    fun testUpdateAccountCustom() {
        repository.updateAccountCustom(
            accountHolder = "BUDI SANTOSO",
            accountNumber = "9988776655",
            balance = 50_000_000.0,
            cardType = "Paspor BCA Platinum",
            cardNumber = "1234 5678 9012 3456",
            accountType = "TABUNGAN TAHAPAN"
        )
        val acc = repository.account.value
        assertEquals("BUDI SANTOSO", acc.accountHolder)
        assertEquals("9988776655", acc.accountNumber)
        assertEquals(50_000_000.0, acc.balance, 0.001)
        assertEquals("Paspor BCA Platinum", acc.cardType)
        assertEquals("1234 5678 9012 3456", acc.cardNumber)
        assertEquals("TABUNGAN TAHAPAN", acc.accountType)
    }

    @Test
    fun testVerifyPin() {
        assertTrue("Standard PIN 123456 should pass", repository.verifyPin("123456"))
        assertTrue("Alternate PIN 000000 should pass", repository.verifyPin("000000"))
        assertTrue("PIN with spaces trimmed should pass", repository.verifyPin(" 123456 "))
        assertTrue("Any 6-digit PIN should pass", repository.verifyPin("654321"))
        assertFalse("5-digit PIN should fail", repository.verifyPin("12345"))
        assertFalse("Non-digit PIN should fail", repository.verifyPin("ABCDEF"))
        assertFalse("Empty PIN should fail", repository.verifyPin(""))
    }

    @Test
    fun testTransferAntarRekeningSuccess() {
        val recipient = repository.recipients.value.first { it.bankName == "BCA" }
        val transferAmount = 50_000L
        val initialBalance = repository.account.value.balance

        val result = repository.transferAntarRekening(
            recipient = recipient,
            amount = transferAmount,
            note = "Uang makan siang"
        )

        assertTrue(result.isSuccess)
        val receipt = result.getOrNull()
        assertNotNull(receipt)
        assertEquals("BERHASIL", receipt?.status)
        assertEquals(transferAmount, receipt?.amount)
        assertEquals(0L, receipt?.adminFee)
        assertEquals("Uang makan siang", receipt?.note)
        assertEquals(recipient.name, receipt?.destinationName)

        // Verify account balance deduction
        val updatedBalance = repository.account.value.balance
        assertEquals(initialBalance - transferAmount, updatedBalance, 0.001)

        // Verify latest mutation record
        val latestMutation = repository.mutations.value.first()
        assertEquals(TransactionType.DB, latestMutation.type)
        assertEquals(transferAmount, latestMutation.amount)
        assertEquals(updatedBalance.toLong(), latestMutation.balanceAfter)
        assertTrue(latestMutation.description.contains(recipient.name))
    }

    @Test
    fun testTransferAntarRekeningInsufficientBalance() {
        val recipient = repository.recipients.value.first()
        val initialBalance = repository.account.value.balance
        val excessiveAmount = initialBalance.toLong() + 1_000_000L

        val result = repository.transferAntarRekening(
            recipient = recipient,
            amount = excessiveAmount,
            note = "Over budget"
        )

        assertTrue(result.isFailure)
        assertEquals(initialBalance, repository.account.value.balance, 0.001)
    }

    @Test
    fun testTransferAntarRekeningInvalidAmount() {
        val recipient = repository.recipients.value.first()

        val zeroResult = repository.transferAntarRekening(recipient, 0L, "Zero")
        assertTrue(zeroResult.isFailure)

        val negativeResult = repository.transferAntarRekening(recipient, -50_000L, "Negative")
        assertTrue(negativeResult.isFailure)
    }

    @Test
    fun testTransferAntarBankBiFast() {
        val initialBalance = repository.account.value.balance
        val amount = 50_000L
        val expectedFee = 2_500L

        val result = repository.transferAntarBank(
            recipientBank = "BANK MANDIRI",
            recipientAccount = "1400012345678",
            recipientName = "HENDRA WIJAYA",
            amount = amount,
            service = "BI-FAST",
            note = "Bayar sewa"
        )

        assertTrue(result.isSuccess)
        val receipt = result.getOrNull()
        assertNotNull(receipt)
        assertEquals(expectedFee, receipt?.adminFee)
        assertEquals(amount, receipt?.amount)
        assertEquals("BANK MANDIRI", receipt?.destinationBank)

        // Balance must deduct amount + BI-FAST fee
        val updatedBalance = repository.account.value.balance
        assertEquals(initialBalance - (amount + expectedFee), updatedBalance, 0.001)
    }

    @Test
    fun testTransferAntarBankBiFastWithoutHyphen() {
        // Authentic BCA screen uses "BI FAST" without hyphen
        val initialBalance = repository.account.value.balance
        val amount = 200_000L
        val expectedFee = 2_500L

        val result = repository.transferAntarBank(
            recipientBank = "SEABANK",
            recipientAccount = "901717125251",
            recipientName = "BENI SURYAWAN",
            amount = amount,
            service = "BI FAST",
            note = ""
        )

        assertTrue(result.isSuccess)
        val receipt = result.getOrNull()
        assertNotNull(receipt)
        assertEquals(expectedFee, receipt?.adminFee)
        assertEquals(amount, receipt?.amount)
        assertEquals("SEABANK", receipt?.destinationBank)
        assertEquals("BENI SURYAWAN", receipt?.destinationName)

        val updatedBalance = repository.account.value.balance
        assertEquals(initialBalance - (amount + expectedFee), updatedBalance, 0.001)
    }

    @Test
    fun testTransferAntarBankRealtimeOnline() {
        val initialBalance = repository.account.value.balance
        val amount = 50_000L
        val expectedFee = 6_500L

        val result = repository.transferAntarBank(
            recipientBank = "BRI",
            recipientAccount = "001201998877501",
            recipientName = "DEWI LESTARI",
            amount = amount,
            service = "Realtime Online",
            note = "Invoice belanja"
        )

        assertTrue(result.isSuccess)
        val receipt = result.getOrNull()
        assertNotNull(receipt)
        assertEquals(expectedFee, receipt?.adminFee)
        assertEquals(amount, receipt?.amount)
        assertEquals("BRI", receipt?.destinationBank)

        // Balance must deduct amount + Online fee
        val updatedBalance = repository.account.value.balance
        assertEquals(initialBalance - (amount + expectedFee), updatedBalance, 0.001)
    }

    @Test
    fun testFlazzCardStateAndHistory() {
        val flazzCard = repository.flazzCard.value
        assertEquals("6019 1234 5678 8821", flazzCard.cardNumber)
        assertEquals(175_000L, flazzCard.balance)
        assertFalse(flazzCard.history.isEmpty())
        assertEquals(4, flazzCard.history.size)

        val tapCard = repository.simulateFlazzTap()
        assertEquals(flazzCard.cardNumber, tapCard.cardNumber)
        assertEquals(flazzCard.balance, tapCard.balance)
    }

    @Test
    fun testCardlessWithdrawalCodeGeneration() {
        val amount = 100_000L
        val code1 = repository.generateCardlessCode(amount)
        val code2 = repository.generateCardlessCode(amount)

        assertEquals(6, code1.length)
        assertEquals(6, code2.length)
        assertTrue(code1.all { it.isDigit() })
        assertTrue(code2.all { it.isDigit() })
    }

    @Test
    fun testFormatRupiah() {
        val formatted = BcaRepository.formatRupiah(250_000L)
        assertTrue(formatted.startsWith("Rp"))
        assertTrue(formatted.contains("250"))
    }

    @Test
    fun testRegisterBcaRecipients() {
        val newAccounts = listOf("8881234567", "9998765432")
        val added = repository.registerBcaRecipients(newAccounts)
        assertEquals(2, added.size)
        assertTrue(repository.recipients.value.any { it.accountNumber == "8881234567" })
        assertTrue(repository.recipients.value.any { it.accountNumber == "9998765432" })
    }

    @Test
    fun testLookupBcaAccountHolder() {
        val existingName = repository.lookupBcaAccountHolder("2181475538")
        assertEquals("ABDILLAH", existingName)
        val generatedName = repository.lookupBcaAccountHolder("1234567890")
        assertNotNull(generatedName)
        assertTrue(generatedName.isNotEmpty())
    }

    @Test
    fun testInitialInboxIsEmpty() {
        // User request: Default Inbox must be clean/empty
        assertTrue("Initial inbox should be empty", repository.inboxItems.value.isEmpty())
        assertEquals(0, repository.inboxItems.value.size)
    }

    @Test
    fun testTransferAntarRekeningAutoRecordsToInbox() {
        val recipient = repository.recipients.value.first { it.bankName == "BCA" }
        val amount = 100_000L

        val result = repository.transferAntarRekening(
            recipient = recipient,
            amount = amount,
            note = "Bayar invoice"
        )

        assertTrue(result.isSuccess)
        val receipt = result.getOrNull()
        assertNotNull(receipt)

        // Verify inbox auto-recording
        val inbox = repository.inboxItems.value
        assertEquals(1, inbox.size)

        val item = inbox.first()
        assertEquals("Transfer Antar Rekening", item.typeTitle)
        val combinedText = "${item.line1} ${item.line2.orEmpty()}"
        recipient.name.split(" ").forEach { namePart ->
            assertTrue("Combined line text should contain $namePart", combinedText.contains(namePart))
        }
        assertTrue(item.fullReceiptMessage.contains(recipient.accountNumber))
        assertTrue(item.fullReceiptMessage.contains("STATUS: BERHASIL"))
        assertEquals(receipt, item.receipt)
        assertFalse(item.isYellow)
    }

    @Test
    fun testTransferAntarBankAutoRecordsToInbox() {
        val amount = 75_000L
        val result = repository.transferAntarBank(
            recipientBank = "BANK MANDIRI",
            recipientAccount = "1400012345678",
            recipientName = "HENDRA WIJAYA",
            amount = amount,
            service = "BI-FAST",
            note = "Cicilan"
        )

        assertTrue(result.isSuccess)
        val receipt = result.getOrNull()
        assertNotNull(receipt)

        val inbox = repository.inboxItems.value
        assertEquals(1, inbox.size)

        val item = inbox.first()
        assertEquals("Transfer Antar Bank", item.typeTitle)
        assertTrue(item.line1.contains("HENDRA") || item.line2?.contains("WIJAYA") == true)
        assertTrue(item.fullReceiptMessage.contains("BANK MANDIRI"))
        assertTrue(item.fullReceiptMessage.contains("1400012345678"))
        assertTrue(item.fullReceiptMessage.contains("HENDRA WIJAYA"))
        assertEquals(receipt, item.receipt)
    }

    @Test
    fun testDeleteInboxItem() {
        val recBca = repository.recipients.value.first { it.bankName == "BCA" }
        repository.transferAntarRekening(recBca, 50_000L, "Trx 1")
        repository.transferAntarBank("BRI", "001201998877501", "DEWI LESTARI", 50_000L, "BI-FAST", "Trx 2")

        assertEquals(2, repository.inboxItems.value.size)
        val firstId = repository.inboxItems.value[0].id
        val secondId = repository.inboxItems.value[1].id

        // Delete first item
        repository.deleteInboxItem(firstId)
        assertEquals(1, repository.inboxItems.value.size)
        assertEquals(secondId, repository.inboxItems.value[0].id)

        // Delete remaining item
        repository.deleteInboxItem(secondId)
        assertTrue(repository.inboxItems.value.isEmpty())
    }

    @Test
    fun testClearInbox() {
        val recBca = repository.recipients.value.first { it.bankName == "BCA" }
        repository.transferAntarRekening(recBca, 25_000L, "Trx A")
        repository.transferAntarRekening(recBca, 35_000L, "Trx B")
        assertEquals(2, repository.inboxItems.value.size)

        repository.clearInbox()
        assertTrue("Inbox should be completely empty after clearInbox()", repository.inboxItems.value.isEmpty())
        assertEquals(0, repository.inboxItems.value.size)
    }

    @Test
    fun testInboxNewestFirstOrdering() {
        val recBca = repository.recipients.value.first { it.bankName == "BCA" }
        repository.transferAntarRekening(recBca, 10_000L, "First")
        repository.transferAntarBank("SEABANK", "901717125251", "BENI SURYAWAN", 20_000L, "BI FAST", "Second")

        val inbox = repository.inboxItems.value
        assertEquals(2, inbox.size)
        // Second transaction must be on top (index 0)
        assertEquals("Transfer Antar Bank", inbox[0].typeTitle)
        assertEquals("Transfer Antar Rekening", inbox[1].typeTitle)
    }

    @Test
    fun testAuthenticBcaReferenceGeneration() {
        val ref = repository.generateBcaReferenceNumber(Date(), "1171450646")
        assertEquals(35, ref.length)
        assertTrue("Ref must start with channel prefix 95031", ref.startsWith("95031"))
        // First 27 characters must be digits
        val first27 = ref.substring(0, 27)
        assertTrue("First 27 characters must be digits", first27.all { it.isDigit() })
        // Remaining 8 characters must be hex digits
        val last8 = ref.substring(27)
        assertEquals(8, last8.length)
        assertTrue("Last 8 characters must be uppercase hex chars", last8.all { it.isDigit() || it in 'A'..'F' })
    }

    @Test
    fun testTransferReceiptContainsAuthenticRef() {
        val recBca = repository.recipients.value.first { it.bankName == "BCA" }
        val result = repository.transferAntarRekening(recBca, 50_000L, "Uji Ref")
        assertTrue(result.isSuccess)
        val receipt = result.getOrNull()!!
        assertEquals(35, receipt.referenceNo.length)
        assertTrue(receipt.referenceNo.startsWith("95031"))

        val (part1, part2) = com.bca.mobile.ui.components.formatBcaReceiptRef(
            receipt.referenceNo,
            receipt.timestamp,
            receipt.destinationAccount
        )
        assertEquals(27, part1.length)
        assertEquals(8, part2.length)
        assertEquals(receipt.referenceNo, part1 + part2)
    }

    @Test
    fun testMarkInboxItemAsRead() {
        val recBca = repository.recipients.value.first { it.bankName == "BCA" }
        repository.transferAntarRekening(recBca, 50_000L, "Uji Baca")
        val inbox = repository.inboxItems.value
        assertEquals(1, inbox.size)
        assertFalse("Transaksi baru harus berstatus belum dibaca (isYellow = false)", inbox[0].isYellow)

        // Simulasi pengguna mengklik item di Inbox
        repository.markInboxItemAsRead(inbox[0].id)
        val updated = repository.inboxItems.value
        assertEquals(1, updated.size)
        assertTrue("Setelah diklik, amplop harus terbuka dengan kertas kuning (isYellow = true)", updated[0].isYellow)
    }
}
