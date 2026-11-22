package com.example.mytime.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mytime.domain.model.Alarm
import com.example.mytime.domain.model.AudioOutputMode
import com.example.mytime.domain.model.SnoozeOption
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val repeatDaysMask: Int,
    val specificDateEpochDay: Long?,
    val isEnabled: Boolean,
    val audioOutputModeName: String,
    val snoozeType: String,
    val snoozeValue: Int,
    val isVibrationEnabled: Boolean,
    val soundUri: String?
)

fun AlarmEntity.toDomainModel(): Alarm {
    val days = DayOfWeek.entries.filter { (repeatDaysMask and (1 shl it.ordinal)) != 0 }.toSet()
    val date = specificDateEpochDay?.let { LocalDate.ofEpochDay(it) }
    val audioMode = AudioOutputMode.valueOf(audioOutputModeName)
    val snooze = when (snoozeType) {
        "TEN" -> SnoozeOption.TenMinutes
        "CUSTOM_MIN" -> SnoozeOption.CustomMinutes(snoozeValue)
        "CUSTOM_HOUR" -> SnoozeOption.CustomHours(snoozeValue)
        else -> SnoozeOption.FiveMinutes
    }

    return Alarm(
        id = id,
        time = LocalTime.of(hour, minute),
        repeatDays = days,
        specificDate = date,
        isEnabled = isEnabled,
        audioOutputMode = audioMode,
        snoozeOption = snooze,
        isVibrationEnabled = isVibrationEnabled,
        soundUri = soundUri
    )
}

fun Alarm.toEntity(): AlarmEntity {
    var mask = 0
    repeatDays.forEach { mask = mask or (1 shl it.ordinal) }
    val (snoozeTypeStr, snoozeVal) = when (val s = snoozeOption) {
        is SnoozeOption.FiveMinutes -> "FIVE" to 5
        is SnoozeOption.TenMinutes -> "TEN" to 10
        is SnoozeOption.CustomMinutes -> "CUSTOM_MIN" to s.minutes
        is SnoozeOption.CustomHours -> "CUSTOM_HOUR" to s.hours
    }

    return AlarmEntity(
        id = id,
        hour = time.hour,
        minute = time.minute,
        repeatDaysMask = mask,
        specificDateEpochDay = specificDate?.toEpochDay(),
        isEnabled = isEnabled,
        audioOutputModeName = audioOutputMode.name,
        snoozeType = snoozeTypeStr,
        snoozeValue = snoozeVal,
        isVibrationEnabled = isVibrationEnabled,
        soundUri = soundUri
    )
}