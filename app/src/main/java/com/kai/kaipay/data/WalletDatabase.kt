package com.kai.kaipay.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kai.kaipay.model.DemoAccount
import com.kai.kaipay.model.WalletTransaction

@Database(
    entities = [DemoAccount::class, WalletTransaction::class],
    version = 1,
    exportSchema = false
)
abstract class WalletDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao

    companion object {
        @Volatile private var INSTANCE: WalletDatabase? = null

        fun getInstance(context: Context): WalletDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    WalletDatabase::class.java,
                    "kaipay-demo.db"
                ).build().also { INSTANCE = it }
            }
    }
}
