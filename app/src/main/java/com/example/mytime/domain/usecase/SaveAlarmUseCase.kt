package com.example.mytime.domain.usecase

import com.example.mytime.domain.model.Alarm
import com.example.mytime.domain.repository.AlarmRepository

class SaveAlarmUseCase(
    private val repository: AlarmRepository
) {
    suspend operator fun invoke(alarm: Alarm): Result<Long> {
        return repository.saveAlarm(alarm)
    }
}