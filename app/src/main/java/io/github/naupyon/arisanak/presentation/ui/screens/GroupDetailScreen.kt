package io.github.naupyon.arisanak.presentation.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import io.github.naupyon.arisanak.presentation.ui.utils.PdfExportUtils
import io.github.naupyon.arisanak.domain.model.Interval
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.github.naupyon.arisanak.domain.model.Member
import io.github.naupyon.arisanak.presentation.ui.components.*
import io.github.naupyon.arisanak.presentation.ui.theme.*
import io.github.naupyon.arisanak.presentation.viewmodel.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupId: Long,
    viewModel: ArisanViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]
    val groups by viewModel.groupsUiState.collectAsState()
    val groupState = groups.find { it.group.id == groupId }
    val intervals by viewModel.getIntervalsForGroup(groupId).collectAsState(initial = emptyList())

    var selectedMemberForAction by remember { mutableStateOf<MemberPaymentState?>(null) }
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var activeTabIdx by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Anggota", "Riwayat", "Kocok")

    var memberSearchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Semua") }

    val scope = rememberCoroutineScope()
    var isDrawing by remember { mutableStateOf(false) }
    var winningMemberState by remember { mutableStateOf<Member?>(null) }
    var showCelebrationDialog by remember { mutableStateOf(false) }

    val rotationAnim = remember { Animatable(0f) }
    val translationX = remember { Animatable(0f) }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = groupState?.group?.name ?: "Detail Arisan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (activeTabIdx == 0) {
                        IconButton(onClick = { showAddMemberDialog = true }) {
                            Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Tambah Anggota")
                        }
                    }
                    if (activeTabIdx == 1) {
                        IconButton(onClick = {
                            val fileName = "Rekap_${groupState?.group?.name}_${SimpleDateFormat("yyyyMMdd", locale).format(Date())}.pdf"
                            createPdfLauncher.launch(fileName)
                        }) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = "Export PDF")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (groupState == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Kelompok tidak ditemukan.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SecondaryTabRow(
                    selectedTabIndex = activeTabIdx
                ) {
                    tabTitles.forEachIndexed { idx, title ->
                        Tab(
                            selected = activeTabIdx == idx,
                            onClick = { activeTabIdx = idx },
                            text = { Text(text = title, fontWeight = FontWeight.Bold) }
                        )
                    }
                }

                when (activeTabIdx) {
                    0 -> {
                        Card(
                            shape = RoundedCornerShape(28.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Iuran Dasar", style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        text = String.format(locale, "Rp %,.0f", groupState.group.baseDueAmount),
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                                    Text(text = "Total Pot", style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        text = String.format(locale, "Rp %,.0f", groupState.targetPot),
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        val latestWinnerInterval = intervals.filter { it.winnerMemberId != null }.maxByOrNull { it.sequenceNumber }
                        val winnerState = latestWinnerInterval?.winnerMemberId?.let { winnerId ->
                            groupState.members.find { it.member.id == winnerId }
                        }
                        if (winnerState != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Casino, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.size(20.dp))
                                Text(
                                    text = "Pemenang: ${winnerState.member.displayName}",
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = {
                                    val settings = viewModel.settings.value
                                    val template = settings?.winTemplate ?: "Selamat [NamaAnggota]! Anda menang Arisan [NamaGrup] sebesar Rp [TotalPot]"
                                    val msg = template
                                        .replace("[NamaAnggota]", winnerState.member.displayName)
                                        .replace("[NamaGrup]", groupState.group.name)
                                        .replace("[TotalPot]", String.format(locale, "%,.0f", groupState.targetPot))
                                    
                                    launchWhatsApp(context, winnerState.member.phoneNumber, msg)
                                }) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat Winner", tint = MaterialTheme.colorScheme.onTertiaryContainer)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = memberSearchQuery,
                            onValueChange = { memberSearchQuery = it },
                            placeholder = { Text("Cari anggota...") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Semua", "Belum Bayar", "Lunas", "Ditalangi").forEach { filter ->
                                FilterChip(
                                    selected = selectedFilter == filter,
                                    onClick = { selectedFilter = filter },
                                    label = { Text(filter, fontSize = 11.sp) },
                                    shape = CircleShape
                                )
                            }
                        }

                        val filteredMembers = groupState.members.filter {
                            val matchesSearch = it.member.displayName.contains(memberSearchQuery, ignoreCase = true)
                            val matchesFilter = when(selectedFilter) {
                                "Belum Bayar" -> it.state == PaymentState.UNPAID || it.state == PaymentState.PARTIAL
                                "Lunas" -> it.state == PaymentState.PAID
                                "Ditalangi" -> it.state.name.startsWith("DITALANGI")
                                else -> true
                            }
                            matchesSearch && matchesFilter
                        }

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                            items(filteredMembers) { item ->
                                RosterRowItem(
                                    item = item,
                                    onClick = { selectedMemberForAction = item },
                                    onSendReminder = {
                                        val settings = viewModel.settings.value
                                        val template = settings?.reminderTemplate ?: "Halo [NamaAnggota], tagihan Anda Rp [SisaTagihan]"
                                        val msg = template
                                            .replace("[NamaAnggota]", item.member.displayName)
                                            .replace("[NamaGrup]", groupState.group.name)
                                            .replace("[SisaTagihan]", String.format(Locale.getDefault(), "%,.0f", item.sisa))
                                        
                                        launchWhatsApp(context, item.member.phoneNumber, msg)
                                    }
                                )
                            }
                        }
                    }
                    1 -> {
                        val completedIntervals = intervals.filter { it.isCompleted || it.winnerMemberId != null }
                        val sortedIntervals = completedIntervals.sortedByDescending { it.sequenceNumber }
                        
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                            items(sortedIntervals) { interval ->
                                val winner = groupState.members.find { it.member.id == interval.winnerMemberId }
                                Card(
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = "Putaran #${interval.sequenceNumber}", fontWeight = FontWeight.Bold)
                                            Text(text = "Pemenang: ${winner?.member?.displayName ?: "Unknown"}")
                                        }
                                        if (winner != null) {
                                            IconButton(onClick = {
                                                val settings = viewModel.settings.value
                                                val template = settings?.winTemplate ?: "Selamat [NamaAnggota]! Anda menang Arisan [NamaGrup] sebesar Rp [TotalPot]"
                                                val msg = template
                                                    .replace("[NamaAnggota]", winner.member.displayName)
                                                    .replace("[NamaGrup]", groupState.group.name)
                                                    .replace("[TotalPot]", String.format(locale, "%,.0f", groupState.targetPot))
                                                
                                                launchWhatsApp(context, winner.member.phoneNumber, msg)
                                            }) {
                                                Icon(Icons.AutoMirrored.Filled.Chat, "Chat Winner")
                                            }
                                        }
                                        Text(text = formatEpochToDate(interval.startDate), style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        val bottleColor = MaterialTheme.colorScheme.primary
                        val slipColor1 = MaterialTheme.colorScheme.tertiary
                        val slipColor2 = MaterialTheme.colorScheme.onTertiary
                        Column(
                            modifier = Modifier.fillMaxSize().weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(240.dp)
                                    .graphicsLayer(rotationZ = rotationAnim.value, translationX = translationX.value),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val w = size.width
                                    val h = size.height
                                    val path = Path().apply {
                                        moveTo(w * 0.35f, h * 0.15f); lineTo(w * 0.65f, h * 0.15f); lineTo(w * 0.65f, h * 0.25f)
                                        quadraticTo(w * 0.85f, h * 0.35f, w * 0.85f, h * 0.55f); lineTo(w * 0.85f, h * 0.85f)
                                        quadraticTo(w * 0.85f, h * 0.92f, w * 0.75f, h * 0.92f); lineTo(w * 0.25f, h * 0.92f)
                                        quadraticTo(w * 0.15f, h * 0.92f, w * 0.15f, h * 0.85f); lineTo(w * 0.15f, h * 0.55f)
                                        quadraticTo(w * 0.15f, h * 0.35f, w * 0.35f, h * 0.25f); close()
                                    }
                                    drawPath(path = path, color = bottleColor, style = Stroke(width = 4.dp.toPx()))

                                    val random = Random(42)
                                    for (i in 0 until 12) {
                                        val offset = Offset(x = w * 0.25f + random.nextFloat() * (w * 0.40f), y = h * 0.45f + random.nextFloat() * (h * 0.35f))
                                        drawRoundRect(color = if (i % 2 == 0) slipColor1 else slipColor2, topLeft = offset, size = Size(w * 0.1f, h * 0.04f), cornerRadius = CornerRadius(2.dp.toPx()), style = Stroke(width = 1.dp.toPx()))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = {
                                    isDrawing = true
                                    scope.launch {
                                        repeat(20) {
                                            translationX.animateTo(25f, tween(50))
                                            translationX.animateTo(-25f, tween(50))
                                        }
                                        translationX.animateTo(0f)
                                        viewModel.winnerDraw(groupId) { winner ->
                                            winningMemberState = winner
                                            showCelebrationDialog = true
                                            isDrawing = false
                                        }
                                    }
                                },
                                enabled = !isDrawing && groupState.isReadyToKocok,
                                shape = CircleShape,
                                modifier = Modifier.fillMaxWidth().height(56.dp)
                            ) {
                                Text(if (isDrawing) "Mengocok..." else "Kocok Arisan")
                            }
                        }
                    }
                }
            }
        }
        
        if (showAddMemberDialog && groupState != null) {
            AddMemberMidCycleDialog(
                onDismiss = { showAddMemberDialog = false },
                onAdd = { name, phone ->
                    viewModel.addMemberMidCycle(groupId, name, phone)
                    showAddMemberDialog = false
                }
            )
        }

        if (selectedMemberForAction != null && groupState != null) {
            MemberActionDialog(
                item = selectedMemberForAction!!,
                onDismiss = { selectedMemberForAction = null },
                onFullPay = {
                    viewModel.payFullAmount(selectedMemberForAction!!.member.id, groupState.activeInterval?.id ?: 0, selectedMemberForAction!!.requiredDue)
                    selectedMemberForAction = null
                },
                onInstallment = { amount ->
                    viewModel.payCustomAmount(selectedMemberForAction!!.member.id, groupState.activeInterval?.id ?: 0, amount)
                    selectedMemberForAction = null
                },
                onTalangi = {
                    viewModel.talangiMember(selectedMemberForAction!!.member.id, groupState.activeInterval?.id ?: 0, selectedMemberForAction!!.requiredDue)
                    selectedMemberForAction = null
                },
                onPrune = {
                    viewModel.removeMember(selectedMemberForAction!!.member)
                    selectedMemberForAction = null
                }
            )
        }
    }

    if (showCelebrationDialog && winningMemberState != null) {
        Dialog(onDismissRequest = { showCelebrationDialog = false }) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                ConfettiCanvas(modifier = Modifier.fillMaxSize())
                Card(
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(text = "Selamat! 🎉", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                        Text(text = winningMemberState!!.displayName, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold), textAlign = TextAlign.Center)
                        Text(text = "Telah memenangkan Arisan kelompok ${groupState?.group?.name}", textAlign = TextAlign.Center)
                        Button(onClick = { 
                            showCelebrationDialog = false
                            viewModel.advanceInterval(groupId)
                        }, shape = CircleShape) {
                            Text("Mantap")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RosterRowItem(item: MemberPaymentState, onClick: () -> Unit, onSendReminder: () -> Unit) {
    val locale = LocalConfiguration.current.locales[0]
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val (icon, bgColor, tint) = when (item.state) {
                    PaymentState.PAID -> Triple(Icons.Default.CheckCircle, Color(0xFFE8F5E9), Color(0xFF2E7D32))
                    PaymentState.PARTIAL -> Triple(Icons.Default.History, Color(0xFFFFF3E0), Color(0xFFEF6C00))
                    PaymentState.DITALANGI_PAID -> Triple(Icons.Default.CheckCircle, Color(0xFFE8F5E9), Color(0xFF2E7D32))
                    PaymentState.DITALANGI_PARTIAL, PaymentState.DITALANGI_UNPAID -> Triple(Icons.Default.Warning, Color(0xFFFFEBEE), Color(0xFFC62828))
                    PaymentState.UNPAID -> Triple(Icons.Default.Pending, Color(0xFFF5F5F5), Color(0xFF757575))
                }
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(bgColor), contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = tint)
                }
                Column {
                    Text(text = item.member.displayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                    val statusText = when {
                        item.state == PaymentState.PAID -> "Lunas"
                        item.state == PaymentState.PARTIAL -> "Sisa Rp ${String.format(locale, "%,.0f", item.sisa)}"
                        item.state == PaymentState.DITALANGI_PAID -> "Lunas (Ditalangi)"
                        item.state.name.startsWith("DITALANGI") -> "Ditalangi (Rp ${String.format(locale, "%,.0f", item.sisa)})"
                        else -> "Belum Bayar"
                    }
                    Text(
                        text = statusText,
                        color = tint,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            if (item.state != PaymentState.PAID && item.state != PaymentState.DITALANGI_PAID) {
                IconButton(onClick = onSendReminder, modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer, CircleShape).size(36.dp)) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
