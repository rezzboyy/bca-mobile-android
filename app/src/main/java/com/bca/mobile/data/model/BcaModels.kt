package com.bca.mobile.data.model

data class Account(
    val accountNumber: String = "2270216440",
    val accountType: String = "TABUNGAN TAHAPAN",
    val accountHolder: String = "DADANG MUBAROK",
    val balance: Double = 260240.16,
    val currency: String = "IDR",
    val cardNumber: String = "5307 9521 1830 4782",
    val cardType: String = "Paspor BCA Mastercard Platinum"
)

enum class TransactionType {
    CR, // Kredit / Uang Masuk
    DB  // Debit / Uang Keluar
}

data class Transaction(
    val id: String,
    val date: String,
    val description: String,
    val amount: Long,
    val type: TransactionType,
    val balanceAfter: Long
)

data class TransferRecipient(
    val id: String,
    val accountNumber: String,
    val name: String,
    val bankName: String = "BCA"
)

data class TransferReceipt(
    val referenceNo: String,
    val timestamp: String,
    val sourceAccount: String,
    val destinationAccount: String,
    val destinationName: String,
    val destinationBank: String,
    val amount: Long,
    val adminFee: Long,
    val note: String,
    val status: String = "BERHASIL",
    val service: String = "Realtime Online"
)

data class BcaInboxItem(
    val id: String,
    val typeTitle: String,
    val line1: String,
    val line2: String? = null,
    val fullReceiptMessage: String,
    val isYellow: Boolean = false,
    val receipt: TransferReceipt? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class FlazzTransaction(
    val date: String,
    val merchant: String,
    val amount: Long,
    val type: TransactionType
)

data class FlazzCard(
    val cardNumber: String = "6019 1234 5678 8821",
    val balance: Long = 175_000L,
    val history: List<FlazzTransaction> = emptyList()
)

data class ExchangeRate(
    val currency: String,
    val buyRate: Long,
    val sellRate: Long
)

enum class NetworkSignalState {
    READY,      // Green: Ready / Siap Transaksi (koneksi stabil)
    PROCESSING, // Blue: Processing / Sedang proses transaksi
    OFFLINE     // Red: Offline / Gangguan (tidak ada sinyal / server sibuk)
}
