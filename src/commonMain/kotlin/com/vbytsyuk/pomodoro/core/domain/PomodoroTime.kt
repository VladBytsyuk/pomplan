package com.vbytsyuk.pomodoro.core.domain


data class PomodoroTime(val timestamp: Long) {

    val minutes: Long get() = timestamp / MILLIS_IN_MINUTE
    val seconds: Long get() = (timestamp - minutes * MILLIS_IN_MINUTE) / MILLIS_IN_SECOND
    val milliseconds: Long get() = timestamp % MILLIS_IN_SECOND

    private fun takeSeconds(n: Int): PomodoroTime = this.copy(timestamp = timestamp - n * MILLIS_IN_SECOND)
    fun takeSecond(): PomodoroTime = this.takeSeconds(n = 1)

    operator fun compareTo(other: PomodoroTime): Int = this.timestamp.compareTo(other.timestamp)
    operator fun compareTo(other: Int): Int = this.timestamp.compareTo(other.toLong())

    override fun toString(): String = "$minutes:${formatSecond(seconds)}"
    private fun formatSecond(component: Long) = if (component < 10) "0$component" else "$component"
}

fun pomodoroTime(minutes: Int = 0, seconds: Int = 0): PomodoroTime {
    require(minutes >= 0) { "Minutes can't be negative" }
    require(seconds in 0..59) { "Seconds should be in [0..59]" }
    return PomodoroTime(timestamp = minutes * MILLIS_IN_MINUTE + seconds * MILLIS_IN_SECOND)
}
