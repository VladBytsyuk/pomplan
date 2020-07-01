package com.vbytsyuk.pomodoro.core.repositories

import com.vbytsyuk.pomodoro.core.api.SettingsRepository
import com.vbytsyuk.pomodoro.core.domain.PomodoroTime


class SettingsRepositoryImpl : SettingsRepository {
    override suspend fun getWorkTime() = PomodoroTime(minutes = 0, seconds = 25)
    override suspend fun getShortBreakTime() = PomodoroTime(minutes = 0, seconds = 5)
    override suspend fun getLongBreakTime() = PomodoroTime(minutes = 0, seconds = 15)
    override suspend fun getSessionLength() = 4
}
