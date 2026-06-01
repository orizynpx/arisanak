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
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import io.github.naupyon.arisanak.domain.model.Group
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
    onNavigateToKocok: (Long) -> Unit,
    onNavigateToMembers: (Long) -> Unit,
    onNavigateToHistory: (Long) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]
    val groups by viewModel.groupsUiState.collectAsState()
    val groupState = groups.find { it.group.id == groupId }
    
    var selectedMemberForAction by remember { mutableStateOf<MemberPaymentState?>(null) }
    var showEditGroupDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Info grup", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Grup") },
                            onClick = { 
                                menuExpanded = false
                                showEditGroupDialog = true 
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus Grup", color = MaterialTheme.colorScheme.error) },
                            onClick = { 
                                menuExpanded = false
                                showDeleteConfirmDialog = true 
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                        )
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
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GroupCardItem(
                    groupState = groupState,
                    onCardClick = {},
                    isClickable = false
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    GroupActionButton(
                        icon = Icons.Default.Casino,
                        label = "Kocok Arisan",
                        onClick = { onNavigateToKocok(groupId) },
                        modifier = Modifier.weight(1f)
                    )
                    GroupActionButton(
                        icon = Icons.Default.Group,
                        label = "Manage Members",
                        onClick = { onNavigateToMembers(groupId) },
                        modifier = Modifier.weight(1f)
                    )
                    GroupActionButton(
                        icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                        label = "Riwayat Arisan",
                        onClick = { onNavigateToHistory(groupId) },
                        modifier = Modifier.weight(1f)
                    )
                }

                HorizontalDivider()

                Text(
                    text = "Putaran ${groupState.group.currentIntervalSequence}/${groupState.totalCycles}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                groupState.members.forEach { item ->
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
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        
        if (showEditGroupDialog && groupState != null) {
            EditGroupDialog(
                group = groupState.group,
                onDismiss = { showEditGroupDialog = false },
                onUpdate = { updatedGroup ->
                    viewModel.updateGroup(updatedGroup)
                    showEditGroupDialog = false
                }
            )
        }

        if (showDeleteConfirmDialog && groupState != null) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("Hapus Grup") },
                text = { Text("Apakah Anda yakin ingin menghapus grup '${groupState.group.name}'? Seluruh data anggota dan riwayat akan hilang.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteGroup(groupId)
                            showDeleteConfirmDialog = false
                            onBack()
                        }
                    ) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) { Text("Batal") }
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
                onRevoke = {
                    viewModel.revokePayment(selectedMemberForAction!!.member.id, groupState.activeInterval?.id ?: 0)
                    selectedMemberForAction = null
                },
                onPrune = {
                    viewModel.removeMember(selectedMemberForAction!!.member)
                    selectedMemberForAction = null
                }
            )
        }
    }
}

@Composable
fun GroupActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
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
