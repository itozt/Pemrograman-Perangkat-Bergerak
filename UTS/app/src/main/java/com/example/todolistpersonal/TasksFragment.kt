package com.example.todolistpersonal

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * TasksFragment - Menampilkan tasks per section (Today, Future, Completed Today).
 */
class TasksFragment : Fragment() {

    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var recyclerViewCategories: RecyclerView
    private lateinit var recyclerViewTodayTasks: RecyclerView
    private lateinit var recyclerViewFutureTasks: RecyclerView
    private lateinit var recyclerViewCompletedTasks: RecyclerView

    private var selectedCategoryId: String = "cat_all"

    /**
     * onCreate - Baca filter category jika fragment dibuka dari Groups.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedCategoryId = arguments?.getString("category_id") ?: "cat_all"
    }

    /**
     * onCreateView - Inflate layout fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    /**
     * onViewCreated - Setup RecyclerView dan action button.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        TaskManager.init(requireContext().applicationContext)

        fabAddTask = view.findViewById(R.id.fab_add_task)
        fabAddTask.backgroundTintList = ColorStateList.valueOf(
            ThemeSettingsManager.getThemeColor(requireContext())
        )
        fabAddTask.setOnClickListener {
            startActivity(Intent(requireContext(), NewTaskActivity::class.java))
        }

        recyclerViewCategories = view.findViewById(R.id.rv_categories)
        recyclerViewTodayTasks = view.findViewById(R.id.rv_today_tasks)
        recyclerViewFutureTasks = view.findViewById(R.id.rv_future_tasks)
        recyclerViewCompletedTasks = view.findViewById(R.id.rv_completed_tasks)

        recyclerViewCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewTodayTasks.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewFutureTasks.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewCompletedTasks.layoutManager = LinearLayoutManager(requireContext())

        refreshTasksDisplay()
    }

    /**
     * onResume - Refresh data agar sinkron setelah user kembali dari layar lain.
     */
    override fun onResume() {
        super.onResume()
        refreshTasksDisplay()
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
                val intent = Intent(requireContext(), TaskDetailActivity::class.java)
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
