package com.example.echo.ui.screens

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.echo.data.player.AudioVisualizer
import com.example.echo.data.player.EchoPlayer
import kotlinx.coroutines.flow.Flow
import java.util.jar.Manifest

@Composable
fun EchoScreen(player: EchoPlayer, visualizer: AudioVisualizer) {

    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    val streamUrl = "http://ice1.somafm.com/spacestation-128-mp3"
    val stationName = "Drone Zone"
    var amplitudeFlow by remember { mutableStateOf<Flow<Float>?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        granted ->
        if (granted) {
            player.play(streamUrl)
            isPlaying = true
            amplitudeFlow = visualizer.start()
        }
    }

    Column(Modifier.fillMaxSize().background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally){

        IconButton(
            onClick = {
                if (isPlaying) {
                    player.pause()
                    isPlaying = false
                    visualizer.stop()
                    amplitudeFlow = null
                }
                else {
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) ==
                        PackageManager.PERMISSION_GRANTED) {
                        player.play(streamUrl)
                        isPlaying = true
                        amplitudeFlow = visualizer.start()
                    }
                    else {
                        permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                    }
                }
            }
        )
    }
}