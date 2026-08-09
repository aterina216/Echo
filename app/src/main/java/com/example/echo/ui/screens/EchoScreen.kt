package com.example.echo.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.echo.ui.components.AudioVisualizerView
import com.example.echo.ui.components.TimerDialog
import com.example.echo.ui.viewmodel.EchoViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.echo.util.TimeFormatter.formatTime

@Composable
fun EchoScreen(viewModel: EchoViewModel) {
    val context = LocalContext.current

    // --- Состояния из ViewModel ---
    val currentStation by viewModel.currentStation.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val visualData by viewModel.visualData.collectAsState()
    val remainingMs by viewModel.remainings.collectAsState()
    val isTimerActive = remainingMs > 0L

    // --- Локальные UI-состояния ---
    var expanded by remember { mutableStateOf(false) }
    var showTimerDialog by remember { mutableStateOf(false) }

    // --- Разрешение микрофона (только UI) ---
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.onPermissionGranted()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0D0D0D), Color(0xFF1A1A2E))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = currentStation.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Light,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currentStation.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            // --- Выбор станции ---
            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(
                            listOf(
                                Color.White.copy(alpha = 0.4f),
                                Color.White.copy(alpha = 0.4f)
                            )
                        )
                    )
                ) {
                    Text("Сменить станцию")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    offset = DpOffset(x = 0.dp, y = 8.dp),
                    modifier = Modifier.background(Color(0xFF1A1A2E))
                ) {
                    viewModel.stations.forEach { station ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = station.title,
                                    color = if (station.id == currentStation.id) Color.White
                                    else Color.White.copy(alpha = 0.6f)
                                )
                            },
                            onClick = {
                                viewModel.selectStation(station)
                                expanded = false
                            },
                            modifier = Modifier.background(Color(0xFF1A1A2E))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // --- Кнопка Play/Pause ---
            IconButton(
                onClick = {
                    if (isPlaying) {
                        viewModel.togglePlayPause()   // пауза
                    } else {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            viewModel.togglePlayPause()   // запуск
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                },
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // --- Визуализация ---
            AnimatedVisibility(
                visible = visualData != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                AudioVisualizerView(
                    audioData = visualData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
            }
        }

        // --- Таймер в правом верхнем углу ---
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(35.dp)
        ) {
            if (isTimerActive) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        formatTime(remainingMs),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { viewModel.cancelTimer() },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Отменить таймер",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            } else if (isPlaying) {
                IconButton(
                    onClick = { showTimerDialog = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = "Установить таймер",
                        tint = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }

    // --- Диалог таймера ---
    if (showTimerDialog) {
        TimerDialog(
            onDismiss = { showTimerDialog = false },
            onStart = { minutes -> viewModel.startTimer(minutes) }
        )
    }
}