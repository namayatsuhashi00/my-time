package com.example.mytime.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mytime.domain.model.Alarm
import com.example.mytime.domain.model.AudioOutputMode
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MainScreen(
    uiState: MainUiState,
    onToggleAlarm: (Alarm) -> Unit,
    onSaveAlarm: (Alarm) -> Unit = {}
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "アラームを追加"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "My Time",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.alarms) { alarm ->
                    AlarmItemCard(alarm = alarm, onToggle = { onToggleAlarm(alarm) })
                }
            }
        }

        if (showAddDialog) {
            AddAlarmDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { newAlarm ->
                    onSaveAlarm(newAlarm)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AlarmItemCard(
    alarm: Alarm,
    onToggle: () -> Unit
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = alarm.time.format(timeFormatter),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "出力: ${if (alarm.audioOutputMode == AudioOutputMode.SPEAKER_ONLY) "スピーカー固定" else "Bluetooth優先"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Switch(
                checked = alarm.isEnabled,
                onCheckedChange = { onToggle() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddAlarmDialog(
    onDismiss: () -> Unit,
    onConfirm: (Alarm) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = LocalTime.now().hour,
        initialMinute = LocalTime.now().minute
    )
    var selectedAudioMode by remember { mutableStateOf(AudioOutputMode.SPEAKER_ONLY) }
    var selectedDays by remember { mutableStateOf(setOf<DayOfWeek>()) }
    var isVibrationEnabled by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "アラームを追加") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 時刻選択ピッカー
                TimePicker(state = timePickerState)

                Spacer(modifier = Modifier.height(16.dp))

                // 繰り返し曜日選択
                Text(
                    text = "繰り返し曜日",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Start)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DayOfWeek.values().forEach { day ->
                        val isSelected = selectedDays.contains(day)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedDays = if (isSelected) {
                                    selectedDays - day
                                } else {
                                    selectedDays + day
                                }
                            },
                            label = {
                                Text(text = day.getDisplayName(TextStyle.SHORT, Locale.JAPANESE))
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 音声出力先設定
                Text(
                    text = "音声出力先",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Start)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedAudioMode = AudioOutputMode.SPEAKER_ONLY }
                ) {
                    RadioButton(
                        selected = selectedAudioMode == AudioOutputMode.SPEAKER_ONLY,
                        onClick = { selectedAudioMode = AudioOutputMode.SPEAKER_ONLY }
                    )
                    Text("スピーカー固定")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedAudioMode = AudioOutputMode.BLUETOOTH_PREFERRED }
                ) {
                    RadioButton(
                        selected = selectedAudioMode == AudioOutputMode.BLUETOOTH_PREFERRED,
                        onClick = { selectedAudioMode = AudioOutputMode.BLUETOOTH_PREFERRED }
                    )
                    Text("Bluetooth優先（未接続時スピーカー）")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // バイブレーション設定
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("バイブレーション")
                    Switch(
                        checked = isVibrationEnabled,
                        onCheckedChange = { isVibrationEnabled = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val alarm = Alarm(
                        time = LocalTime.of(timePickerState.hour, timePickerState.minute),
                        repeatDays = selectedDays,
                        audioOutputMode = selectedAudioMode,
                        isVibrationEnabled = isVibrationEnabled
                    )
                    onConfirm(alarm)
                }
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("キャンセル")
            }
        }
    )
}