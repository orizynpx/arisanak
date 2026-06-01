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
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.naupyon.arisanak.presentation.viewmodel.*
import io.github.naupyon.arisanak.presentation.ui.theme.*
import io.github.naupyon.arisanak.presentation.ui.components.QuickLogPaymentDialog
import androidx.compose.ui.text.style.TextOverflow
import java.util.Locale

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
    val locale = LocalConfiguration.current.locales[0]

    var selectedFilter by remember { mutableStateOf<String?>(null) }
    val filteredHistory = remember(transactionHistory, selectedFilter) {
        when (selectedFilter) {
            null -> transactionHistory
            "Lunas" -> transactionHistory.filter { it.status == PaymentState.PAID || it.status == PaymentState.DITALANGI_PAID }
            "Belum Bayar" -> transactionHistory.filter { it.status == PaymentState.UNPAID }
            "Angsuran" -> transactionHistory.filter { it.status == PaymentState.PARTIAL }
            "Ditalangi" -> transactionHistory.filter { it.status.name.startsWith("DITALANGI") }
            "Hutang Talangan" -> transactionHistory.filter { it.status == PaymentState.DITALANGI_UNPAID || it.status == PaymentState.DITALANGI_PARTIAL }
            else -> transactionHistory
        }
    }

    var showQuickLogSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Arisanak",
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showQuickLogSheet = true },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(bottom = 0.dp, end = 16.dp)
                    .size(64.dp)
                    .testTag("quick_log_fab"),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Pembayaran", modifier = Modifier.size(28.dp))
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .padding(vertical = 0.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPiutang() }
                        .testTag("balance_summary_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.DarkGray
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total yang Anda Talangi",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Detail",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                  text = String.format(locale, "Rp%,.0f", totalPiutang),
                                  style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Klik untuk mengelola cicilan piutang anggota.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Kelompok Arisan Anda",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    
                    if (groups.isEmpty()) {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                disabledContainerColor = Color.LightGray,
                                disabledContentColor = Color.DarkGray
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(imageVector = Icons.Outlined.Groups, contentDescription = null, modifier = Modifier.size(48.dp))
                                    Text("Belum ada kelompok aktif. Tambahkan di tab Groups!", textAlign = TextAlign.Center)
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
                                    modifier = Modifier
                                        .width(300.dp)
                                        .clickable { onNavigateToGroup(groupState.group.id) },
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        disabledContainerColor = Color.LightGray,
                                        disabledContentColor = Color.DarkGray
                                    )
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
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "Putaran #${groupState.group.currentIntervalSequence}",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .padding(start = 8.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.secondary)
                                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = if (groupState.isReadyToKocok) "Siap Kocok" else "Berjalan",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    maxLines = 1,
                                                    color = MaterialTheme.colorScheme.onSecondary
                                                )
                                            }
                                        }
                                        val progress = if (groupState.targetPot > 0) (groupState.collectedAmount / groupState.targetPot).toFloat() else 0f
                                        LinearProgressIndicator(
                                            progress = { progress },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(CircleShape)
                                                .border(
                                                    width = 1.dp,
                                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                                    shape = CircleShape
                                                ),
                                            trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f),
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            IconButton(
                                                onClick = { onNavigateToGroup(groupState.group.id) },
                                                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.primary)
                                            ) {
                                                Icon(imageVector = Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onPrimary)
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
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    val filters = listOf(
                        null to "Semua",
                        "Lunas" to "Lunas",
                        "Belum Bayar" to "Belum Bayar",
                        "Angsuran" to "Angsuran/Hutang",
                        "Ditalangi" to "Semua Talangan",
                        "Hutang Talangan" to "Hutang Talangan"
                    )
                    items(filters) { (id, label) ->
                        FilterChip(
                            selected = selectedFilter == id,
                            onClick = { selectedFilter = id },
                            label = { Text(label) }
                        )
                    }
                }
            }

            if (filteredHistory.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(imageVector = Icons.AutoMirrored.Outlined.ReceiptLong, contentDescription = null, modifier = Modifier.size(48.dp))
                                Text("Tidak ada data untuk filter ini.", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            } else {
                items(filteredHistory) { item ->
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.DarkGray
                        )
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
                                val (icon, bgColor, tint) = when (item.status) {
                                    PaymentState.PAID, PaymentState.DITALANGI_PAID -> Triple(Icons.Default.CheckCircle, Color(0xFFE8F5E9), Color(0xFF2E7D32))
                                    PaymentState.DITALANGI_UNPAID -> Triple(Icons.Default.Warning, Color(0xFFFFEBEE), Color(0xFFC62828))
                                    PaymentState.PARTIAL, PaymentState.DITALANGI_PARTIAL -> Triple(Icons.Default.History, Color(0xFFFFF3E0), Color(0xFFEF6C00))
                                    PaymentState.UNPAID -> Triple(Icons.Default.Pending, Color(0xFFF5F5F5), Color(0xFF757575))
                                }
                                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(bgColor), contentAlignment = Alignment.Center) {
                                    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = tint)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.memberName, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(text = item.groupName, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(text = item.formattedDate, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (item.status == PaymentState.UNPAID) "Rp 0" else String.format(locale, "Rp %,.0f", item.amount),
                                    fontWeight = FontWeight.Bold,
                                    color = when(item.status) {
                                        PaymentState.DITALANGI_UNPAID -> MaterialTheme.colorScheme.error
                                        PaymentState.PAID, PaymentState.DITALANGI_PAID -> MaterialTheme.colorScheme.primary
                                        PaymentState.PARTIAL, PaymentState.DITALANGI_PARTIAL -> MaterialTheme.colorScheme.secondary
                                        else -> MaterialTheme.colorScheme.outline
                                    }
                                )
                                if (item.status.name.startsWith("DITALANGI")) {
                                    val talanganLabel = when(item.status) {
                                        PaymentState.DITALANGI_PAID -> "TALANGAN LUNAS"
                                        PaymentState.DITALANGI_PARTIAL -> "TALANGAN CICIL"
                                        else -> "DITALANGI"
                                    }
                                    Box(modifier = Modifier.clip(CircleShape).background(
                                        if (item.status == PaymentState.DITALANGI_PAID) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                                    ).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                        Text(talanganLabel, fontSize = 8.sp, fontWeight = FontWeight.Bold)
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
