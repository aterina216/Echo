package com.example.echo

import androidx.lifecycle.viewModelScope
import com.example.echo.data.player.AudioVisualData
import com.example.echo.data.player.AudioVisualizer
import com.example.echo.data.player.EchoPlayer
import com.example.echo.data.repository.RadioRepository
import com.example.echo.data.repository.SettingsRepository
import com.example.echo.ui.theme.EchoTheme
import com.example.echo.ui.theme.EchoThemeOption
import com.example.echo.ui.theme.VisualizationMode
import com.example.echo.ui.viewmodel.EchoViewModel
import com.example.echo.util.SleepTimer
import io.mockk.Runs
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EchoViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var player: EchoPlayer
    private lateinit var visualizer: AudioVisualizer
    private lateinit var radioRepository: RadioRepository
    private lateinit var settingsRepository: SettingsRepository

    private lateinit var timer: SleepTimer
    private lateinit var remainingsFlow: MutableStateFlow<Long>
    private lateinit var viewModel: EchoViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        player = mockk(relaxed = true)
        visualizer = mockk(relaxed = true)
        every { visualizer.start() } returns emptyFlow()

        radioRepository = RadioRepository()
        settingsRepository = mockk(relaxed = true)
        every { settingsRepository.themeFlow } returns MutableStateFlow<EchoThemeOption?>(null)
        every { settingsRepository.visualizationModeFlow } returns MutableStateFlow<VisualizationMode?>(null)
        remainingsFlow = MutableStateFlow(0L)
        timer = mockk(relaxed = true)
        every { timer.remainings } returns remainingsFlow
        every { timer.start(any(), any()) } just Runs
        every { timer.cancel() } just Runs

        viewModel = EchoViewModel(player, visualizer, radioRepository, settingsRepository, timer)
    }

    @After
    fun tearDown(){
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has correct defauts`() {
        assertFalse(viewModel.isPlaying.value)
        assertNull(viewModel.visualData.value)
        assertEquals(EchoThemeOption.BLUE, viewModel.themeOption.value)
        assertEquals(VisualizationMode.WAVE, viewModel.visualizationMode.value)
        assertEquals(0L, viewModel.remainings.value)
    }

    @Test
    fun `initial station is first from repository`(){
        assertEquals("spacestation", viewModel.currentStation.value.id)
        assertTrue(viewModel.stations.isNotEmpty())
    }

    @Test
    fun `remainings reflects timer flow`(){
        remainingsFlow.value = 120_000L
        assertEquals(120_000L, viewModel.remainings.value)
    }

    @Test
    fun `togglePlayPause when stopped starts playback`() = runTest {
        viewModel.togglePlayPause()
        advanceUntilIdle()

        assertTrue(viewModel.isPlaying.value)
        verify { player.play(viewModel.currentStation.value.url) }
    }

    @Test
    fun `togglePlayPause when playing pauses`() = runTest {
        viewModel.togglePlayPause()
        advanceUntilIdle()

        viewModel.togglePlayPause()
        advanceUntilIdle()

        assertFalse(viewModel.isPlaying.value)
        verify { player.pause() }
    }

    @Test
    fun `onPermissionGranted starts playback`() = runTest {
        viewModel.onPermissionGranted()
        advanceUntilIdle()

        assertTrue(viewModel.isPlaying.value)
        verify { player.play(viewModel.currentStation.value.url) }
    }

    @Test
    fun `pause stops player and clears visual data`() = runTest {
        viewModel.togglePlayPause()
        advanceUntilIdle()

        viewModel.togglePlayPause()
        advanceUntilIdle()

        assertNull(viewModel.visualData.value)
        verify { visualizer.stop() }
    }

    @Test
    fun `play collects data from visualizer`() = runTest {
        val data = AudioVisualData(amplitude = 0.7f)
        every { visualizer.start() } returns flowOf(data)

        viewModel.togglePlayPause()
        advanceUntilIdle()

        assertEquals(0.7f, viewModel.visualData.value?.amplitude)
    }

    @Test
    fun`selectStation when stopped updates current station without playing`() = runTest {
        val drone = viewModel.stations.first {it.id == "dronezone"}

        viewModel.selectStation(drone)
        advanceUntilIdle()

        assertEquals("dronezone", viewModel.currentStation.value.id)
        verify(exactly = 0) {player.play(any())}
    }

    @Test
    fun `selectStation while playing restarts playback with new url`() = runTest {
        viewModel.togglePlayPause()
        advanceUntilIdle()

        val drone = viewModel.stations.first {it.id == "dronezone"}
        viewModel.selectStation(drone)
        advanceUntilIdle()

        assertEquals("dronezone", viewModel.currentStation.value.id)
        verify { player.play(drone.url) }
        assertTrue(viewModel.isPlaying.value)
    }

    @Test
    fun `startTimer calls timer start with minutes`(){
        viewModel.startTimer(5)

        verify { timer.start(5, any()) }
    }

    @Test
    fun `startTimer onFinish pauses playback`() = runTest {
        val capturedCallback = slot<() -> Unit>()
        every { timer.start(any(), capture(capturedCallback)) } just Runs

        viewModel.onPermissionGranted()
        advanceUntilIdle()
        assertTrue(viewModel.isPlaying.value)

        viewModel.startTimer(1)
        capturedCallback.captured.invoke()
        advanceUntilIdle()

        assertFalse(viewModel.isPlaying.value)
        verify { player.pause() }
    }

    @Test
    fun `cancelTimer calls timer cancel`(){
        viewModel.cancelTimer()
        verify { timer.cancel() }
    }

    @Test
    fun `pause cancels timer`() = runTest {
        viewModel.onPermissionGranted()
        advanceUntilIdle()

        viewModel.togglePlayPause()
        advanceUntilIdle()

        verify { timer.cancel() }
    }

    @Test
    fun `setTheme updates state and persists`() = runTest {
        viewModel.setTheme(EchoThemeOption.PINK)
        advanceUntilIdle()

        assertEquals(EchoThemeOption.PINK, viewModel.themeOption.value)
        coVerify { settingsRepository.saveTheme(EchoThemeOption.PINK) }
    }

    @Test
    fun `setVisualizationMode updates state and persists`() = runTest {
        viewModel.setVisualizationMode(VisualizationMode.BARS)
        advanceUntilIdle()

        assertEquals(VisualizationMode.BARS, viewModel.visualizationMode.value)
        coVerify { settingsRepository.saveVisualizationMode(VisualizationMode.BARS) }
    }

    @Test
    fun `init collects saved theme and mode`() = runTest {
        (settingsRepository.themeFlow as MutableStateFlow).value = EchoThemeOption.GREEN
        (settingsRepository.visualizationModeFlow as MutableStateFlow).value = VisualizationMode.CIRCLE

        advanceUntilIdle()

        assertEquals(EchoThemeOption.GREEN, viewModel.themeOption.value)
        assertEquals(VisualizationMode.CIRCLE, viewModel.visualizationMode.value)
    }

    @Test
    fun `init keeps defaults when nothing saved`() = runTest {
        advanceUntilIdle()

        assertEquals(EchoThemeOption.BLUE, viewModel.themeOption.value)
        assertEquals(VisualizationMode.WAVE, viewModel.visualizationMode.value)
    }
 }