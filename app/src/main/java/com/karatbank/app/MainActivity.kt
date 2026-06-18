package com.karatbank.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.karatbank.app.ui.dashboard.DashboardScreen
import com.karatbank.app.ui.theme.KaratBankTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KaratBankTheme {
                DashboardScreen()
            }
        }
    }
}
