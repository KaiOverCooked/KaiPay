package com.kai.kaipay.data

import androidx.room.withTransaction
import com.kai.kaipay.model.DemoAccount
import com.kai.kaipay.model.WalletTransaction
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class WalletRepository(private val database: WalletDatabase) {
    private val dao = database.walletDao()

    val accounts: Flow<List<DemoAccount>> = dao.observeAccounts()
    val transactions: Flow<List<WalletTransaction>> = dao.observeTransactions()

    suspend fun initialize() {
        database.withTransaction {
            if (dao.accountCount() == 0) {
                dao.insertAccounts(
                    listOf(
                        DemoAccount("kai", "Kai Demo", 1_000_000L),
                        DemoAccount("alex", "Alex Demo", 750_000L),
                        DemoAccount("naya", "Naya Demo", 500_000L)
                    )
                )
            }
        }
    }

    suspend fun transfer(fromId: String, toId: String, amount: Long, note: String = "") {
        require(fromId != toId) { "Pilih akun tujuan yang berbeda." }
        require(amount > 0L) { "Nominal harus lebih dari Rp0." }

        database.withTransaction {
            val sender = dao.getAccount(fromId) ?: error("Akun pengirim tidak ditemukan.")
            val receiver = dao.getAccount(toId) ?: error("Akun penerima tidak ditemukan.")
            require(sender.balance >= amount) { "Saldo demo tidak mencukupi." }
            val receiverBalance = Math.addExact(receiver.balance, amount)

            dao.updateBalance(sender.id, sender.balance - amount)
            dao.updateBalance(receiver.id, receiverBalance)
            dao.insertTransaction(
                WalletTransaction(
                    id = UUID.randomUUID().toString(),
                    fromAccountId = sender.id,
                    toAccountId = receiver.id,
                    amount = amount,
                    createdAt = System.currentTimeMillis(),
                    status = "SIMULATED_SUCCESS",
                    note = note.trim()
                )
            )
        }
    }
}
