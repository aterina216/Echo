package com.example.echo.data.prefs

import androidx.datastore.preferences.core.stringPreferencesKey

object PrefsKeys {

    val THEME = stringPreferencesKey("theme_option")
    val VISUALIZATION_MODE = stringPreferencesKey("visualization_mode")
}