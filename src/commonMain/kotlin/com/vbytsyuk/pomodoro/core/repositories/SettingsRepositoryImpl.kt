package com.vbytsyuk.pomodoro.core.repositories

import com.vbytsyuk.pomodoro.core.api.SettingsRepository
import com.vbytsyuk.pomodoro.core.domain.pomodoroTime


class SettingsRepositoryImpl : SettingsRepository {
    override suspend fun getWorkTime() = pomodoroTime(seconds = 5)
    override suspend fun getShortBreakTime() = pomodoroTime(seconds = 2)
    override suspend fun getLongBreakTime() = pomodoroTime(seconds = 4)
    override suspend fun getSessionLength() = 4
}
