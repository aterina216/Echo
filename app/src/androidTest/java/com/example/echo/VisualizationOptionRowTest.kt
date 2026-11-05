package com.example.echo

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.echo.ui.components.VisualizationOptionRow
import com.example.echo.ui.theme.EchoTheme
import com.example.echo.ui.theme.EchoThemeOption
import com.example.echo.ui.theme.VisualizationMode
import junit.framework.TestCase.assertTrue
import org.jetbrains.annotations.TestOnly
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VisualizationOptionRowTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private fun setContent(
        mode: VisualizationMode = VisualizationMode.WAVE,
        isSelected: Boolean = false,
        onClick: () -> Unit = {}
    ) {
        rule.setContent {
            EchoTheme(themeOption = EchoThemeOption.BLUE) {
                VisualizationOptionRow(
                    mode = mode,
                    isSelected = isSelected,
                    onClick = onClick
                )
            }
        }
    }

    @Test
    fun displayName_isDisplayed(){
        setContent(mode = VisualizationMode.WAVE)
        rule.onNodeWithText(VisualizationMode.WAVE.displayName).assertIsDisplayed()
    }

    @Test
    fun displayName_isShownExactlyOnce(){
        setContent(mode = VisualizationMode.BARS)

        rule.onNodeWithText(VisualizationMode.BARS.displayName).assertIsDisplayed()
    }

    @Test
    fun radioButton_isNotSelectedWhenNotSelected(){
        setContent(isSelected = false)

        rule.onNode(isSelectable()).assertIsNotSelected()
    }

    @Test
    fun radioButton_isSelectedWhenSelected(){
        setContent(isSelected = true)

        rule.onNode(isSelectable()).assertIsSelected()
    }

    @Test
    fun clickingRow_callsOnClick(){
        var clicked = false
        setContent(onClick = {clicked=true})

        rule.onNodeWithText(VisualizationMode.WAVE.displayName).performClick()

        assertTrue(clicked)
    }

    @Test
    fun clickingRow_whenSelected_callsOnClick(){
        var clicked = false
        setContent(isSelected =  true, onClick = {clicked = true})

        rule.onNodeWithText(VisualizationMode.WAVE.displayName).performClick()

        assertTrue(clicked)
    }

    @Test
    fun clickingRadioButton_callsOnClick(){
        var clicked =false
        setContent(onClick = {clicked = true})

        rule.onNode(isSelectable()).performClick()
        assertTrue(clicked)
    }

    @Test
    fun circleMode_displaysCorrectText(){
        setContent(mode = VisualizationMode.CIRCLE)
        rule.onNodeWithText(VisualizationMode.CIRCLE.displayName).assertIsDisplayed()
    }

    @Test
    fun barsMode_displaysCorrectText(){
        setContent(mode = VisualizationMode.BARS)
        rule.onNodeWithText(VisualizationMode.BARS.displayName).assertIsDisplayed()
    }
}