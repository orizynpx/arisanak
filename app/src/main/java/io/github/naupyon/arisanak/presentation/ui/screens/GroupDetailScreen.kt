package io.github.naupyon.arisanak.presentation.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmBackground)
            )
        },
        containerColor = WarmBackground
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
                    selectedTabIndex = activeTabIdx,
                    containerColor = Color.Transparent,
                    contentColor = RoseRed,
                    indicator = {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(activeTabIdx),
                            color = RoseRed
                        )
                    }
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
                            colors = CardDefaults.cardColors(containerColor = RoseContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Iuran Dasar", style = MaterialTheme.typography.bodySmall, color = OnRoseContainer)
                                    Text(
                                        text = String.format(locale, "Rp %,.0f", groupState.group.baseDueAmount),
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = RoseRed
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                                    Text(text = "Total Pot", style = MaterialTheme.typography.bodySmall, color = OnRoseContainer)
                                    Text(
                                        text = String.format(locale, "Rp %,.0f", groupState.targetPot),
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = RoseRed
                                    )
                                }
                            }
                        }

                        val activeWinner = groupState.members.find { it.member.id == groupState.activeInterval?.winnerMemberId }
                        if (activeWinner != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(GoldTertiary.copy(alpha = 0.1f))
                                    .padding(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Casino, contentDescription = null, tint = GoldTertiary, modifier = Modifier.size(20.dp))
                                Text(
                                    text = "Pemenang: ${activeWinner.member.displayName}",
                                    color = OnRoseContainer,
                                    fontWeight = FontWeight.Bold
                                )
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
                                "Ditalangi" -> it.state == PaymentState.DITALANGI
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
                                    colors = CardDefaults.cardColors(containerColor = WarmSurface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text(text = "Putaran #${interval.sequenceNumber}", fontWeight = FontWeight.Bold)
                                            Text(text = "Pemenang: ${winner?.member?.displayName ?: "Unknown"}")
                                        }
                                        Text(text = formatEpochToDate(interval.startDate), color = BalanceSec, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
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
                                    drawPath(path = path, color = RoseRed, style = Stroke(width = 4.dp.toPx()))

                                    val random = Random(42)
                                    for (i in 0 until 12) {
                                        val offset = Offset(x = w * 0.25f + random.nextFloat() * (w * 0.40f), y = h * 0.45f + random.nextFloat() * (h * 0.35f))
                                        drawRoundRect(color = if (i % 2 == 0) RoseRed else Color.White, topLeft = offset, size = Size(w * 0.1f, h * 0.04f), cornerRadius = CornerRadius(2.dp.toPx()), style = Stroke(width = 1.dp.toPx()))
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
                                colors = ButtonDefaults.buttonColors(containerColor = RoseRed),
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
                onAdd = { name, phone, isCatchUp ->
                    viewModel.addMemberMidCycle(groupId, name, phone, isCatchUp)
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
                    colors = CardDefaults.cardColors(containerColor = WarmBackground),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(text = "Selamat! 🎉", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold), color = RoseRed)
                        Text(text = winningMemberState!!.displayName, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold), textAlign = TextAlign.Center)
                        Text(text = "Telah memenangkan Arisan kelompok ${groupState?.group?.name}", textAlign = TextAlign.Center)
                        Button(onClick = { showCelebrationDialog = false }, shape = CircleShape, colors = ButtonDefaults.buttonColors(containerColor = RoseRed)) {
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
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.member.displayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                val statusText = when(item.state) {
                    PaymentState.PAID -> "Lunas"
                    PaymentState.PARTIAL -> "Sisa Rp ${String.format(locale, "%,.0f", item.sisa)}"
                    PaymentState.DITALANGI -> "Ditalangi"
                    PaymentState.UNPAID -> "Belum Bayar"
                }
                Text(text = statusText, color = if (item.state == PaymentState.PAID) Color(0xFF2E7D32) else RoseRed, fontWeight = FontWeight.Bold)
            }
            if (item.state != PaymentState.PAID) {
                IconButton(onClick = onSendReminder, modifier = Modifier.background(RoseContainer, CircleShape).size(36.dp)) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = RoseRed, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
