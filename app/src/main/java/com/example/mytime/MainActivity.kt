package com.example.mytime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.room.Room
import com.example.mytime.data.local.AlarmDatabase
import com.example.mytime.data.repository.AlarmRepositoryImpl
import com.example.mytime.domain.usecase.GetAlarmsUseCase
import com.example.mytime.domain.usecase.SaveAlarmUseCase
import com.example.mytime.ui.main.MainScreen
import com.example.mytime.ui.main.MainViewModel
import com.example.mytime.ui.theme.MyTimeTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Room データベースの初期化
        val database = Room.databaseBuilder(
            applicationContext,
            AlarmDatabase::class.java,
            "alarm_database"
        ).build()

        // 2. Repository および UseCase の初期化
        val repository = AlarmRepositoryImpl(database.alarmDao())
        val getAlarmsUseCase = GetAlarmsUseCase(repository)
        val saveAlarmUseCase = SaveAlarmUseCase(repository)

        // 3. ViewModel の初期化
        viewModel = MainViewModel(getAlarmsUseCase, saveAlarmUseCase)

        setContent {
            MyTimeTheme {
                // ViewModelのUiStateをStateとして監視・収集
                val uiState by viewModel.uiState.collectAsState()

                MainScreen(
                    uiState = uiState,
                    onToggleAlarm = { alarm ->
                        viewModel.toggleAlarm(alarm)
                    },
                    onSaveAlarm = { newAlarm ->
                        // ダイアログで「保存」を押したアラームをDBに保存
                        viewModel.saveAlarm(newAlarm)
                    }
                )
            }
        }
    }
}