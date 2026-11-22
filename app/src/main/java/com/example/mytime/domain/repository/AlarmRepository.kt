package com.example.mytime.domain.repository

import com.example.mytime.domain.model.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun getAllAlarms(): Flow<List<Alarm>>
    suspend fun getAlarmCount(): Int
    suspend fun getAlarmById(id: Long): Alarm?
    suspend fun saveAlarm(alarm: Alarm): Result<Long>
    suspend fun deleteAlarm(alarm: Alarm)
}