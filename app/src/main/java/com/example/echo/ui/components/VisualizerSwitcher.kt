package com.example.echo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.echo.data.player.AudioVisualData
import com.example.echo.ui.theme.EchoThemeOption
import com.example.echo.ui.theme.VisualizationMode


@Composable
fun VisualizerSwitcher(
    mode: VisualizationMode,
    audioVisualData: AudioVisualData?,
    themeOption: EchoThemeOption,
    modifier: Modifier = Modifier
) {

    when(mode) {
        VisualizationMode.WAVE -> WaveVisualizerView(audioVisualData, themeOption, modifier)
        VisualizationMode.CIRCLE -> CircleVisualizer(audioVisualData, themeOption, modifier)
        VisualizationMode.BARS -> BarsVisualizer(audioVisualData, themeOption, modifier)
    }
}