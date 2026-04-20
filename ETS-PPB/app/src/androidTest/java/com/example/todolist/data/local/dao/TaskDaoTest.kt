package com.example.todolist.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todolist.data.local.db.AppDatabase
import com.example.todolist.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        taskDao = database.taskDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun observeAll_ordersByDeadlineThenCreatedAtDesc() = runTest {
        taskDao.insert(
            task(title = "Tanpa deadline", deadlineMillis = null, createdAt = 100)
        )
        taskDao.insert(
            task(title = "Deadline lambat", deadlineMillis = 2_000L, createdAt = 101)
        )
        taskDao.insert(
            task(title = "Deadline awal lama", deadlineMillis = 1_000L, createdAt = 90)
        )
        taskDao.insert(
            task(title = "Deadline awal baru", deadlineMillis = 1_000L, createdAt = 110)
        )

        val titles = taskDao.observeAll().first().map { it.title }

        assertEquals(
            listOf(
                "Deadline awal baru",
                "Deadline awal lama",
                "Deadline lambat",
                "Tanpa deadline"
            ),
            titles
        )
    }

    @Test
    fun updateAndDelete_changesDataAsExpected() = runTest {
        val id = taskDao.insert(task(title = "Belajar", deadlineMillis = null, createdAt = 1L))
        var current = taskDao.observeAll().first().first { it.id == id }

        taskDao.update(current.copy(isDone = true, updatedAtMillis = 999L))
        current = taskDao.observeAll().first().first { it.id == id }
        assertTrue(current.isDone)

        taskDao.delete(current)
        val all = taskDao.observeAll().first()
        assertTrue(all.isEmpty())
    }

    private fun task(title: String, deadlineMillis: Long?, createdAt: Long): TaskEntity {
        return TaskEntity(
            title = title,
            notes = null,
            deadlineMillis = deadlineMillis,
            deadlineHasTime = false,
            isDone = false,
            createdAtMillis = createdAt,
            updatedAtMillis = createdAt
        )
    }
}
