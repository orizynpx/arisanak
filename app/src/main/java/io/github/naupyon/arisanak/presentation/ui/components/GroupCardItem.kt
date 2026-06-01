package io.github.naupyon.arisanak.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.naupyon.arisanak.presentation.viewmodel.GroupUiState

@Composable
fun GroupCardItem(
    groupState: GroupUiState,
    onCardClick: () -> Unit,
    isClickable: Boolean = true
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isClickable) Modifier.clickable { onCardClick() } else Modifier)
            .testTag("group_card_${groupState.group.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (groupState.group.isArchived) Color.LightGray else MaterialTheme.colorScheme.primaryContainer,
            contentColor = if (groupState.group.isArchived) Color.DarkGray else MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = groupState.group.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Putaran ${groupState.group.currentIntervalSequence}/${groupState.totalCycles}",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Frekuensi: ${groupState.group.frequency.label}",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (groupState.group.isArchived) Color.Gray 
                            else if (groupState.isReadyToKocok) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.secondary
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (groupState.group.isArchived) "Selesai" else if (groupState.isReadyToKocok) "Siap Kocok" else "Berjalan",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (groupState.group.isArchived) Color.White else if (groupState.isReadyToKocok) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                    )
                }
            }

            val progress = if (groupState.targetPot > 0) (groupState.collectedAmount / groupState.targetPot).toFloat() else 0f
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Terkumpul: Rp${String.format("%,.0f", groupState.collectedAmount)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Target: Rp${String.format("%,.0f", groupState.targetPot)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
