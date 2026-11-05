package com.example.echo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.echo.data.prefs.PrefsKeys
import com.example.echo.data.repository.SettingsRepository
import com.example.echo.ui.theme.EchoThemeOption
import com.example.echo.ui.theme.VisualizationMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.test.DefaultAsserter.assertTrue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue


@OptIn(ExperimentalCoroutinesApi::class)
class SettingsRepositoryTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private lateinit var scope: CoroutineScope
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: SettingsRepository

    @Before
    fun setup() {
        scope = CoroutineScope(UnconfinedTestDispatcher())
        dataStore = PreferenceDataStoreFactory.create(
            scope = scope,
            produceFile = { File (tmp.root, "test.preferences_pb")}
        )
        repository = SettingsRepository(dataStore)
    }

    @After
    fun teardown() {
        scope.cancel()
    }

    @Test
    fun `themeFlow is null when nothing saved`() = runTest {
        assertNull(repository.themeFlow.first())
    }

    @Test
    fun `visualizationModeFlow is null when nothing saved`() = runTest {
        assertNull(repository.visualizationModeFlow.first())
    }

    @Test
    fun `saveTheme persists theme`() = runTest {
        repository.saveTheme(EchoThemeOption.PINK)

        assertEquals(EchoThemeOption.PINK, repository.themeFlow.first())
    }

    @Test
    fun `saveTheme overwrites previous value`() =runTest {
        repository.saveTheme(EchoThemeOption.GREEN)
        repository.saveTheme(EchoThemeOption.RED)

        assertEquals(EchoThemeOption.RED, repository.themeFlow.first())
    }

    @Test
    fun `saveTheme does not touch visualization mode`() = runTest {
        repository.saveVisualizationMode(VisualizationMode.BARS)
        repository.saveTheme(EchoThemeOption.GREEN)

        assertEquals(VisualizationMode.BARS, repository.visualizationModeFlow.first())
    }

    @Test
    fun `saveVisualizationMode persists mode`() = runTest {
        repository.saveVisualizationMode(VisualizationMode.CIRCLE)

        assertEquals(VisualizationMode.CIRCLE, repository.visualizationModeFlow.first())
    }

    @Test
    fun `saveVisualizationMode overwrites previous value`() = runTest {
        repository.saveVisualizationMode(VisualizationMode.WAVE)
        repository.saveVisualizationMode(VisualizationMode.BARS)

        assertEquals(VisualizationMode.BARS, repository.visualizationModeFlow.first())
    }

    @Test
    fun `saveVisualizationMode does not touch theme`() = runTest {
        repository.saveTheme(EchoThemeOption.PINK)
        repository.saveVisualizationMode(VisualizationMode.CIRCLE)

        assertEquals(EchoThemeOption.PINK, repository.themeFlow.first())
    }

    @Test
    fun `both values persist independently`() = runTest {
        repository.saveTheme(EchoThemeOption.GREEN)
        repository.saveVisualizationMode(VisualizationMode.BARS)

        assertEquals(EchoThemeOption.GREEN, repository.themeFlow.first())
        assertEquals(VisualizationMode.BARS, repository.visualizationModeFlow.first())
    }

    @Test
    fun `unknown theme name in prefs return null`() = runTest {
        dataStore.edit { prefs ->
            prefs[PrefsKeys.THEME] = "NOT_A_REAL_THEME"
        }
        assertNull(repository.themeFlow.first())
    }

    @Test
    fun `saveTheme writes to disk`() = runTest {
        repository.saveTheme(EchoThemeOption.PINK)

        val file = File(tmp.root, "test.preferences_pb")
        assertTrue("Файл настроек должен быть создан", file.exists())
        assertTrue("Файл настроек не должен быть пустым", file.length() > 0)
    }

    @Test
    fun `values survive new repository instance`() = runBlocking {
        val file = File(tmp.root, "restart.preferences_pb")

        val scope1 = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val ds1 = PreferenceDataStoreFactory.create(
            scope = scope1,
            produceFile = {file}
        )
        val repo1 = SettingsRepository(ds1)
        repo1.saveTheme(EchoThemeOption.PINK)
        repo1.saveVisualizationMode(VisualizationMode.CIRCLE)
        ds1.data.first()

        scope1.cancel()
        delay(200)

        val scope2 = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val ds2 = PreferenceDataStoreFactory.create(
            scope = scope2,
            produceFile = {file}
        )
        val repo2 = SettingsRepository(ds2)

        assertEquals(EchoThemeOption.PINK, repo2.themeFlow.first())
        assertEquals(VisualizationMode.CIRCLE, repo2.visualizationModeFlow.first())

        scope2.cancel()
    }
}