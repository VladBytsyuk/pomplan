package com.vbytsyuk.pomodoro.core.domain


const val MILLIS_IN_SECOND = 1000L
const val SECONDS_IN_MINUTE = 60L
const val MINUTES_IN_HOUR = 60L
const val HOURS_IN_DAY = 24L

const val MILLIS_IN_MINUTE = MILLIS_IN_SECOND * SECONDS_IN_MINUTE
const val MILLIS_IN_HOUR = MILLIS_IN_MINUTE * MINUTES_IN_HOUR
const val MILLIS_IN_DAY = MILLIS_IN_HOUR * HOURS_IN_DAY

const val SECONDS_IN_HOUR = SECONDS_IN_MINUTE * MINUTES_IN_HOUR
const val SECONDS_IN_DAY = SECONDS_IN_HOUR * HOURS_IN_DAY

const val MINUTES_IN_DAY = MINUTES_IN_HOUR * HOURS_IN_DAY


val Int.millisecond: Long get() = this.toLong()
val Int.milliseconds: Long get() = millisecond

val Int.second: Long get() = this * MILLIS_IN_SECOND
val Int.seconds: Long get() = second
val Double.seconds: Long get() = (this * MILLIS_IN_SECOND).toLong()

val Int.minute: Long get() = this * MILLIS_IN_MINUTE
val Int.minutes: Long get() = minute
val Double.minutes: Long get() = (this * MILLIS_IN_MINUTE).toLong()

val Int.hour: Long get() = this * MILLIS_IN_HOUR
val Int.hours: Long get() = hour
val Double.hours: Long get() = (this * MILLIS_IN_HOUR).toLong()

val Int.day: Long get() = this * MILLIS_IN_DAY
val Int.days: Long get() = day
val Double.days: Long get() = (this * MILLIS_IN_DAY).toLong()
