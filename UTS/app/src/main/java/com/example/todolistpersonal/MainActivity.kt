package com.example.todolistpersonal

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

/**
 * MainActivity - Container utama aplikasi dengan Drawer dan Bottom Navigation.
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var drawerToggle: ActionBarDrawerToggle

    private var currentMenuId: Int = R.id.menu_tasks

    companion object {
        private const val KEY_CURRENT_MENU = "current_menu"
    }

    /**
     * onCreate - Alokasikan komponen utama dan siapkan navigasi.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TaskManager.init(applicationContext)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        applyThemeToToolbar(toolbar)

        currentMenuId = savedInstanceState?.getInt(KEY_CURRENT_MENU) ?: R.id.menu_tasks

        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.nav_open,
            R.string.nav_close
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)
        setupBottomNavigation()

        if (savedInstanceState == null) {
            bottomNavigation.selectedItemId = currentMenuId
            loadRootFragment(TasksFragment(), R.id.menu_tasks)
        } else {
            bottomNavigation.selectedItemId = currentMenuId
            updateTitleForMenu(currentMenuId)
        }
    }

    /**
     * onStart - Activity mulai terlihat.
     */
    override fun onStart() {
        super.onStart()
    }

    /**
     * onResume - Pastikan UI sinkron dengan preferensi tema terbaru.
     */
    override fun onResume() {
        super.onResume()
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).let {
            applyThemeToToolbar(it)
        }
    }

    /**
     * onPause - Simpan transisi bila Activity kehilangan fokus.
     */
    override fun onPause() {
        super.onPause()
    }

    /**
     * onStop - Titik aman saat Activity tidak terlihat.
     */
    override fun onStop() {
        super.onStop()
    }

    /**
     * onRestart - Dipanggil saat Activity bangkit kembali dari state Stopped.
     */
    override fun onRestart() {
        super.onRestart()
    }

    /**
     * onDestroy - Cleanup komponen listener.
     */
    override fun onDestroy() {
        drawerLayout.removeDrawerListener(drawerToggle)
        super.onDestroy()
    }

    /**
     * onSaveInstanceState - Simpan state navigasi aktif.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_CURRENT_MENU, currentMenuId)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val handled = when (item.itemId) {
            R.id.menu_groups -> {
                loadRootFragment(GroupsFragment(), R.id.menu_groups)
                true
            }
            R.id.menu_theme -> {
                startActivity(Intent(this, ThemeActivity::class.java))
                true
            }
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> false
        }

        drawerLayout.closeDrawer(Gravity.START)
        return handled
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_tasks -> {
                    loadRootFragment(TasksFragment(), R.id.menu_tasks)
                    true
                }
                R.id.menu_calendar -> {
                    loadRootFragment(CalendarFragment(), R.id.menu_calendar)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadRootFragment(fragment: Fragment, menuId: Int) {
        if (currentMenuId == menuId && supportFragmentManager.findFragmentById(R.id.fragment_container) != null) {
            return
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        currentMenuId = menuId
        updateTitleForMenu(menuId)
    }

    private fun updateTitleForMenu(menuId: Int) {
        supportActionBar?.title = when (menuId) {
            R.id.menu_calendar -> getString(R.string.menu_calendar)
            R.id.menu_groups -> getString(R.string.nav_groups)
            else -> getString(R.string.app_name)
        }
    }

    private fun applyThemeToToolbar(toolbar: androidx.appcompat.widget.Toolbar) {
        val tint = ThemeSettingsManager.getThemeColor(this)
        toolbar.setBackgroundColor(tint)
        val colorState = android.content.res.ColorStateList.valueOf(tint)
        bottomNavigation.itemIconTintList = colorState
        bottomNavigation.itemTextColor = colorState
    }
}
