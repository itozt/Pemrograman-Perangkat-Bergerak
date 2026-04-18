package com.example.todolistpersonal

import android.content.Context
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

enum class RepeatType(val displayName: String) {
    NONE("None"),
    HOURLY("Hourly"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly"),
    CUSTOM("Custom")
}

data class Category(
    val id: String = "",
    val name: String = "",
    val color: String = "#4da8a3"
) : Serializable

data class Task(
    val id: String = "",
    val title: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val dueDate: LocalDate? = null,
    val dueTime: LocalTime? = null,
    val hasReminder: Boolean = false,
    val reminderTime: LocalTime? = null,
    val repeatType: RepeatType = RepeatType.NONE,
    val customRepeatDays: List<String> = emptyList(),
    val notes: String = "",
    val isCompleted: Boolean = false,
    val isFlagged: Boolean = false,
    val completedDate: LocalDate? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
) : Serializable

/**
 * Controller untuk domain task/category.
 * Menyediakan API sederhana untuk UI dan mendelegasikan penyimpanan ke SQLite helper.
 */
object TaskManager {
    private var dbHelper: TaskDatabaseHelper? = null

    fun init(context: Context) {
        if (dbHelper == null) {
            dbHelper = TaskDatabaseHelper(context.applicationContext)
        }
    }

    fun getAllCategories(): List<Category> = requireDb().getAllCategories()

    fun getCategoryById(id: String): Category? = requireDb().getCategoryById(id)

    fun addCategory(category: Category) = requireDb().addCategory(category)

    fun updateCategory(category: Category) = requireDb().updateCategory(category)

    fun deleteCategory(id: String) = requireDb().deleteCategory(id)

    fun getAllTasks(): List<Task> = requireDb().getAllTasks()

    fun getTaskById(id: String): Task? = requireDb().getTaskById(id)

    fun getTasksForDate(date: LocalDate): List<Task> = requireDb().getTasksForDate(date)

    fun getTodayTasks(today: LocalDate = LocalDate.now()): List<Task> = requireDb().getTodayTasks(today)

    fun getCompletedTodayTasks(today: LocalDate = LocalDate.now()): List<Task> =
        requireDb().getCompletedTodayTasks(today)

    fun getFutureTasks(today: LocalDate = LocalDate.now()): List<Task> = requireDb().getFutureTasks(today)

    fun getTasksByCategory(categoryId: String): List<Task> = requireDb().getTasksByCategory(categoryId)

    fun addTask(task: Task) = requireDb().addTask(task)

    fun updateTask(task: Task) = requireDb().updateTask(task)

    fun deleteTask(id: String) = requireDb().deleteTask(id)

    fun toggleTaskCompletion(id: String) = requireDb().toggleTaskCompletion(id, LocalDate.now())

    fun toggleTaskFlag(id: String) = requireDb().toggleTaskFlag(id)

    fun generateTaskId(): String = "task_${System.currentTimeMillis()}"

    fun generateCategoryId(): String = "cat_${System.currentTimeMillis()}"

    private fun requireDb(): TaskDatabaseHelper {
        return dbHelper ?: error("TaskManager belum diinisialisasi. Panggil TaskManager.init(context) lebih dulu.")
    }
}
