package com.kai.kaipay.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kai.kaipay.model.DemoAccount
import com.kai.kaipay.model.WalletTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Query("SELECT * FROM demo_accounts ORDER BY id")
    fun observeAccounts(): Flow<List<DemoAccount>>

    @Query("SELECT * FROM wallet_transactions ORDER BY createdAt DESC")
    fun observeTransactions(): Flow<List<WalletTransaction>>

    @Query("SELECT * FROM demo_accounts WHERE id = :id LIMIT 1")
    suspend fun getAccount(id: String): DemoAccount?

    @Query("SELECT COUNT(*) FROM demo_accounts")
    suspend fun accountCount(): Int

    @Insert
    suspend fun insertAccounts(accounts: List<DemoAccount>)

    @Insert
    suspend fun insertTransaction(transaction: WalletTransaction)

    @Query("UPDATE demo_accounts SET balance = :balance WHERE id = :id")
    suspend fun updateBalance(id: String, balance: Long)
}
