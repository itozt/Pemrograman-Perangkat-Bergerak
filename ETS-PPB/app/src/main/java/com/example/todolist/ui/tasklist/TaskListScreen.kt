package com.example.todolist.ui.tasklist

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todolist.R
import com.example.todolist.domain.model.Task
import com.example.todolist.viewmodel.TaskGroup
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

enum class TaskFilter {
    ALL,
    ACTIVE,
    COMPLETED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    tasks: List<Task>,
    groupedTasks: List<TaskGroup> = emptyList(),
    showCompleted: Boolean = false,
    onToggleShowCompleted: () -> Unit = {},
    onTaskCheckedChange: (Task, Boolean) -> Unit = { _, _ -> },
    onToggleFlagTask: (Task, Boolean) -> Unit = { _, _ -> },
        onAddTask: (
        title: String,
        notes: String?,
        deadlineMillis: Long?,
        deadlineHasTime: Boolean,
        repeatMode: com.example.todolist.domain.model.RepeatMode,
        repeatDays: String?,
        repeatCount: Int?
    ) -> Unit = { _, _, _, _, _, _, _ -> },
    onEditTask: (Task) -> Unit = {},
    onDeleteTask: (Task) -> Unit = {},
    onUndoDelete: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    val deletedMessagePrefix = stringResource(id = R.string.tugas_dihapus)
    val undoLabel = stringResource(id = R.string.batal)

    if (showAddDialog || taskToEdit != null) {
        AddTaskDialog(
            initialTask = taskToEdit,
            onDismiss = {
                showAddDialog = false
                taskToEdit = null
            },
            onDelete = { task ->
                onDeleteTask(task)
                showAddDialog = false
                taskToEdit = null
            },
            onSave = { title, notes, deadlineMillis, deadlineHasTime, repeatMode, repeatDays, repeatCount ->
                if (taskToEdit != null) {
                    onEditTask(
                        taskToEdit!!.copy(
                            title = title,
                            notes = notes,
                            deadlineMillis = deadlineMillis,
                            deadlineHasTime = deadlineHasTime,
                            repeatMode = repeatMode,
                            repeatDays = repeatDays
                        )
                    )
                } else {
                    onAddTask(title, notes, deadlineMillis, deadlineHasTime, repeatMode, repeatDays, repeatCount)
                }
                showAddDialog = false
                taskToEdit = null
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            var menuExpanded by remember { mutableStateOf(false) }
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(id = R.string.judul_tugas_harian),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    androidx.compose.material3.IconButton(onClick = { menuExpanded = true }) {
                        Icon(androidx.compose.material.icons.Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    androidx.compose.material3.DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text(if (showCompleted) "Sembunyikan yang selesai" else "Tampilkan yang selesai") },
                            onClick = {
                                onToggleShowCompleted()
                                menuExpanded = false
                            }
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        TaskListContent(
            tasks = tasks,
            groupedTasks = groupedTasks,
            innerPadding = innerPadding,
            onTaskCheckedChange = onTaskCheckedChange,
            onToggleFlagTask = onToggleFlagTask,
            onEditTask = { task -> taskToEdit = task },
            onDeleteTask = { task ->
                scope.launch {
                    onDeleteTask(task)
                    val result = snackbarHostState.showSnackbar(
                        message = "$deletedMessagePrefix: ${task.title}",
                        actionLabel = undoLabel,
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        onUndoDelete()
                    }
                }
            }
        )
    }
}

@Composable
private fun TaskListContent(
    tasks: List<Task>,
    groupedTasks: List<TaskGroup> = emptyList(),
    innerPadding: PaddingValues,
    onTaskCheckedChange: (Task, Boolean) -> Unit,
    onToggleFlagTask: (Task, Boolean) -> Unit,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit
) {
    var expandedStates by remember { mutableStateOf(mapOf("Hari Ini" to true, "Tanpa Tenggat" to true)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (groupedTasks.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.empty_tugas),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                groupedTasks.forEach { group ->
                    item {
                        val isExpanded = expandedStates[group.category] ?: false
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedStates = expandedStates.toMutableMap().apply {
                                        this[group.category] = !isExpanded
                                    }
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Expand/Collapse"
                            )
                            Text(
                                text = "${group.category} (${group.tasks.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                    if (expandedStates[group.category] == true) {
                        items(items = group.tasks, key = { it.id }) { task ->
                            TaskItem(
                                task = task,
                                onCheckedChange = { checked ->
                                    onTaskCheckedChange(task, checked)
                                },
                                onFlagChange = { isImportant ->
                                    onToggleFlagTask(task, isImportant)
                                },
                                onEdit = { onEditTask(task) },
                                onDelete = { onDeleteTask(task) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onFlagChange: (Boolean) -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit
) {
    val density = androidx.compose.ui.platform.LocalDensity.current
    var offsetX by remember { mutableStateOf(0f) }
    val maxSwipeOffSet = with(density) { -80.dp.toPx() } // adjust based on density

    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        label = "swipeOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(12.dp))
            .clickable { onDelete() },
        contentAlignment = Alignment.CenterEnd
    ) {
        androidx.compose.material3.IconButton(
            onClick = onDelete,
            modifier = Modifier.padding(end = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(id = R.string.hapus),
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(animatedOffsetX.toInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = { offsetX = if (offsetX < maxSwipeOffSet / 2) maxSwipeOffSet else 0f },
                        onDragCancel = { offsetX = 0f }
                    ) { change: androidx.compose.ui.input.pointer.PointerInputChange, dragAmount: Float ->
                        change.consume()
                        offsetX = (offsetX + dragAmount).coerceIn(maxSwipeOffSet, 0f)
                    }
                }
                .clickable { onEdit() },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = task.isDone,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (task.isDone) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    task.notes?.takeIf { it.isNotBlank() }?.let {
                        Spacer(modifier = Modifier.height(0.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(0.dp))

                    val locale = remember { java.util.Locale.forLanguageTag("id-ID") }
                    val deadlineText = task.deadlineMillis?.let { deadlineMillis ->
                        val pattern = if (task.deadlineHasTime) {
                            "dd MMM yyyy, HH:mm"
                        } else {
                            "dd MMM yyyy"
                        }
                        val formatter = SimpleDateFormat(pattern, locale)
                        formatter.format(Date(deadlineMillis))
                    } ?: stringResource(id = R.string.tanpa_deadline)

                    Text(
                        text = deadlineText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                androidx.compose.material3.IconButton(
                    onClick = { onFlagChange(!task.isImportant) },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(
                        imageVector = if (task.isImportant) Icons.Filled.Flag else Icons.Outlined.Flag,
                        contentDescription = stringResource(id = R.string.tandai_penting),
                        tint = if (task.isImportant) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
