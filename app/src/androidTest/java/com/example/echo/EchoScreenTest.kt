package com.example.echo

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.example.echo.data.player.AudioVisualData
import com.example.echo.data.player.AudioVisualizer
import com.example.echo.data.player.EchoPlayer
import com.example.echo.data.repository.RadioRepository
import com.example.echo.data.repository.SettingsRepository
import com.example.echo.domain.model.RadioStation
import com.example.echo.ui.screens.EchoScreen
import com.example.echo.ui.theme.EchoTheme
import com.example.echo.ui.theme.EchoThemeOption
import com.example.echo.ui.theme.VisualizationMode
import com.example.echo.ui.viewmodel.EchoViewModel
import com.example.echo.util.SleepTimer
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.onNodeWithContentDescription

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 33)
class EchoScreenTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val stations = listOf(
        RadioStation(
            id = "spacestation",
            title = "Space Station Soma",
            description = "Ambient, space music",
            url = "http://ice1.somafm.com/spacestation-128-mp3"
        ),
        RadioStation(
            id = "dronezone",
            title = "Drone Zone",
            description = "Droning, atmospheric soundscapes",
            url = "http://ice1.somafm.com/dronezone-128-mp3"
        )
    )

    private lateinit var viewModel: EchoViewModel

    private lateinit var currentStationFlow: MutableStateFlow<RadioStation>
    private lateinit var isPlayingFlow: MutableStateFlow<Boolean>
    private lateinit var visualDataFlow: MutableStateFlow<AudioVisualData?>
    private lateinit var remainingsFlow: MutableStateFlow<Long>
    private lateinit var themeOptionFlow: MutableStateFlow<EchoThemeOption>
    private lateinit var visualizationViewModeFlow: MutableStateFlow<VisualizationMode>

    @Before
    fun setup(){
        currentStationFlow = MutableStateFlow(stations.first())
        isPlayingFlow = MutableStateFlow(false)
        visualDataFlow = MutableStateFlow(null)
        remainingsFlow = MutableStateFlow(0L)
        themeOptionFlow = MutableStateFlow(EchoThemeOption.BLUE)
        visualizationViewModeFlow = MutableStateFlow(VisualizationMode.WAVE)

        viewModel = mockk(relaxed = true)

        every { viewModel.stations } returns stations
        every { viewModel.currentStation } returns currentStationFlow
        every { viewModel.isPlaying } returns isPlayingFlow
        every { viewModel.visualData } returns visualDataFlow
        every { viewModel.remainings } returns remainingsFlow
        every { viewModel.themeOption } returns themeOptionFlow
        every { viewModel.visualizationMode } returns visualizationViewModeFlow
    }

    private fun setContent(
        onNavigateSettings: () -> Unit = {},
        onNavigateToAbout: () -> Unit = {}
    ) {
        rule.setContent {
            EchoTheme(themeOption = themeOptionFlow.collectAsState().value) {
                EchoScreen(
                    viewModel,
                    onNavigateSettings,
                    onNavigateToAbout
                )
            }
        }
    }

    @Test
    fun stationTitle_isDisplayed(){
        setContent()
        rule.onNodeWithText("Space Station Soma").assertIsDisplayed()
    }

    @Test
    fun stationDescription_isDisplayed(){
        setContent()
        rule.onNodeWithText("Ambient, space music").assertIsDisplayed()
    }

    @Test
    fun stationTitle_updatesWhenFlowChanges(){
        setContent()
        currentStationFlow.value = stations[1]
        rule.waitForIdle()

        rule.onNodeWithText("Drone Zone").assertIsDisplayed()
    }

    @Test
    fun changeStationButton_isDisplayed(){
        setContent()
        rule.onNodeWithText("Сменить станцию").assertIsDisplayed()
    }

    @Test
    fun playIcon_isDisplayedWhenPaused(){
        setContent()
        rule.onNodeWithContentDescription("Play").assertIsDisplayed()
    }

    @Test
    fun pauseIcon_isDisplayedWhenPlaying(){
        isPlayingFlow.value = true
        setContent()
        rule.onNodeWithContentDescription("Pause").assertIsDisplayed()
    }

    @Test
    fun playIcon_isNotDisplayedWhenPlaying(){
        isPlayingFlow.value = true
        setContent()
        rule.onNodeWithContentDescription("Play").assertDoesNotExist()
    }

    @Test
    fun timerSetupIcon_isVisibleWhenPlaying(){
        isPlayingFlow.value = true
        setContent()
        rule.onNodeWithContentDescription("Установить таймер").assertIsDisplayed()
    }

    @Test
    fun timerSetupIcon_isVisibleWhenPaused(){
        setContent()
        rule.onNodeWithContentDescription("Установить таймер").assertDoesNotExist()
    }

    @Test
    fun remainingTime_isDisplayedWhenTimerActive(){
        isPlayingFlow.value = true
        remainingsFlow.value = 60_000L
        setContent()

        rule.onNodeWithText("01:00").assertIsDisplayed()
    }

    @Test
    fun cancelTimerIcon_isDisplayedWhenTimerActive(){
        isPlayingFlow.value = true
        remainingsFlow.value = 60_000L
        setContent()

        rule.onNodeWithContentDescription("Отменить таймер").assertIsDisplayed()
    }

    @Test
    fun topBar_isHiddenInitially(){
        setContent()
        rule.onNodeWithText("Echo").assertDoesNotExist()
    }
}