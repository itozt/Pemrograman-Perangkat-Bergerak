package com.example.todolist.ui.calendar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.R
import com.example.todolist.domain.model.Task
import com.example.todolist.ui.tasklist.TaskItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun isSameDay(millis1: Long, millis2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = millis1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = millis2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

@Composable
fun MonthPickerDialog(
    selectedMonth: Int,
    onMonthSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val monthNames = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Bulan", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                for (row in 0..3) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (col in 0..2) {
                            val monthIndex = row * 3 + col
                            if (monthIndex < monthNames.size) {
                                Button(
                                    onClick = { onMonthSelected(monthIndex) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                        containerColor = if (monthIndex == selectedMonth) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.secondaryContainer
                                    )
                                ) {
                                    Text(
                                        monthNames[monthIndex].substring(0, 3),
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (monthIndex == selectedMonth)
                                            MaterialTheme.colorScheme.onPrimary
                                        else
                                            MaterialTheme.colorScheme.onSecondaryContainer,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun YearPickerDialog(
    selectedYear: Int,
    onYearSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val yearRange = (currentYear - 10)..(currentYear + 10)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Tahun", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                val yearList = yearRange.toList()
                for (row in yearList.indices step 3) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (col in 0..2) {
                            val yearIndex = row + col
                            if (yearIndex < yearList.size) {
                                val year = yearList[yearIndex]
                                Button(
                                    onClick = { onYearSelected(year) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                        containerColor = if (year == selectedYear) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.secondaryContainer
                                    )
                                ) {
                                    Text(
                                        year.toString(),
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (year == selectedYear)
                                            MaterialTheme.colorScheme.onPrimary
                                        else
                                            MaterialTheme.colorScheme.onSecondaryContainer,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun CalendarScreen(
    tasks: List<Task>,
    onTaskCheckedChange: (Task, Boolean) -> Unit,
    onToggleFlagTask: (Task, Boolean) -> Unit = { _, _ -> },
    onDeleteTask: (Task) -> Unit,
    onEditTask: (Task) -> Unit,
    onAddTask: (String, String?, Long?, Boolean, com.example.todolist.domain.model.RepeatMode, String?, Int?) -> Unit
) {
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showYearPicker by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val initialPage = Int.MAX_VALUE / 2
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { Int.MAX_VALUE })
    val startMonthMillis = remember { Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }.timeInMillis }
    val currentMonth = Calendar.getInstance().apply { 
        timeInMillis = startMonthMillis
        add(Calendar.MONTH, pagerState.currentPage - initialPage)
    }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    val todayCalendar = remember { Calendar.getInstance() }

    // Month picker dialog
    if (showMonthPicker) {
        MonthPickerDialog(
            selectedMonth = currentMonth.get(Calendar.MONTH),
            onMonthSelected = { month ->
                val targetMonth = Calendar.getInstance().apply {
                    timeInMillis = startMonthMillis
                    set(Calendar.MONTH, month)
                }
                val monthsFromStart = (targetMonth.get(Calendar.YEAR) - Calendar.getInstance().apply { timeInMillis = startMonthMillis }.get(Calendar.YEAR)) * 12 + 
                    (month - Calendar.getInstance().apply { timeInMillis = startMonthMillis }.get(Calendar.MONTH))
                coroutineScope.launch {
                    pagerState.animateScrollToPage(initialPage + monthsFromStart)
                }
                showMonthPicker = false
            },
            onDismiss = { showMonthPicker = false }
        )
    }

    // Year picker dialog
    if (showYearPicker) {
        YearPickerDialog(
            selectedYear = currentMonth.get(Calendar.YEAR),
            onYearSelected = { year ->
                val targetMonth = Calendar.getInstance().apply {
                    timeInMillis = startMonthMillis
                    set(Calendar.YEAR, year)
                }
                val monthsFromStart = (year - Calendar.getInstance().apply { timeInMillis = startMonthMillis }.get(Calendar.YEAR)) * 12 + 
                    (currentMonth.get(Calendar.MONTH) - Calendar.getInstance().apply { timeInMillis = startMonthMillis }.get(Calendar.MONTH))
                coroutineScope.launch {
                    pagerState.animateScrollToPage(initialPage + monthsFromStart)
                }
                showYearPicker = false
            },
            onDismiss = { showYearPicker = false }
        )
    }

    if (taskToEdit != null) {
        com.example.todolist.ui.tasklist.AddTaskDialog(
            initialTask = taskToEdit,
            onDismiss = { taskToEdit = null },
            onDelete = { task ->
                onDeleteTask(task)
                taskToEdit = null
            },
            onSave = { title, notes, deadlineMillis, deadlineHasTime, repeatMode, repeatDays, repeatCount ->
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
                taskToEdit = null
            }
        )
    }

    if (showAddDialog) {
        com.example.todolist.ui.tasklist.AddTaskDialog(
            initialDateMillis = selectedDate.timeInMillis,
            onDismiss = { showAddDialog = false },
            onSave = { title, notes, deadlineMillis, deadlineHasTime, repeatMode, repeatDays, repeatCount ->
                onAddTask(title, notes, deadlineMillis, deadlineHasTime, repeatMode, repeatDays, repeatCount)
                showAddDialog = false
            }
        )
    }

    val tasksOnSelectedDate = tasks.filter { task ->
        task.deadlineMillis != null && isSameDay(task.deadlineMillis, selectedDate.timeInMillis)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        @OptIn(ExperimentalMaterial3Api::class)
        TopAppBar(
            title = {
                Text(
                    text = "Kalender",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        )
        
        // Month/Year Navigation with Pickers - Combined in Single Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left group: Navigation arrows and month/year buttons
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous month button
                androidx.compose.material3.IconButton(
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Bulan Sebelumnya",
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Month button - text only without border
                androidx.compose.material3.TextButton(
                    onClick = { showMonthPicker = true },
                    modifier = Modifier.padding(horizontal = 2.dp)
                ) {
                    Text(
                        text = SimpleDateFormat("MMMM", Locale("id")).format(currentMonth.time),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Year button - text only without border
                androidx.compose.material3.TextButton(
                    onClick = { showYearPicker = true },
                    modifier = Modifier.padding(horizontal = 2.dp)
                ) {
                    Text(
                        text = SimpleDateFormat("yyyy", Locale.getDefault()).format(currentMonth.time),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Next month button
                androidx.compose.material3.IconButton(
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Bulan Berikutnya",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Right group: Today button
            androidx.compose.material3.TextButton(
                onClick = {
                    selectedDate = todayCalendar.clone() as Calendar
                    coroutineScope.launch {
                        val monthDiff = (todayCalendar.get(Calendar.YEAR) - Calendar.getInstance().apply { timeInMillis = startMonthMillis }.get(Calendar.YEAR)) * 12 +
                            (todayCalendar.get(Calendar.MONTH) - Calendar.getInstance().apply { timeInMillis = startMonthMillis }.get(Calendar.MONTH))
                        pagerState.animateScrollToPage(initialPage + monthDiff)
                    }
                },
                modifier = Modifier.padding(0.dp)
            ) {
                Text("Hari Ini", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelSmall)
            }
        }

        // Day headers (Sun, Mon, Tue, etc.)
        val daysOfWeek = listOf("M", "S", "R", "K", "J", "S", "M")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp, vertical = 1.dp)
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier
                        .weight(1f)
                        .padding(1.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(1.dp))

        // Calendar grid
        HorizontalPager(state = pagerState) { page ->
            val pageMonth = Calendar.getInstance().apply {
                timeInMillis = startMonthMillis
                add(Calendar.MONTH, page - initialPage)
            }
            val daysInMonth = pageMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
            val firstDayOfMonth = Calendar.getInstance().apply {
                timeInMillis = pageMonth.timeInMillis
                set(Calendar.DAY_OF_MONTH, 1)
            }.get(Calendar.DAY_OF_WEEK)
            val daysShift = if (firstDayOfMonth == Calendar.SUNDAY) 6 else firstDayOfMonth - 2

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(horizontal = 2.dp)
            ) {
                for (week in 0..5) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        for (dayOfWeek in 0..6) {
                            val dayNumber = week * 7 + dayOfWeek - daysShift + 1
                            if (dayNumber in 1..daysInMonth) {
                                val currentDate = Calendar.getInstance().apply {
                                    timeInMillis = pageMonth.timeInMillis
                                    set(Calendar.DAY_OF_MONTH, dayNumber)
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }

                            val isSelected = isSameDay(currentDate.timeInMillis, selectedDate.timeInMillis)
                            val isToday = isSameDay(currentDate.timeInMillis, todayCalendar.timeInMillis)
                            val hasTasks = tasks.any { task ->
                                task.deadlineMillis != null && isSameDay(task.deadlineMillis, currentDate.timeInMillis)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(1.dp)
                                    .background(
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        selectedDate = currentDate.clone() as Calendar
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = dayNumber.toString(),
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isToday && !isSelected) FontWeight.Bold else FontWeight.Normal,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 11.sp
                                    )
                                    if (hasTasks) {
                                        Box(
                                            modifier = Modifier
                                                .size(2.dp)
                                                .background(
                                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                                    shape = CircleShape
                                                )
                                        )
                                    }
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            }
        }

        androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))

        // Selected date tasks header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tugas: ${SimpleDateFormat("dd MMM yyyy", Locale("id")).format(selectedDate.time)}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            androidx.compose.material3.IconButton(onClick = { showAddDialog = true }, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Tugas", modifier = Modifier.size(20.dp))
            }
        }

        // Tasks list
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = tasksOnSelectedDate, key = { it.id }) { task ->
                AnimatedVisibility(
                    visible = true,
                    exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
                ) {
                    TaskItem(
                        task = task,
                        onCheckedChange = { isChecked -> onTaskCheckedChange(task, isChecked) },
                        onFlagChange = { isImportant -> onToggleFlagTask(task, isImportant) },
                        onDelete = { onDeleteTask(task) },
                        onEdit = { taskToEdit = task }
                    )
                }
            }
        }
    }
}
