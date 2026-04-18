package com.example.todolistpersonal

import android.app.Application

class ToDoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TaskManager.init(this)
        ThemeSettingsManager.applySavedNightMode(this)
    }
}
