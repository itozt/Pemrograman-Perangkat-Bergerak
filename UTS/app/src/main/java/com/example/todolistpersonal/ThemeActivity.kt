package com.example.todolistpersonal

import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

/**
 * ThemeActivity - Pengaturan tema warna aplikasi.
 */
class ThemeActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var radioGroupTheme: RadioGroup
    private lateinit var radioBtnTealTheme: RadioButton
    private lateinit var radioBtnBlueTheme: RadioButton
    private lateinit var radioBtnGreenTheme: RadioButton

    /**
     * onCreate - Setup pilihan tema dan sinkronkan dari preferensi.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme)

        toolbar = findViewById(R.id.toolbar_theme)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.nav_theme)
        }
        toolbar.setBackgroundColor(ThemeSettingsManager.getThemeColor(this))

        radioGroupTheme = findViewById(R.id.radio_group_theme)
        radioBtnTealTheme = findViewById(R.id.radio_theme_teal)
        radioBtnBlueTheme = findViewById(R.id.radio_theme_blue)
        radioBtnGreenTheme = findViewById(R.id.radio_theme_green)

        when (ThemeSettingsManager.getThemeName(this)) {
            "blue" -> radioBtnBlueTheme.isChecked = true
            "green" -> radioBtnGreenTheme.isChecked = true
            else -> radioBtnTealTheme.isChecked = true
        }

        radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            val selected = when (checkedId) {
                R.id.radio_theme_blue -> "blue"
                R.id.radio_theme_green -> "green"
                else -> "teal"
            }
            ThemeSettingsManager.setThemeName(this, selected)
            toolbar.setBackgroundColor(ThemeSettingsManager.getThemeColor(this))
        }
    }

    /**
     * onResume - Pastikan indikator radio sinkron dengan preferensi tersimpan.
     */
    override fun onResume() {
        super.onResume()
        when (ThemeSettingsManager.getThemeName(this)) {
            "blue" -> radioBtnBlueTheme.isChecked = true
            "green" -> radioBtnGreenTheme.isChecked = true
            else -> radioBtnTealTheme.isChecked = true
        }
        toolbar.setBackgroundColor(ThemeSettingsManager.getThemeColor(this))
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
