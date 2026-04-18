package com.example.todolistpersonal

import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

/**
 * SettingsActivity - Pengaturan global aplikasi.
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var switchNotifications: Switch
    private lateinit var switchDarkMode: Switch

    /**
     * onCreate - Bind komponen settings dan load nilai tersimpan.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        toolbar = findViewById(R.id.toolbar_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.nav_settings)
        }
        toolbar.setBackgroundColor(ThemeSettingsManager.getThemeColor(this))

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

    /**
     * onDestroy - Cleanup akhir layar settings.
     */
    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
