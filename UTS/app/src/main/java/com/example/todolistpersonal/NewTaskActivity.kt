package com.example.todolistpersonal

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * NewTaskActivity - Form pembuatan task baru.
 */
class NewTaskActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var editTaskTitle: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var textViewDueDate: TextView
    private lateinit var textViewDueTime: TextView
    private lateinit var checkBoxReminder: CheckBox
    private lateinit var textViewReminderTime: TextView
    private lateinit var spinnerRepeatType: Spinner
    private lateinit var editNotes: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonCancel: Button

    private var selectedDate: LocalDate = LocalDate.now()
    private var selectedTime: LocalTime = LocalTime.now().withSecond(0).withNano(0)
    private var selectedReminderTime: LocalTime = LocalTime.now().withSecond(0).withNano(0)

    private var categories: List<Category> = emptyList()
    private val createCategoryLabel by lazy { getString(R.string.create_new_category_inline) }

    companion object {
        private const val KEY_TASK_TITLE = "task_title"
        private const val KEY_SELECTED_DATE = "selected_date"
        private const val KEY_SELECTED_TIME = "selected_time"
        private const val KEY_REMINDER_TIME = "reminder_time"
        private const val KEY_REPEAT_TYPE = "repeat_type"
        private const val KEY_NOTES = "notes"
    }

    /**
     * onCreate - Setup form dan event handler.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)
        TaskManager.init(applicationContext)

        toolbar = findViewById(R.id.toolbar_new_task)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.new_task)
        }
        toolbar.setBackgroundColor(ThemeSettingsManager.getThemeColor(this))

        editTaskTitle = findViewById(R.id.edit_task_title)
        spinnerCategory = findViewById(R.id.spinner_category)
        textViewDueDate = findViewById(R.id.text_due_date)
        textViewDueTime = findViewById(R.id.text_due_time)
        checkBoxReminder = findViewById(R.id.checkbox_reminder)
        textViewReminderTime = findViewById(R.id.text_reminder_time)
        spinnerRepeatType = findViewById(R.id.spinner_repeat_type)
        editNotes = findViewById(R.id.edit_notes)
        buttonSave = findViewById(R.id.btn_save_task)
        buttonCancel = findViewById(R.id.btn_cancel_task)

        setupCategorySpinner()
        setupRepeatTypeSpinner()
        setupDateTimeInputs()

        buttonSave.setOnClickListener { saveTask() }
        buttonCancel.setOnClickListener { finish() }

        savedInstanceState?.let { restoreState(it) }
        renderDateTimeValues()
    }

    /**
     * onSaveInstanceState - Simpan state input agar tidak hilang saat rotasi.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_TASK_TITLE, editTaskTitle.text.toString())
        outState.putString(KEY_SELECTED_DATE, selectedDate.toString())
        outState.putString(KEY_SELECTED_TIME, selectedTime.toString())
        outState.putString(KEY_REMINDER_TIME, selectedReminderTime.toString())
        outState.putString(KEY_REPEAT_TYPE, spinnerRepeatType.selectedItem?.toString() ?: RepeatType.NONE.displayName)
        outState.putString(KEY_NOTES, editNotes.text.toString())
    }

    /**
     * onStart - Form mulai terlihat ke pengguna.
     */
    override fun onStart() {
        super.onStart()
    }

    /**
     * onResume - Form siap menerima input.
     */
    override fun onResume() {
        super.onResume()
    }

    /**
     * onPause - Simpan perubahan sementara saat kehilangan fokus.
     */
    override fun onPause() {
        super.onPause()
    }

    /**
     * onStop - Titik aman saat layar tertutup.
     */
    override fun onStop() {
        super.onStop()
    }

    /**
     * onRestart - Form aktif kembali setelah sempat tertutup.
     */
    override fun onRestart() {
        super.onRestart()
    }

    /**
     * onDestroy - Cleanup sebelum Activity dihancurkan.
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

    private fun restoreState(saved: Bundle) {
        editTaskTitle.setText(saved.getString(KEY_TASK_TITLE, ""))
        selectedDate = LocalDate.parse(saved.getString(KEY_SELECTED_DATE) ?: LocalDate.now().toString())
        selectedTime = LocalTime.parse(saved.getString(KEY_SELECTED_TIME) ?: LocalTime.now().toString())
        selectedReminderTime = LocalTime.parse(saved.getString(KEY_REMINDER_TIME) ?: LocalTime.now().toString())
        editNotes.setText(saved.getString(KEY_NOTES, ""))

        val repeatLabel = saved.getString(KEY_REPEAT_TYPE, RepeatType.NONE.displayName)
        val repeatIndex = RepeatType.values().indexOfFirst { it.displayName == repeatLabel }
        if (repeatIndex >= 0) {
            spinnerRepeatType.setSelection(repeatIndex)
        }
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

    private fun saveTask() {
        val title = editTaskTitle.text.toString().trim()

        // Validate task title
        val (isValidTitle, titleError) = ValidationHelper.validateTaskTitle(title)
        if (!isValidTitle) {
            editTaskTitle.error = titleError
            editTaskTitle.requestFocus()
            UIHelper.showToast(this, titleError ?: "Invalid task title")
            return
        }

        val selectedCategoryIndex = spinnerCategory.selectedItemPosition
        if (selectedCategoryIndex < 0 || selectedCategoryIndex >= categories.size) {
            UIHelper.showToast(this, "Please select a category")
            spinnerCategory.requestFocus()
            return
        }
        val category = categories[selectedCategoryIndex]

        // Validate notes
        val notes = editNotes.text.toString()
        val (isValidNotes, notesError) = ValidationHelper.validateNotes(notes)
        if (!isValidNotes) {
            UIHelper.showToast(this, notesError ?: "Notes too long")
            return
        }

        val repeatType = RepeatType.values()[spinnerRepeatType.selectedItemPosition]

        try {
            val newTask = Task(
                id = TaskManager.generateTaskId(),
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

            TaskManager.addTask(newTask)
            UIHelper.showToast(this, "Task \"$title\" created successfully")
            finish()
        } catch (e: Exception) {
            UIHelper.showErrorDialog(this, "Error", "Failed to create task: ${e.message}")
        }
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
                    spinnerCategory.setSelection(0)
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                spinnerCategory.setSelection(0)
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
