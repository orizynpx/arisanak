package io.github.naupyon.arisanak.presentation.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.naupyon.arisanak.domain.model.Member
import io.github.naupyon.arisanak.presentation.ui.components.AddMemberMidCycleDialog
import io.github.naupyon.arisanak.presentation.viewmodel.ArisanViewModel
import io.github.naupyon.arisanak.presentation.viewmodel.MemberPaymentState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageMembersScreen(
    groupId: Long,
    viewModel: ArisanViewModel,
    onBack: () -> Unit
) {
    val groups by viewModel.groupsUiState.collectAsState()
    val groupState = groups.find { it.group.id == groupId }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var memberToEdit by remember { mutableStateOf<Member?>(null) }
    var memberToDelete by remember { mutableStateOf<Member?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Anggota", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Tambah Anggota")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (groupState == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Kelompok tidak ditemukan")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                items(groupState.members) { memberState ->
                    MemberManageItem(
                        member = memberState.member,
                        onEdit = { memberToEdit = memberState.member },
                        onDelete = { memberToDelete = memberState.member }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }

        if (showAddDialog) {
            AddMemberMidCycleDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { name, phone ->
                    viewModel.addMemberMidCycle(groupId, name, phone)
                    showAddDialog = false
                }
            )
        }

        if (memberToEdit != null) {
            EditMemberDialog(
                member = memberToEdit!!,
                onDismiss = { memberToEdit = null },
                onUpdate = { updatedMember ->
                    viewModel.updateMember(updatedMember)
                    memberToEdit = null
                }
            )
        }

        if (memberToDelete != null) {
            AlertDialog(
                onDismissRequest = { memberToDelete = null },
                title = { Text("Hapus Anggota") },
                text = { 
                    val msg = if (memberToDelete!!.hasWon) {
                        "Anggota ini sudah memenangkan arisan dan tidak dapat dihapus."
                    } else {
                        "Apakah Anda yakin ingin menghapus '${memberToDelete!!.displayName}'? Kontribusi yang sudah dibayarkan akan masuk ke saldo kas kelompok."
                    }
                    Text(msg)
                },
                confirmButton = {
                    if (!memberToDelete!!.hasWon) {
                        TextButton(onClick = {
                            viewModel.removeMember(memberToDelete!!)
                            memberToDelete = null
                        }) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { memberToDelete = null }) { Text("Batal") }
                }
            )
        }
    }
}

@Composable
fun MemberManageItem(
    member: Member,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(text = member.displayName, fontWeight = FontWeight.Bold)
                if (!member.phoneNumber.isNullOrBlank()) {
                    Text(text = member.phoneNumber, style = MaterialTheme.typography.bodySmall)
                }
                if (member.hasWon) {
                    Text(
                        text = "Sudah Menang",
                        color = Color(0xFF2E7D32),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = if (member.hasWon) Color.Gray else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMemberDialog(
    member: Member,
    onDismiss: () -> Unit,
    onUpdate: (Member) -> Unit
) {
    var name by remember { mutableStateOf(member.displayName) }
    var phone by remember { mutableStateOf(member.phoneNumber?.replace("+62", "0") ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp).imePadding(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "Edit Member", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            
            OutlinedTextField(
                value = name, onValueChange = { name = it }, label = { Text("Nama") }, 
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = phone, onValueChange = { phone = it }, label = { Text("WhatsApp") }, placeholder = { Text("08...") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)
            )

            Button(
                onClick = { 
                    // Simplified formatting for brevity
                    val formattedPhone = if (phone.startsWith("0")) "+62${phone.substring(1)}" else phone
                    onUpdate(member.copy(displayName = name, phoneNumber = formattedPhone)) 
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = CircleShape
            ) { Text("Update", fontWeight = FontWeight.Bold) }
        }
    }
}
