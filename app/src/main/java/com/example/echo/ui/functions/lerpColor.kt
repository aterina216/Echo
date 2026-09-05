package com.example.echo.ui.functions

import androidx.compose.ui.graphics.Color

fun lerpColor(start: Color, end: Color, fraction: Float): Color {

    return Color(
        red = start.red + (end.red - start.red) * fraction,
        green = start.green + (end.green - start.green) * fraction,
        blue = start.blue + (end.blue - start.blue) * fraction,
        alpha = 1f
    )
}