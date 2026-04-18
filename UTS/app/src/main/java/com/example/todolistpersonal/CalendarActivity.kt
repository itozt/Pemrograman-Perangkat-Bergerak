package com.example.todolistpersonal

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.CalendarView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate

/**
 * CalendarActivity - Menampilkan kalender dan tasks untuk tanggal yang dipilih
 * 
 * Fitur:
 * - Kalender untuk navigate antar bulan
 * - Menampilkan tasks untuk tanggal yang dipilih
 * - FAB untuk tambah task baru
 * - Click pada task untuk lihat detail
 * 
 * Activity Lifecycle:
 * - onCreate(): Setup kalender dan load tasks
 * - onResume(): Refresh tasks saat kembali ke activity
 * - onStop(): Save selected date dan tasks state
 */
class CalendarActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var calendarView: CalendarView
    private lateinit var recyclerViewTasks: RecyclerView
    private lateinit var fabAddTask: FloatingActionButton

    private var selectedDate: LocalDate = LocalDate.now()

    companion object {
        private const val KEY_SELECTED_DATE = "selected_date"
    }

    /**
     * onCreate - Alokasikan memori awal dan setup layout
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        TaskManager.init(applicationContext)

        // Setup toolbar
        toolbar = findViewById(R.id.toolbar_calendar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Calendar"
        }
        toolbar.setBackgroundColor(ThemeSettingsManager.getThemeColor(this))

        // Setup calendar view
        calendarView = findViewById(R.id.calendar_view)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            refreshTasksDisplay()
        }

        // Setup FAB
        fabAddTask = findViewById(R.id.fab_add_task_calendar)
        fabAddTask.backgroundTintList = ColorStateList.valueOf(
            ThemeSettingsManager.getThemeColor(this)
        )
        fabAddTask.setOnClickListener {
            startActivity(Intent(this, NewTaskActivity::class.java))
        }

        // Setup recycler view untuk tasks
        recyclerViewTasks = findViewById(R.id.rv_tasks_for_date)

        // Restore state jika ada
        if (savedInstanceState != null) {
            val dateString = savedInstanceState.getString(KEY_SELECTED_DATE)
            if (dateString != null) {
                selectedDate = LocalDate.parse(dateString)
            }
        }

        // Initial display
        refreshTasksDisplay()
    }

    /**
     * onStart - Transisi visual saat layar mulai terlihat
     */
    override fun onStart() {
        super.onStart()
    }

    /**
     * onResume - Aplikasi siap menerima input user
     */
    override fun onResume() {
        super.onResume()
        refreshTasksDisplay()
    }

    /**
     * onPause - Activity kehilangan fokus
     */
    override fun onPause() {
        super.onPause()
    }

    /**
     * onStop - Activity tertutup total, save state
     */
    override fun onStop() {
        super.onStop()
    }

    /**
     * onSaveInstanceState - Simpan selected date
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SELECTED_DATE, selectedDate.toString())
    }

    /**
     * onDestroy - Cleanup
     */
    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     * Refresh tasks display untuk selected date
     */
    private fun refreshTasksDisplay() {
        val tasksForDate = TaskManager.getTasksForDate(selectedDate)

        val taskAdapter = TaskListAdapter(tasksForDate) { task, action ->
            handleTaskAction(task, action)
        }

        recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(this@CalendarActivity)
            adapter = taskAdapter
        }
    }

    /**
     * Handle aksi pada task
     */
    private fun handleTaskAction(task: Task, action: String) {
        when (action) {
            "CLICK" -> {
                val intent = Intent(this, TaskDetailActivity::class.java)
                intent.putExtra("task_id", task.id)
                startActivity(intent)
            }
            "TOGGLE_COMPLETE" -> {
                TaskManager.toggleTaskCompletion(task.id)
                refreshTasksDisplay()
            }
            "TOGGLE_FLAG" -> {
                TaskManager.toggleTaskFlag(task.id)
                refreshTasksDisplay()
            }
        }
    }
}
