package com.example.todolist.viewmodel

import com.example.todolist.MainDispatcherRule
import com.example.todolist.data.repository.TaskRepository
import com.example.todolist.domain.model.Task
import com.example.todolist.ui.tasklist.TaskFilter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun setFilter_active_onlyShowsUndoneTasks() = runTest {
        val repository = FakeTaskRepository()
        repository.seed(
            listOf(
                sampleTask(id = 1, title = "A", isDone = false),
                sampleTask(id = 2, title = "B", isDone = true)
            )
        )
        val viewModel = TaskListViewModel(repository)
        val collectJob = backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.setFilter(TaskFilter.ACTIVE)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(TaskFilter.ACTIVE, state.selectedFilter)
        assertEquals(1, state.tasks.size)
        assertEquals("A", state.tasks.first().title)

        collectJob.cancel()
    }

    @Test
    fun addTask_trimsTitleAndBlankNotes() = runTest {
        val repository = FakeTaskRepository()
        val viewModel = TaskListViewModel(repository)

        viewModel.addTask(
            title = "  Belajar PPB  ",
            notes = "   ",
            deadlineMillis = null,
            deadlineHasTime = false
        )
        advanceUntilIdle()

        val inserted = repository.currentTasks().first()
        assertEquals("Belajar PPB", inserted.title)
        assertNull(inserted.notes)
        assertFalse(inserted.deadlineHasTime)
    }

    @Test
    fun deleteAndUndo_restoresTask() = runTest {
        val repository = FakeTaskRepository()
        val initialTask = sampleTask(id = 7, title = "Tugas ETS")
        repository.seed(listOf(initialTask))
        val viewModel = TaskListViewModel(repository)

        viewModel.deleteTask(initialTask)
        advanceUntilIdle()
        assertTrue(repository.currentTasks().isEmpty())

        viewModel.undoDelete()
        advanceUntilIdle()
        assertEquals(1, repository.currentTasks().size)
        assertEquals("Tugas ETS", repository.currentTasks().first().title)
    }

    @Test
    fun onTaskCheckedChanged_updatesDoneState() = runTest {
        val repository = FakeTaskRepository()
        val initialTask = sampleTask(id = 11, title = "Checklist", isDone = false)
        repository.seed(listOf(initialTask))
        val viewModel = TaskListViewModel(repository)

        viewModel.onTaskCheckedChanged(initialTask, isChecked = true)
        advanceUntilIdle()

        val updated = repository.currentTasks().first()
        assertTrue(updated.isDone)
    }

    private fun sampleTask(
        id: Long,
        title: String,
        isDone: Boolean = false
    ): Task {
        return Task(
            id = id,
            title = title,
            notes = null,
            deadlineMillis = null,
            deadlineHasTime = false,
            isDone = isDone,
            createdAtMillis = 1L,
            updatedAtMillis = 1L
        )
    }
}

private class FakeTaskRepository : TaskRepository {
    private val tasks = MutableStateFlow<List<Task>>(emptyList())
    private var nextId = 1L

    override fun observeTasks(): Flow<List<Task>> = tasks

    override suspend fun addTask(
        title: String,
        notes: String?,
        deadlineMillis: Long?,
        deadlineHasTime: Boolean,
        repeatMode: com.example.todolist.domain.model.RepeatMode,
        repeatDays: String?
    ): Long {
        val id = nextId++
        val now = System.currentTimeMillis()
        val task = Task(
            id = id,
            title = title,
            notes = notes,
            deadlineMillis = deadlineMillis,
            deadlineHasTime = deadlineHasTime,
            isDone = false,
            createdAtMillis = now,
            updatedAtMillis = now,
            repeatMode = repeatMode,
            repeatDays = repeatDays
        )
        tasks.value = tasks.value + task
        return id
    }

    override suspend fun updateTask(task: Task) {
        tasks.value = tasks.value.map { existing ->
            if (existing.id == task.id) task else existing
        }
    }

    override suspend fun deleteTask(task: Task) {
        tasks.value = tasks.value.filterNot { it.id == task.id }
    }

    override suspend fun restoreTask(task: Task): Long {
        tasks.value = tasks.value.filterNot { it.id == task.id } + task
        return task.id
    }

    fun seed(values: List<Task>) {
        tasks.value = values
        nextId = (values.maxOfOrNull { it.id } ?: 0L) + 1
    }

    fun currentTasks(): List<Task> = tasks.value
}
