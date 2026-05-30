package io.github.naupyon.arisanak.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.naupyon.arisanak.presentation.viewmodel.GroupUiState
import io.github.naupyon.arisanak.presentation.ui.theme.*

@Composable
fun GroupCardItem(
    groupState: GroupUiState,
    onCardClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("group_card_${groupState.group.id}")
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
                        color = OnRoseContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Putaran #${groupState.group.currentIntervalSequence} • Frk: ${groupState.group.frequency}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BalanceSec,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (groupState.isReadyToKocok) RoseContainer else StatusSecContainer)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (groupState.isReadyToKocok) "Siap Kocok" else "Kas Belum Siap",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (groupState.isReadyToKocok) RoseRed else BalanceSec
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
                        text = "Terkumpul: Rp ${String.format("%,.0f", groupState.collectedAmount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnRoseContainer
                    )
                    Text(
                        text = "Target: Rp ${String.format("%,.0f", groupState.targetPot)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BalanceSec
                    )
                }

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = RoseRed,
                    trackColor = DividerVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onCardClick,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(RoseContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = "Kocok Botol",
                        tint = RoseRed,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
