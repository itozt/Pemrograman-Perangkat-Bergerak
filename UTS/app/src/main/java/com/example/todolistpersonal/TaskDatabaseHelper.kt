package com.example.todolistpersonal

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class TaskDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE categories (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                color TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE tasks (
                id TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                category_id TEXT NOT NULL,
                category_name TEXT NOT NULL,
                due_date TEXT,
                due_time TEXT,
                has_reminder INTEGER NOT NULL DEFAULT 0,
                reminder_time TEXT,
                repeat_type TEXT NOT NULL,
                custom_repeat_days TEXT NOT NULL DEFAULT '',
                notes TEXT NOT NULL DEFAULT '',
                is_completed INTEGER NOT NULL DEFAULT 0,
                is_flagged INTEGER NOT NULL DEFAULT 0,
                completed_date TEXT,
                created_at TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL("CREATE INDEX idx_tasks_due_date ON tasks(due_date)")
        db.execSQL("CREATE INDEX idx_tasks_category_id ON tasks(category_id)")
        seedDefaultCategories(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS tasks")
        db.execSQL("DROP TABLE IF EXISTS categories")
        onCreate(db)
    }

    fun getAllCategories(): List<Category> {
        val db = readableDatabase
        val categories = mutableListOf<Category>()
        val cursor = db.query(
            "categories",
            arrayOf("id", "name", "color"),
            null,
            null,
            null,
            null,
            "name COLLATE NOCASE ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                categories.add(
                    Category(
                        id = it.getString(0),
                        name = it.getString(1),
                        color = it.getString(2)
                    )
                )
            }
        }

        return categories
    }

    fun getCategoryById(id: String): Category? {
        val db = readableDatabase
        val cursor = db.query(
            "categories",
            arrayOf("id", "name", "color"),
            "id = ?",
            arrayOf(id),
            null,
            null,
            null
        )
        cursor.use {
            if (!it.moveToFirst()) {
                return null
            }
            return Category(
                id = it.getString(0),
                name = it.getString(1),
                color = it.getString(2)
            )
        }
    }

    fun addCategory(category: Category) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", category.id)
            put("name", category.name)
            put("color", category.color)
            put("created_at", System.currentTimeMillis())
        }
        db.insertWithOnConflict("categories", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun updateCategory(category: Category) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", category.name)
            put("color", category.color)
        }
        db.update("categories", values, "id = ?", arrayOf(category.id))
    }

    fun deleteCategory(id: String) {
        if (id == DEFAULT_CATEGORY_ID) {
            return
        }

        val db = writableDatabase
        db.beginTransaction()
        try {
            val fallbackCategory = getCategoryById(DEFAULT_CATEGORY_ID)
                ?: Category(DEFAULT_CATEGORY_ID, "All", "#4da8a3").also { addCategory(it) }

            val updateValues = ContentValues().apply {
                put("category_id", fallbackCategory.id)
                put("category_name", fallbackCategory.name)
            }

            db.update("tasks", updateValues, "category_id = ?", arrayOf(id))
            db.delete("categories", "id = ?", arrayOf(id))
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getTaskById(id: String): Task? {
        val db = readableDatabase
        val cursor = db.query(
            "tasks",
            TASK_COLUMNS,
            "id = ?",
            arrayOf(id),
            null,
            null,
            null
        )
        cursor.use {
            if (!it.moveToFirst()) {
                return null
            }
            return cursorToTask(it)
        }
    }

    fun getAllTasks(): List<Task> = queryTasks()

    fun getTasksForDate(date: LocalDate): List<Task> = queryTasks(
        selection = "due_date = ?",
        selectionArgs = arrayOf(date.toString()),
        orderBy = "is_completed ASC, due_time ASC"
    )

    fun getTodayTasks(today: LocalDate): List<Task> = queryTasks(
        selection = "due_date = ? AND is_completed = 0",
        selectionArgs = arrayOf(today.toString()),
        orderBy = "due_time ASC"
    )

    fun getCompletedTodayTasks(today: LocalDate): List<Task> = queryTasks(
        selection = "is_completed = 1 AND completed_date = ?",
        selectionArgs = arrayOf(today.toString()),
        orderBy = "due_time ASC"
    )

    fun getFutureTasks(today: LocalDate): List<Task> = queryTasks(
        selection = "due_date > ? AND is_completed = 0",
        selectionArgs = arrayOf(today.toString()),
        orderBy = "due_date ASC, due_time ASC"
    )

    fun getTasksByCategory(categoryId: String): List<Task> {
        if (categoryId == DEFAULT_CATEGORY_ID) {
            return queryTasks(orderBy = "due_date ASC, due_time ASC")
        }
        return queryTasks(
            selection = "category_id = ?",
            selectionArgs = arrayOf(categoryId),
            orderBy = "due_date ASC, due_time ASC"
        )
    }

    fun addTask(task: Task) {
        val db = writableDatabase
        db.insertWithOnConflict("tasks", null, taskToContentValues(task), SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun updateTask(task: Task) {
        val db = writableDatabase
        db.update("tasks", taskToContentValues(task), "id = ?", arrayOf(task.id))
    }

    fun deleteTask(id: String) {
        writableDatabase.delete("tasks", "id = ?", arrayOf(id))
    }

    fun toggleTaskCompletion(id: String, completedDate: LocalDate) {
        val task = getTaskById(id) ?: return
        val updatedTask = task.copy(
            isCompleted = !task.isCompleted,
            completedDate = if (!task.isCompleted) completedDate else null
        )
        updateTask(updatedTask)
    }

    fun toggleTaskFlag(id: String) {
        val task = getTaskById(id) ?: return
        updateTask(task.copy(isFlagged = !task.isFlagged))
    }

    private fun queryTasks(
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        orderBy: String = "created_at DESC"
    ): List<Task> {
        val db = readableDatabase
        val tasks = mutableListOf<Task>()
        val cursor = db.query(
            "tasks",
            TASK_COLUMNS,
            selection,
            selectionArgs,
            null,
            null,
            orderBy
        )

        cursor.use {
            while (it.moveToNext()) {
                tasks.add(cursorToTask(it))
            }
        }

        return tasks
    }

    private fun taskToContentValues(task: Task): ContentValues {
        return ContentValues().apply {
            put("id", task.id)
            put("title", task.title)
            put("category_id", task.categoryId)
            put("category_name", task.categoryName)
            put("due_date", task.dueDate?.toString())
            put("due_time", task.dueTime?.toString())
            put("has_reminder", if (task.hasReminder) 1 else 0)
            put("reminder_time", task.reminderTime?.toString())
            put("repeat_type", task.repeatType.name)
            put("custom_repeat_days", task.customRepeatDays.joinToString(","))
            put("notes", task.notes)
            put("is_completed", if (task.isCompleted) 1 else 0)
            put("is_flagged", if (task.isFlagged) 1 else 0)
            put("completed_date", task.completedDate?.toString())
            put("created_at", task.createdAt.toString())
        }
    }

    private fun cursorToTask(cursor: android.database.Cursor): Task {
        val customRepeatRaw = cursor.getString(cursor.getColumnIndexOrThrow("custom_repeat_days"))
        val customRepeatDays = customRepeatRaw
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        return Task(
            id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
            title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
            categoryId = cursor.getString(cursor.getColumnIndexOrThrow("category_id")),
            categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name")),
            dueDate = cursor.getString(cursor.getColumnIndexOrThrow("due_date"))?.let { LocalDate.parse(it) },
            dueTime = cursor.getString(cursor.getColumnIndexOrThrow("due_time"))?.let { LocalTime.parse(it) },
            hasReminder = cursor.getInt(cursor.getColumnIndexOrThrow("has_reminder")) == 1,
            reminderTime = cursor.getString(cursor.getColumnIndexOrThrow("reminder_time"))?.let { LocalTime.parse(it) },
            repeatType = RepeatType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("repeat_type"))),
            customRepeatDays = customRepeatDays,
            notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")),
            isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("is_completed")) == 1,
            isFlagged = cursor.getInt(cursor.getColumnIndexOrThrow("is_flagged")) == 1,
            completedDate = cursor.getString(cursor.getColumnIndexOrThrow("completed_date"))?.let { LocalDate.parse(it) },
            createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))?.let { LocalDateTime.parse(it) }
                ?: LocalDateTime.now()
        )
    }

    private fun seedDefaultCategories(db: SQLiteDatabase) {
        val defaults = listOf(
            Category(DEFAULT_CATEGORY_ID, "All", "#4da8a3"),
            Category("cat_work", "Work", "#2196F3"),
            Category("cat_personal", "Personal", "#9C27B0"),
            Category("cat_wishlist", "Wishlist", "#FF9800"),
            Category("cat_birthday", "Birthday", "#4CAF50"),
            Category("cat_sports", "Sports", "#3F51B5")
        )

        defaults.forEach { category ->
            val values = ContentValues().apply {
                put("id", category.id)
                put("name", category.name)
                put("color", category.color)
                put("created_at", System.currentTimeMillis())
            }
            db.insert("categories", null, values)
        }
    }

    companion object {
        private const val DATABASE_NAME = "todo_list_personal.db"
        private const val DATABASE_VERSION = 1
        private const val DEFAULT_CATEGORY_ID = "cat_all"

        private val TASK_COLUMNS = arrayOf(
            "id",
            "title",
            "category_id",
            "category_name",
            "due_date",
            "due_time",
            "has_reminder",
            "reminder_time",
            "repeat_type",
            "custom_repeat_days",
            "notes",
            "is_completed",
            "is_flagged",
            "completed_date",
            "created_at"
        )
    }
}
