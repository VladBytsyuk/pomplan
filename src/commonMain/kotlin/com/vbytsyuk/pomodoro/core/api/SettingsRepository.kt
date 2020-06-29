package com.vbytsyuk.pomodoro.core.api

import com.vbytsyuk.pomodoro.core.domain.PomodoroTime


interface SettingsRepository {
    suspend fun getWorkTime(): PomodoroTime
    suspend fun getShortBreakTime(): PomodoroTime
    suspend fun getLongBreakTime(): PomodoroTime
    suspend fun getSessionLength(): Int
}
