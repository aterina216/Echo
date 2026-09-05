package com.example.echo.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.echo.data.prefs.PrefsDataStore
import com.example.echo.data.prefs.PrefsKeys
import com.example.echo.ui.theme.EchoThemeOption
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

    suspend fun saveTheme(option: EchoThemeOption) {
        dataStore.edit {
            preferences ->
            preferences[PrefsKeys.THEME] = option.name
        }
    }
}