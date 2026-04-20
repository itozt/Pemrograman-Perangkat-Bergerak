package com.example.todolist.data.repository

import com.example.todolist.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(): Flow<List<Task>>

    suspend fun addTask(
        title: String,
        notes: String? = null,
        deadlineMillis: Long? = null,
        deadlineHasTime: Boolean = false,
        repeatMode: com.example.todolist.domain.model.RepeatMode = com.example.todolist.domain.model.RepeatMode.NONE,
        repeatDays: String? = null,
        groupId: String? = null
    ): Long

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)

    suspend fun restoreTask(task: Task): Long
}
