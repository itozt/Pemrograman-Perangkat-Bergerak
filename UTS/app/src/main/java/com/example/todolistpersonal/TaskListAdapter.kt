package com.example.todolistpersonal

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.format.DateTimeFormatter

/**
 * TaskListAdapter - Adapter untuk menampilkan list tasks di RecyclerView.
 * Menyediakan callback untuk aksi: CLICK, TOGGLE_COMPLETE, TOGGLE_FLAG.
 */
class TaskListAdapter(
    private val tasks: List<Task>,
    private val onTaskAction: (Task, String) -> Unit
) : RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBoxComplete = itemView.findViewById<CheckBox>(R.id.cb_task_complete)
        private val textViewTitle = itemView.findViewById<TextView>(R.id.tv_task_title)
        private val textViewSubtitle = itemView.findViewById<TextView>(R.id.tv_task_subtitle)
        private val textViewIndicators = itemView.findViewById<TextView>(R.id.tv_task_indicators)
        private val buttonFlag = itemView.findViewById<ImageButton>(R.id.btn_flag_task)

        fun bind(task: Task) {
            // Set checkbox state
            checkBoxComplete.isChecked = task.isCompleted

            // Set title with strikethrough if completed
            textViewTitle.text = task.title
            if (task.isCompleted) {
                textViewTitle.paintFlags = textViewTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                textViewTitle.paintFlags = textViewTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            // Set subtitle: Category • Date Time
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            val dateStr = task.dueDate?.format(dateFormatter) ?: "No date"
            val timeStr = task.dueTime?.format(timeFormatter) ?: ""
            textViewSubtitle.text = "${task.categoryName} • $dateStr ${if (timeStr.isNotEmpty()) timeStr else ""}"

            // Set indicators: Reminder time and repeat type
            val indicators = mutableListOf<String>()
            if (task.hasReminder && task.reminderTime != null) {
                val reminderStr = task.reminderTime.format(timeFormatter)
                indicators.add("Reminder $reminderStr")
            }
            if (task.repeatType != RepeatType.NONE) {
                indicators.add("Repeat ${task.repeatType.displayName}")
            }
            textViewIndicators.text = indicators.joinToString(" | ")
            textViewIndicators.visibility = if (indicators.isEmpty()) View.GONE else View.VISIBLE

            // Set flag button appearance
            updateFlagButtonAppearance(task.isFlagged)

            // Event handlers
            itemView.setOnClickListener {
                onTaskAction(task, "CLICK")
            }

            checkBoxComplete.setOnCheckedChangeListener { _, isChecked ->
                onTaskAction(task, "TOGGLE_COMPLETE")
            }

            buttonFlag.setOnClickListener {
                onTaskAction(task, "TOGGLE_FLAG")
            }
        }

        private fun updateFlagButtonAppearance(isFlagged: Boolean) {
            if (isFlagged) {
                // Flag is filled
                buttonFlag.setImageResource(R.drawable.flag_finish_fill)
                buttonFlag.clearColorFilter()
            } else {
                // Flag is not filled
                buttonFlag.setImageResource(R.drawable.flag_finish)
                buttonFlag.clearColorFilter()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount() = tasks.size
}
