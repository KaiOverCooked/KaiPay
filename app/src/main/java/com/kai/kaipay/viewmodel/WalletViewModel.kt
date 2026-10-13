package com.kai.kaipay.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kai.kaipay.data.WalletRepository
import com.kai.kaipay.model.DemoAccount
import com.kai.kaipay.model.WalletTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WalletUiState(
    val accounts: List<DemoAccount> = emptyList(),
    val transactions: List<WalletTransaction> = emptyList(),
    val message: String? = null,
    val busy: Boolean = false
)

class WalletViewModel(private val repository: WalletRepository) : ViewModel() {
    private val message = MutableStateFlow<String?>(null)
    private val busy = MutableStateFlow(false)

    val uiState: StateFlow<WalletUiState> = combine(
        repository.accounts, repository.transactions, message, busy
    ) { accounts, transactions, info, isBusy ->
        WalletUiState(accounts, transactions, info, isBusy)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WalletUiState())

    init {
        viewModelScope.launch {
            runCatching { repository.initialize() }
                .onFailure { message.value = "Gagal menyiapkan akun demo." }
        }
    }

    fun dismissMessage() { message.value = null }

    fun transfer(fromId: String, toId: String, amountText: String, note: String) {
        val amount = amountText.trim().toLongOrNull()
        if (amount == null || amount <= 0L) {
            message.value = "Masukkan nominal rupiah yang valid."
            return
        }
        viewModelScope.launch {
            busy.value = true
            try {
                repository.transfer(fromId, toId, amount, note)
                message.value = "Transfer simulasi berhasil."
            } catch (e: IllegalArgumentException) {
                message.value = e.message ?: "Transfer ditolak."
            } catch (e: Exception) {
                message.value = "Transfer gagal diproses. Saldo tidak boleh berubah."
            } finally {
                busy.value = false
            }
        }
    }

    class Factory(private val repository: WalletRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
                return WalletViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
