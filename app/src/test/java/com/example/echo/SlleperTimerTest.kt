package com.example.echo

import com.example.echo.util.SleepTimer
import junit.framework.TestCase
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SlleperTimerTest {

    private val minuteMs = 60_000L
    private lateinit var scheduler: TestCoroutineScheduler
    private lateinit var dispatcher: TestDispatcher
    private lateinit var timer: SleepTimer

    @Before
    fun setup() {
        scheduler = TestCoroutineScheduler()
        dispatcher = StandardTestDispatcher(scheduler)
        timer = SleepTimer(dispatcher)
    }

    @Test
    fun `initial remaining is zero` () = runTest {

        TestCase.assertEquals(0L, timer.remainings.value)
    }

    @Test
    fun `start immediately sets remaining to full duration`() = runTest {
        timer.start(5) {}

        TestCase.assertEquals(5 * minuteMs, timer.remainings.value)
        timer.cancel()
    }

    @Test
    fun `start counts down by tick`() = runTest(dispatcher) {
        timer.start(1) {}
        advanceTimeBy(1_600)
        TestCase.assertEquals(minuteMs - 1_500L, timer.remainings.value)
        timer.cancel()
    }

    @Test
    fun `onFinish is called exactly once after full duration`() = runTest(dispatcher) {
        var count = 0

        timer.start(1) { count++ }
        advanceTimeBy(minuteMs + 1)

        TestCase.assertEquals(1, count)
        TestCase.assertEquals(0L, timer.remainings.value)
    }

    @Test
    fun `onFinish is not called before duration`() = runTest(dispatcher) {
        var called = false
        timer.start(1) { called = true }
        advanceTimeBy(30_000)
        TestCase.assertFalse(called)
        timer.cancel()
    }

    @Test
    fun `cancel resets remaining and prevents onFinish`() = runTest(dispatcher) {
        var called = false
        timer.start(5) { called = true }
        advanceTimeBy(10_000)
        timer.cancel()
        advanceTimeBy(5 * minuteMs + 1)
        TestCase.assertFalse(called)
        TestCase.assertEquals(0L, timer.remainings.value)
    }

    @Test
    fun `cancel when not started is safe`() = runTest {
        timer.cancel()
        TestCase.assertEquals(0L, timer.remainings.value)
    }

    @Test
    fun `cancel after finish does not call onFinish again`() = runTest(dispatcher) {
        var count = 0
        timer.start(1) { count++ }
        advanceTimeBy(minuteMs + 1)
        timer.cancel()
        advanceTimeBy(10_000)
        TestCase.assertEquals(1, count)
    }

    @Test
    fun `second start replaces first countdown`() = runTest(dispatcher) {
        var firstCalled = false
        var secondCalled = false

        timer.start(1) { firstCalled = true }
        advanceTimeBy(10_000)
        timer.start(10) { secondCalled = true }
        advanceTimeBy(minuteMs + 1)
        TestCase.assertFalse("старый колбэк не должен сработать", firstCalled)
        TestCase.assertFalse("новый ещё не должен сработать", secondCalled)

        assertTrue(timer.remainings.value > 8 * minuteMs)
        timer.cancel()
    }

    @Test
    fun `second start eventually finishes`() = runTest(dispatcher) {
        var called = false
        timer.start(1) {}
        advanceTimeBy(30_000)
        timer.start(1) { called = true }
        advanceTimeBy(minuteMs + 1)
        assertTrue(called)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `start with zero throws` () = runTest {
        timer.start(0) {}
    }

    @Test(expected = IllegalArgumentException::class)
    fun `start with negative throws`() = runTest {
        timer.start(-5) {}
    }
}