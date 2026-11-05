package com.example.echo

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.echo.ui.components.TimerDialog
import com.example.echo.ui.theme.EchoTheme
import com.example.echo.ui.theme.EchoThemeOption
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimerDialogtest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private var startedWith: Int? = null
    private var dismissed = false

    @Before
    fun setup(){
        startedWith = null
        dismissed = false
    }

    private fun setContent(){
        rule.setContent {
            EchoTheme(themeOption = EchoThemeOption.BLUE) {
                TimerDialog(
                    onDismiss = { dismissed = true },
                    onStart = { startedWith = it }
                )
            }
        }
    }

    private fun textField() = rule.onNode(hasSetTextAction())

    @Test
    fun title_isDisplayed(){
        setContent()
        rule.onNodeWithText("Таймер сна").assertIsDisplayed()
    }

    @Test
    fun hint_isDisplayed(){
        setContent()
        rule.onNodeWithText("Введите количество минут").assertIsDisplayed()
    }

    @Test
    fun cancelButton_isDisplayed(){
        setContent()
        rule.onNodeWithText("Отмена").assertIsDisplayed()
    }

    @Test
    fun confirmButton_isDisplayed(){
        setContent()
        rule.onNodeWithText("Установить").assertIsDisplayed()
    }

    @Test
    fun placeholder_isShownWhenEmpty(){
        setContent()
        rule.onNodeWithText("0").assertIsDisplayed()
    }

    @Test
    fun placaholder_hiddenAfterTyping(){
        setContent()
        textField().performTextInput("5")
        rule.onNodeWithText("0").assertDoesNotExist()
    }

    @Test
    fun confirmButton_isDisableWhenInputEmpty(){
        setContent()
        rule.onNodeWithText("Установить").assertIsNotEnabled()
    }

    @Test
    fun confirmButton_isEnabledAfterTypingDigits(){
        setContent()
        textField().performTextInput("5")
        rule.onNodeWithText("Установить").assertIsEnabled()
    }

    @Test
    fun confirmButton_isEnableWithZeroInput(){
        setContent()
        textField().performTextInput("0")
        rule.onNodeWithText("Установить").assertIsEnabled()
    }

    @Test
    fun clickingConfirm_callsOnStartAndDismiss(){
        setContent()
        textField().performTextInput("5")
        rule.onNodeWithText("Установить").performClick()

        assertEquals(5, startedWith)
        assertTrue(dismissed)
    }

    @Test
    fun clickingConfirm_passesLargeNumbers(){
        setContent()
        textField().performTextInput("120")
        rule.onNodeWithText("Установить").performClick()

        assertEquals(120, startedWith)
        assertTrue(dismissed)
    }

    @Test
    fun zeroInput_doesNotCallOnStart(){
        setContent()
        textField().performTextInput("0")
        rule.onNodeWithText("Установить").performClick()

        assertNull(startedWith)
        assertFalse(dismissed)
    }

    @Test
    fun clickingCancel_callsDismissWithoutStart(){
        setContent()
        rule.onNodeWithText("Отмена").performClick()

        assertNull(startedWith)
        assertTrue(dismissed)
    }

    @Test
    fun clickingCancelAfterTyping_stillDoesNotStart(){
        setContent()
        textField().performTextInput("5")
        rule.onNodeWithText("Отмена").performClick()

        assertNull(startedWith)
        assertTrue(dismissed)
    }

    @Test
    fun letters_areRejectedEntirely(){
        setContent()
        textField().performTextInput("abc")

        rule.onNodeWithText("Установить").assertIsNotEnabled()
        rule.onNodeWithText("0").assertIsDisplayed()
    }

    @Test
    fun mixedInput_withLetters_rejectedEntirely(){
        setContent()
        textField().performTextInput("5a")

        rule.onNodeWithText("Установить").assertIsNotEnabled()
    }

    @Test
    fun letters_afterDigits_doNotAppend(){
        setContent()
        textField().performTextInput("5")
        textField().performTextInput("a")

        rule.onNodeWithText("Установить").assertIsEnabled()
    }

    @Test
    fun specialCharacters_areRejected(){
        setContent()
        textField().performTextInput("-5")

        rule.onNodeWithText("Установить").assertIsNotEnabled()
    }
}