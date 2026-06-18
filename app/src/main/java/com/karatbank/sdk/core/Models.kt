package com.karatbank.sdk.core

import java.util.Date

data class KaratUser(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val createdAt: Date = Date()
)

data class KaratWallet(
    val id: String = "",
    val userId: String = "",
    val balance: Double = 0.0,
    val currency: String = "MXN",
    val status: String = "ACTIVE"
)

data class VirtualCard(
    val id: String = "",
    val userId: String = "",
    val cardName: String = "",
    val cardNumber: String = "",
    val expirationDate: String = "",
    val cvv: String = "",
    val cardholderName: String = "",
    val status: String = "ACTIVE"
)

data class Transaction(
    val id: String = "",
    val amount: Double = 0.0,
    val type: TransactionType = TransactionType.INCOMING,
    val description: String = "",
    val destinationAccount: String = "",
    val timestamp: Date = Date()
)

enum class TransactionType {
    INCOMING, OUTGOING
}
