package com.bca.mobile.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.bca.mobile.data.model.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * Persistent Local Storage for BCA Mobile using Android SharedPreferences.
 * Ensures all custom user data (Name, Saldo, Account Number, Account Type, Card Number,
 * Access Code, Inbox, Recipients, and Mutations) persist permanently across:
 * 1. App exit / kill process
 * 2. Device reboot
 * 3. In-App Updates (OTA GitHub updates) and APK upgrades
 */
class BcaPreferences(context: Context) {

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun saveAccount(account: Account) {
        prefs.edit()
            .putString(KEY_ACC_HOLDER, account.accountHolder)
            .putString(KEY_ACC_NUMBER, account.accountNumber)
            .putString(KEY_ACC_BALANCE, account.balance.toString())
            .putString(KEY_ACC_TYPE, account.accountType)
            .putString(KEY_CARD_TYPE, account.cardType)
            .putString(KEY_CARD_NUMBER, account.cardNumber)
            .putString(KEY_CURRENCY, account.currency)
            .apply()
    }

    fun loadAccount(): Account? {
        if (!prefs.contains(KEY_ACC_HOLDER) && !prefs.contains(KEY_ACC_NUMBER)) {
            return null
        }
        val holder = prefs.getString(KEY_ACC_HOLDER, "DADANG MUBAROK") ?: "DADANG MUBAROK"
        val number = prefs.getString(KEY_ACC_NUMBER, "0060387737") ?: "0060387737"
        val balanceStr = prefs.getString(KEY_ACC_BALANCE, "261232130240.16") ?: "261232130240.16"
        val balance = balanceStr.toDoubleOrNull() ?: 261232130240.16
        val accType = prefs.getString(KEY_ACC_TYPE, "TABUNGAN TAHAPAN") ?: "TABUNGAN TAHAPAN"
        val cardType = prefs.getString(KEY_CARD_TYPE, "Paspor BCA Mastercard Platinum") ?: "Paspor BCA Mastercard Platinum"
        val cardNumber = prefs.getString(KEY_CARD_NUMBER, "5307 9521 1830 4782") ?: "5307 9521 1830 4782"
        val currency = prefs.getString(KEY_CURRENCY, "IDR") ?: "IDR"

        return Account(
            accountNumber = number,
            accountType = accType,
            accountHolder = holder,
            balance = balance,
            currency = currency,
            cardNumber = cardNumber,
            cardType = cardType
        )
    }

    fun saveAccessCode(code: String) {
        prefs.edit().putString(KEY_ACCESS_CODE, code).apply()
    }

    fun loadAccessCode(): String? {
        return prefs.getString(KEY_ACCESS_CODE, null)
    }

