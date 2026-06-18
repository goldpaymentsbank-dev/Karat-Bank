package com.karatbank.app

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class KaratBankApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            // Manual initialization is required because the google-services.json
            // and the corresponding Gradle plugin are not present in this environment.
            val options = FirebaseOptions.Builder()
                .setApiKey(BuildConfig.GOLDPAYMENTS_API_KEY)
                .setApplicationId(BuildConfig.GOLDPAYMENTS_APP_ID)
                .setProjectId(BuildConfig.GOLDPAYMENTS_PROJECT_ID)
                .build()
            
            FirebaseApp.initializeApp(this, options)
            Log.d("KaratBankApp", "Firebase initialized successfully with manual options.")
        } catch (e: Exception) {
            Log.e("KaratBankApp", "Failed to initialize Firebase manually: ${e.message}")
            // We don't want the app to crash even if Firebase init fails.
        }
    }
}
