package com.example.echo.ui.screens

import android.R
import androidx.compose.foundation.gestures.rememberScrollable2DState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.echo.ui.components.NotificationSettingsRow
import com.example.echo.ui.components.ThemeOptionRow
import com.example.echo.ui.components.VisualizationOptionRow
import com.example.echo.ui.theme.EchoThemeOption
import com.example.echo.ui.theme.VisualizationMode
import com.example.echo.util.areNotificationsEnabled
import com.example.echo.util.openNotificationsSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentTheme: EchoThemeOption,
    currentVisualization: VisualizationMode,
    onThemeSelected: (EchoThemeOption) -> Unit,
    onVisualizationSelected: (VisualizationMode) -> Unit,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    var notificationsEnabled by remember {
        mutableStateOf(areNotificationsEnabled(context))
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        notificationsEnabled = areNotificationsEnabled(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )

            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                "Уведомления",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            NotificationSettingsRow(
                notificationsEnabled = notificationsEnabled,
                onClick = { openNotificationsSettings(context) }
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "Тема оформления",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            EchoThemeOption.entries.forEach { option ->
                ThemeOptionRow(
                    option = option,
                    isSelected = option == currentTheme,
                    onClick = { onThemeSelected(option) }
                )
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Визуализация",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            VisualizationMode.entries.forEach { mode ->
                VisualizationOptionRow(
                    mode = mode,
                    isSelected = mode == currentVisualization,
                    onClick = { onVisualizationSelected(mode) }
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}