package io.github.naupyon.arisanak.presentation.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.naupyon.arisanak.presentation.viewmodel.ArisanViewModel
import io.github.naupyon.arisanak.presentation.ui.theme.*

@Composable
fun SecurityLockScreen(
    viewModel: ArisanViewModel,
    onPassed: @Composable () -> Unit
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    var pinState by remember { mutableStateOf("") }
    var isVerified by remember { mutableStateOf(false) }
    var hasInitialized by remember { mutableStateOf(false) }

    if (settings == null) {
        Box(modifier = Modifier.fillMaxSize().background(WarmBackground))
        return
    }

    if (!hasInitialized) {
        if (settings?.isPinEnabled == false) {
            isVerified = true
        }
        hasInitialized = true
    }

    if (isVerified) {
        onPassed()
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WarmBackground)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Aplikasi Terkunci",
                    tint = RoseRed,
                    modifier = Modifier.size(64.dp)
                )

                Text(
                    text = "Arisanak Terkunci",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnRoseContainer
                )

                Text(
                    text = "Masukkan PIN 4-Digit",
                    style = MaterialTheme.typography.bodyLarge,
                    color = BalanceSec
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(if (pinState.length > i) RoseRed else DividerVariant.copy(alpha = 0.5f))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val keys = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("C", "0", "OK")
                    )

                    for (row in keys) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            for (key in row) {
                                Button(
                                    onClick = {
                                        when (key) {
                                            "C" -> if (pinState.isNotEmpty()) pinState = pinState.dropLast(1)
                                            "OK" -> {
                                                if (pinState == settings?.pinCode) isVerified = true
                                                else {
                                                    Toast.makeText(context, "PIN Salah!", Toast.LENGTH_SHORT).show()
                                                    pinState = ""
                                                }
                                            }
                                            else -> {
                                                if (pinState.length < 4) {
                                                    pinState += key
                                                    if (pinState.length == 4 && pinState == settings?.pinCode) {
                                                        isVerified = true
                                                    } else if (pinState.length == 4) {
                                                        Toast.makeText(context, "PIN Salah!", Toast.LENGTH_SHORT).show()
                                                        pinState = ""
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (key == "OK") RoseRed else RoseContainer,
                                        contentColor = if (key == "OK") OnRoseRed else OnRoseContainer
                                    ),
                                    modifier = Modifier.size(72.dp),
                                    shape = CircleShape,
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    when (key) {
                                        "OK" -> {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "OK",
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                        "C" -> {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                                contentDescription = "Clear",
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        else -> {
                                            Text(
                                                text = key,
                                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
