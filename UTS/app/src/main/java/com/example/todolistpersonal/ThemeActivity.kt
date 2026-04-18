package com.example.todolistpersonal

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

/**
 * ThemeActivity - Pengaturan tema warna aplikasi dengan grid color picker.
 */
class ThemeActivity : AppCompatActivity() {

    private lateinit var buttonBack: ImageButton
    private lateinit var themeCardTeal: LinearLayout
    private lateinit var themeCardBlue: LinearLayout
    private lateinit var themeCardGreen: LinearLayout
    private lateinit var checkTeal: ImageView
    private lateinit var checkBlue: ImageView
    private lateinit var checkGreen: ImageView

    /**
     * onCreate - Setup pilihan tema dengan grid dan sinkronkan dari preferensi.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme)

        buttonBack = findViewById(R.id.btn_back_theme)
        buttonBack.setOnClickListener { finish() }

        themeCardTeal = findViewById(R.id.theme_card_teal)
        themeCardBlue = findViewById(R.id.theme_card_blue)
        themeCardGreen = findViewById(R.id.theme_card_green)
        checkTeal = findViewById(R.id.check_teal)
        checkBlue = findViewById(R.id.check_blue)
        checkGreen = findViewById(R.id.check_green)

        updateThemeSelection()

        themeCardTeal.setOnClickListener {
            ThemeSettingsManager.setThemeName(this, "teal")
            updateThemeSelection()
        }

        themeCardBlue.setOnClickListener {
            ThemeSettingsManager.setThemeName(this, "blue")
            updateThemeSelection()
        }

        themeCardGreen.setOnClickListener {
            ThemeSettingsManager.setThemeName(this, "green")
            updateThemeSelection()
        }
    }

    /**
     * onResume - Pastikan indikator pilihan sinkron dengan preferensi tersimpan.
     */
    override fun onResume() {
        super.onResume()
        updateThemeSelection()
    }

    private fun updateThemeSelection() {
        val currentTheme = ThemeSettingsManager.getThemeName(this)

        checkTeal.visibility = if (currentTheme == "teal") ImageView.VISIBLE else ImageView.GONE
        checkBlue.visibility = if (currentTheme == "blue") ImageView.VISIBLE else ImageView.GONE
        checkGreen.visibility = if (currentTheme == "green") ImageView.VISIBLE else ImageView.GONE
    }
}
