package com.example.todolistpersonal

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * TaskDetailActivity - Menampilkan detail task dan mengelola update/delete.
 */
class TaskDetailActivity : AppCompatActivity() {

    private lateinit var buttonBack: ImageButton
    private lateinit var editTaskTitle: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var textViewDueDate: TextView
    private lateinit var textViewDueTime: TextView
    private lateinit var checkBoxReminder: CheckBox
    private lateinit var textViewReminderTime: TextView
    private lateinit var spinnerRepeatType: Spinner
    private lateinit var editNotes: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonDelete: Button

    private var taskId: String = ""
    private var task: Task? = null
    private var selectedDate: LocalDate = LocalDate.now()
    private var selectedTime: LocalTime = LocalTime.now().withSecond(0).withNano(0)
    private var selectedReminderTime: LocalTime = LocalTime.now().withSecond(0).withNano(0)

    private var categories: List<Category> = emptyList()
    private val createCategoryLabel by lazy { getString(R.string.create_new_category_inline) }

    companion object {
        private const val KEY_SELECTED_DATE = "selected_date"
        private const val KEY_SELECTED_TIME = "selected_time"
        private const val KEY_REMINDER_TIME = "reminder_time"
    }

    /**
     * onCreate - Ambil task ID, bind UI, dan isi form.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)
        TaskManager.init(applicationContext)

        taskId = intent.getStringExtra("task_id").orEmpty()
        task = TaskManager.getTaskById(taskId)
        if (task == null) {
            finish()
            return
        }

        // Back button handler
        buttonBack = findViewById(R.id.btn_back_task_detail)
        buttonBack.setOnClickListener { finish() }

        editTaskTitle = findViewById(R.id.edit_task_title_detail)
        spinnerCategory = findViewById(R.id.spinner_category_detail)
        textViewDueDate = findViewById(R.id.text_due_date_detail)
        textViewDueTime = findViewById(R.id.text_due_time_detail)
        checkBoxReminder = findViewById(R.id.checkbox_reminder_detail)
        textViewReminderTime = findViewById(R.id.text_reminder_time_detail)
        spinnerRepeatType = findViewById(R.id.spinner_repeat_type_detail)
        editNotes = findViewById(R.id.edit_notes_detail)
        buttonSave = findViewById(R.id.btn_save_task_detail)
        buttonDelete = findViewById(R.id.btn_delete_task_detail)

        setupCategorySpinner()
        setupRepeatTypeSpinner()
        setupDateTimeInputs()
        loadTaskData()

        buttonSave.setOnClickListener { updateTask() }
        buttonDelete.setOnClickListener { deleteTask() }

        savedInstanceState?.let { restoreState(it) }
        renderDateTimeValues()
    }

    /**
     * onSaveInstanceState - Simpan field tanggal/waktu sementara.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SELECTED_DATE, selectedDate.toString())
        outState.putString(KEY_SELECTED_TIME, selectedTime.toString())
        outState.putString(KEY_REMINDER_TIME, selectedReminderTime.toString())
    }

    /**
     * onStart - Detail task mulai terlihat.
     */
    override fun onStart() {
        super.onStart()
    }

    /**
     * onResume - Detail task siap diubah pengguna.
     */
    override fun onResume() {
        super.onResume()
    }

    /**
     * onPause - Menangani kehilangan fokus sementara.
     */
    override fun onPause() {
        super.onPause()
    }

    /**
     * onStop - Titik aman saat layar detail tertutup.
     */
    override fun onStop() {
        super.onStop()
    }

    /**
     * onRestart - Detail task kembali aktif setelah status Stopped.
     */
    override fun onRestart() {
        super.onRestart()
    }

    /**
     * onDestroy - Cleanup akhir sebelum Activity dihancurkan.
     */
    override fun onDestroy() {
        super.onDestroy()
    }

    private fun setupCategorySpinner() {
        categories = TaskManager.getAllCategories().filter { it.id != "cat_all" }
        val categoryNames = categories.map { it.name }.toMutableList().apply { add(createCategoryLabel) }
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        spinnerCategory.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                if (position == categories.size) {
                    showCreateCategoryDialog()
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
    }

    private fun setupRepeatTypeSpinner() {
        val repeatTypes = RepeatType.values().map { it.displayName }
        val repeatAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, repeatTypes)
        repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRepeatType.adapter = repeatAdapter
    }

    private fun setupDateTimeInputs() {
        textViewDueDate.setOnClickListener { showDatePicker() }
        textViewDueTime.setOnClickListener { showTimePicker() }

        checkBoxReminder.setOnCheckedChangeListener { _, isChecked ->
            textViewReminderTime.isEnabled = isChecked
        }

        textViewReminderTime.setOnClickListener {
            if (checkBoxReminder.isChecked) {
                showReminderTimePicker()
            }
        }
    }

    private fun loadTaskData() {
        val currentTask = task ?: return
        editTaskTitle.setText(currentTask.title)
        editNotes.setText(currentTask.notes)

        selectedDate = currentTask.dueDate ?: LocalDate.now()
        selectedTime = currentTask.dueTime ?: LocalTime.now().withSecond(0).withNano(0)
        checkBoxReminder.isChecked = currentTask.hasReminder
        selectedReminderTime = currentTask.reminderTime ?: selectedTime

        val categoryIndex = categories.indexOfFirst { it.id == currentTask.categoryId }
        if (categoryIndex >= 0) {
            spinnerCategory.setSelection(categoryIndex)
        }

        val repeatTypeIndex = RepeatType.values().indexOf(currentTask.repeatType)
        if (repeatTypeIndex >= 0) {
            spinnerRepeatType.setSelection(repeatTypeIndex)
        }
    }

