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
                title = { Text(text = "Profil & Pengaturan", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmBackground)
            )
        },
        containerColor = WarmBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ProfileHeader(settings?.userName ?: "Ibu Nurul") { viewModel.updateUserName(it) }

            SettingsSection("Tampilan") {
                ToggleSetting("Mode Gelap", settings?.isDarkMode ?: false) { viewModel.updateDarkMode(it) }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Warna Tema")
                    TextButton(onClick = { viewModel.updateColorMode(if (settings?.colorMode == "Default") "Material You" else "Default") }) {
                        Text(settings?.colorMode ?: "Default")
                    }
                }
            }

            SettingsSection("Bahasa") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Bahasa Aplikasi")
                    Row {
                        FilterChip(selected = settings?.language == "ID", onClick = { viewModel.updateLanguage("ID") }, label = { Text("ID") })
                        Spacer(Modifier.width(8.dp))
                        FilterChip(selected = settings?.language == "EN", onClick = { viewModel.updateLanguage("EN") }, label = { Text("EN") })
                    }
                }
            }

            SettingsSection("Keamanan") {
                ToggleSetting("Aktifkan PIN", settings?.isPinEnabled ?: false) { viewModel.updatePinEnabled(it) }
                if (settings?.isPinEnabled == true) {
                    OutlinedTextField(
                        value = settings?.pinCode ?: "",
                        onValueChange = { if (it.length <= 4) viewModel.updatePinCode(it) },
                        label = { Text("PIN 4-Digit") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            SettingsSection("Template Pesan") {
                OutlinedTextField(
                    value = settings?.reminderTemplate ?: "",
                    onValueChange = { viewModel.updateReminderTemplate(it) },
                    label = { Text("Template Tagihan") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                OutlinedTextField(
                    value = settings?.winTemplate ?: "",
                    onValueChange = { viewModel.updateWinTemplate(it) },
                    label = { Text("Template Pemenang") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        }
    }
}

@Composable
fun ProfileHeader(name: String, onNameChange: (String) -> Unit) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = RoseContainer), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.size(64.dp).background(RoseRed, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(36.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                var isEditing by remember { mutableStateOf(false) }
                if (isEditing) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = onNameChange,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { IconButton(onClick = { isEditing = false }) { Icon(Icons.Default.Check, null) } }
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = OnRoseContainer)
                        IconButton(onClick = { isEditing = true }) { Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp)) }
                    }
                }
                Text(text = "PRO MEMBER", style = MaterialTheme.typography.labelSmall, color = GoldTertiary)
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnRoseContainer)
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = WarmSurface), modifier = Modifier.fillMaxWidth()) {
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
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedTrackColor = RoseRed))
    }
}
