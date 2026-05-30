package io.github.naupyon.arisanak.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.naupyon.arisanak.presentation.viewmodel.ArisanViewModel
import io.github.naupyon.arisanak.presentation.viewmodel.PiutangDebtorState
import io.github.naupyon.arisanak.presentation.ui.theme.*
import io.github.naupyon.arisanak.presentation.ui.components.PiutangRepaymentDialog
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PiutangScreen(
    viewModel: ArisanViewModel,
    onBack: () -> Unit
) {
    val totalPiutang by viewModel.totalPiutangBalance.collectAsState()
    val debtors by viewModel.piutangDebtors.collectAsState()
    var selectedDebtor by remember { mutableStateOf<PiutangDebtorState?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Piutang & Talangan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmBackground)
            )
        },
        containerColor = WarmBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = RoseContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(text = "Total Piutang Belum Tertagih", color = OnRoseContainer)
                    Text(
                        text = String.format(LocalLocale.current.platformLocale, "Rp %,.0f", totalPiutang),
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                        color = RoseRed
                    )
                }
            }

            if (debtors.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(64.dp), tint = DividerVariant)
                        Text(text = "Hebat! Tidak ada piutang yang aktif.", color = BalanceSec)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                    items(debtors) { debtor ->
                        PiutangRowItem(debtor = debtor, onPay = { selectedDebtor = debtor })
                    }
                }
            }
        }
        
        if (selectedDebtor != null) {
            PiutangRepaymentDialog(
                memberName = selectedDebtor!!.member.displayName,
                maxRepay = selectedDebtor!!.totalDebt - selectedDebtor!!.totalRepaid,
                onDismiss = { selectedDebtor = null },
                onConfirm = { amount: Double ->
                    viewModel.payPiutang(selectedDebtor!!.member.id, amount)
                    selectedDebtor = null
                }
            )
        }
    }
}

@Composable
fun PiutangRowItem(debtor: PiutangDebtorState, onPay: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = debtor.member.displayName, fontWeight = FontWeight.Bold, color = OnRoseContainer)
                Text(text = "Asal Kelompok: ${debtor.groupName}", fontSize = 12.sp, color = BalanceSec)
                Text(text = "Hutang: Rp ${String.format(LocalLocale.current.platformLocale, "%,.0f", debtor.totalDebt - debtor.totalRepaid)}", color = RoseRed)
            }
            Button(onClick = onPay, colors = ButtonDefaults.buttonColors(containerColor = RoseRed), shape = CircleShape) {
                Text("Bayar")
            }
        }
    }
}