    private fun restoreState(saved: Bundle) {
        selectedDate = LocalDate.parse(saved.getString(KEY_SELECTED_DATE) ?: LocalDate.now().toString())
        selectedTime = LocalTime.parse(saved.getString(KEY_SELECTED_TIME) ?: LocalTime.now().toString())
        selectedReminderTime = LocalTime.parse(saved.getString(KEY_REMINDER_TIME) ?: LocalTime.now().toString())
    }

    private fun renderDateTimeValues() {
        textViewDueDate.text = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        textViewDueTime.text = formatTime(selectedTime)
        textViewReminderTime.text = formatTime(selectedReminderTime)
        textViewReminderTime.isEnabled = checkBoxReminder.isChecked
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                renderDateTimeValues()
            },
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth
        ).show()
    }

    private fun showTimePicker() {
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                selectedTime = LocalTime.of(hourOfDay, minute)
                renderDateTimeValues()
            },
            selectedTime.hour,
            selectedTime.minute,
            DateFormat.is24HourFormat(this)
        ).show()
    }

    private fun showReminderTimePicker() {
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                selectedReminderTime = LocalTime.of(hourOfDay, minute)
                renderDateTimeValues()
            },
            selectedReminderTime.hour,
            selectedReminderTime.minute,
            DateFormat.is24HourFormat(this)
        ).show()
    }

    private fun updateTask() {
        val currentTask = task ?: return
        val title = editTaskTitle.text.toString().trim()

        // Validate task title
        val (isValidTitle, titleError) = ValidationHelper.validateTaskTitle(title)
        if (!isValidTitle) {
            editTaskTitle.error = titleError
            editTaskTitle.requestFocus()
            UIHelper.showToast(this, titleError ?: "Invalid task title")
            return
        }

        val categoryIndex = spinnerCategory.selectedItemPosition
        if (categoryIndex !in categories.indices) {
            UIHelper.showToast(this, "Please select a category")
            spinnerCategory.requestFocus()
            return
        }
        val category = categories[categoryIndex]

        // Validate notes
        val notes = editNotes.text.toString()
        val (isValidNotes, notesError) = ValidationHelper.validateNotes(notes)
        if (!isValidNotes) {
            UIHelper.showToast(this, notesError ?: "Notes too long")
            return
        }

        val repeatType = RepeatType.values()[spinnerRepeatType.selectedItemPosition]

        try {
            val updatedTask = currentTask.copy(
                title = title,
                categoryId = category.id,
                categoryName = category.name,
                dueDate = selectedDate,
                dueTime = selectedTime,
                hasReminder = checkBoxReminder.isChecked,
                reminderTime = if (checkBoxReminder.isChecked) selectedReminderTime else null,
                repeatType = repeatType,
                notes = notes.trim()
            )
            TaskManager.updateTask(updatedTask)
            UIHelper.showToast(this, "Task updated successfully")
            finish()
        } catch (e: Exception) {
            UIHelper.showErrorDialog(this, "Error", "Failed to update task: ${e.message}")
        }
    }

    private fun deleteTask() {
        // HCI: Show confirmation dialog sebelum delete
        UIHelper.showDeleteConfirmation(
            this,
            title = "Delete Task",
            message = "Are you sure you want to delete \"${task?.title}\"?\nThis action cannot be undone.",
            onConfirm = {
                try {
                    TaskManager.deleteTask(taskId)
                    UIHelper.showToast(this, "Task deleted successfully")
                    finish()
                } catch (e: Exception) {
                    UIHelper.showErrorDialog(this, "Error", "Failed to delete task: ${e.message}")
                }
            }
        )
    }

    private fun showCreateCategoryDialog() {
        val input = EditText(this).apply {
            hint = getString(R.string.enter_group_name)
            setPadding(24, 24, 24, 24)
        }

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.create_new_group))
            .setView(input)
            .setPositiveButton(getString(R.string.create)) { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    val category = Category(
                        id = TaskManager.generateCategoryId(),
                        name = name,
                        color = "#4da8a3"
                    )
                    TaskManager.addCategory(category)
                    setupCategorySpinner()
                    val index = categories.indexOfFirst { it.id == category.id }
                    if (index >= 0) {
                        spinnerCategory.setSelection(index)
                    }
                } else {
                    val existing = categories.indexOfFirst { it.id == task?.categoryId }
                    spinnerCategory.setSelection(if (existing >= 0) existing else 0)
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                val existing = categories.indexOfFirst { it.id == task?.categoryId }
                spinnerCategory.setSelection(if (existing >= 0) existing else 0)
            }
            .show()
    }

    private fun formatTime(time: LocalTime): String {
        val formatter = if (DateFormat.is24HourFormat(this)) {
            DateTimeFormatter.ofPattern("HH:mm")
        } else {
            DateTimeFormatter.ofPattern("hh:mm a")
        }
        return time.format(formatter)
    }
}
