# Karat Bank - Backend & Security Configuration

To ensure the security and functionality of the Welcome Bonus and transactional logic, deploy the following configurations to Firebase.

## 1. Firestore Security Rules
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /wallets/{walletId} {
      // Users can only read their own wallet. Managed exclusively by Cloud Functions.
      allow read: if request.auth != null && request.auth.uid == resource.data.userId;
      allow write: if false; 
    }
    match /transactions/{transactionId} {
      allow read: if request.auth != null && request.auth.uid == resource.data.userId;
      allow write: if false;
    }
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    match /system/metrics {
      allow read: if request.auth != null;
      allow write: if false; // Counter only modified via Cloud Functions
    }
  }
}
```

## 2. Cloud Function: Welcome Bonus (secured)
This trigger runs when a user is created in Firebase Auth.

```typescript
import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

admin.initializeApp();
const db = admin.firestore();

export const onUserCreated = functions.auth.user().onCreate(async (user) => {
    const metricsRef = db.doc('system/metrics');
    const bonusAmount = 1000.00;
    const maxBonusUsers = 1000000;

    await db.runTransaction(async (transaction) => {
        const metrics = await transaction.get(metricsRef);
        let userCount = 0;
        if (metrics.exists) {
            userCount = metrics.data()?.userCount || 0;
        }

        let initialBalance = 0.0;
        if (userCount < maxBonusUsers) {
            initialBalance = bonusAmount;
            transaction.set(metricsRef, { userCount: userCount + 1 }, { merge: true });
        }

        // Create Wallet
        const walletRef = db.collection('wallets').doc();
        transaction.set(walletRef, {
            userId: user.uid,
            balance: initialBalance,
            currency: 'MXN',
            status: 'ACTIVE',
            createdAt: admin.firestore.FieldValue.serverTimestamp()
        });

        // Generate Transaction if bonus applied
        if (initialBalance > 0) {
            const txRef = db.collection('transactions').doc();
            transaction.set(txRef, {
                userId: user.uid,
                amount: initialBalance,
                type: 'INCOMING',
                description: 'Bono de Apertura Karat',
                timestamp: admin.firestore.FieldValue.serverTimestamp()
            });
        }
    });

    // Create User Profile
    await db.collection('users').doc(user.uid).set({
        uid: user.uid,
        email: user.email,
        name: user.displayName || 'Karat User',
        createdAt: admin.firestore.FieldValue.serverTimestamp()
    });
});
```

## 3. Cloud Function: Process Transfer
Secure transactional logic to prevent balance manipulation.

```typescript
export const processTransfer = functions.https.onCall(async (data, context) => {
    if (!context.auth) throw new functions.https.HttpsError('unauthenticated', 'Login required');
    
    const { targetEmail, amount } = data;
    const uid = context.auth.uid;

    if (amount <= 0) throw new functions.https.HttpsError('invalid-argument', 'Amount must be > 0');

    return await db.runTransaction(async (transaction) => {
        // 1. Get sender wallet
        const senderWalletQuery = await db.collection('wallets').where('userId', '==', uid).limit(1).get();
        if (senderWalletQuery.empty) throw new functions.https.HttpsError('not-found', 'Sender wallet not found');
        const senderWallet = senderWalletQuery.docs[0];
        const senderData = senderWallet.data();

        if (senderData.balance < amount) throw new functions.https.HttpsError('failed-precondition', 'Insufficient funds');

        // 2. Get target user/wallet
        const targetUserQuery = await db.collection('users').where('email', '==', targetEmail).limit(1).get();
        if (targetUserQuery.empty) throw new functions.https.HttpsError('not-found', 'Target user not found');
        const targetUid = targetUserQuery.docs[0].id;

        const targetWalletQuery = await db.collection('wallets').where('userId', '==', targetUid).limit(1).get();
        if (targetWalletQuery.empty) throw new functions.https.HttpsError('not-found', 'Target wallet not found');
        const targetWallet = targetWalletQuery.docs[0];

        // 3. Update balances
        transaction.update(senderWallet.ref, { balance: senderData.balance - amount });
        transaction.update(targetWallet.ref, { balance: targetWallet.data().balance + amount });

        // 4. Record transactions
        const sTx = db.collection('transactions').doc();
        transaction.set(sTx, { userId: uid, amount, type: 'OUTGOING', description: `Transferencia a ${targetEmail}`, timestamp: admin.firestore.FieldValue.serverTimestamp() });
        
        const rTx = db.collection('transactions').doc();
        transaction.set(rTx, { userId: targetUid, amount, type: 'INCOMING', description: `Transferencia de ${context.auth.token.email}`, timestamp: admin.firestore.FieldValue.serverTimestamp() });

        return "Transferencia exitosa";
    });
});
```
