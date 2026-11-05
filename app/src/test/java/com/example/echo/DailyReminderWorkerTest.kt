package com.example.echo

import android.Manifest
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.echo.service.DailyReminderWorker
import com.example.echo.util.notifyPermission
import com.example.echo.util.reminderNotification
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runner.manipulation.Ordering
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import kotlin.test.DefaultAsserter.assertNotNull
import kotlin.test.DefaultAsserter.assertTrue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class DailyReminderWorkerTest {

    private lateinit var context: Application
    private lateinit var manager: NotificationManager

    @Before
    fun setup(){
        context = ApplicationProvider.getApplicationContext()
        manager = context.getSystemService(NotificationManager::class.java)
        manager.cancelAll()

        val channel = NotificationChannel(
            DailyReminderWorker.CHANNEL_ID,
            "Daily Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)
        mockkStatic(::notifyPermission)
        mockkStatic(::reminderNotification)

    }

    @After
    fun tearDown(){
        unmockkStatic(::notifyPermission)
        unmockkStatic(::reminderNotification)
    }

    private fun buildWorker(): DailyReminderWorker =
        TestListenableWorkerBuilder<DailyReminderWorker>(context).build()

    private fun postedIds(): List<Int> =
        manager.activeNotifications.map { it.id }

    private fun stubNotification(){
        every { reminderNotification(any(), any()) } returns mockk<Notification>(relaxed = true)
    }

    @Test
    fun `when permission granted, notification is posted`() = runBlocking {
        shadowOf(context).grantPermissions(Manifest.permission.POST_NOTIFICATIONS)

        val result = buildWorker().doWork()
        assertEquals(ListenableWorker.Result.success(), result)

        val ids = postedIds()
        assertTrue(
            "Ожидалось уведомление с id${DailyReminderWorker.NOTIFICATION_ID}, получено: $ids",
            ids.contains(DailyReminderWorker.NOTIFICATION_ID)
        )
    }

    @Test
    fun `whem permission denied, no notification and success`() = runBlocking {
        shadowOf(context).denyPermissions(Manifest.permission.POST_NOTIFICATIONS)

        val result = buildWorker().doWork()

        assertEquals(ListenableWorker.Result.success(), result)

        val ids = postedIds()
        assertTrue(
            "Уведомлений быть не должно, но найдены: $ids",
            ids.isEmpty()
        )
    }

    @Test
    fun `when notification creation throws returns failure`() = runBlocking {
        every { notifyPermission(any()) } returns true
        every { reminderNotification(any(), any()) } throws RuntimeException("boom")

        val result = buildWorker().doWork()

        assertEquals(ListenableWorker.Result.failure(), result)
    }
}