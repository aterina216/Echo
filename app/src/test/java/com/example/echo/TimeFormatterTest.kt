package com.example.echo

import com.example.echo.util.TimeFormatter
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeFormatterTest {

    @Test
    fun `zero formats as 00 colon 00`(){
        assertEquals("00:00", TimeFormatter.formatTime(0))
    }

    @Test
    fun `one second`(){
        assertEquals("00:01", TimeFormatter.formatTime(1_000))
    }

    @Test
    fun `less than one second floors to zero`(){
        assertEquals("00:00", TimeFormatter.formatTime(999))
    }

    @Test
    fun `fifty nine seconds`(){
        assertEquals("00:59", TimeFormatter.formatTime(59_000))
    }

    @Test
    fun `half a second floors to zero`(){
        assertEquals("00:00", TimeFormatter.formatTime(500))
    }

    @Test
    fun `one minute exactly`(){
        assertEquals("01:00", TimeFormatter.formatTime(60_000))
    }

    @Test
    fun `one minute one second`(){
        assertEquals("01:01", TimeFormatter.formatTime(61_000))
    }

    @Test
    fun `five minutes`(){
        assertEquals("05:00", TimeFormatter.formatTime(5 * 60_000))
    }

    @Test
    fun `sixty minutes stays in minutes`(){
        assertEquals("60:00", TimeFormatter.formatTime(60*60_000))
    }

    @Test
    fun `large duration does not break`(){
        assertEquals("999:59", TimeFormatter.formatTime(999*60_000+59_000))
    }

    @Test
    fun `milliseconds are truncated not rounded`(){
        assertEquals("01:01", TimeFormatter.formatTime(61_999))
    }

    @Test
    fun `negative milliseconds produces implementation-defined output`(){
        val result = TimeFormatter.formatTime(-1_000)
        assertEquals("00:-1", result)
    }
}