package com.example.todolist.data.repository

import com.example.todolist.data.local.dao.TaskDao
import com.example.todolist.data.local.entity.TaskEntity
import com.example.todolist.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun observeTasks(): Flow<List<Task>> {
        return taskDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addTask(
        title: String,
        notes: String?,
        deadlineMillis: Long?,
        deadlineHasTime: Boolean,
        repeatMode: com.example.todolist.domain.model.RepeatMode,
        repeatDays: String?,
        groupId: String?
    ): Long {
        val now = System.currentTimeMillis()
        return taskDao.insert(
            TaskEntity(
                title = title,
                notes = notes,
                deadlineMillis = deadlineMillis,
                deadlineHasTime = deadlineHasTime,
                isDone = false,
                createdAtMillis = now,
                updatedAtMillis = now,
                repeatMode = repeatMode,
                repeatDays = repeatDays,
                groupId = groupId
            )
        )
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(task.toEntity())
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.delete(task.toEntity())
    }

    override suspend fun restoreTask(task: Task): Long {
        return taskDao.insert(task.toEntity())
    }
}

private fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        notes = notes,
        deadlineMillis = deadlineMillis,
        deadlineHasTime = deadlineHasTime,
        isDone = isDone,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis,
        repeatMode = repeatMode,
        repeatDays = repeatDays,
        groupId = groupId,
        isImportant = isImportant
    )
}

private fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        notes = notes,
        deadlineMillis = deadlineMillis,
        deadlineHasTime = deadlineHasTime,
        isDone = isDone,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis,
        repeatMode = repeatMode,
        repeatDays = repeatDays,
        groupId = groupId,
        isImportant = isImportant
    )
}
