package com.kai.kaipay.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "demo_accounts")
data class DemoAccount(
    @PrimaryKey val id: String,
    val displayName: String,
    val balance: Long
)

@Entity(tableName = "wallet_transactions")
data class WalletTransaction(
    @PrimaryKey val id: String,
    val fromAccountId: String,
    val toAccountId: String,
    val amount: Long,
    val createdAt: Long,
    val status: String,
    val note: String
)
