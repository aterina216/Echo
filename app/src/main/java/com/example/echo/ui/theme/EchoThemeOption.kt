package com.example.echo.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class EchoThemeOption(
    val displayName: String,
    val backgroundColor: Color,
    val waveColor: Color,
    val waveColorDark: Color,
    val waveColorLight: Color,
    val isLight: Boolean = false
) {
    // Тёмные темы
    BLUE(
        "Синяя",
        backgroundColor = Color(0xFF0D1B2A),
        waveColor = Color(0xFF4FC3F7),
        waveColorDark = Color(0xFF0277BD),
        waveColorLight = Color(0xFFB3E5FC)
    ),
    GREEN(
        "Зелёная",
        backgroundColor = Color(0xFF0B2E13),
        waveColor = Color(0xFF00E676),
        waveColorDark = Color(0xFF00A152),
        waveColorLight = Color(0xFF69F0AE)
    ),
    PINK(
        "Розовая",
        backgroundColor = Color(0xFF3E0D22),
        waveColor = Color(0xFFF06292),
        waveColorDark = Color(0xFFC2185B),
        waveColorLight = Color(0xFFF8BBD0)
    ),
    VIOLET(
        "Фиолетовая",
        backgroundColor = Color(0xFF1F0B3E),
        waveColor = Color(0xFFB388FF),
        waveColorDark = Color(0xFF7C4DFF),
        waveColorLight = Color(0xFFD1C4E9)
    ),
    YELLOW(
        "Жёлтая",
        backgroundColor = Color(0xFF3E2D00),
        waveColor = Color(0xFFFFEB3B),
        waveColorDark = Color(0xFFF9A825),
        waveColorLight = Color(0xFFFFF9C4)
    ),
    BLACK(
        "Чёрная",
        backgroundColor = Color(0xFF000000),
        waveColor = Color(0xFFFFFFFF),
        waveColorDark = Color(0xFFB0BEC5),
        waveColorLight = Color(0xFFECEFF1)
    ),
    RED(
        "Красная",
        backgroundColor = Color(0xFF3E0A0A),
        waveColor = Color(0xFFFF5252),
        waveColorDark = Color(0xFFB71C1C),
        waveColorLight = Color(0xFFFF8A80)
    ),
    CYAN(
        "Голубая",
        backgroundColor = Color(0xFF00363A),
        waveColor = Color(0xFF18FFFF),
        waveColorDark = Color(0xFF00838F),
        waveColorLight = Color(0xFF80DEEA)
    ),
    LIME(
        "Салатовая",
        backgroundColor = Color(0xFF1B2E00),
        waveColor = Color(0xFF76FF03),
        waveColorDark = Color(0xFF64DD17),
        waveColorLight = Color(0xFFB2FF59)
    ),
    MAGENTA(
        "Пурпурная",
        backgroundColor = Color(0xFF3E003E),
        waveColor = Color(0xFFFF00FF),
        waveColorDark = Color(0xFF8E24AA),
        waveColorLight = Color(0xFFEA80FC)
    ),
    TEAL(
        "Бирюзовая",
        backgroundColor = Color(0xFF00363A),
        waveColor = Color(0xFF1DE9B6),
        waveColorDark = Color(0xFF00897B),
        waveColorLight = Color(0xFF64FFDA)
    ),
    ORANGE(
        "Оранжевая",
        backgroundColor = Color(0xFF3E2A00),
        waveColor = Color(0xFFFF9800),
        waveColorDark = Color(0xFFF57C00),
        waveColorLight = Color(0xFFFFCC80)
    ),
    SILVER(
        "Серебристая",
        backgroundColor = Color(0xFFF5F5F5),
        waveColor = Color(0xFF607D8B),
        waveColorDark = Color(0xFF37474F),
        waveColorLight = Color(0xFFB0BEC5),
        isLight = true
    );

    fun colorScheme(): ColorScheme = when {
        isLight -> lightColorScheme(
            primary = waveColor,
            background = backgroundColor,
            surface = backgroundColor,
            onBackground = Color(0xFF212121),
            onSurface = Color(0xFF212121),
            secondary = waveColor,
            onSecondary = Color.White
        )
        else -> darkColorScheme(
            primary = waveColor,
            background = backgroundColor,
            surface = backgroundColor,
            onBackground = Color.White,
            onSurface = Color.White,
            secondary = waveColor,
            onSecondary = Color.Black
        )
    }
}
