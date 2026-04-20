package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.di.AppModule
import com.example.todolist.domain.model.Task
import com.example.todolist.ui.calendar.CalendarScreen
import com.example.todolist.ui.tasklist.TaskFilter
import com.example.todolist.ui.tasklist.TaskListScreen
import com.example.todolist.ui.theme.ToDoListTheme
import com.example.todolist.viewmodel.TaskListViewModel
import com.example.todolist.viewmodel.TaskListViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                var showSplash by remember { mutableStateOf(true) }
                
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(1500)
                    showSplash = false
                }

                if (showSplash) {
                    SplashScreen()
                } else {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Tugas") },
                    label = { Text("Tugas") },
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Kalender") },
                    label = { Text("Kalender") },
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(innerPadding)
        ) { page ->
            if (page == 0) {
                ToDoListApp()
            } else {
                val context = LocalContext.current
                val repository = remember(context) { AppModule.provideTaskRepository(context) }
                val taskListViewModel: TaskListViewModel = viewModel(
                    factory = TaskListViewModelFactory(repository)
                )
                val uiState by taskListViewModel.uiState.collectAsState()

                CalendarScreen(
                    tasks = uiState.tasks,
                    onTaskCheckedChange = taskListViewModel::onTaskCheckedChanged,
                    onToggleFlagTask = { task: Task, isImportant: Boolean ->
                        taskListViewModel.editTask(task.copy(isImportant = isImportant))
                    },
                    onDeleteTask = taskListViewModel::deleteTask,
                    onEditTask = taskListViewModel::editTask,
                    onAddTask = { title: String, notes: String?, deadlineMillis: Long?, deadlineHasTime: Boolean, repeatMode: com.example.todolist.domain.model.RepeatMode, repeatDays: String?, repeatCount: Int? ->
                        taskListViewModel.addTask(title, notes, deadlineMillis, deadlineHasTime, repeatMode, repeatDays, repeatCount)
                    }
                )
            }
        }
    }
}

@Composable
fun ToDoListApp() {
    val context = LocalContext.current
    val repository = remember(context) {
        AppModule.provideTaskRepository(context)
    }
    val taskListViewModel: TaskListViewModel = viewModel(
        factory = TaskListViewModelFactory(repository)
    )
    val uiState by taskListViewModel.uiState.collectAsState()

    TaskListScreen(
        tasks = uiState.tasks,
        groupedTasks = uiState.groupedTasks,
        showCompleted = uiState.showCompleted,
        onToggleShowCompleted = taskListViewModel::toggleShowCompleted,
        onTaskCheckedChange = taskListViewModel::onTaskCheckedChanged,
        onToggleFlagTask = { task, isImportant ->
            taskListViewModel.editTask(task.copy(isImportant = isImportant))
        },
        onAddTask = { title, notes, deadlineMillis, deadlineHasTime, repeatMode, repeatDays, repeatCount -> taskListViewModel.addTask(title, notes, deadlineMillis, deadlineHasTime, repeatMode, repeatDays, repeatCount) },
        onEditTask = taskListViewModel::editTask,
        onDeleteTask = taskListViewModel::deleteTask,
        onUndoDelete = taskListViewModel::undoDelete
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ToDoListTheme {
        TaskListScreen(
            tasks = listOf(
                Task(
                    id = 1,
                    title = "Contoh tugas",
                    notes = "Ini hanya preview",
                    deadlineMillis = null,
                    deadlineHasTime = false,
                    isDone = false,
                    createdAtMillis = 0L,
                    updatedAtMillis = 0L
                )
            ),
            showCompleted = false
        )
    }
}
@Composable
fun SplashScreen() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.task_planner_logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(200.dp)
        )
    }
}