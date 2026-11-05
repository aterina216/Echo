package com.example.echo

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.echo.ui.screens.SettingsScreen
import com.example.echo.ui.theme.EchoTheme
import com.example.echo.ui.theme.EchoThemeOption
import com.example.echo.ui.theme.VisualizationMode
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private fun setContent(
        currentTheme: EchoThemeOption = EchoThemeOption.BLUE,
        currentVisualization: VisualizationMode = VisualizationMode.WAVE,
        onThemeSelected: (EchoThemeOption) -> Unit = {},
        onVisualizationSelected: (VisualizationMode) -> Unit = {},
        onBack: () -> Unit = {}
    ) {

        rule.setContent {
            EchoTheme(themeOption = currentTheme) {
                SettingsScreen(
                    currentTheme,
                    currentVisualization,
                    onThemeSelected,
                    onVisualizationSelected,
                    onBack
                )
            }
        }
    }

    @Test
    fun title_isDisplayed(){
        setContent()
        rule.onNodeWithText("Настройки").assertIsDisplayed()
    }

    @Test
    fun sectionHeaders_areDisplayed(){
        setContent()

        rule.onNodeWithText("Уведомления").assertIsDisplayed()
        rule.onNodeWithText("Тема оформления").assertIsDisplayed()
        rule.onNodeWithText("Визуализация")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun allThemeOptions_areDisplayed(){
        setContent()

        EchoThemeOption.entries.forEach { option ->
            rule.onAllNodesWithText(option.displayName)
                .onFirst()
                .performScrollTo()
                .assertIsDisplayed()
        }
    }

    @Test
    fun allVisualizationModes_areDisplayed(){
        setContent()

        VisualizationMode.entries.forEach { mode ->
            rule.onNodeWithText(mode.displayName).performScrollTo()
                .assertIsDisplayed()
        }
    }

    @Test
    fun cliclingTheme_callsCallback(){
        var selected: EchoThemeOption?=null
        setContent(onThemeSelected = {selected=it})

        rule.onAllNodesWithText(EchoThemeOption.PINK.displayName)
            .onFirst()
            .performClick()

        assertEquals(EchoThemeOption.PINK, selected)
    }

    @Test
    fun clickingVisualization_callsCallback(){
        var selected: VisualizationMode? = null
        setContent(onVisualizationSelected = {selected=it})

        rule.onNodeWithText(VisualizationMode.BARS.displayName)
            .performScrollTo()
            .performClick()

        assertEquals(VisualizationMode.BARS, selected)
    }

    @Test
    fun clickingBack_callsCallback(){
        var backCalled = false
        setContent(onBack = {backCalled = true})

        rule.onNodeWithContentDescription("Назад").performClick()
        assertTrue(backCalled)
    }
}