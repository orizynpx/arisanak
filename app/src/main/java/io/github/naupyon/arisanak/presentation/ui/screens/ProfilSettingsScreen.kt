package io.github.naupyon.arisanak.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.naupyon.arisanak.presentation.viewmodel.ArisanViewModel
import io.github.naupyon.arisanak.presentation.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilSettingsScreen(
    viewModel: ArisanViewModel
) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Profil & Pengaturan", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ProfileHeader(settings?.userName ?: "Ibu Siti") { viewModel.updateUserName(it) }

            SettingsSection("Tampilan") {
                ToggleSetting("Mode Gelap", settings?.isDarkMode ?: false) { viewModel.updateDarkMode(it) }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Warna Tema")
                    TextButton(
                        onClick = { viewModel.updateColorMode(if (settings?.colorMode == "Default") "Material You" else "Default") },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        ) {
                        Text(settings?.colorMode ?: "Default")
                    }
                }
            }

//            SettingsSection("Bahasa") {
//                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
//                    Text("Bahasa Aplikasi")
//                    Row {
//                        FilterChip(selected = settings?.language == "ID", onClick = { viewModel.updateLanguage("ID") }, label = { Text("ID") })
//                        Spacer(Modifier.width(8.dp))
//                        FilterChip(selected = settings?.language == "EN", onClick = { viewModel.updateLanguage("EN") }, label = { Text("EN") })
//                    }
//                }
//            }

//            SettingsSection("Keamanan") {
//                ToggleSetting("Aktifkan PIN", settings?.isPinEnabled ?: false) { viewModel.updatePinEnabled(it) }
//                if (settings?.isPinEnabled == true) {
//                    OutlinedTextField(
//                        value = settings?.pinCode ?: "",
//                        onValueChange = { if (it.length <= 4) viewModel.updatePinCode(it) },
//                        label = { Text("PIN 4-Digit") },
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                }
//            }

            SettingsSection("Template Pesan") {
                var localReminder by remember { mutableStateOf(settings?.reminderTemplate ?: "") }
                var localWin by remember { mutableStateOf(settings?.winTemplate ?: "") }
                var isReminderFocused by remember { mutableStateOf(false) }
                var isWinFocused by remember { mutableStateOf(false) }

                LaunchedEffect(settings?.reminderTemplate) {
                    if (!isReminderFocused) {
                        settings?.reminderTemplate?.let { localReminder = it }
                    }
                }
                LaunchedEffect(settings?.winTemplate) {
                    if (!isWinFocused) {
                        settings?.winTemplate?.let { localWin = it }
                    }
                }

                OutlinedTextField(
                    value = localReminder,
                    onValueChange = { 
                        localReminder = it
                        viewModel.updateReminderTemplate(it) 
                    },
                    label = { Text("Template Tagihan") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isReminderFocused = it.isFocused },
                    maxLines = 3
                )
                OutlinedTextField(
                    value = localWin,
                    onValueChange = { 
                        localWin = it
                        viewModel.updateWinTemplate(it) 
                    },
                    label = { Text("Template Pemenang") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isWinFocused = it.isFocused },
                    maxLines = 3
                )
            }
        }
    }
}

@Composable
fun ProfileHeader(name: String, onNameChange: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.DarkGray
        )
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.primary, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(36.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                var isEditing by remember { mutableStateOf(false) }
                var localName by remember { mutableStateOf(name) }

                // Sync from remote only when not editing
                LaunchedEffect(name, isEditing) {
                    if (!isEditing) {
                        localName = name
                    }
                }

                if (isEditing) {
                    OutlinedTextField(
                        value = localName,
                        onValueChange = { localName = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { 
                            IconButton(onClick = { 
                                onNameChange(localName)
                                isEditing = false 
                            }) { 
                                Icon(Icons.Default.Check, null) 
                            } 
                        }
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { 
                            localName = name
                            isEditing = true 
                        }) { 
                            Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp)) 
                        }
                    }
                }
                Text(text = "Bandar Arisan", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.DarkGray
            )
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                content()
            }
        }
    }
}

@Composable
fun ToggleSetting(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
