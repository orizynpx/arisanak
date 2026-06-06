package io.github.naupyon.arisanak.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.naupyon.arisanak.presentation.ui.components.CreateGroupDialog
import io.github.naupyon.arisanak.presentation.ui.components.GroupCardItem
import io.github.naupyon.arisanak.presentation.viewmodel.ArisanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsListScreen(
    viewModel: ArisanViewModel,
    onNavigateToGroup: (Long) -> Unit
) {
    val groups by viewModel.groupsUiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    
    val lazyListState = rememberLazyListState()
    val isFabVisible by remember {
        derivedStateOf { lazyListState.firstVisibleItemIndex == 0 || !lazyListState.isScrollInProgress }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelompok Arisan", fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isFabVisible,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .padding(bottom = 0.dp, end = 16.dp)
                        .size(64.dp)
                        .testTag("add_group_fab"),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Kelompok", modifier = Modifier.size(28.dp))
                }
            }
        }
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
                    Icon(imageVector = Icons.Outlined.People, contentDescription = null, modifier = Modifier.size(64.dp))
                    Text("Belum ada kelompok arisan.", style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            LazyColumn(
                state = lazyListState,
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
