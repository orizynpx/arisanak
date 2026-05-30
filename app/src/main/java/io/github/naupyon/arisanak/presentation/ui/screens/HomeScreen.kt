package io.github.naupyon.arisanak.presentation.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.naupyon.arisanak.presentation.viewmodel.ArisanViewModel
import io.github.naupyon.arisanak.presentation.ui.theme.*
import io.github.naupyon.arisanak.presentation.ui.components.QuickLogPaymentDialog
import androidx.compose.ui.text.style.TextOverflow
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ArisanViewModel,
    onNavigateToGroup: (Long) -> Unit,
    onNavigateToPiutang: () -> Unit
) {
    val groups by viewModel.groupsUiState.collectAsState()
    val totalPiutang by viewModel.totalPiutangBalance.collectAsState()
    val transactionHistory by viewModel.transactionHistory.collectAsState()

    var showQuickLogSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Arisanak",
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                            color = RoseRed
                        )
                        Text(
                            text = "Aplikasi Bendahara Arisan",
                            style = MaterialTheme.typography.titleSmall,
                            color = BalanceSec
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(CircleShape)
                            .background(RoseContainer)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = "PRO ADMIN", color = RoseRed, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = WarmBackground)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showQuickLogSheet = true },
                containerColor = RoseRed,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(bottom = 0.dp, end = 16.dp)
                    .size(64.dp)
                    .testTag("quick_log_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Pembayaran", modifier = Modifier.size(28.dp))
            }
        },
        containerColor = WarmBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = RoseContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPiutang() }
                        .testTag("balance_summary_card")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(RoseContainer, RoseContainer.copy(alpha = 0.8f))
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Piutang Anda (Talangan)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = OnRoseContainer
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Detail",
                                    tint = RoseRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                  text = String.format(LocalLocale.current.platformLocale, "Rp %,.0f", totalPiutang),
                                  style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                                  color = RoseRed
                            )
                            Text(
                                text = "Klik untuk mengelola cicilan piutang anggota.",
                                style = MaterialTheme.typography.bodySmall,
                                color = BalanceSec
                            )
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Kelompok Arisan Anda",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = OnRoseContainer
                    )
                    
                    if (groups.isEmpty()) {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = WarmSurface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(imageVector = Icons.Outlined.Groups, contentDescription = null, modifier = Modifier.size(48.dp), tint = BalanceSec)
                                    Text("Belum ada kelompok aktif. Tambahkan di tab Groups!", color = BalanceSec, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(groups) { groupState ->
                                Card(
                                    shape = RoundedCornerShape(28.dp),
                                    colors = CardDefaults.cardColors(containerColor = WarmSurface),
                                    modifier = Modifier
                                        .width(300.dp)
                                        .clickable { onNavigateToGroup(groupState.group.id) }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = groupState.group.name,
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = OnRoseContainer,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "Putaran #${groupState.group.currentIntervalSequence}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = BalanceSec
                                                )
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .padding(start = 8.dp)
                                                    .clip(CircleShape)
                                                    .background(if (groupState.isReadyToKocok) RoseContainer else StatusSecContainer)
                                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = if (groupState.isReadyToKocok) "Siap Kocok" else "Berjalan",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = if (groupState.isReadyToKocok) RoseRed else BalanceSec,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                        val progress = if (groupState.targetPot > 0) (groupState.collectedAmount / groupState.targetPot).toFloat() else 0f
                                        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape), color = RoseRed, trackColor = DividerVariant)

                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            IconButton(
                                                onClick = { onNavigateToGroup(groupState.group.id) },
                                                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(16.dp)).background(RoseContainer)
                                            ) {
                                                Icon(imageVector = Icons.Default.Casino, contentDescription = null, tint = RoseRed, modifier = Modifier.size(24.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Riwayat Pembayaran Global",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = OnRoseContainer
                )
            }

            if (transactionHistory.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmSurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(imageVector = Icons.Outlined.ReceiptLong, contentDescription = null, modifier = Modifier.size(48.dp), tint = DividerVariant)
                                Text("Belum ada transaksi pembayaran.", color = BalanceSec, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            } else {
                items(transactionHistory) { item ->
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmSurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f).padding(end = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (item.isDitalangi) AlertError.copy(alpha = 0.1f) else RoseContainer), contentAlignment = Alignment.Center) {
                                    Icon(imageVector = if (item.isDitalangi) Icons.Default.Warning else Icons.Default.Check, contentDescription = null, tint = if (item.isDitalangi) AlertError else RoseRed, modifier = Modifier.size(20.dp))
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.memberName, fontWeight = FontWeight.Bold, color = OnRoseContainer, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(text = item.groupName, style = MaterialTheme.typography.bodySmall, color = BalanceSec, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(text = item.formattedDate, style = MaterialTheme.typography.bodySmall, color = BalanceSec.copy(alpha = 0.8f))
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = String.format(LocalLocale.current.platformLocale, "Rp %,.0f", item.amount), fontWeight = FontWeight.Bold, color = if (item.isDitalangi) AlertError else RoseRed)
                                if (item.isDitalangi) {
                                    Box(modifier = Modifier.clip(CircleShape).background(AlertError.copy(alpha = 0.1f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                        Text("DITALANGI", color = AlertError, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (showQuickLogSheet) {
            QuickLogPaymentDialog(
                groups = groups,
                onDismiss = { showQuickLogSheet = false },
                onLog = { groupId, memberId, amount, isDitalangi ->
                    val group = groups.find { it.group.id == groupId }
                    val intervalId = group?.activeInterval?.id ?: 0
                    if (isDitalangi) {
                        viewModel.talangiMember(memberId, intervalId, amount)
                    } else {
                        viewModel.payCustomAmount(memberId, intervalId, amount)
                    }
                    showQuickLogSheet = false
                }
            )
        }
    }
}
