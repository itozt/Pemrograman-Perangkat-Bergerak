package com.example.todolistpersonal

import android.os.Bundle
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

/**
 * ThemeActivity - Pengaturan tema warna aplikasi.
 */
class ThemeActivity : AppCompatActivity() {

    private lateinit var buttonBack: ImageButton
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

        // Back button handler
        buttonBack = findViewById(R.id.btn_back_theme)
        buttonBack.setOnClickListener { finish() }

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
    }
}
