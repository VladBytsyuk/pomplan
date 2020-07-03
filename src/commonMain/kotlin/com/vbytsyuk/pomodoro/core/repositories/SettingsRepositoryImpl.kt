package com.vbytsyuk.pomodoro.core.repositories

import com.vbytsyuk.pomodoro.core.api.SettingsRepository
import com.vbytsyuk.pomodoro.core.domain.pomodoroTime


class SettingsRepositoryImpl : SettingsRepository {
    override suspend fun getWorkTime() = pomodoroTime(minutes = 0, seconds = 5)
    override suspend fun getShortBreakTime() = pomodoroTime(minutes = 0, seconds = 2)
    override suspend fun getLongBreakTime() = pomodoroTime(minutes = 0, seconds = 4)
    override suspend fun getSessionLength() = 4
}
