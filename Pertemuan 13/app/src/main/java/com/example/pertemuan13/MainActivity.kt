package com.example.pertemuan13

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.pertemuan13.data.AppDatabase
import com.example.pertemuan13.ui.MainScreen
import com.example.pertemuan13.ui.theme.Pertemuan13Theme
import com.example.pertemuan13.viewmodel.StudentViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi database dan DAO
        val dao = AppDatabase.getDatabase(applicationContext).siswaDao()

        // Buat ViewModel menggunakan factory agar lifecycle-aware
        val factory = StudentViewModel.factory(dao)
        val viewModel: StudentViewModel by viewModels { factory }

        enableEdgeToEdge()
        setContent {
            Pertemuan13Theme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
