package com.kai.kaipay

import android.app.Application
import com.kai.kaipay.data.WalletDatabase
import com.kai.kaipay.data.WalletRepository

class WalletApp : Application() {
    val repository: WalletRepository by lazy {
        WalletRepository(WalletDatabase.getInstance(this))
    }
}
