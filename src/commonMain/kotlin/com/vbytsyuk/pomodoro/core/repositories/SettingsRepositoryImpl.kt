package com.vbytsyuk.pomodoro.core.repositories

import com.vbytsyuk.pomodoro.core.api.SettingsRepository
import com.vbytsyuk.pomodoro.core.domain.PomodoroTime


class SettingsRepositoryImpl : SettingsRepository {
    override suspend fun getWorkTime() = PomodoroTime(minutes = 25)
    override suspend fun getShortBreakTime() = PomodoroTime(minutes = 5)
    override suspend fun getLongBreakTime() = PomodoroTime(minutes = 15)
    override suspend fun getSessionLength() = 4
}
