package io.github.naupyon.arisanak.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.naupyon.arisanak.presentation.viewmodel.ArisanViewModel
import io.github.naupyon.arisanak.presentation.viewmodel.GroupUiState
import io.github.naupyon.arisanak.presentation.ui.theme.*
import io.github.naupyon.arisanak.presentation.ui.components.CreateGroupDialog
import io.github.naupyon.arisanak.presentation.ui.components.GroupCardItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsListScreen(
    viewModel: ArisanViewModel,
    onNavigateToGroup: (Long) -> Unit
) {
    val groups by viewModel.groupsUiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelompok Arisan", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmBackground)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = RoseRed,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(bottom = 0.dp, end = 16.dp)
                    .size(64.dp)
                    .testTag("add_group_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Kelompok", modifier = Modifier.size(28.dp))
            }
        },
        containerColor = WarmBackground
    ) { innerPadding ->
        if (groups.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.People, contentDescription = null, modifier = Modifier.size(64.dp), tint = BalanceSec)
                    Text("Belum ada kelompok arisan.", color = BalanceSec, style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(groups) { gState ->
                    GroupCardItem(
                        groupState = gState,
                        onCardClick = { onNavigateToGroup(gState.group.id) }
                    )
                }
            }
        }
        
        if (showCreateDialog) {
            CreateGroupDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, freq, amount, list ->
                    viewModel.createGroup(name, freq, amount, list)
                    showCreateDialog = false
                }
            )
        }
    }
}
