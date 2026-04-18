package com.example.todolistpersonal

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

/**
 * SettingsActivity - Pengaturan global aplikasi.
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var buttonBack: ImageButton
    private lateinit var switchNotifications: Switch
    private lateinit var switchDarkMode: Switch

    /**
     * onCreate - Bind komponen settings dan load nilai tersimpan.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Back button handler
        buttonBack = findViewById(R.id.btn_back_settings)
        buttonBack.setOnClickListener { finish() }

        switchNotifications = findViewById(R.id.switch_notifications)
        switchDarkMode = findViewById(R.id.switch_dark_mode)

        switchNotifications.isChecked = ThemeSettingsManager.isNotificationsEnabled(this)
        switchDarkMode.isChecked = ThemeSettingsManager.isDarkModeEnabled(this)

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            ThemeSettingsManager.setNotificationsEnabled(this, isChecked)
        }

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            ThemeSettingsManager.setDarkModeEnabled(this, isChecked)
            recreate()
        }
    }

    /**
     * onStop - Simpan state UI terakhir.
     */
    override fun onStop() {
        super.onStop()
        ThemeSettingsManager.setNotificationsEnabled(this, switchNotifications.isChecked)
    }
}
