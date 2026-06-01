package io.github.naupyon.arisanak.presentation.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.naupyon.arisanak.domain.model.Member
import io.github.naupyon.arisanak.presentation.ui.components.ConfettiCanvas
import io.github.naupyon.arisanak.presentation.ui.components.launchWhatsApp
import io.github.naupyon.arisanak.presentation.viewmodel.ArisanViewModel
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WinnerDrawScreen(
    groupId: Long,
    viewModel: ArisanViewModel,
    onBack: () -> Unit
) {
    val groups by viewModel.groupsUiState.collectAsState()
    val groupState = groups.find { it.group.id == groupId }
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    
    val scope = rememberCoroutineScope()
    var isDrawing by remember { mutableStateOf(false) }
    var winningMemberState by remember { mutableStateOf<Member?>(null) }
    var showCelebrationDialog by remember { mutableStateOf(false) }

    val rotationAnim = remember { Animatable(0f) }
    val translationX = remember { Animatable(0f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kocok Arisan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Putaran ${groupState.group.currentIntervalSequence}/${groupState.totalCycles}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .graphicsLayer(rotationZ = rotationAnim.value, translationX = translationX.value),
                    contentAlignment = Alignment.Center
                ) {
                    val bottleColor = MaterialTheme.colorScheme.primary
                    val slipColor1 = MaterialTheme.colorScheme.tertiary
                    val slipColor2 = MaterialTheme.colorScheme.onTertiary
                    
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
                        drawPath(path = path, color = bottleColor, style = Stroke(width = 4.dp.toPx()))

                        val random = Random(42)
                        for (i in 0 until 12) {
                            val offset = Offset(x = w * 0.25f + random.nextFloat() * (w * 0.40f), y = h * 0.45f + random.nextFloat() * (h * 0.35f))
                            drawRoundRect(color = if (i % 2 == 0) slipColor1 else slipColor2, topLeft = offset, size = Size(w * 0.1f, h * 0.04f), cornerRadius = CornerRadius(2.dp.toPx()), style = Stroke(width = 1.dp.toPx()))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

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
                    enabled = !isDrawing && groupState.isReadyToKocok && !groupState.group.isArchived,
                    shape = CircleShape,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(if (isDrawing) "Mengocok..." else "Kocok Arisan")
                }
                
                if (!groupState.isReadyToKocok && !groupState.group.isArchived) {
                    Text(
                        text = "Belum bisa kocok, masih ada anggota yang belum lunas.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }

    if (showCelebrationDialog && winningMemberState != null) {
        Dialog(onDismissRequest = { showCelebrationDialog = false }) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                ConfettiCanvas(modifier = Modifier.fillMaxSize())
                Card(
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(text = "Selamat! 🎉", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                        Text(text = winningMemberState!!.displayName, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold), textAlign = TextAlign.Center)
                        Text(text = "Telah memenangkan Arisan kelompok ${groupState?.group?.name}", textAlign = TextAlign.Center)
                        
                        if (!winningMemberState!!.phoneNumber.isNullOrBlank()) {
                            Button(
                                onClick = {
                                    val template = settings?.winTemplate ?: "Selamat kepada [NamaAnggota] telah memenangkan kocokan arisan kelompok [NamaGrup]!"
                                    val msg = template
                                        .replace("[NamaAnggota]", winningMemberState!!.displayName)
                                        .replace("[NamaGrup]", groupState?.group?.name ?: "")
                                    launchWhatsApp(context, winningMemberState!!.phoneNumber, msg)
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366), contentColor = Color.White)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Chat, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Kabari di WA")
                            }
                        }

                        Button(onClick = { 
                            showCelebrationDialog = false
                            viewModel.advanceInterval(groupId)
                            onBack()
                        }, shape = CircleShape) {
                            Text("Mantap")
                        }
                    }
                }
            }
        }
    }
}
