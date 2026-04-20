package com.example.todolist.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todolist.domain.model.RepeatMode

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val notes: String? = null,
    val deadlineMillis: Long? = null,
    val deadlineHasTime: Boolean = false,
    val isDone: Boolean = false,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
    val repeatMode: RepeatMode = RepeatMode.NONE,
    val repeatDays: String? = null,
    val groupId: String? = null,
    val isImportant: Boolean = false
)
