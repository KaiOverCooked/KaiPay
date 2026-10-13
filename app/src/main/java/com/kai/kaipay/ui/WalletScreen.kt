package com.kai.kaipay.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kai.kaipay.model.DemoAccount
import com.kai.kaipay.model.WalletTransaction
import com.kai.kaipay.viewmodel.WalletViewModel
import java.text.NumberFormat
import java.util.Locale

private fun rupiah(value: Long): String =
    NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(value).substringBefore(",").trim()

@Composable
fun WalletScreen(viewModel: WalletViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var fromId by remember { mutableStateOf("kai") }
    var toId by remember { mutableStateOf("alex") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    if (state.message != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissMessage,
            confirmButton = { TextButton(onClick = viewModel::dismissMessage) { Text("OK") } },
            title = { Text("KaiPay") },
            text = { Text(state.message.orEmpty()) }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("KaiPay", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text("DOMPET DIGITAL • MODE SIMULASI", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(4.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(18.dp)) {
                    Text("Saldo akun demo", style = MaterialTheme.typography.titleMedium)
                    Text(
                        rupiah(state.accounts.firstOrNull { it.id == fromId }?.balance ?: 0L),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Saldo dan transfer hanya tersimpan di perangkat ini.")
                }
            }
        }
        item {
            Text("Kirim uang simulasi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            AccountPicker("Dari akun", state.accounts, fromId) { selected ->
                fromId = selected
                if (toId == selected) toId = state.accounts.firstOrNull { it.id != selected }?.id ?: selected
            }
            AccountPicker("Ke akun", state.accounts, toId) { selected -> toId = selected }
            OutlinedTextField(
                value = amount,
                onValueChange = { value -> if (value.all(Char::isDigit)) amount = value },
                label = { Text("Nominal (Rupiah)") },
                placeholder = { Text("Contoh: 25000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = note,
                onValueChange = { note = it.take(80) },
                label = { Text("Catatan (opsional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = { viewModel.transfer(fromId, toId, amount, note) },
                enabled = !state.busy && state.accounts.size >= 2,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (state.busy) "Memproses..." else "Transfer simulasi") }
            Text(
                "Tidak memakai uang sungguhan, rekening bank, atau payment gateway.",
                style = MaterialTheme.typography.bodySmall
            )
        }
        item {
            Text("Akun demo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        }
        items(state.accounts, key = { it.id }) { account ->
            AccountRow(account)
        }
        item {
            Text("Riwayat transaksi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        }
        if (state.transactions.isEmpty()) {
            item { Text("Belum ada transaksi. Coba transfer antar akun demo.") }
        } else {
            items(state.transactions, key = { it.id }) { transaction ->
                TransactionRow(transaction, state.accounts)
            }
        }
    }
}

@Composable
private fun AccountPicker(
    label: String,
    accounts: List<DemoAccount>,
    selectedId: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = accounts.firstOrNull { it.id == selectedId }
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge)
        androidx.compose.foundation.layout.Box {
            TextButton(onClick = { expanded = true }, enabled = accounts.isNotEmpty()) {
                Text(selected?.displayName ?: "Memuat akun…")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                accounts.forEach { account ->
                    DropdownMenuItem(
                        text = { Text(account.displayName) },
                        onClick = { onSelected(account.id); expanded = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountRow(account: DemoAccount) {
    Card(shape = RoundedCornerShape(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(account.displayName, fontWeight = FontWeight.SemiBold)
                Text("Akun demo", style = MaterialTheme.typography.bodySmall)
            }
            Text(rupiah(account.balance), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TransactionRow(transaction: WalletTransaction, accounts: List<DemoAccount>) {
    val from = accounts.firstOrNull { it.id == transaction.fromAccountId }?.displayName ?: transaction.fromAccountId
    val to = accounts.firstOrNull { it.id == transaction.toAccountId }?.displayName ?: transaction.toAccountId
    Card(shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text("${from} → ${to}", fontWeight = FontWeight.SemiBold)
            Text(rupiah(transaction.amount), style = MaterialTheme.typography.titleMedium)
            if (transaction.note.isNotBlank()) Text(transaction.note)
            Text("SIMULASI BERHASIL", style = MaterialTheme.typography.labelSmall)
        }
    }
}
