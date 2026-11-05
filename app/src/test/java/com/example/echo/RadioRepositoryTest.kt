package com.example.echo

import com.example.echo.data.repository.RadioRepository
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RadioRepositoryTest {

    private lateinit var repository: RadioRepository

    @Before
    fun setup() {
        repository = RadioRepository()
    }

    @Test
    fun `getAllStations returns non-empty list`() {
        assertTrue(repository.getAllStations().isNotEmpty())
    }

    @Test
    fun `current station defaults to first`() {
        val first = repository.getAllStations().first()
        assertEquals(first, repository.currentStation.value)
    }

    @Test
    fun `setCurrentStation updates flow`() {
        val drone = repository.getAllStations().first() {it.id == "dronezone"}
        repository.setCurrentStation(drone)
        assertEquals(drone, repository.currentStation.value)
    }
}