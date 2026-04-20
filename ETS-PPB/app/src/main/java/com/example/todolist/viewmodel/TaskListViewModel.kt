package com.example.todolist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.repository.TaskRepository
import com.example.todolist.domain.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TaskGroup(val category: String, val tasks: List<Task>)

data class TaskListUiState(
    val showCompleted: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val groupedTasks: List<TaskGroup> = emptyList()
)

class TaskListViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val showCompleted = MutableStateFlow(false)
    private var recentlyDeletedTask: Task? = null

    val uiState: StateFlow<TaskListUiState> = combine(
        taskRepository.observeTasks(),
        showCompleted
    ) { allTasks, show ->
        val filteredTasks = if (show) allTasks else allTasks.filter { !it.isDone }

        val grouped = groupTasksByCategory(filteredTasks)

        TaskListUiState(
            showCompleted = show,
            tasks = filteredTasks,
            groupedTasks = grouped
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TaskListUiState()
    )

    fun toggleShowCompleted() {
        showCompleted.value = !showCompleted.value
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) {
        if (task.isDone == isChecked) return

        viewModelScope.launch {
            taskRepository.updateTask(
                task.copy(
                    isDone = isChecked,
                    updatedAtMillis = System.currentTimeMillis()
                )
            )
        }
    }

        fun addTask(
        title: String,
        notes: String?,
        deadlineMillis: Long?,
        deadlineHasTime: Boolean,
        repeatMode: com.example.todolist.domain.model.RepeatMode = com.example.todolist.domain.model.RepeatMode.NONE,
        repeatDays: String? = null,
        repeatCount: Int? = null
    ) {
        val normalizedTitle = title.trim()
        if (normalizedTitle.isBlank()) return

        viewModelScope.launch {
            if (repeatMode == com.example.todolist.domain.model.RepeatMode.NONE || deadlineMillis == null) {
                taskRepository.addTask(
                    title = normalizedTitle,
                    notes = notes?.trim()?.ifBlank { null },
                    deadlineMillis = deadlineMillis,
                    deadlineHasTime = deadlineHasTime,
                    repeatMode = repeatMode,
                    repeatDays = repeatDays
                )
                return@launch
            }

            val groupId = java.util.UUID.randomUUID().toString()
            val cal = java.util.Calendar.getInstance()
            cal.timeInMillis = deadlineMillis

                        val limitCal = java.util.Calendar.getInstance()
            limitCal.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER)
            limitCal.set(java.util.Calendar.DAY_OF_MONTH, 31)
            limitCal.set(java.util.Calendar.HOUR_OF_DAY, 23)
            limitCal.set(java.util.Calendar.MINUTE, 59)
            val maxLimitMillis = limitCal.timeInMillis
            
            val occurrences = repeatCount ?: 365 
            for (i in 0 until occurrences) {
                if (repeatCount == null && cal.timeInMillis > maxLimitMillis) {
                    break
                }
                taskRepository.addTask(
                    title = normalizedTitle,
                    notes = notes?.trim()?.ifBlank { null },
                    deadlineMillis = cal.timeInMillis,
                    deadlineHasTime = deadlineHasTime,
                    repeatMode = repeatMode,
                    repeatDays = repeatDays,
                    groupId = groupId
                )

                // Kalkulasi tanggal berikutnya
                when (repeatMode) {
                    com.example.todolist.domain.model.RepeatMode.DAILY -> cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
                    com.example.todolist.domain.model.RepeatMode.WEEKLY -> cal.add(java.util.Calendar.WEEK_OF_YEAR, 1)
                    com.example.todolist.domain.model.RepeatMode.MONTHLY -> cal.add(java.util.Calendar.MONTH, 1)
                    com.example.todolist.domain.model.RepeatMode.CUSTOM_DAYS -> {
                        val daysSet = repeatDays?.split(",")?.map { it.trim() }?.toSet() ?: emptySet()
                        if (daysSet.isEmpty()) break
                        
                        var foundNext = false
                        for (d in 1..7) {
                            cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
                            val dayName = when (cal.get(java.util.Calendar.DAY_OF_WEEK)) {
                                java.util.Calendar.MONDAY -> "Mon"
                                java.util.Calendar.TUESDAY -> "Tue"
                                java.util.Calendar.WEDNESDAY -> "Wed"
                                java.util.Calendar.THURSDAY -> "Thu"
                                java.util.Calendar.FRIDAY -> "Fri"
                                java.util.Calendar.SATURDAY -> "Sat"
                                java.util.Calendar.SUNDAY -> "Sun"
                                else -> ""
                            }
                            if (daysSet.contains(dayName)) {
                                foundNext = true
                                break
                            }
                        }
                        if (!foundNext) break 
                    }
                    else -> break
                }
            }
        }
    }

    fun editTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(updatedAtMillis = System.currentTimeMillis()))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            recentlyDeletedTask = task
            taskRepository.deleteTask(task)
        }
    }

    fun undoDelete() {
        val taskToRestore = recentlyDeletedTask ?: return
        viewModelScope.launch {
            taskRepository.restoreTask(taskToRestore)
            recentlyDeletedTask = null
        }
    }

    private fun groupTasksByCategory(tasks: List<Task>): List<TaskGroup> {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val todayStart = calendar.timeInMillis
        
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        val tomorrowStart = calendar.timeInMillis
        
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        val lusaStart = calendar.timeInMillis
        
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        val afterLusaStart = calendar.timeInMillis

        val lewatWaktu = mutableListOf<Task>()
        val hariIni = mutableListOf<Task>()
        val besok = mutableListOf<Task>()
        val lusa = mutableListOf<Task>()
        val tanpaTenggat = mutableListOf<Task>()

        tasks.forEach { task ->
            if (task.deadlineMillis == null) {
                tanpaTenggat.add(task)
            } else {
                when {
                    task.deadlineMillis < todayStart -> {
                        if (!task.isDone) lewatWaktu.add(task)
                    }
                    task.deadlineMillis < tomorrowStart -> hariIni.add(task)
                    task.deadlineMillis < lusaStart -> besok.add(task)
                    task.deadlineMillis < afterLusaStart -> lusa.add(task)
                }
            }
        }

        val result = mutableListOf<TaskGroup>()
        if (lewatWaktu.isNotEmpty()) result.add(TaskGroup("Lewat Waktu", lewatWaktu))
        if (hariIni.isNotEmpty()) result.add(TaskGroup("Hari Ini", hariIni))
        if (besok.isNotEmpty()) result.add(TaskGroup("Besok", besok))
        if (lusa.isNotEmpty()) result.add(TaskGroup("Lusa", lusa))
        if (tanpaTenggat.isNotEmpty()) result.add(TaskGroup("Tanpa Tenggat", tanpaTenggat))

        return result
    }
}

class TaskListViewModelFactory(
    private val taskRepository: TaskRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskListViewModel(taskRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
