package com.example.echo

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.media3.exoplayer.ExoPlayer
import com.example.echo.data.player.AudioVisualizer
import com.example.echo.data.player.EchoPlayer
import com.example.echo.ui.navigation.InitNavigation
import com.example.echo.ui.screens.EchoScreen
import com.example.echo.ui.theme.EchoTheme
import com.example.echo.ui.viewmodel.EchoViewModel
import com.example.echo.ui.viewmodel.EchoViewModelFactory
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var echoPlayer: EchoPlayer

    @Inject
    lateinit var visualizer: AudioVisualizer

    @Inject
    lateinit var factory: EchoViewModelFactory

    lateinit var viewModel: EchoViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        (application as EchoApplication).appComponent.inject(this)
        viewModel = ViewModelProvider(this, factory)[EchoViewModel::class]
        setContent {
            EchoTheme {
                InitNavigation(viewModel)
            }
        }
    }
}

