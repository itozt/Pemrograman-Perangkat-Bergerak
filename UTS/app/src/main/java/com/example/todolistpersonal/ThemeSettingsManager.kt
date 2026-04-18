package com.example.todolistpersonal

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatDelegate

object ThemeSettingsManager {
    private const val PREFS_NAME = "todo_prefs"
    private const val KEY_THEME_NAME = "theme_name"
    private const val KEY_NOTIFICATIONS = "notifications_enabled"
    private const val KEY_DARK_MODE = "dark_mode_enabled"

    fun getThemeName(context: Context): String {
        return prefs(context).getString(KEY_THEME_NAME, "teal") ?: "teal"
    }

    fun setThemeName(context: Context, themeName: String) {
        prefs(context).edit().putString(KEY_THEME_NAME, themeName).apply()
    }

    fun getThemeColor(context: Context): Int {
        return when (getThemeName(context)) {
            "blue" -> Color.parseColor("#2196F3")
            "green" -> Color.parseColor("#4CAF50")
            else -> Color.parseColor("#4da8a3")
        }
    }

    fun isNotificationsEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_NOTIFICATIONS, true)
    }

    fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
    }

    fun isDarkModeEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_DARK_MODE, false)
    }

    fun setDarkModeEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_DARK_MODE, enabled).apply()
        applyNightMode(enabled)
    }

    fun applySavedNightMode(context: Context) {
        applyNightMode(isDarkModeEnabled(context))
    }

    private fun applyNightMode(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
