package com.example.todolistpersonal

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate

/**
 * TasksActivity - Layar daftar task (opsional saat dipanggil langsung via Intent).
 */
class TasksActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var recyclerViewCategories: RecyclerView
    private lateinit var recyclerViewTodayTasks: RecyclerView
    private lateinit var recyclerViewFutureTasks: RecyclerView
    private lateinit var recyclerViewCompletedTasks: RecyclerView
    private var selectedCategoryId: String = "cat_all"

    /**
     * onCreate - Setup UI dasar dan binding listener.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        TaskManager.init(applicationContext)

        toolbar = findViewById(R.id.toolbar_tasks)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.menu_tasks)
        }
        toolbar.setBackgroundColor(ThemeSettingsManager.getThemeColor(this))

        fabAddTask = findViewById(R.id.fab_add_task)
        fabAddTask.backgroundTintList = ColorStateList.valueOf(
            ThemeSettingsManager.getThemeColor(this)
        )
        fabAddTask.setOnClickListener {
            showNewTaskBottomSheet()
        }

        recyclerViewCategories = findViewById(R.id.rv_categories)
        recyclerViewTodayTasks = findViewById(R.id.rv_today_tasks)
        recyclerViewFutureTasks = findViewById(R.id.rv_future_tasks)
        recyclerViewCompletedTasks = findViewById(R.id.rv_completed_tasks)

        recyclerViewCategories.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewTodayTasks.layoutManager = LinearLayoutManager(this)
        recyclerViewFutureTasks.layoutManager = LinearLayoutManager(this)
        recyclerViewCompletedTasks.layoutManager = LinearLayoutManager(this)

        refreshTasksDisplay()
    }

    /**
     * onResume - Refresh data saat layar kembali aktif.
     */
    override fun onResume() {
        super.onResume()
        refreshTasksDisplay()
    }

    /**
     * onPause - Activity kehilangan fokus.
     */
    override fun onPause() {
        super.onPause()
    }

    /**
     * onStop - Activity tidak terlihat.
     */
    override fun onStop() {
        super.onStop()
    }

    private fun refreshTasksDisplay() {
        val categories = TaskManager.getAllCategories()
        recyclerViewCategories.adapter = CategoryAdapter(categories, selectedCategoryId) { category ->
            selectedCategoryId = category.id
            refreshTasksDisplay()
        }

        val todayTasks = filterByCategory(TaskManager.getTodayTasks())
        val futureTasks = filterByCategory(TaskManager.getFutureTasks())
        val completedTasks = filterByCategory(TaskManager.getCompletedTodayTasks())

        recyclerViewTodayTasks.adapter = TaskListAdapter(todayTasks) { task, action ->
            handleTaskAction(task, action)
        }
        recyclerViewFutureTasks.adapter = TaskListAdapter(futureTasks) { task, action ->
            handleTaskAction(task, action)
        }
        recyclerViewCompletedTasks.adapter = TaskListAdapter(completedTasks) { task, action ->
            handleTaskAction(task, action)
        }
    }

    private fun filterByCategory(tasks: List<Task>): List<Task> {
        return if (selectedCategoryId == "cat_all") {
            tasks
        } else {
            tasks.filter { it.categoryId == selectedCategoryId }
        }
    }

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

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showNewTaskBottomSheet() {
        val dialog = NewTaskBottomSheetFragment.newInstance()
        dialog.show(supportFragmentManager, NewTaskBottomSheetFragment::class.java.simpleName)
    }
}
