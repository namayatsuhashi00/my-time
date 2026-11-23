package com.example.mytime.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytime.domain.model.Alarm
import com.example.mytime.domain.usecase.GetAlarmsUseCase
import com.example.mytime.domain.usecase.SaveAlarmUseCase
import com.example.mytime.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainUiState(
    val alarms: List<Alarm> = emptyList(),
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val errorMessage: String? = null
)

class MainViewModel(
    private val getAlarmsUseCase: GetAlarmsUseCase,
    private val saveAlarmUseCase: SaveAlarmUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadAlarms()
    }

    private fun loadAlarms() {
        viewModelScope.launch {
            getAlarmsUseCase().collect { alarmList ->
                _uiState.value = _uiState.value.copy(alarms = alarmList)
            }
        }
    }

    fun toggleAlarm(alarm: Alarm) {
        viewModelScope.launch {
            val updated = alarm.copy(isEnabled = !alarm.isEnabled)
            saveAlarmUseCase(updated)
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        _uiState.value = _uiState.value.copy(themeMode = mode)
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    fun saveAlarm(alarm: Alarm) {
        viewModelScope.launch {
            saveAlarmUseCase(alarm)
        }
    }
}