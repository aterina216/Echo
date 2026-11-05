package com.example.echo

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.echo.ui.components.ThemeOptionRow
import com.example.echo.ui.theme.EchoTheme
import com.example.echo.ui.theme.EchoThemeOption
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ThemeOptionRowTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private fun setContent(
        option: EchoThemeOption = EchoThemeOption.BLUE,
        isSelected: Boolean = false,
        onClick: () -> Unit = {}
    ) {
        rule.setContent {
            EchoTheme(themeOption = option) {
                ThemeOptionRow(
                    option = option,
                    isSelected,
                    onClick
                )
            }
        }
    }

    @Test
    fun displayName_isShownExactlyOne(){
        setContent()

        rule.onAllNodesWithText(EchoThemeOption.BLUE.displayName)
            .assertCountEquals(1)
    }

    @Test
    fun displayName_isDisplayed(){
        setContent()

        rule.onAllNodesWithText(EchoThemeOption.BLUE.displayName)
            .onFirst()
            .assertIsDisplayed()
    }

    @Test
    fun radioButton_isOffWhenNotSelected(){
        setContent(isSelected = false)

        rule.onNodeWithText(EchoThemeOption.BLUE.displayName)
            .assertIsNotSelected()
    }

    @Test
    fun radioButton_isOnWhenSelected(){
        setContent(isSelected = true)

        rule.onNodeWithText(EchoThemeOption.BLUE.displayName)
            .assertIsSelected()
    }

    @Test
    fun clickingRow_callsOnClick(){
        var clicked = false
        setContent(onClick = {clicked = true})

        rule.onAllNodesWithText(EchoThemeOption.BLUE.displayName)
            .onFirst()
            .performClick()

        assertTrue(clicked)
    }

    @Test
    fun clickingRadioButton_callsOnClick(){
        var clicked = false
        setContent(onClick = {clicked = true})

        rule.onNodeWithText(EchoThemeOption.BLUE.displayName)
            .performClick()
        assertTrue(clicked)
    }

    @Test
    fun clickingRow_doesNotDoubleCallOnClick(){
        var clickCount = 0
        setContent(onClick = {clickCount++})

        rule.onAllNodesWithText(EchoThemeOption.BLUE.displayName)
            .onFirst()
            .performClick()

        assertTrue("Ожидался 1 клик, получено $clickCount", clickCount == 1)
    }
}