package io.github.naupyon.arisanak.presentation.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.naupyon.arisanak.domain.model.Interval
import io.github.naupyon.arisanak.presentation.ui.utils.PdfExportUtils
import io.github.naupyon.arisanak.presentation.viewmodel.ArisanViewModel
import io.github.naupyon.arisanak.presentation.viewmodel.PaymentState
import io.github.naupyon.arisanak.presentation.viewmodel.TransactionHistoryItem
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatArisanScreen(
    groupId: Long,
    viewModel: ArisanViewModel,
    onBack: () -> Unit
) {
    val groups by viewModel.groupsUiState.collectAsState()
    val groupState = groups.find { it.group.id == groupId }
    val intervals by viewModel.getIntervalsForGroup(groupId).collectAsState(initial = emptyList())
    val transactionHistory by viewModel.transactionHistory.collectAsState()
    val context = LocalContext.current

    val createPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf"),
        onResult = { uri ->
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { os ->
                    if (groupState != null) {
                        PdfExportUtils.generateGroupRecapPdf(
                            outputStream = os,
                            groupState = groupState,
                            intervals = intervals
                        )
                        Toast.makeText(context, "PDF berhasil disimpan", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    )
    
    // Filter intervals to only those that are relevant (active or completed)
    // Actually, based on requirement: "Tabs only show up once they are relevant"
    val relevantIntervals = intervals.sortedBy { it.sequenceNumber }
    
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Update tab index when group state changes (e.g. to current cycle)
    LaunchedEffect(groupState?.group?.currentIntervalSequence, relevantIntervals) {
        groupState?.group?.currentIntervalSequence?.let { currentSeq ->
            val idx = relevantIntervals.indexOfFirst { it.sequenceNumber == currentSeq }
            if (idx >= 0) selectedTabIndex = idx
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Arisan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val fileName = "Rekap_${groupState?.group?.name}_${SimpleDateFormat("yyyyMMdd").format(Date())}.pdf"
                        createPdfLauncher.launch(fileName)
                    }) {
                        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = "Export PDF")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (groupState == null || relevantIntervals.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada data riwayat.")
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    divider = {}
                ) {
                    relevantIntervals.forEachIndexed { index, interval ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { 
                                Text(
                                    text = "Putaran ${interval.sequenceNumber}/${groupState.totalCycles}",
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                val currentInterval = relevantIntervals.getOrNull(selectedTabIndex)
                if (currentInterval != null) {
                    val intervalHistory = transactionHistory.filter { it.intervalId == currentInterval.id }
                        .sortedByDescending { it.timestamp }
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                        
                        // Winner info if completed
                        if (currentInterval.isCompleted && currentInterval.winnerMemberId != null) {
                            item {
                                val winner = groupState.members.find { it.member.id == currentInterval.winnerMemberId }
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.EmojiEvents, null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                                        Spacer(Modifier.width(12.dp))
                                        Column {
                                            Text("Pemenang Putaran", style = MaterialTheme.typography.labelSmall)
                                            Text(winner?.member?.displayName ?: "Unknown", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Detail Pembayaran", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        // Filtered logs for this interval
                        items(intervalHistory) { item ->
                            HistoryLogItem(item)
                        }
                        
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryLogItem(item: TransactionHistoryItem) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (item.status) {
                            PaymentState.PAID, PaymentState.DITALANGI_PAID -> Icons.Default.Check
                            PaymentState.UNPAID -> Icons.Default.Pending
                            else -> Icons.Default.Warning
                        }, 
                        contentDescription = null, 
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = item.memberName, 
                        fontWeight = FontWeight.Bold, 
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(text = item.formattedDate, style = MaterialTheme.typography.labelSmall)
                }
            }
            Text(
                text = if (item.status == PaymentState.UNPAID) "Rp 0" else "Rp ${String.format("%,.0f", item.amount)}",
                fontWeight = FontWeight.Bold,
                color = when (item.status) {
                    PaymentState.PAID, PaymentState.DITALANGI_PAID -> MaterialTheme.colorScheme.primary
                    PaymentState.PARTIAL, PaymentState.DITALANGI_PARTIAL -> MaterialTheme.colorScheme.secondary
                    PaymentState.UNPAID, PaymentState.DITALANGI_UNPAID -> MaterialTheme.colorScheme.error
                },
                textAlign = TextAlign.End
            )
        }
    }
}
