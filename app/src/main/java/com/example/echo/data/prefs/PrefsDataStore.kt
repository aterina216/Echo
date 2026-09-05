package com.example.echo.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

object PrefsDataStore {

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore("echo_settings")

}