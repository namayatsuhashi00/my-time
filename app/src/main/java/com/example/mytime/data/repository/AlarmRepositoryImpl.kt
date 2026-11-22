package com.example.mytime.data.repository

import com.example.mytime.data.local.AlarmDao
import com.example.mytime.data.local.toDomainModel
import com.example.mytime.data.local.toEntity
import com.example.mytime.domain.model.Alarm
import com.example.mytime.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlarmRepositoryImpl(
    private val alarmDao: AlarmDao
) : AlarmRepository {

    override fun getAllAlarms(): Flow<List<Alarm>> {
        return alarmDao.getAllAlarms().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getAlarmCount(): Int {
        return alarmDao.getAlarmCount()
    }

    override suspend fun getAlarmById(id: Long): Alarm? {
        return alarmDao.getAlarmById(id)?.toDomainModel()
    }

    override suspend fun saveAlarm(alarm: Alarm): Result<Long> {
        if (alarm.id == 0L && alarmDao.getAlarmCount() >= 30) {
            return Result.failure(IllegalStateException("登録上限（30件）に達しています"))
        }
        val id = alarmDao.insertAlarm(alarm.toEntity())
        return Result.success(id)
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        alarmDao.deleteAlarm(alarm.toEntity())
    }
}