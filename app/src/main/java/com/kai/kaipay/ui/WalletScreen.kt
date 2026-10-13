package com.kai.kaipay.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kai.kaipay.model.DemoAccount
import com.kai.kaipay.model.WalletTransaction
import com.kai.kaipay.viewmodel.WalletViewModel
import java.text.NumberFormat
import java.util.Locale

private val KaiLight = lightColorScheme(
    primary = Color(0xFF17191D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE9EDF2),
    onPrimaryContainer = Color(0xFF17191D),
    secondary = Color(0xFF16835D),
    background = Color(0xFFF5F6F8),
    surface = Color.White,
    onSurface = Color(0xFF17191D),
    onSurfaceVariant = Color(0xFF686E78),
    outline = Color(0xFFD9DDE3)
)

private val KaiDark = darkColorScheme(
    primary = Color(0xFFF3F4F6),
    onPrimary = Color(0xFF111318),
    primaryContainer = Color(0xFF242830),
    onPrimaryContainer = Color(0xFFF3F4F6),
    secondary = Color(0xFF66D6A8),
    background = Color(0xFF0C0E12),
    surface = Color(0xFF15181E),
    onSurface = Color(0xFFF0F1F3),
    onSurfaceVariant = Color(0xFFB0B5BF),
    outline = Color(0xFF343943)
)

private fun rupiah(value: Long): String =
    NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(value).substringBefore(",").trim()

@Composable
fun WalletScreen(viewModel: WalletViewModel) {
    var isDark by rememberSaveable { mutableStateOf(false) }
    MaterialTheme(colorScheme = if (isDark) KaiDark else KaiLight) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            WalletContent(
                viewModel = viewModel,
                isDark = isDark,
                onToggleTheme = { isDark = !isDark }
            )
        }
    }
}

@Composable
private fun WalletContent(
    viewModel: WalletViewModel,
    isDark: Boolean,
    onToggleTheme: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var fromId by rememberSaveable { mutableStateOf("kai") }
    var toId by rememberSaveable { mutableStateOf("alex") }
    var amount by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }

    if (state.message != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissMessage,
            confirmButton = { TextButton(onClick = viewModel::dismissMessage) { Text("Oke") } },
            title = { Text("KaiPay") },
            text = { Text(state.message.orEmpty()) }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 18.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("KaiPay", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                    Text("DOMPET DIGITAL • SIMULASI", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    TextButton(onClick = onToggleTheme) {
                        Text(if (isDark) "☀  Mode putih" else "◐  Mode hitam")
                    }
                }
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("TOTAL SALDO AKUN", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        rupiah(state.accounts.firstOrNull { it.id == fromId }?.balance ?: 0L),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(Modifier.height(8.dp).background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(50)).padding(horizontal = 0.dp))
                        Text("Akun demo aktif", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("Uang simulasi — tidak terhubung ke rekening asli.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            SectionHeading(title = "Transfer", subtitle = "Kirim saldo antar akun demo")
            Spacer(Modifier.height(4.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AccountPicker("DARI AKUN", state.accounts, fromId) { selected ->
                        fromId = selected
                        if (toId == selected) toId = state.accounts.firstOrNull { it.id != selected }?.id ?: selected
                    }
                    AccountPicker("KE AKUN", state.accounts, toId) { selected -> toId = selected }
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { value -> if (value.all(Char::isDigit)) amount = value },
                        label = { Text("Nominal (Rupiah)") },
                        placeholder = { Text("Contoh: 25000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it.take(80) },
                        label = { Text("Catatan (opsional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                    Button(
                        onClick = { viewModel.transfer(fromId, toId, amount, note) },
                        enabled = !state.busy && state.accounts.size >= 2,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) { Text(if (state.busy) "Memproses..." else "Transfer sekarang", fontWeight = FontWeight.SemiBold) }
                    Text("Fitur simulasi. Tidak ada uang sungguhan yang dipindahkan.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item { SectionHeading(title = "Akun", subtitle = "Pilih akun untuk melihat saldo dan melakukan transfer") }
        items(state.accounts, key = { it.id }) { account -> AccountRow(account) }
        item { SectionHeading(title = "Aktivitas", subtitle = "Riwayat transaksi terbaru") }
        if (state.transactions.isEmpty()) {
            item {
                Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Text("Belum ada transaksi. Coba transfer antar akun demo.", modifier = Modifier.padding(18.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(state.transactions, key = { it.id }) { transaction -> TransactionRow(transaction, state.accounts) }
        }
    }
}

@Composable
private fun SectionHeading(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { expanded = true },
                    enabled = accounts.isNotEmpty()
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(selected?.displayName ?: "Memuat akun…")
                        Text("⌄")
                    }
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
}

@Composable
private fun AccountRow(account: DemoAccount) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(account.displayName, fontWeight = FontWeight.SemiBold)
                Text("AKUN DEMO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(rupiah(account.balance), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TransactionRow(transaction: WalletTransaction, accounts: List<DemoAccount>) {
    val from = accounts.firstOrNull { it.id == transaction.fromAccountId }?.displayName ?: transaction.fromAccountId
    val to = accounts.firstOrNull { it.id == transaction.toAccountId }?.displayName ?: transaction.toAccountId
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text("$from → $to", fontWeight = FontWeight.SemiBold)
            Text(rupiah(transaction.amount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (transaction.note.isNotBlank()) Text(transaction.note, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("SIMULASI BERHASIL", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
        }
    }
}
