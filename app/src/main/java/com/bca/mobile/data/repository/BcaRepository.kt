package com.bca.mobile.data.repository

import android.content.Context
import com.bca.mobile.data.model.*
import com.bca.mobile.data.storage.BcaPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class BcaRepository private constructor(context: Context? = null) {

    private var preferences: BcaPreferences? = context?.let { BcaPreferences(it) }

    private val defaultAccount = Account(
        accountNumber = "0060387737",
        accountType = "TABUNGAN TAHAPAN",
        accountHolder = "DADANG MUBAROK",
        balance = 261232130240.16,
        currency = "IDR",
        cardNumber = "5307 9521 1830 4782",
        cardType = "Paspor BCA Mastercard Platinum"
    )

    private val _account = MutableStateFlow(
        preferences?.loadAccount() ?: defaultAccount
    )
    val account: StateFlow<Account> = _account.asStateFlow()

    private val _networkSignal = MutableStateFlow(NetworkSignalState.READY)
    val networkSignal: StateFlow<NetworkSignalState> = _networkSignal.asStateFlow()

    // Authentic BCA Connection Indicator: biru saat baru masuk menu 1.5 detik, terus hijau terus (tidak looping)
    private val _indicatorSignal = MutableStateFlow(NetworkSignalState.PROCESSING)
    val indicatorSignal: StateFlow<NetworkSignalState> = _indicatorSignal.asStateFlow()

    private val repositoryScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var transitionJob: Job? = null

    init {
        // Awal masuk aplikasi: biru 1.5 detik, lalu hijau terus
        triggerMenuTransition(1500L)
    }

    fun attachContext(context: Context) {
        if (preferences == null) {
            val prefs = BcaPreferences(context.applicationContext)
            preferences = prefs
            prefs.loadAccount()?.let { _account.value = it }
            prefs.loadAccessCode()?.let { _accessCode.value = it }
            prefs.loadInbox()?.let { _inboxItems.value = it }
            prefs.loadRecipients()?.let { _recipients.value = it }
            prefs.loadMutations()?.let { _mutations.value = it }
        }
    }

    fun triggerMenuTransition(durationMs: Long = 1500L) {
        if (_networkSignal.value == NetworkSignalState.OFFLINE) {
            _indicatorSignal.value = NetworkSignalState.OFFLINE
            return
        }
        transitionJob?.cancel()
        _indicatorSignal.value = NetworkSignalState.PROCESSING
        transitionJob = repositoryScope.launch {
            delay(durationMs)
            if (_networkSignal.value != NetworkSignalState.OFFLINE) {
                _indicatorSignal.value = NetworkSignalState.READY
            }
        }
    }

    fun setNetworkSignal(state: NetworkSignalState) {
        _networkSignal.value = state
        if (state == NetworkSignalState.OFFLINE) {
            _indicatorSignal.value = NetworkSignalState.OFFLINE
        }
    }

    fun triggerProcessingPulse(durationMs: Long = 1500L) {
        triggerMenuTransition(durationMs)
    }

    suspend fun <T> runWithNetworkSimulation(block: suspend () -> T): T {
        if (_networkSignal.value == NetworkSignalState.OFFLINE) {
            throw IllegalStateException("205 - Silakan tunggu beberapa saat sampai lampu indikator di pojok kanan atas berwarna hijau sebelum melanjutkan transaksi.")
        }
        val prevState = _networkSignal.value
        _networkSignal.value = NetworkSignalState.PROCESSING
        return try {
            kotlinx.coroutines.delay(900)
            block()
        } finally {
            _networkSignal.value = prevState
        }
    }

    private val defaultMutations = listOf(
        Transaction(
            id = "TRX-001",
            date = "25/10 10:15",
            description = "TRSF E-BANKING CR DARI BUDI SANTOSO",
            amount = 2_500_000L,
            type = TransactionType.CR,
            balanceAfter = 25_850_000L
        ),
        Transaction(
            id = "TRX-002",
            date = "24/10 19:40",
            description = "DEBIT EDC RESTO SEDAP",
            amount = 320_000L,
            type = TransactionType.DB,
            balanceAfter = 23_350_000L
        ),
        Transaction(
            id = "TRX-003",
            date = "23/10 14:22",
            description = "TARIK TUNAI ATM BCA PIM",
            amount = 1_000_000L,
            type = TransactionType.DB,
            balanceAfter = 23_670_000L
        ),
        Transaction(
            id = "TRX-004",
            date = "22/10 09:05",
            description = "TRSF E-BANKING CR GAJI PT RAPTECH DIGITAL",
            amount = 12_500_000L,
            type = TransactionType.CR,
            balanceAfter = 24_670_000L
        ),
        Transaction(
            id = "TRX-005",
            date = "20/10 16:30",
            description = "BCA VIRTUAL ACCOUNT TOKOPEDIA",
            amount = 450_000L,
            type = TransactionType.DB,
            balanceAfter = 12_170_000L
        )
    )

    private val _mutations = MutableStateFlow(
        preferences?.loadMutations() ?: defaultMutations
    )
    val mutations: StateFlow<List<Transaction>> = _mutations.asStateFlow()

    private val defaultRecipients = listOf(
        TransferRecipient("REC-FIQIH", "3741720467", "FIQIH NURAJIZAH", "BCA"),
        TransferRecipient("REC-1", "2181475538", "ABDILLAH", "BCA"),
        TransferRecipient("REC-2", "2481574694", "ACHMAD RIZAL", "BCA"),
        TransferRecipient("REC-3", "6520643140", "ADE FERY PERMANA", "BCA"),
        TransferRecipient("REC-4", "2990889042", "ADE JAJANG NURJAMAN", "BCA"),
        TransferRecipient("REC-5", "0881234567", "BUDI SANTOSO", "BCA"),
        TransferRecipient("REC-6", "1400012345678", "HENDRA WIJAYA", "BANK MANDIRI"),
        TransferRecipient("REC-7", "001201998877501", "DEWI LESTARI", "BRI"),
        TransferRecipient("REC-8", "0779876543", "SITI RAHMAWATI", "BNI"),
        TransferRecipient("REC-9", "0112233445", "AHMAD FAUZI", "CIMB NIAGA"),
        TransferRecipient("REC-SEABANK", "901717125251", "BENI SURYAWAN", "SEABANK")
    )

    private val _recipients = MutableStateFlow(
        preferences?.loadRecipients() ?: defaultRecipients
    )
    val recipients: StateFlow<List<TransferRecipient>> = _recipients.asStateFlow()

    private val _flazzCard = MutableStateFlow(
        FlazzCard(
            cardNumber = "6019 1234 5678 8821",
            balance = 175_000L,
            history = listOf(
                FlazzTransaction("24/10 17:30", "GARDU TOL CILILITAN", 17_000L, TransactionType.DB),
                FlazzTransaction("23/10 12:15", "INDOMARET POINT SUDIRMAN", 32_500L, TransactionType.DB),
                FlazzTransaction("22/10 08:00", "TRANSJAKARTA HALTE BUNDARAN HI", 3_500L, TransactionType.DB),
                FlazzTransaction("20/10 14:00", "TOP UP FLAZZ ATM BCA", 100_000L, TransactionType.CR)
            )
        )
    )
    val flazzCard: StateFlow<FlazzCard> = _flazzCard.asStateFlow()

    private val _exchangeRates = MutableStateFlow(
        listOf(
            ExchangeRate("USD", 15_650L, 15_750L),
            ExchangeRate("SGD", 11_850L, 11_950L),
            ExchangeRate("EUR", 16_980L, 17_150L),
            ExchangeRate("JPY", 102L, 106L),
            ExchangeRate("AUD", 10_380L, 10_520L)
        )
    )
    val exchangeRates: StateFlow<List<ExchangeRate>> = _exchangeRates.asStateFlow()

    private val _inboxItems = MutableStateFlow<List<BcaInboxItem>>(
        preferences?.let { pref ->
            val loaded = pref.loadInbox()
            if (!loaded.isNullOrEmpty() && loaded.any { it.line1.contains("FIQIH NURAJIZAH") }) {
                loaded
            } else {
                val defaults = getOfficialDefaultInboxItems()
                pref.saveInbox(defaults)
                defaults
            }
        } ?: emptyList()
    )
    val inboxItems: StateFlow<List<BcaInboxItem>> = _inboxItems.asStateFlow()

    private val _accessCode = MutableStateFlow(
        preferences?.loadAccessCode() ?: "123456"
    )
    val accessCode: StateFlow<String> = _accessCode.asStateFlow()

    fun updateAccessCode(newCode: String): Boolean {
        val trimmed = newCode.trim()
        if (trimmed.length == 6 && trimmed.all { it.isLetterOrDigit() }) {
            _accessCode.value = trimmed
            preferences?.saveAccessCode(trimmed)
            return true
        }
        return false
    }

    fun verifyAccessCode(code: String): Boolean {
        val trimmed = code.trim()
        if (trimmed.length != 6) return false
        return trimmed.equals(_accessCode.value, ignoreCase = true) || trimmed.all { it.isLetterOrDigit() }
    }

    /**
     * Updates custom account attributes and permanently persists them to local storage.
     * Prevents data from reverting when app is closed, rebooted, or updated.
     */
    fun updateAccountCustom(
        accountHolder: String? = null,
        accountNumber: String? = null,
        balance: Double? = null,
        cardType: String? = null,
        cardNumber: String? = null,
        accountType: String? = null
    ) {
        val curr = _account.value
        val updated = curr.copy(
            accountHolder = accountHolder?.trim()?.ifEmpty { curr.accountHolder } ?: curr.accountHolder,
            accountNumber = accountNumber?.trim()?.ifEmpty { curr.accountNumber } ?: curr.accountNumber,
            balance = balance ?: curr.balance,
            cardType = cardType?.trim()?.ifEmpty { curr.cardType } ?: curr.cardType,
            cardNumber = cardNumber?.trim()?.ifEmpty { curr.cardNumber } ?: curr.cardNumber,
            accountType = accountType?.trim()?.ifEmpty { curr.accountType } ?: curr.accountType
        )
        _account.value = updated
        preferences?.saveAccount(updated)
    }

    fun verifyPin(pin: String): Boolean {
        // Authentic BCA 6-digit PIN verification (accepts 123456, 000000, or any 6 digits)
        val trimmed = pin.trim()
        return trimmed.length == 6 && trimmed.all { it.isDigit() }
    }

    /**
     * Menghasilkan nomor referensi otentik BCA Mobile (35 karakter):
     * Format: 95031 + yyyyMMddHHmmss (14 digit) + 8 digit seq + 8 char hex checksum
     * Contoh: 9503120261106234300114789975A81C11F
     */
    fun generateBcaReferenceNumber(date: Date = Date(), accountNum: String = ""): String {
        val tsFmt = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
        val ts = tsFmt.format(date)
        val cleanAcc = accountNum.filter { it.isDigit() }
        val seqDigits = if (cleanAcc.length >= 8) {
            cleanAcc.takeLast(8)
        } else {
            String.format(Locale.US, "%08d", (Math.abs(cleanAcc.hashCode()) % 90000000) + 10000000)
        }
        val hexChars = "0123456789ABCDEF"
        val hex = buildString {
            for (i in 0 until 8) {
                append(hexChars[(Math.random() * hexChars.length).toInt()])
            }
        }
        return "95031$ts$seqDigits$hex"
    }

    fun transferAntarRekening(
        recipient: TransferRecipient,
        amount: Long,
        note: String
    ): Result<TransferReceipt> {
        val currAccount = _account.value
        if (_networkSignal.value == NetworkSignalState.OFFLINE) {
            return Result.failure(IllegalStateException("205 - Silakan tunggu beberapa saat sampai lampu indikator di pojok kanan atas berwarna hijau sebelum melanjutkan transaksi."))
        }
        if (amount <= 0) {
            return Result.failure(IllegalArgumentException("Nominal transfer harus lebih besar dari 0"))
        }
        if (amount > currAccount.balance) {
            return Result.failure(IllegalStateException("Saldo tidak mencukupi (Saldo: ${formatRupiah(currAccount.balance)})"))
        }

        val newBalance = currAccount.balance - amount
        val updatedAccount = currAccount.copy(balance = newBalance)
        _account.value = updatedAccount
        preferences?.saveAccount(updatedAccount)

        val now = Date()
        val dateFmt = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        val fullDateFmt = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val refNo = generateBcaReferenceNumber(now, recipient.accountNumber)

        val newMutation = Transaction(
            id = refNo,
            date = dateFmt.format(now),
            description = "TRSF E-BANKING KE ${recipient.name} $note".trim(),
            amount = amount,
            type = TransactionType.DB,
            balanceAfter = newBalance.toLong()
        )
        val updatedMutations = listOf(newMutation) + _mutations.value
        _mutations.value = updatedMutations
        preferences?.saveMutations(updatedMutations)

        val receipt = TransferReceipt(
            referenceNo = refNo,
            timestamp = fullDateFmt.format(now),
            sourceAccount = currAccount.accountNumber,
            destinationAccount = recipient.accountNumber,
            destinationName = recipient.name,
            destinationBank = "BCA",
            amount = amount,
            adminFee = 0L,
            note = note.trim(),
            status = "BERHASIL",
            service = ""
        )

        // Otomatis catat bukti transaksi ke Inbox
        recordReceiptToInbox(receipt, "Transfer Antar Rekening")

        return Result.success(receipt)
    }

    fun transferAntarBank(
        recipientBank: String,
        recipientAccount: String,
        recipientName: String,
        amount: Long,
        service: String, // "BI-FAST" atau "Realtime Online"
        note: String
    ): Result<TransferReceipt> {
        val currAccount = _account.value
        if (_networkSignal.value == NetworkSignalState.OFFLINE) {
            return Result.failure(IllegalStateException("205 - Silakan tunggu beberapa saat sampai lampu indikator di pojok kanan atas berwarna hijau sebelum melanjutkan transaksi."))
        }
        val adminFee = if (service.contains("BI-FAST", ignoreCase = true) || service.contains("BI FAST", ignoreCase = true)) 2_500L else 6_500L
        val totalDeduction = amount + adminFee

        if (amount <= 0) {
            return Result.failure(IllegalArgumentException("Nominal transfer harus lebih besar dari 0"))
        }
        if (totalDeduction > currAccount.balance) {
            return Result.failure(IllegalStateException("Saldo tidak cukup untuk nominal + biaya admin"))
        }

        val newBalance = currAccount.balance - totalDeduction
        val updatedAccount = currAccount.copy(balance = newBalance)
        _account.value = updatedAccount
        preferences?.saveAccount(updatedAccount)

        val now = Date()
        val dateFmt = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        val fullDateFmt = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val refNo = generateBcaReferenceNumber(now, recipientAccount)

        val newMutation = Transaction(
            id = refNo,
            date = dateFmt.format(now),
            description = "TRSF KE $recipientBank $recipientName ($service)".trim(),
            amount = totalDeduction,
            type = TransactionType.DB,
            balanceAfter = newBalance.toLong()
        )
        val updatedMutations = listOf(newMutation) + _mutations.value
        _mutations.value = updatedMutations
        preferences?.saveMutations(updatedMutations)

        val receipt = TransferReceipt(
            referenceNo = refNo,
            timestamp = fullDateFmt.format(now),
            sourceAccount = currAccount.accountNumber,
            destinationAccount = recipientAccount.trim(),
            destinationName = recipientName.trim(),
            destinationBank = recipientBank.trim(),
            amount = amount,
            adminFee = adminFee,
            note = note.trim(),
            status = "BERHASIL",
            service = service
        )

        // Otomatis catat bukti transaksi ke Inbox
        recordReceiptToInbox(receipt, "Transfer Antar Bank")

        return Result.success(receipt)
    }

    /**
     * Memformat TransferReceipt ke BcaInboxItem dan menyimpannya ke _inboxItems (urutan terbaru di atas)
     */
    fun recordReceiptToInbox(receipt: TransferReceipt, typeTitle: String) {
        val dotTime = receipt.timestamp.replace(":", ".")
        val nameParts = receipt.destinationName.trim().split(" ")
        val line1: String
        val line2: String?
        if (nameParts.size > 2) {
            line1 = "$dotTime ${nameParts.take(2).joinToString(" ")}"
            line2 = nameParts.drop(2).joinToString(" ")
        } else if (nameParts.size == 2 && receipt.destinationName.length > 12) {
            line1 = "$dotTime ${nameParts[0]}"
            line2 = nameParts[1]
        } else {
            line1 = "$dotTime ${receipt.destinationName}".trim()
            line2 = null
        }

        val fullMsg = buildString {
            append(typeTitle.uppercase(Locale.getDefault())).append("\n")
            append("Tanggal: ").append(receipt.timestamp).append("\n")
            if (!receipt.destinationBank.equals("BCA", ignoreCase = true)) {
                append("Bank: ").append(receipt.destinationBank).append("\n")
            }
            append("Ke: ").append(receipt.destinationAccount).append("\n")
            append("Nama: ").append(receipt.destinationName).append("\n")
            append("Jumlah: Rp ").append(formatNumberOnly(receipt.amount)).append("\n")
            if (receipt.adminFee > 0) {
                append("Biaya Admin: Rp ").append(formatNumberOnly(receipt.adminFee)).append("\n")
            }
            if (receipt.note.isNotBlank()) {
                append("Berita: ").append(receipt.note).append("\n")
            }
            append("STATUS: ").append(receipt.status)
        }

        val inboxItem = BcaInboxItem(
            id = receipt.referenceNo.ifEmpty { "INB-${System.currentTimeMillis()}" },
            typeTitle = typeTitle,
            line1 = line1,
            line2 = line2,
            fullReceiptMessage = fullMsg,
            isYellow = false,
            receipt = receipt,
            timestamp = System.currentTimeMillis()
        )
        val updatedInbox = listOf(inboxItem) + _inboxItems.value
        _inboxItems.value = updatedInbox
        preferences?.saveInbox(updatedInbox)
    }

    fun addInboxItem(item: BcaInboxItem) {
        val updated = listOf(item) + _inboxItems.value
        _inboxItems.value = updated
        preferences?.saveInbox(updated)
    }

    /**
     * Menandai pesan di Inbox sebagai telah dibuka/dibaca (isYellow = true).
     * Ikon amplop akan berubah menjadi amplop terbuka dengan kertas kuning (paritas 100% BCA Mobile).
     * Perubahan disimpan secara permanen ke SharedPreferences sehingga tidak kembali tertutup.
     */
    fun markInboxItemAsRead(id: String) {
        val currentList = _inboxItems.value
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1 && !currentList[index].isYellow) {
            val updated = currentList.toMutableList()
            updated[index] = currentList[index].copy(isYellow = true)
            _inboxItems.value = updated
            preferences?.saveInbox(updated)
        }
    }

    fun deleteInboxItem(id: String) {
        val updated = _inboxItems.value.filterNot { it.id == id }
        _inboxItems.value = updated
        preferences?.saveInbox(updated)
    }

    fun clearInbox() {
        _inboxItems.value = emptyList()
        preferences?.saveInbox(emptyList())
    }

    /**
     * Mengembalikan 13 item riwayat transaksi resmi m-Transfer Inbox persis 100%
     * sesuai screenshot referensi target (WhatsApp / uploaded_media_0_1794321406239.jpg).
     */
    fun getOfficialDefaultInboxItems(): List<BcaInboxItem> {
        fun makeReceipt(
            type: String,
            dateStr: String,
            refSeq: String,
            destAcc: String,
            destName: String,
            destBank: String,
            amount: Long,
            adminFee: Long = 0L,
            service: String = if (destBank == "BCA") "" else "BI FAST"
        ): Pair<TransferReceipt, String> {
            val dateClean = dateStr.replace("/", "").replace(":", "").replace(" ", "")
            val refNo = "95031${dateClean}${refSeq}"
            val r = TransferReceipt(
                referenceNo = refNo,
                timestamp = dateStr,
                sourceAccount = "0060387737",
                destinationAccount = destAcc,
                destinationName = destName,
                destinationBank = destBank,
                amount = amount,
                adminFee = adminFee,
                note = "",
                status = "BERHASIL",
                service = service
            )
            val msg = buildString {
                append("m-Transfer:\n")
                append("BERHASIL\n")
                append(dateStr).append("\n")
                append(refNo).append("\n")
                append("Dari: 0060387737\n")
                append("Ke: ").append(destAcc).append("\n")
                append(destName).append("\n")
                if (destBank != "BCA") {
                    append(destBank).append("\n")
                }
                append(formatRupiah(amount)).append("\n")
                if (adminFee > 0) {
                    append("BIAYA ").append(formatRupiah(adminFee)).append("\n")
                }
                if (service.isNotBlank()) {
                    append("LAYANAN ").append(service).append("\n")
                }
                append("STATUS: BERHASIL")
            }
            return r to msg
        }

        val list = mutableListOf<BcaInboxItem>()

        // 1. 09/11/2026 06:04:57 FIQIH NURAJIZAH (Antar Rekening, Blue)
        val (r1, m1) = makeReceipt("Transfer Antar Rekening", "09/11/2026 06:04:57", "114002875A81", "2181475538", "FIQIH NURAJIZAH", "BCA", 150_000L)
        list.add(BcaInboxItem("INB-20261109-060457", "Transfer Antar Rekening", "09/11/2026 06:04:57 FIQIH NURAJIZAH", null, m1, false, r1, 1794265497000L))

        // 2. 08/11/2026 21:17:36 BENI S... (Antar Bank, Blue)
        val (r2, m2) = makeReceipt("Transfer Antar Bank", "08/11/2026 21:17:36", "118224982B33", "901717125251", "BENI SURYAWAN", "SEABANK", 200_000L, 2500L, "BI FAST")
        list.add(BcaInboxItem("INB-20261108-211736", "Transfer Antar Bank", "08/11/2026 21:17:36 BENI S...", null, m2, false, r2, 1794233856000L))

        // 3. 08/11/2026 18:57:10 AGUS S... (Antar Bank, Blue)
        val (r3, m3) = makeReceipt("Transfer Antar Bank", "08/11/2026 18:57:10", "119033481C44", "1400012345678", "AGUS SETIAWAN", "MANDIRI", 350_000L, 6500L, "REALTIME ONLINE")
        list.add(BcaInboxItem("INB-20261108-185710", "Transfer Antar Bank", "08/11/2026 18:57:10 AGUS S...", null, m3, false, r3, 1794225430000L))

        // 4. 08/11/2026 11:40:35 AGUS S... (Antar Bank, Blue)
        val (r4, m4) = makeReceipt("Transfer Antar Bank", "08/11/2026 11:40:35", "117822904D55", "1400012345678", "AGUS SETIAWAN", "MANDIRI", 500_000L, 6500L, "REALTIME ONLINE")
        list.add(BcaInboxItem("INB-20261108-114035", "Transfer Antar Bank", "08/11/2026 11:40:35 AGUS S...", null, m4, false, r4, 1794199235000L))

        // 5. 08/11/2026 07:42:35 AHMAD FAUZI (Antar Rekening, Blue)
        val (r5, m5) = makeReceipt("Transfer Antar Rekening", "08/11/2026 07:42:35", "113400192E66", "2481574694", "AHMAD FAUZI", "BCA", 100_000L)
        list.add(BcaInboxItem("INB-20261108-074235", "Transfer Antar Rekening", "08/11/2026 07:42:35 AHMAD FAUZI", null, m5, false, r5, 1794184955000L))

        // 6. 06/11/2026 23:43:00 ANDHIKA RAMADHONA (Antar Rekening, 2 Lines, Blue)
        val (r6, m6) = makeReceipt("Transfer Antar Rekening", "06/11/2026 23:43:00", "114789975A81", "6520643140", "ANDHIKA RAMADHONA", "BCA", 250_000L)
        list.add(BcaInboxItem("INB-20261106-234300", "Transfer Antar Rekening", "06/11/2026 23:43:00 ANDHIKA", "RAMADHONA", m6, false, r6, 1794069780000L))

        // 7. 06/11/2026 06:40:55 FIQIH NURAJIZAH (Antar Rekening, Blue)
        val (r7, m7) = makeReceipt("Transfer Antar Rekening", "06/11/2026 06:40:55", "116544321F88", "2181475538", "FIQIH NURAJIZAH", "BCA", 75_000L)
        list.add(BcaInboxItem("INB-20261106-064055", "Transfer Antar Rekening", "06/11/2026 06:40:55 FIQIH NURAJIZAH", null, m7, false, r7, 1794008455000L))

        // 8. 05/11/2026 23:09:34 AHMAD FAUZI (Antar Rekening, Yellow - SUDAH DIBACA / READ!)
        val (r8, m8) = makeReceipt("Transfer Antar Rekening", "05/11/2026 23:09:34", "119877443A99", "2481574694", "AHMAD FAUZI", "BCA", 300_000L)
        list.add(BcaInboxItem("INB-20261105-230934", "Transfer Antar Rekening", "05/11/2026 23:09:34 AHMAD FAUZI", null, m8, true, r8, 1793981374000L))

        // 9. 05/11/2026 23:08:46 BENI S... (Antar Bank, Blue)
        val (r9, m9) = makeReceipt("Transfer Antar Bank", "05/11/2026 23:08:46", "117655432B01", "901717125251", "BENI SURYAWAN", "SEABANK", 120_000L, 2500L, "BI FAST")
        list.add(BcaInboxItem("INB-20261105-230846", "Transfer Antar Bank", "05/11/2026 23:08:46 BENI S...", null, m9, false, r9, 1793981326000L))

        // 10. 05/11/2026 22:45:59 DANA (BCA Virtual Account, Blue)
        val (r10, m10) = makeReceipt("Transfer BCA Virtual Account", "05/11/2026 22:45:59", "115433219C12", "390108812345678", "DANA TOPUP", "BCA VA", 50_000L, 0L, "BCA VIRTUAL ACCOUNT")
        list.add(BcaInboxItem("INB-20261105-224559", "Transfer BCA Virtual Account", "05/11/2026 22:45:59 DANA", null, m10, false, r10, 1793979959000L))

        // 11. 04/11/2026 23:38:32 MERRY ... (Antar Bank, Blue)
        val (r11, m11) = makeReceipt("Transfer Antar Bank", "04/11/2026 23:38:32", "113211098D23", "001201998877501", "MERRY ANGGRAINI", "BRI", 1_000_000L, 2500L, "BI FAST")
        list.add(BcaInboxItem("INB-20261104-233832", "Transfer Antar Bank", "04/11/2026 23:38:32 MERRY ...", null, m11, false, r11, 1793896712000L))

        // 12. 04/11/2026 22:43:11 ANDHIKA RAMADHONA (Antar Rekening, 2 Lines, Blue)
        val (r12, m12) = makeReceipt("Transfer Antar Rekening", "04/11/2026 22:43:11", "119800765E34", "6520643140", "ANDHIKA RAMADHONA", "BCA", 400_000L)
        list.add(BcaInboxItem("INB-20261104-224311", "Transfer Antar Rekening", "04/11/2026 22:43:11 ANDHIKA", "RAMADHONA", m12, false, r12, 1793893391000L))

        // 13. 04/11/2026 16:19:26 AHMAD FAUZI (Antar Rekening, Blue)
        val (r13, m13) = makeReceipt("Transfer Antar Rekening", "04/11/2026 16:19:26", "117654321F45", "2481574694", "AHMAD FAUZI", "BCA", 80_000L)
        list.add(BcaInboxItem("INB-20261104-161926", "Transfer Antar Rekening", "04/11/2026 16:19:26 AHMAD FAUZI", null, m13, false, r13, 1793870366000L))

        return list
    }

    fun resetInboxToOfficialTarget() {
        val target = getOfficialDefaultInboxItems()
        _inboxItems.value = target
        preferences?.saveInbox(target)
    }

    fun lookupBcaAccountHolder(accountNumber: String): String {
        val existing = _recipients.value.firstOrNull { it.accountNumber == accountNumber.trim() }
        if (existing != null) return existing.name
        val mockNames = listOf(
            "YUSUF MAULANA", "DIMAS PRASETYO", "EKO WAHYUDI", 
            "FAJAR NUGROHO", "INDAH PERMATA", "TRI WAHYUNI",
            "REZA PAHLEVI", "BAYU ANGGORO", "CANDRA KURNIAWAN"
        )
        val idx = kotlin.math.abs(accountNumber.trim().hashCode()) % mockNames.size
        return mockNames[idx]
    }

    fun registerBcaRecipients(accountNumbers: List<String>): List<TransferRecipient> {
        val addedList = mutableListOf<TransferRecipient>()
        val currentRecipients = _recipients.value.toMutableList()
        
        for (acc in accountNumbers) {
            val cleanAcc = acc.trim()
            if (cleanAcc.isNotEmpty()) {
                val holderName = lookupBcaAccountHolder(cleanAcc)
                val newRec = TransferRecipient(
                    id = "REC-${System.currentTimeMillis()}-${addedList.size}",
                    accountNumber = cleanAcc,
                    name = holderName,
                    bankName = "BCA"
                )
                // If not already exists, add
                if (currentRecipients.none { it.accountNumber == cleanAcc && it.bankName == "BCA" }) {
                    currentRecipients.add(newRec)
                    addedList.add(newRec)
                }
            }
        }
        _recipients.value = currentRecipients
        preferences?.saveRecipients(currentRecipients)
        return addedList
    }

    fun addRecipient(accountNumber: String, name: String, bankName: String = "BCA") {
        val newRec = TransferRecipient(
            id = "REC-${System.currentTimeMillis()}",
            accountNumber = accountNumber,
            name = name.uppercase(),
            bankName = bankName
        )
        val updated = _recipients.value + newRec
        _recipients.value = updated
        preferences?.saveRecipients(updated)
    }

    fun simulateFlazzTap(): FlazzCard {
        return _flazzCard.value
    }

    fun generateCardlessCode(amount: Long = 0L): String {
        return (100000..999999).random().toString()
    }

    companion object {
        @Volatile
        private var INSTANCE: BcaRepository? = null
        @Volatile
        private var appContext: Context? = null

        fun initialize(context: Context) {
            val app = context.applicationContext
            appContext = app
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = BcaRepository(app)
                    } else {
                        INSTANCE?.attachContext(app)
                    }
                }
            } else {
                INSTANCE?.attachContext(app)
            }
        }

        fun getInstance(context: Context? = null): BcaRepository {
            if (context != null && appContext == null) {
                appContext = context.applicationContext
            }
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BcaRepository(appContext).also { INSTANCE = it }
            }
        }

        internal fun resetInstance() {
            synchronized(this) {
                INSTANCE = null
            }
        }

        fun formatRupiah(amount: Double): String {
            val formatter = java.text.DecimalFormat("#,##0.00", java.text.DecimalFormatSymbols(Locale.US))
            return "Rp. " + formatter.format(amount)
        }

        fun formatRupiah(amount: Long): String {
            val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
            return "Rp " + formatter.format(amount) + ",00"
        }

        fun formatNumberOnly(amount: Long): String {
            val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
            return formatter.format(amount)
        }
    }
}
