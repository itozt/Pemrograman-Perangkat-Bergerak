package com.example.pertemuan14

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.pertemuan14.ui.navigation.NewsAppNavigation
import com.example.pertemuan14.ui.theme.Pertemuan14Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pertemuan14Theme {
                val navController = rememberNavController()
                NewsAppNavigation(navController = navController)
            }
        }
    }
}