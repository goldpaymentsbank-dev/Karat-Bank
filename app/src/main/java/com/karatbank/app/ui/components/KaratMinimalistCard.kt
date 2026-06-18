package com.karatbank.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karatbank.app.ui.theme.KaratGold
import com.karatbank.app.ui.theme.White60
import java.text.NumberFormat
import java.util.*

@Composable
fun KaratMinimalistCard(
    balance: Double,
    currency: String,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF1C1C1E), Color(0xFF2C2C2E))
    )

    val formattedBalance = NumberFormat.getCurrencyInstance(Locale("es", "MX")).format(balance)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.6f)
            .background(gradient, RoundedCornerShape(24.dp))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        // Brand Name
        Text(
            text = "KARAT BANK",
            color = KaratGold,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.align(Alignment.TopStart)
        )

        // Currency
        Text(
            text = currency,
            color = Color.White.copy(alpha = 0.3f),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            modifier = Modifier.align(Alignment.TopEnd)
        )

        // Balance Section
        Column(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Text(
                text = "BALANCE DISPONIBLE",
                color = Color.White.copy(alpha = 0.4f),
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 2.sp,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formattedBalance,
                color = KaratGold,
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 32.sp),
                fontWeight = FontWeight.Bold
            )
        }

        // Bottom section with mask and logo placeholders
        Row(
            modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "••••  ••••  ••••  8848",
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyMedium,
                letterSpacing = 4.sp
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.size(24.dp).background(Color.White.copy(alpha = 0.1f), CircleShape))
                Box(modifier = Modifier.size(24.dp).background(Color.White.copy(alpha = 0.05f), CircleShape))
            }
        }
    }
}
