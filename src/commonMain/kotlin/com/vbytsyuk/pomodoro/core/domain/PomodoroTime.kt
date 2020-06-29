package com.vbytsyuk.pomodoro.core.domain


data class PomodoroTime(val timestamp: Long) {

    val minutes: Long get() = timestamp / MILLIS_IN_MINUTE
    val seconds: Long get() = timestamp - timestamp % MILLIS_IN_MINUTE / MILLIS_IN_SECOND
    val milliseconds: Long get() = timestamp % MILLIS_IN_SECOND

    fun addMinutes(n: Int): PomodoroTime = this.copy(timestamp = timestamp + n * MILLIS_IN_MINUTE)
    fun addMinute(n: Int): PomodoroTime = this.addMinutes(n = 1)
    fun takeMinutes(n: Int): PomodoroTime = this.copy(timestamp = timestamp - n * MILLIS_IN_MINUTE)
    fun takeMinute(n: Int): PomodoroTime = this.takeMinutes(n = 1)


    fun addSeconds(n: Int): PomodoroTime = this.copy(timestamp = timestamp + n * MILLIS_IN_SECOND)
    fun addSecond(n: Int): PomodoroTime = this.addSecond(n = 1)
    fun takeSeconds(n: Int): PomodoroTime = this.copy(timestamp = timestamp - n * MILLIS_IN_SECOND)
    fun takeSecond(n: Int): PomodoroTime = this.takeSecond(n = 1)


    fun addMilliseconds(n: Int): PomodoroTime = this.copy(timestamp = timestamp + n)
    fun addMillisecond(n: Int): PomodoroTime = this.addMilliseconds(n = 1)
    fun takeMilliseconds(n: Int): PomodoroTime = this.copy(timestamp = timestamp - n)
    fun takeMillisecond(n: Int): PomodoroTime = this.takeMilliseconds(n = 1)
}

fun PomodoroTime(minutes: Int, seconds: Int = 0): PomodoroTime {
    require(minutes >= 0) { "Minutes can't be negative" }
    require(seconds in 0..59) { "Seconds should be in [0..59]" }
    return PomodoroTime(timestamp = minutes * MILLIS_IN_MINUTE + seconds * MILLIS_IN_SECOND)
}
