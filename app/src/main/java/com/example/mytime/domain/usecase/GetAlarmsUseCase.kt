package com.example.mytime.domain.usecase

import com.example.mytime.domain.model.Alarm
import com.example.mytime.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow

class GetAlarmsUseCase(
    private val repository: AlarmRepository
) {
    operator fun invoke(): Flow<List<Alarm>> = repository.getAllAlarms()
}