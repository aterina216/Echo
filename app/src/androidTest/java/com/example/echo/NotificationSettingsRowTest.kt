package com.example.echo

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.echo.ui.components.NotificationSettingsRow
import com.example.echo.ui.theme.EchoTheme
import com.example.echo.ui.theme.EchoThemeOption
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.isToggleable


@RunWith(AndroidJUnit4::class)
class NotificationSettingsRowTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private fun setContent(
        notificationsEnabled: Boolean = true,
        onClick: () -> Unit = {}
    ) {
        rule.setContent {
            EchoTheme(themeOption = EchoThemeOption.BLUE) {
                NotificationSettingsRow(
                    notificationsEnabled = notificationsEnabled,
                    onClick = onClick
                )
            }
        }
    }

    @Test
    fun enabledText_isDisplayedWhenEnabled(){
        setContent(notificationsEnabled = true)
        rule.onNodeWithText("Включены").assertIsDisplayed()
    }

    @Test
    fun disabledText_isDisplayedWhenDisabled(){
        setContent(notificationsEnabled = false)
        rule.onNodeWithText("Отключены - нажмите, чтобы открыть настройки")
            .assertIsDisplayed()
    }

    @Test
    fun enabledText_isNotDisplayedWhenDisabled(){
        setContent(notificationsEnabled = false)
        rule.onNodeWithText("Включены").assertDoesNotExist()
    }

    @Test
    fun disabledText_isNotDisplayedWhenEnabled(){
        setContent(notificationsEnabled = true)
        rule.onNodeWithText("Отключены - нажмите, чтобы открыть настройки")
            .assertDoesNotExist()
    }

    @Test
    fun switch_isOnWhenEnabled(){
        setContent(notificationsEnabled = true)
        rule.onNode(isToggleable()).assertIsOn()
    }

    @Test
    fun switch_isOffWhenDisabled(){
        setContent(notificationsEnabled = false)
        rule.onNode(isToggleable()).assertIsOff()
    }

    @Test
    fun clickingRow_callsOnClick(){
        var clicked = false
        setContent(onClick = {clicked = true})

        rule.onAllNodesWithText("Включены")
            .onFirst()
            .performClick()

        assertTrue(clicked)
    }

    @Test
    fun clickingRow_whenDisabled_callsOnClick() {
        var clicked = false
        setContent(notificationsEnabled = false, onClick = {clicked = true})

        rule.onAllNodesWithText("Отключены - нажмите, чтобы открыть настройки")
            .onFirst()
            .performClick()

        assertTrue(clicked)
    }

    @Test
    fun textChanges_whenStateChanges(){
        setContent(notificationsEnabled = true)

        rule.onNodeWithText("Включены").assertIsDisplayed()

        rule.runOnUiThread {  }
    }
}