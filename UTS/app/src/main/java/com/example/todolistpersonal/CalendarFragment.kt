package com.example.todolistpersonal

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * CalendarFragment - Menampilkan kalender dan tasks berdasarkan tanggal terpilih.
 */
class CalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerViewTasks: RecyclerView
    private lateinit var fabAddTask: FloatingActionButton
    private var selectedDate = LocalDate.now()

    /**
     * onCreateView - Inflate layout fragment calendar.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    /**
     * onViewCreated - Setup komponen kalender dan list task.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        TaskManager.init(requireContext().applicationContext)

        calendarView = view.findViewById(R.id.calendar_view)
        recyclerViewTasks = view.findViewById(R.id.rv_tasks_for_date)
        fabAddTask = view.findViewById(R.id.fab_add_task_calendar)

        recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())

        val todayMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        calendarView.date = todayMillis

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            refreshTasksDisplay()
        }

        fabAddTask.backgroundTintList = ColorStateList.valueOf(
            ThemeSettingsManager.getThemeColor(requireContext())
        )
        fabAddTask.setOnClickListener {
            startActivity(Intent(requireContext(), NewTaskActivity::class.java))
        }

        refreshTasksDisplay()
    }

    /**
     * onResume - Refresh data setelah kembali dari layar lain.
     */
    override fun onResume() {
        super.onResume()
        selectedDate = Instant.ofEpochMilli(calendarView.date).atZone(ZoneId.systemDefault()).toLocalDate()
        refreshTasksDisplay()
    }

    private fun refreshTasksDisplay() {
        val tasksForDate = TaskManager.getTasksForDate(selectedDate)
        recyclerViewTasks.adapter = TaskListAdapter(tasksForDate) { task, action ->
            handleTaskAction(task, action)
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
