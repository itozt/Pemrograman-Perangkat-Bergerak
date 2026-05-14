package com.example.pertemuan12

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pertemuan12.data.local.database.AppDatabase
import com.example.pertemuan12.data.repository.UserRepository
import com.example.pertemuan12.ui.screen.LoginScreen
import com.example.pertemuan12.ui.theme.Pertemuan12Theme
import com.example.pertemuan12.viewmodel.LoginViewModel
import com.example.pertemuan12.viewmodel.LoginViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pertemuan12Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val database = AppDatabase.getDatabase(this@MainActivity)
                    val userDao = database.userDao()
                    val repository = UserRepository(userDao)
                    val factory = LoginViewModelFactory(repository)
                    val viewModel: LoginViewModel = viewModel(factory = factory)
                    
                    LaunchedEffect(Unit) {
                        viewModel.insertDummyUser()
                    }
                    
                    LoginScreen(viewModel)
                }
            }
        }
    }
}
