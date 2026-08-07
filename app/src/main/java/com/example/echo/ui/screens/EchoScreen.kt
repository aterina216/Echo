package com.example.echo.ui.screens

import android.R
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.rememberLifecycleOwner
import com.example.echo.data.player.AudioVisualData
import com.example.echo.data.player.AudioVisualizer
import com.example.echo.data.player.EchoPlayer
import com.example.echo.domain.model.defaultStations
import com.example.echo.ui.components.AudioVisualizerView
import com.example.echo.ui.components.TimerDialog
import com.example.echo.util.SleepTimer
import com.example.echo.util.TimeFormatter.formatTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.util.jar.Manifest

@Composable
fun EchoScreen(player: EchoPlayer, visualizer: AudioVisualizer) {

    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    val streamUrl = "http://ice1.somafm.com/spacestation-128-mp3"
    val stations = remember { defaultStations() }
    var currentStation by remember { mutableStateOf(stations.first()) }
    var expanded by remember { mutableStateOf(false) }
    var visualDataFlow by remember { mutableStateOf<Flow<AudioVisualData>?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            player.play(streamUrl)
            isPlaying = true
            visualDataFlow = visualizer.start()
        }
    }
    val sleepTimer = remember { SleepTimer() }
    val remainings by sleepTimer.remainings.collectAsState()
    val isTimerActive = remainings > 0L

    var showTimerDialog by remember { mutableStateOf(false) }

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
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box{
                OutlinedButton(
                    onClick = {expanded = true},
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.4f),
                            Color.White.copy(alpha = 0.4f)))
                    )
                ) {
                    Text("Сменить станцию")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {expanded = false},
                    offset = DpOffset(x = 0.dp, y = 8.dp),
                    modifier = Modifier.background(Color(0xFF1A1A2E))
                ) {
                    stations.forEach {
                        station ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = station.title,
                                    color = if (station.id == currentStation.id) Color.White else
                                    Color.White.copy(alpha = 0.6f)
                                )
                            },
                            onClick = {
                                currentStation = station
                                expanded = false
                                if (isPlaying) {
                                    player.play(station.url)
                                    visualDataFlow = visualizer.start()
                                }
                            },
                            modifier = Modifier.background(Color(0xFF1A1A2E))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            IconButton(
                onClick = {
                    if (isPlaying) {
                        player.pause()
                        isPlaying = false
                        visualizer.stop()
                        visualDataFlow = null
                        sleepTimer.cancel()
                    } else {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            player.play(currentStation.url)
                            isPlaying = true
                            visualDataFlow = visualizer.start()
                        } else {
                            permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
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
            Spacer(modifier = Modifier.height(48.dp))



            AnimatedVisibility(
                visible = visualDataFlow != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                visualDataFlow?.let { flow ->
                    AudioVisualizerView(
                        visualDataFlow = flow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                }
            }
        }

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
                    Text(formatTime(remainings),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {sleepTimer.cancel()},
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(Icons.Default.Close,
                            contentDescription = "Отменить таймер",
                            tint = Color.White.copy(alpha = 0.7f))
                    }
                }
            }
            else if (isPlaying) {
                IconButton(
                    onClick = {showTimerDialog = true},
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

        if (showTimerDialog) {
            TimerDialog(
                onDismiss = {showTimerDialog = false},
                onStart = {minutes ->
                    sleepTimer.start(minutes) {
                        player.pause()
                        isPlaying = false
                        visualizer.stop()
                        visualDataFlow = null
                    }
                }
            )
        }
    }
}