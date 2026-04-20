package com.example.todolist.domain.model

import java.util.Calendar

data class Task(
    val id: Long,
    val title: String,
    val notes: String?,
    val deadlineMillis: Long?,
    val deadlineHasTime: Boolean,
    val isDone: Boolean,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
    val repeatMode: RepeatMode = RepeatMode.NONE,
    val repeatDays: String? = null,
    val groupId: String? = null,
    val isImportant: Boolean = false
)

fun Task.isOnDate(targetDate: Calendar): Boolean {
    if (deadlineMillis == null) return false

    val deadlineCal = Calendar.getInstance().apply {
        timeInMillis = deadlineMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val targetCal = Calendar.getInstance().apply {
        timeInMillis = targetDate.timeInMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    // If repeats, targetDate shouldn't be before deadline
    if (repeatMode != RepeatMode.NONE && targetCal.before(deadlineCal)) return false

    return when (repeatMode) {
        RepeatMode.NONE -> targetCal.timeInMillis == deadlineCal.timeInMillis
        RepeatMode.DAILY -> targetCal.timeInMillis >= deadlineCal.timeInMillis
        RepeatMode.WEEKLY -> {
            targetCal.timeInMillis >= deadlineCal.timeInMillis &&
                    targetCal.get(Calendar.DAY_OF_WEEK) == deadlineCal.get(Calendar.DAY_OF_WEEK)
        }
        RepeatMode.MONTHLY -> {
            targetCal.timeInMillis >= deadlineCal.timeInMillis &&
                    targetCal.get(Calendar.DAY_OF_MONTH) == deadlineCal.get(Calendar.DAY_OF_MONTH)
        }
        RepeatMode.CUSTOM_DAYS -> {
            if (targetCal.timeInMillis < deadlineCal.timeInMillis) return false
            val dayOfWeek = targetCal.get(Calendar.DAY_OF_WEEK)
            val dayString = when (dayOfWeek) {
                Calendar.MONDAY -> "Mon"
                Calendar.TUESDAY -> "Tue"
                Calendar.WEDNESDAY -> "Wed"
                Calendar.THURSDAY -> "Thu"
                Calendar.FRIDAY -> "Fri"
                Calendar.SATURDAY -> "Sat"
                Calendar.SUNDAY -> "Sun"
                else -> ""
            }
            repeatDays?.contains(dayString) == true
        }
    }
}
