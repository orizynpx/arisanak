package io.github.naupyon.arisanak.presentation.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.github.naupyon.arisanak.domain.model.ArisanFrequency
import io.github.naupyon.arisanak.domain.model.Group
import io.github.naupyon.arisanak.presentation.viewmodel.GroupUiState
import io.github.naupyon.arisanak.presentation.viewmodel.MemberPaymentState
import io.github.naupyon.arisanak.presentation.viewmodel.PaymentState
import io.github.naupyon.arisanak.util.CurrencyUtil

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateGroupDialog(
    onDismiss: () -> Unit,
    onCreate: (String, ArisanFrequency, Double, List<Pair<String, String?>>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var baseDueStr by remember { mutableStateOf("") }
    var selectedFreq by remember { mutableStateOf<ArisanFrequency?>(null) }
    var freshMemberName by remember { mutableStateOf("") }
    var freshMemberPhone by remember { mutableStateOf("") }
    val membersList = remember { mutableStateListOf<Pair<String, String?>>() }
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current
    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri ->
        if (uri != null) {
            try {
                parseContactResult(context, uri)?.let { membersList.add(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            contactPickerLauncher.launch(null)
        } else {
            Toast.makeText(context, "Izin kontak diperlukan untuk fitur ini", Toast.LENGTH_SHORT).show()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp).imePadding().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Buat Kelompok Baru", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "Langkah 1: Nama Kelompok Arisan", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.primary)
                OutlinedTextField(
                    value = name, onValueChange = { name = it }, placeholder = { Text("cth: Arisan Keluarga") }, singleLine = true, 
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }

            val isStepOneDone = name.trim().length >= 3
            AnimatedVisibility(visible = isStepOneDone) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HorizontalDivider()
                    Text(text = "Langkah 2: Frekuensi Putaran", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.primary)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ArisanFrequency.entries.forEach { freq ->
                            val isSel = freq == selectedFreq
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedFreq = freq },
                                label = {
                                    Text(
                                        text = freq.label,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                },
                                shape = CircleShape
                            )
                        }
                    }
                }
            }

            val isStepTwoDone = isStepOneDone && selectedFreq != null
            AnimatedVisibility(visible = isStepTwoDone) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HorizontalDivider()
                    Text(text = "Langkah 3: Jumlah Iuran Dasar", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.primary)
                    OutlinedTextField(
                        value = baseDueStr, onValueChange = { baseDueStr = it }, prefix = { Text("Rp") }, 
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next), 
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true
                    )
                }
            }

            val isStepThreeDone = isStepTwoDone && baseDueStr.toDoubleOrNull() != null
            AnimatedVisibility(visible = isStepThreeDone) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    HorizontalDivider()
                    Text(text = "Langkah 4: Tambahkan Anggota (${membersList.size})", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.primary)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = freshMemberName, onValueChange = { freshMemberName = it }, placeholder = { Text("Nama") }, 
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        OutlinedTextField(
                            value = freshMemberPhone, onValueChange = { freshMemberPhone = it }, placeholder = { Text("WA") }, 
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            supportingText = {
                                formatPhoneNumber(freshMemberPhone)?.let {
                                    Text("Format WA: $it", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            if (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                                contactPickerLauncher.launch(null)
                            } else {
                                permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                            }
                        }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.Default.Contacts, null, modifier = Modifier.size(16.dp)); Text("Kontak", fontSize = 11.sp)
                        }
                        Button(onClick = { if (freshMemberName.isNotBlank()) { membersList.add(freshMemberName to formatPhoneNumber(freshMemberPhone)); freshMemberName = ""; freshMemberPhone = "" } }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                            Text("Tambah +", fontSize = 11.sp)
                        }
                    }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        membersList.forEachIndexed { i, p ->
                            InputChip(selected = true, onClick = { membersList.removeAt(i) }, label = { Text(p.first) }, trailingIcon = { Icon(Icons.Default.Delete, null, modifier = Modifier.size(12.dp)) })
                        }
                    }
                }
            }

            AnimatedVisibility(visible = isStepThreeDone && membersList.isNotEmpty()) {
                Button(
                    onClick = { onCreate(name, selectedFreq!!, baseDueStr.toDouble(), membersList.toList()) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Save, null); Spacer(Modifier.width(8.dp)); Text("Simpan", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickLogPaymentDialog(
    groups: List<GroupUiState>,
    onDismiss: () -> Unit,
    onLog: (Long, Long, Double, Boolean) -> Unit
) {
    var stepOneGroupId by remember { mutableStateOf<Long?>(null) }
    var stepTwoMemberId by remember { mutableStateOf<Long?>(null) }
    var stepThreeAmountStr by remember { mutableStateOf("") }
    var stepFourIsDitalangi by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp).imePadding().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "Catat Pembayaran Iuran", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))

            Text("Langkah 1: Pilih Kelompok", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.primary)
            var stepOneExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = stepOneExpanded, onExpandedChange = { stepOneExpanded = it }) {
                OutlinedTextField(
                    value = groups.find { it.group.id == stepOneGroupId }?.group?.name ?: "Pilih Kelompok...",
                    onValueChange = {}, readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stepOneExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(), shape = RoundedCornerShape(16.dp)
                )
                ExposedDropdownMenu(expanded = stepOneExpanded, onDismissRequest = { stepOneExpanded = false }) {
                    groups.forEach { g -> DropdownMenuItem(text = { Text(g.group.name) }, onClick = { stepOneGroupId = g.group.id; stepTwoMemberId = null; stepOneExpanded = false }) }
                }
            }

            AnimatedVisibility(visible = stepOneGroupId != null) {
                val members = groups.find { it.group.id == stepOneGroupId }?.members?.filter { it.state != PaymentState.PAID && !it.state.name.startsWith("DITALANGI") } ?: emptyList()
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HorizontalDivider()
                    Text("Langkah 2: Pilih Nama Anggota", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.primary)
                    var stepTwoExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = stepTwoExpanded, onExpandedChange = { stepTwoExpanded = it }) {
                        val selM = members.find { it.member.id == stepTwoMemberId }
                        OutlinedTextField(
                            value = selM?.let { "${it.member.displayName} (Sisa: ${CurrencyUtil.formatCurrency(it.sisa)})" } ?: "Pilih Anggota...",
                            onValueChange = {}, readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stepTwoExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(), shape = RoundedCornerShape(16.dp)
                        )
                        ExposedDropdownMenu(expanded = stepTwoExpanded, onDismissRequest = { stepTwoExpanded = false }) {
                            members.forEach { m -> DropdownMenuItem(text = { Text("${m.member.displayName} (Sisa: ${CurrencyUtil.formatCurrency(m.sisa)})") }, onClick = { stepTwoMemberId = m.member.id; stepThreeAmountStr = m.sisa.toString(); stepTwoExpanded = false }) }
                        }
                    }
                }
            }

            AnimatedVisibility(visible = stepTwoMemberId != null) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HorizontalDivider()
                    Text("Langkah 3: Jumlah Pembayaran", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.primary)
                    OutlinedTextField(
                        value = stepThreeAmountStr, onValueChange = { stepThreeAmountStr = it }, prefix = { Text("Rp") }, 
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done), 
                        keyboardActions = KeyboardActions(onDone = { 
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }),
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true
                    )
                }
            }

            val amountValid = stepThreeAmountStr.toDoubleOrNull() != null
            AnimatedVisibility(visible = stepTwoMemberId != null && amountValid) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column { Text("Talangi Iuran", fontWeight = FontWeight.Bold); Text("Gunakan saldo kas admin", fontSize = 11.sp) }
                        Switch(checked = stepFourIsDitalangi, onCheckedChange = { stepFourIsDitalangi = it })
                    }
                    Button(onClick = { onLog(stepOneGroupId!!, stepTwoMemberId!!, stepThreeAmountStr.toDouble(), stepFourIsDitalangi) }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = CircleShape) {
                        Icon(Icons.Default.CheckCircle, null); Spacer(Modifier.width(8.dp)); Text("Simpan Pembayaran", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditGroupDialog(
    group: Group,
    onDismiss: () -> Unit,
    onUpdate: (Group) -> Unit
) {
    var name by remember { mutableStateOf(group.name) }
    var baseDueStr by remember { mutableStateOf(group.baseDueAmount.toString()) }
    var selectedFreq by remember { mutableStateOf(group.frequency) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp).imePadding().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Edit Kelompok", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "Nama Kelompok", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.primary)
                OutlinedTextField(
                    value = name, onValueChange = { name = it }, placeholder = { Text("cth: Arisan Keluarga") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HorizontalDivider()
                Text(text = "Frekuensi Putaran", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.primary)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ArisanFrequency.entries.forEach { freq ->
                        val isSel = freq == selectedFreq
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedFreq = freq },
                            label = {
                                Text(
                                    text = freq.label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            },
                            shape = CircleShape
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HorizontalDivider()
                Text(text = "Iuran Dasar", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.primary)
                OutlinedTextField(
                    value = baseDueStr, onValueChange = { baseDueStr = it }, prefix = { Text("Rp") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    baseDueStr.toDoubleOrNull()?.let { amount ->
                        onUpdate(group.copy(name = name, frequency = selectedFreq, baseDueAmount = amount))
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(8.dp))
                Text("Simpan Perubahan", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberActionDialog(
    item: MemberPaymentState,
    onDismiss: () -> Unit,
    onFullPay: () -> Unit,
    onInstallment: (Double) -> Unit,
    onTalangi: () -> Unit,
    onRevoke: () -> Unit,
    onPrune: () -> Unit
) {
    var customAmt by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp).imePadding().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "Catat Transaksi: ${item.member.displayName}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            
            if (item.state == PaymentState.UNPAID || item.state == PaymentState.PARTIAL) {
                Button(onClick = { onFullPay(); onDismiss() }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = CircleShape, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) {
                    Icon(Icons.Default.Check, null); Spacer(Modifier.width(8.dp)); Text("Lunas Instan (${CurrencyUtil.formatCurrency(item.sisa)})")
                }

                HorizontalDivider()
                OutlinedTextField(
                    value = customAmt, onValueChange = { customAmt = it }, label = { Text("Jumlah Angsuran (Rp)") }, 
                    prefix = { Text("Rp") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done), 
                    keyboardActions = KeyboardActions(onDone = { 
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }),
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = { customAmt.toDoubleOrNull()?.let { onInstallment(it); onDismiss() } }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = CircleShape) {
                    Text("Simpan Angsuran")
                }

                HorizontalDivider()
                Button(onClick = { onTalangi(); onDismiss() }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = CircleShape) {
                    Icon(Icons.Default.Paid, null); Spacer(Modifier.width(8.dp)); Text("Talangi (Bail out)")
                }
            } else {
                // Already paid or ditalangi, show revoke
                Button(onClick = { onRevoke(); onDismiss() }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = CircleShape, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Icon(Icons.Default.Undo, null); Spacer(Modifier.width(8.dp)); Text("Revoke Pembayaran")
                }
            }

//            HorizontalDivider()
//            TextButton(onClick = { onPrune(); onDismiss() }, modifier = Modifier.align(Alignment.CenterHorizontally), colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
//                Icon(Icons.Default.PersonRemove, null); Spacer(Modifier.width(8.dp)); Text("Keluarkan dari Roster")
//            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberMidCycleDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        val context = LocalContext.current
        val contactPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickContact()
        ) { uri ->
            if (uri != null) {
                parseContactResult(context, uri)?.let { (cName, cPhone) ->
                    name = cName
                    phone = cPhone?.replace("+62", "0") ?: ""
                }
            }
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                contactPickerLauncher.launch(null)
            } else {
                Toast.makeText(context, "Izin kontak diperlukan untuk fitur ini", Toast.LENGTH_SHORT).show()
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(24.dp).imePadding().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "Tambah Member Baru", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Nama Anggota", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.primary)
                    TextButton(onClick = {
                        if (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                            contactPickerLauncher.launch(null)
                        } else {
                            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                        }
                    }) {
                        Icon(Icons.Default.ContactPage, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Pilih Kontak", fontSize = 12.sp)
                    }
                }
                OutlinedTextField(
                    value = name, onValueChange = { name = it }, placeholder = { Text("Nama") }, singleLine = true, 
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "WhatsApp (Opsional)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.primary)
                OutlinedTextField(
                    value = phone, onValueChange = { phone = it }, placeholder = { Text("08...") }, singleLine = true, 
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { 
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }),
                    supportingText = {
                        Text("Format: 08...", style = MaterialTheme.typography.labelSmall)
                    }
                )
            }

            Button(
                onClick = { if (name.isNotBlank()) { onAdd(name, formatPhoneNumber(phone)); onDismiss() } },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = CircleShape
            ) { Text("Simpan", fontWeight = FontWeight.Bold) }
        }
    }
}


@Composable
fun PiutangRepaymentDialog(
    memberName: String,
    maxRepay: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(28.dp)) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "Bayar Hutang: $memberName", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = "Sisa Hutang Maksimal: ${CurrencyUtil.formatCurrency(maxRepay)}")
                OutlinedTextField(
                    value = amount, onValueChange = { amount = it }, label = { Text("Jumlah Pengembalian (Rp)") }, singleLine = true,
                    prefix = { Text("Rp") },
                    modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Batal") }
                    Button(onClick = { onConfirm(amount.toDoubleOrNull() ?: 0.0) }, modifier = Modifier.weight(1f), shape = CircleShape) { Text("Konfirmasi") }
                }
            }
        }
    }
}
