package com.vbytsyuk.pomodoro.core

import com.vbytsyuk.pomodoro.core.domain.PomodoroTime
import kotlin.test.Test
import kotlin.test.assertEquals


class `Pomodoro time` {

    @Test
    fun `Time with simple timestamp`() = test(
        time = PomodoroTime(timestamp = 600_000L),
        minutes = 10
    )

    @Test
    fun `Time with complex timestamp`() = test(
        time = PomodoroTime(timestamp = 612_256L),
        minutes = 10,
        seconds = 12,
        milliseconds = 256
    )

    @Test
    fun `Time simple`() = test(
        time = PomodoroTime(minutes = 25),
        minutes = 25
    )

    @Test
    fun `Time complex`() = test(
        time = PomodoroTime(minutes = 6, seconds = 57),
        minutes = 6,
        seconds = 57
    )


    private fun test(
        time: PomodoroTime,
        minutes: Long,
        seconds: Long = 0,
        milliseconds: Long = 0
    ) {
        assertEquals(expected = minutes, actual = time.minutes, message = "Minute")
        assertEquals(expected = seconds, actual = time.seconds, message = "Second")
        assertEquals(expected = milliseconds, actual = time.milliseconds, message = "Millis")
    }
}
