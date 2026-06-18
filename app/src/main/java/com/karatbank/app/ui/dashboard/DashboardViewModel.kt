package com.karatbank.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karatbank.sdk.core.KaratWallet
import com.karatbank.sdk.core.Transaction
import com.karatbank.sdk.core.VirtualCard
import com.karatbank.sdk.wallet.KaratWalletManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val walletManager: KaratWalletManager = KaratWalletManager()
) : ViewModel() {

    val walletState: StateFlow<KaratWallet?> = walletManager.listenToWallet()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val virtualCardsState: StateFlow<List<VirtualCard>> = walletManager.listenToVirtualCards()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val transactionsState: StateFlow<List<Transaction>> = walletManager.listenToTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
    fun createVirtualCard(cardName: String) {
        viewModelScope.launch {
            walletManager.createVirtualCard(cardName)
        }
    }
    
    fun executeSPEITransfer(destinationAccount: String, amount: Double, description: String) {
        viewModelScope.launch {
            walletManager.executeSPEITransfer(destinationAccount, amount, description)
        }
    }
}











