package com.example.todolistpersonal

import android.app.Dialog
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import android.view.View

/**
 * UIHelper - Utility functions untuk common UI operations
 * Fokus pada HCI principles: clear feedback, error messages, user confirmation
 */
object UIHelper {

    /**
     * Show short toast message
     */
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Show long toast message
     */
    fun showLongToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Show snackbar dengan action button
     */
    fun showSnackbar(view: View, message: String, actionText: String? = null, action: (() -> Unit)? = null) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        if (actionText != null && action != null) {
            snackbar.setAction(actionText) { action() }
        }
        snackbar.show()
    }

    /**
     * Show confirmation dialog sebelum delete
     * HCI: Prevent user errors dengan confirmation
     */
    fun showDeleteConfirmation(
        context: Context,
        title: String = "Delete",
        message: String = "Are you sure?",
        onConfirm: () -> Unit
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Delete") { _, _ -> onConfirm() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Show info dialog
     */
    fun showInfoDialog(context: Context, title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    /**
     * Show error dialog dengan actionable message
     * HCI: Clear error messages dengan solusi
     */
    fun showErrorDialog(context: Context, title: String = "Error", message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
}

/**
 * ValidationHelper - Input validation utilities
 * HCI: Real-time validation feedback untuk mencegah error
 */
object ValidationHelper {

    /**
     * Validate task title - tidak boleh kosong
     */
    fun validateTaskTitle(title: String): Pair<Boolean, String?> {
        val trimmed = title.trim()
        return when {
            trimmed.isEmpty() -> Pair(false, "Task title cannot be empty")
            trimmed.length > 100 -> Pair(false, "Task title too long (max 100 characters)")
            else -> Pair(true, null)
        }
    }

    /**
     * Validate notes - optional tapi limited length
     */
    fun validateNotes(notes: String): Pair<Boolean, String?> {
        return when {
            notes.trim().length > 500 -> Pair(false, "Notes too long (max 500 characters)")
            else -> Pair(true, null)
        }
    }

    /**
     * Validate category selection
     */
    fun validateCategory(categoryId: String): Pair<Boolean, String?> {
        return when {
            categoryId.isEmpty() -> Pair(false, "Please select a category")
            else -> Pair(true, null)
        }
    }

    /**
     * Validate group name saat create group
     */
    fun validateGroupName(name: String): Pair<Boolean, String?> {
        val trimmed = name.trim()
        return when {
            trimmed.isEmpty() -> Pair(false, "Group name cannot be empty")
            trimmed.length > 50 -> Pair(false, "Group name too long (max 50 characters)")
            else -> Pair(true, null)
        }
    }
}

/**
 * FormatHelper - Formatting utilities untuk display
 * HCI: Consistent dan meaningful presentation of data
 */
object FormatHelper {

    /**
     * Format task count untuk display
     */
    fun formatTaskCount(count: Int): String {
        return "$count task" + (if (count != 1) "s" else "")
    }

    /**
     * Format repeat type dengan description
     */
    fun formatRepeatType(repeatType: RepeatType): String {
        return when (repeatType) {
            RepeatType.NONE -> "No repeat"
            RepeatType.HOURLY -> "Every hour"
            RepeatType.DAILY -> "Every day"
            RepeatType.WEEKLY -> "Every week"
            RepeatType.MONTHLY -> "Every month"
            RepeatType.YEARLY -> "Every year"
            RepeatType.CUSTOM -> "Custom repeat"
        }
    }

    /**
     * Get meaningful status text untuk task
     */
    fun getTaskStatus(task: Task): String {
        return when {
            task.isCompleted -> "Completed"
            task.dueDate != null && task.dueDate.isBefore(java.time.LocalDate.now()) -> "Overdue"
            task.dueDate == java.time.LocalDate.now() -> "Due today"
            else -> "Pending"
        }
    }

    /**
     * Get color untuk status
     */
    fun getStatusColor(task: Task, context: Context): Int {
        return when {
            task.isCompleted -> context.getColor(android.R.color.darker_gray)
            task.dueDate != null && task.dueDate.isBefore(java.time.LocalDate.now()) ->
                context.getColor(android.R.color.holo_red_light)
            task.dueDate == java.time.LocalDate.now() ->
                context.getColor(android.R.color.holo_orange_light)
            else -> context.getColor(android.R.color.darker_gray)
        }
    }
}
