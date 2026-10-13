package com.kai.kaipay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kai.kaipay.ui.WalletScreen
import com.kai.kaipay.viewmodel.WalletViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = (application as WalletApp).repository
        setContent {
            MaterialTheme {
                Surface {
                    val vm: WalletViewModel = viewModel(factory = WalletViewModel.Factory(repository))
                    WalletScreen(vm)
                }
            }
        }
    }
}
