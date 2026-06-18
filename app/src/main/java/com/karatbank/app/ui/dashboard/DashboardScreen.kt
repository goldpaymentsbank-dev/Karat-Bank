package com.karatbank.app.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karatbank.app.ui.components.KaratMinimalistCard
import com.karatbank.app.ui.components.WelcomeBonusBanner
import com.karatbank.app.ui.theme.DarkGrey
import com.karatbank.app.ui.theme.DeepBlack
import com.karatbank.app.ui.theme.KaratGold
import com.karatbank.app.ui.theme.White60
import com.karatbank.sdk.core.TransactionType
import com.karatbank.sdk.core.VirtualCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel()
) {
    val wallet by viewModel.walletState.collectAsState()
    val transactions by viewModel.transactionsState.collectAsState()
    val virtualCards by viewModel.virtualCardsState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = DeepBlack,
        topBar = {
            KaratTopAppBar()
        },
        bottomBar = {
            KaratBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> {
                    // Inicio
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            KaratMinimalistCard(
                                balance = wallet?.balance ?: 0.0,
                                currency = wallet?.currency ?: "MXN"
                            )
                        }

                        item {
                            WelcomeBonusBanner()
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "MOVIMIENTOS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp
                                )
                                Text(
                                    text = "Ver todo",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = KaratGold,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.clickable { }
                                )
                            }
                        }

                        if (transactions.isEmpty()) {
                            item {
                                Text(
                                    text = "No hay transacciones aún.",
                                    color = White60,
                                    modifier = Modifier.padding(vertical = 32.dp)
                                )
                            }
                        } else {
                            items(transactions) { transaction ->
                                TransactionItem(transaction = transaction)
                            }
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
                1 -> {
                    // Tarjetas
                    CardsTab(
                        virtualCards = virtualCards,
                        onCreateCard = { name -> viewModel.createVirtualCard(name) }
                    )
                }
                2 -> {
                    // Pagos / Transferencias
                    TransfersTab(
                        walletBalance = wallet?.balance ?: 0.0,
                        onTransfer = { clabe, amount, desc -> 
                            viewModel.executeSPEITransfer(clabe, amount, desc) 
                            selectedTab = 0 
                        }
                    )
                }
                3 -> {
                    // Perfil
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Perfil del Usuario", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CardsTab(virtualCards: List<VirtualCard>, onCreateCard: (String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var newCardName by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nueva Tarjeta Virtual", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = newCardName,
                    onValueChange = { newCardName = it },
                    label = { Text("Nombre de la tarjeta (ej. Streaming)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = KaratGold,
                        unfocusedBorderColor = White60,
                        focusedLabelColor = KaratGold,
                        unfocusedLabelColor = White60
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCardName.isNotBlank()) {
                            onCreateCard(newCardName)
                            showDialog = false
                            newCardName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KaratGold, contentColor = DeepBlack)
                ) {
                    Text("Crear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar", color = White60)
                }
            },
            containerColor = DarkGrey,
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = KaratGold, contentColor = DeepBlack),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Generar Nueva Tarjeta Virtual", modifier = Modifier.padding(8.dp), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (virtualCards.isEmpty()) {
            item {
                Text("No tienes tarjetas virtuales.", color = White60)
            }
        } else {
            items(virtualCards) { card ->
                VirtualCardItem(card)
            }
        }
    }
}

@Composable
fun VirtualCardItem(card: VirtualCard) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkGrey, RoundedCornerShape(20.dp))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(card.cardName, color = KaratGold, fontWeight = FontWeight.Bold)
            Text("VISA", color = Color.White.copy(alpha = 0.8f), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = card.cardNumber.chunked(4).joinToString(" "),
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Vence: ${card.expirationDate}", color = White60)
            Text("CVV: ${card.cvv}", color = White60)
        }
    }
}

@Composable
fun TransfersTab(walletBalance: Double, onTransfer: (String, Double, String) -> Unit) {
    var clabe by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var concept by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Transferencia SPEI / STP", color = Color.White, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Balance Disponible: $${String.format("%,.2f", walletBalance)}", color = KaratGold, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = clabe,
            onValueChange = { clabe = it },
            label = { Text("CLABE o Cuenta Destino") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = KaratGold,
                unfocusedBorderColor = White60,
                focusedLabelColor = KaratGold,
                unfocusedLabelColor = White60
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Monto a enviar") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = KaratGold,
                unfocusedBorderColor = White60,
                focusedLabelColor = KaratGold,
                unfocusedLabelColor = White60
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = concept,
            onValueChange = { concept = it },
            label = { Text("Concepto") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = KaratGold,
                unfocusedBorderColor = White60,
                focusedLabelColor = KaratGold,
                unfocusedLabelColor = White60
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val amountDouble = amount.toDoubleOrNull()
                if (amountDouble != null && amountDouble > 0 && clabe.isNotBlank()) {
                    onTransfer(clabe, amountDouble, concept)
                    clabe = ""
                    amount = ""
                    concept = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = KaratGold, contentColor = DeepBlack),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Transferir Ahora", modifier = Modifier.padding(8.dp), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun KaratTopAppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "KARAT BANK",
                color = KaratGold,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "PREMIUM FINTECH",
                color = Color.White.copy(alpha = 0.4f),
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 2.sp
            )
        }
        
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(DarkGrey, CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(2.dp, KaratGold, CircleShape)
            )
        }
    }
}

@Composable
fun KaratBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        color = DarkGrey,
        modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Inicio",
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            )
            BottomNavItem(
                icon = Icons.Default.Style,
                label = "Tarjetas",
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            )
            BottomNavItem(
                icon = Icons.Default.Payment,
                label = "Pagos",
                isSelected = selectedTab == 2,
                onClick = { onTabSelected(2) }
            )
            BottomNavItem(
                icon = Icons.Default.Person,
                label = "Perfil",
                isSelected = selectedTab == 3,
                onClick = { onTabSelected(3) }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) KaratGold else Color.White.copy(alpha = 0.4f),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            color = if (isSelected) KaratGold else Color.White.copy(alpha = 0.4f),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun TransactionItem(transaction: com.karatbank.sdk.core.Transaction) {
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val isIncoming = transaction.type == TransactionType.INCOMING
    val amountPrefix = if (isIncoming) "+" else "-"
    val amountColor = if (isIncoming) KaratGold else Color.White.copy(alpha = 0.8f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkGrey, RoundedCornerShape(20.dp))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    if (isIncoming) KaratGold.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.05f),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isIncoming) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .border(2.dp, KaratGold, RoundedCornerShape(2.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.description,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${dateFormat.format(transaction.timestamp)} • ${if (isIncoming) "Depósito" else "Transferencia"}",
                color = White60,
                style = MaterialTheme.typography.labelSmall
            )
        }
        
        Text(
            text = "$amountPrefix${String.format("%,.2f", transaction.amount)}",
            color = amountColor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
