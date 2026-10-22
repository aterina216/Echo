package com.example.echo.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.echo.data.prefs.PrefsDataStore
import com.example.echo.data.prefs.PrefsKeys
import com.example.echo.ui.theme.EchoThemeOption
import com.example.echo.ui.theme.VisualizationMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    var themeFlow: Flow<EchoThemeOption?> = dataStore.data.map {
        prefs ->
        prefs[PrefsKeys.THEME]?.let { name ->
            runCatching {
                EchoThemeOption.valueOf(name)
            }.getOrNull()
        }
    }

    val visualizationModeFlow: Flow<VisualizationMode?> = dataStore.data.map {
        prefs ->
        prefs[PrefsKeys.VISUALIZATION_MODE]?.let {
            name ->
            runCatching { VisualizationMode.valueOf(name) }.getOrNull()
        }
    }

    suspend fun saveTheme(option: EchoThemeOption) {
        dataStore.edit {
            preferences ->
            preferences[PrefsKeys.THEME] = option.name
        }
    }

    suspend fun saveVisualizationMode(mode: VisualizationMode) {
        dataStore.edit { prefs ->
            prefs[PrefsKeys.VISUALIZATION_MODE] = mode.name
        }
    }
}