    fun saveInbox(items: List<BcaInboxItem>) {
        val jsonArray = JSONArray()
        for (item in items) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("typeTitle", item.typeTitle)
                put("line1", item.line1)
                put("line2", item.line2 ?: "")
                put("fullReceiptMessage", item.fullReceiptMessage)
                put("isYellow", item.isYellow)
                put("timestamp", item.timestamp)
                item.receipt?.let { r ->
                    val rObj = JSONObject().apply {
                        put("referenceNo", r.referenceNo)
                        put("timestamp", r.timestamp)
                        put("sourceAccount", r.sourceAccount)
                        put("destinationAccount", r.destinationAccount)
                        put("destinationName", r.destinationName)
                        put("destinationBank", r.destinationBank)
                        put("amount", r.amount)
                        put("adminFee", r.adminFee)
                        put("note", r.note)
                        put("status", r.status)
                        put("service", r.service)
                    }
                    put("receipt", rObj)
                }
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_INBOX_ITEMS, jsonArray.toString()).apply()
    }

    fun loadInbox(): List<BcaInboxItem>? {
        val raw = prefs.getString(KEY_INBOX_ITEMS, null) ?: return null
        return try {
            val jsonArray = JSONArray(raw)
            val list = mutableListOf<BcaInboxItem>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val rObj = obj.optJSONObject("receipt")
                val receipt = if (rObj != null) {
                    TransferReceipt(
                        referenceNo = rObj.optString("referenceNo"),
                        timestamp = rObj.optString("timestamp"),
                        sourceAccount = rObj.optString("sourceAccount"),
                        destinationAccount = rObj.optString("destinationAccount"),
                        destinationName = rObj.optString("destinationName"),
                        destinationBank = rObj.optString("destinationBank"),
                        amount = rObj.optLong("amount"),
                        adminFee = rObj.optLong("adminFee"),
                        note = rObj.optString("note"),
                        status = rObj.optString("status"),
                        service = rObj.optString("service")
                    )
                } else null

                val line2Raw = obj.optString("line2")
                list.add(
                    BcaInboxItem(
                        id = obj.getString("id"),
                        typeTitle = obj.getString("typeTitle"),
                        line1 = obj.getString("line1"),
                        line2 = if (line2Raw.isNullOrEmpty()) null else line2Raw,
                        fullReceiptMessage = obj.getString("fullReceiptMessage"),
                        isYellow = obj.optBoolean("isYellow", false),
                        receipt = receipt,
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (_: Exception) {
            null
        }
    }

    fun saveRecipients(recipients: List<TransferRecipient>) {
        val jsonArray = JSONArray()
        for (rec in recipients) {
            val obj = JSONObject().apply {
                put("id", rec.id)
                put("accountNumber", rec.accountNumber)
                put("name", rec.name)
                put("bankName", rec.bankName)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_RECIPIENTS, jsonArray.toString()).apply()
    }

    fun loadRecipients(): List<TransferRecipient>? {
        val raw = prefs.getString(KEY_RECIPIENTS, null) ?: return null
        return try {
            val jsonArray = JSONArray(raw)
            val list = mutableListOf<TransferRecipient>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    TransferRecipient(
                        id = obj.getString("id"),
                        accountNumber = obj.getString("accountNumber"),
                        name = obj.getString("name"),
                        bankName = obj.optString("bankName", "BCA")
                    )
                )
            }
            list
        } catch (_: Exception) {
            null
        }
    }

    fun saveMutations(mutations: List<Transaction>) {
        val jsonArray = JSONArray()
        for (m in mutations) {
            val obj = JSONObject().apply {
                put("id", m.id)
                put("date", m.date)
                put("description", m.description)
                put("amount", m.amount)
                put("type", m.type.name)
                put("balanceAfter", m.balanceAfter)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_MUTATIONS, jsonArray.toString()).apply()
    }

    fun loadMutations(): List<Transaction>? {
        val raw = prefs.getString(KEY_MUTATIONS, null) ?: return null
        return try {
            val jsonArray = JSONArray(raw)
            val list = mutableListOf<Transaction>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    Transaction(
                        id = obj.getString("id"),
                        date = obj.getString("date"),
                        description = obj.getString("description"),
                        amount = obj.getLong("amount"),
                        type = TransactionType.valueOf(obj.getString("type")),
                        balanceAfter = obj.getLong("balanceAfter")
                    )
                )
            }
            list
        } catch (_: Exception) {
            null
        }
    }

    fun saveInboxUnreadCount(count: Int) {
        prefs.edit().putInt(KEY_INBOX_UNREAD_COUNT, count).apply()
    }

    fun loadInboxUnreadCount(): Int? {
        return if (prefs.contains(KEY_INBOX_UNREAD_COUNT)) {
            prefs.getInt(KEY_INBOX_UNREAD_COUNT, 1652)
        } else null
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "bca_mobile_prefs"
        private const val KEY_ACC_HOLDER = "acc_holder"
        private const val KEY_ACC_NUMBER = "acc_number"
        private const val KEY_ACC_BALANCE = "acc_balance"
        private const val KEY_ACC_TYPE = "acc_type"
        private const val KEY_CARD_TYPE = "card_type"
        private const val KEY_CARD_NUMBER = "card_number"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_ACCESS_CODE = "access_code"
        private const val KEY_INBOX_ITEMS = "inbox_items"
        private const val KEY_INBOX_UNREAD_COUNT = "inbox_unread_count"
        private const val KEY_RECIPIENTS = "recipients"
        private const val KEY_MUTATIONS = "mutations"
    }
}
