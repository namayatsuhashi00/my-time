package com.example.mytime.domain.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

data class Alarm(
    val id: Long = 0,
    val time: LocalTime,
    val repeatDays: Set<DayOfWeek> = emptySet(),
    val specificDate: LocalDate? = null,
    val isEnabled: Boolean = true,
    val audioOutputMode: AudioOutputMode = AudioOutputMode.SPEAKER_ONLY,
    val snoozeOption: SnoozeOption = SnoozeOption.FiveMinutes,
    val isVibrationEnabled: Boolean = true,
    val soundUri: String? = null
)