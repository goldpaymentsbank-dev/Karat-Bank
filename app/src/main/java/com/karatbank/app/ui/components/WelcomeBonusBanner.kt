package com.karatbank.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.karatbank.app.ui.theme.White80

@Composable
fun WelcomeBonusBanner(
    modifier: Modifier = Modifier
) {
    val gradient = Brush.linearGradient(
        colors = listOf(KaratGold.copy(alpha = 0.2f), Color.Transparent)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(gradient, RoundedCornerShape(16.dp))
            .border(1.dp, KaratGold.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Subtle glow effect
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(KaratGold.copy(alpha = 0.1f), RoundedCornerShape(40.dp))
                .align(Alignment.TopEnd)
                .offset(x = 24.dp, y = (-24).dp)
        )

        Column {
            Text(
                text = "INCENTIVO DE FUNDACIÓN",
                color = KaratGold,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "¡Bono de Bienvenida Activado!",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    text = "Hemos depositado ",
                    color = White60,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "$1,000.00 MXN",
                    color = KaratGold,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "en tu cuenta por ser de los primeros usuarios.",
                color = White60,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
