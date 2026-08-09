package com.example.echo.data.repository

import com.example.echo.domain.model.RadioStation
import com.example.echo.domain.model.defaultStations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RadioRepository {

    private val stations = defaultStations()
    private val _currentStation = MutableStateFlow(stations.first())
    val currentStation : StateFlow<RadioStation> = _currentStation

    fun getAllStations(): List<RadioStation> = stations

    fun setCurrentStation(station: RadioStation) {
        _currentStation.value = station
    }
}