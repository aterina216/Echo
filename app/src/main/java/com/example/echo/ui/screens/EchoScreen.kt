package com.example.echo.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import com.example.echo.ui.components.TimerDialog
import com.example.echo.ui.viewmodel.EchoViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import com.example.echo.service.startEchoService
import com.example.echo.ui.components.VisualizerSwitcher
import com.example.echo.ui.components.WaveVisualizerView
import com.example.echo.ui.components.menu
import com.example.echo.util.TimeFormatter.formatTime
import com.example.echo.util.notifyPermission
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun EchoScreen(viewModel: EchoViewModel, onNavigateToSettings: () -> Unit) {
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
        startEchoService(context, currentStation.url)
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {

    }

    var menuIsVisible by remember { mutableStateOf(false) }
    var isDrownExpanded by remember { mutableStateOf(false) }

    val themeOption by viewModel.themeOption.collectAsState()
    val visualizationMode by viewModel.visualizationMode.collectAsState()

    LaunchedEffect(menuIsVisible, isDrownExpanded) {
        if(menuIsVisible && !isDrownExpanded){
            delay(3000)
            menuIsVisible = false
        }
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = menuIsVisible,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it })
            ) {
                menu(modifier = Modifier,
                    onMenuItemclick = { item ->
                        when (item) {
                            "settings" -> onNavigateToSettings()
                            // "about" -> ...
                        }
                    },
                    onExpandedChange = {isDrownExpanded = it})
            }
        }
    )

    { paddingValues ->

        Box(modifier = Modifier.fillMaxWidth().height(100.dp)
            .pointerInput(Unit){
                detectTapGestures { menuIsVisible = !menuIsVisible }
            }) {

        }

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                        MaterialTheme.colorScheme.background,
                        themeOption.waveColorDark.copy(alpha = 0.2f))
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
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentStation.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                // --- Выбор станции ---
                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary
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
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        viewModel.stations.forEach { station ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = station.title,
                                        color = if (station.id == currentStation.id) MaterialTheme.colorScheme.onSurface
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                },
                                onClick = {
                                    viewModel.selectStation(station)
                                    expanded = false
                                },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
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
                                viewModel.togglePlayPause()
                                startEchoService(context, currentStation.url)
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                            if (!notifyPermission(context)) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        .border(
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                // --- Визуализация ---
                AnimatedVisibility(
                    visible = visualData != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    VisualizerSwitcher(
                        mode = visualizationMode,
                        audioVisualData = visualData,
                        themeOption = themeOption,
                        modifier = Modifier.fillMaxWidth().height(100.dp)
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
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            formatTime(remainingMs),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
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
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
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
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
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