package com.karatbank.sdk.wallet

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.karatbank.sdk.core.KaratWallet
import com.karatbank.sdk.core.Transaction
import com.karatbank.sdk.core.VirtualCard
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class KaratWalletManager(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance()
) {

    fun listenToWallet(): Flow<KaratWallet?> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(null)
            awaitClose {}
            return@callbackFlow
        }

        val registration = firestore.collection("wallets")
            .whereEqualTo("userId", userId)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val wallet = snapshot?.documents?.firstOrNull()?.toObject(KaratWallet::class.java)
                trySend(wallet)
            }

        awaitClose { registration.remove() }
    }

    fun listenToVirtualCards(): Flow<List<VirtualCard>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }

        val registration = firestore.collection("virtualCards")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val cards = snapshot?.documents?.mapNotNull { it.toObject(VirtualCard::class.java) } ?: emptyList()
                trySend(cards)
            }

        awaitClose { registration.remove() }
    }

    suspend fun createVirtualCard(cardName: String): Result<String> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Not logged in"))
        return try {
            val db = firestore
            val newCardRef = db.collection("virtualCards").document()
            
            // Random Card generation logic
            val cardNumber = (1..16).map { (0..9).random() }.joinToString("")
            val cvv = (1..3).map { (0..9).random() }.joinToString("")
            val expMonth = (1..12).random().toString().padStart(2, '0')
            val expYear = (26..30).random().toString()
            val expirationDate = "$expMonth/$expYear"
            
            val virtualCard = VirtualCard(
                id = newCardRef.id,
                userId = userId,
                cardName = cardName,
                cardNumber = cardNumber,
                expirationDate = expirationDate,
                cvv = cvv,
                cardholderName = auth.currentUser?.displayName ?: "User",
                status = "ACTIVE"
            )
            
            newCardRef.set(virtualCard).await()
            Result.success(newCardRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun executeSPEITransfer(destinationAccount: String, amount: Double, description: String): Result<String> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Not logged in"))
        return try {
            // Write to transactions to simulate SPEI locally
            val db = firestore
            val newTxRef = db.collection("transactions").document()
            
            val walletQuery = db.collection("wallets").whereEqualTo("userId", userId).get().await()
            val walletDoc = walletQuery.documents.firstOrNull()
            
            if (walletDoc != null) {
                val currentBalance = walletDoc.getDouble("balance") ?: 0.0
                if (currentBalance < amount) {
                    return Result.failure(Exception("Insufficient funds"))
                }
                db.runBatch { batch ->
                    batch.update(walletDoc.reference, "balance", currentBalance - amount)
                    
                    val tx = Transaction(
                        id = newTxRef.id,
                        amount = amount,
                        type = com.karatbank.sdk.core.TransactionType.OUTGOING,
                        description = "SPEI STP: $description",
                        destinationAccount = destinationAccount,
                        timestamp = java.util.Date()
                    )
                    batch.set(newTxRef, tx)
                }.await()
            } else {
               return Result.failure(Exception("Wallet not found"))
            }

            Result.success(newTxRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun listenToTransactions(): Flow<List<Transaction>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }

        val registration = firestore.collection("transactions")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val transactions = snapshot?.documents?.mapNotNull { it.toObject(Transaction::class.java) } ?: emptyList()
                trySend(transactions)
            }

        awaitClose { registration.remove() }
    }

    suspend fun executeTransfer(targetEmail: String, amount: Double): Result<String> {
        return try {
            val data = hashMapOf(
                "targetEmail" to targetEmail,
                "amount" to amount
            )
            val result = functions.getHttpsCallable("processTransfer")
                .call(data)
                .await()
            
            Result.success(result.data.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
