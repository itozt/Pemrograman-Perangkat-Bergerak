package com.example.todolist.di

import android.content.Context
import androidx.room.Room
import com.example.todolist.data.local.db.AppDatabase
import com.example.todolist.data.repository.TaskRepository
import com.example.todolist.data.repository.TaskRepositoryImpl

object AppModule {
    @Volatile
    private var database: AppDatabase? = null

    fun provideTaskRepository(context: Context): TaskRepository {
        val db = database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "task_db"
            ).fallbackToDestructiveMigration(dropAllTables = true).build().also { database = it }
        }

        return TaskRepositoryImpl(db.taskDao())
    }
}
