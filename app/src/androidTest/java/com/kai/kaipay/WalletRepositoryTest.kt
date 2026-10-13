package com.kai.kaipay

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kai.kaipay.data.WalletDatabase
import com.kai.kaipay.data.WalletRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WalletRepositoryTest {
    private lateinit var database: WalletDatabase
    private lateinit var repository: WalletRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, WalletDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = WalletRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun transferUpdatesBalancesAndHistory() = runBlocking {
        repository.initialize()
        repository.transfer("kai", "alex", 125_000L, "Test transfer")

        val accounts = repository.accounts.first().associateBy { it.id }
        assertEquals(875_000L, accounts.getValue("kai").balance)
        assertEquals(875_000L, accounts.getValue("alex").balance)
        val history = repository.transactions.first()
        assertEquals(1, history.size)
        assertEquals(125_000L, history.first().amount)
        assertEquals("SIMULATED_SUCCESS", history.first().status)
    }

    @Test
    fun insufficientBalanceDoesNotChangeBalancesOrHistory() = runBlocking {
        repository.initialize()
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking { repository.transfer("kai", "alex", 5_000_000L) }
        }
        val accounts = repository.accounts.first().associateBy { it.id }
        assertEquals(1_000_000L, accounts.getValue("kai").balance)
        assertEquals(750_000L, accounts.getValue("alex").balance)
        assertEquals(0, repository.transactions.first().size)
    }

    @Test
    fun selfTransferIsRejected() = runBlocking {
        repository.initialize()
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking { repository.transfer("kai", "kai", 1L) }
        }
        assertEquals(0, repository.transactions.first().size)
    }

    @Test
    fun zeroAmountIsRejected() = runBlocking {
        repository.initialize()
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking { repository.transfer("kai", "alex", 0L) }
        }
        assertEquals(0, repository.transactions.first().size)
    }
}
