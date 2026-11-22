package com.example.mytime.domain.model

sealed class SnoozeOption {
    data object FiveMinutes : SnoozeOption()
    data object TenMinutes : SnoozeOption()
    data class CustomMinutes(val minutes: Int) : SnoozeOption()
    data class CustomHours(val hours: Int) : SnoozeOption()
}