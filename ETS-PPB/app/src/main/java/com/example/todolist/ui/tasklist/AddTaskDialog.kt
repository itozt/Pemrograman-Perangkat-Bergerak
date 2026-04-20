package com.example.todolist.ui.tasklist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.PaddingValues
import com.example.todolist.R
import com.example.todolist.domain.model.RepeatMode
import com.example.todolist.domain.model.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AddTaskDialog(
    initialTask: Task? = null,
    initialDateMillis: Long? = null,
    onDismiss: () -> Unit,
    onDelete: ((Task) -> Unit)? = null,
    onSave: (
        title: String,
        notes: String?,
        deadlineMillis: Long?,
        deadlineHasTime: Boolean,
        repeatMode: com.example.todolist.domain.model.RepeatMode,
        repeatDays: String?,
        repeatCount: Int?
    ) -> Unit
) {
    var title by remember { mutableStateOf(initialTask?.title ?: "") }
    var notes by remember { mutableStateOf(initialTask?.notes ?: "") }
    var selectedDateMillis by remember { mutableStateOf<Long?>(initialTask?.deadlineMillis ?: initialDateMillis) }
    var selectedHourMinute by remember { mutableStateOf<Pair<Int, Int>?>(
        initialTask?.deadlineMillis?.takeIf { initialTask.deadlineHasTime }?.let {
            val cal = Calendar.getInstance().apply { timeInMillis = it }
            Pair(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
        }
    ) }
    var repeatMode by remember { mutableStateOf(initialTask?.repeatMode ?: com.example.todolist.domain.model.RepeatMode.NONE) }
    var customDays by remember { mutableStateOf<Set<String>>(
        initialTask?.repeatDays?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
    ) }
    var repeatDropdownExpanded by remember { mutableStateOf(false) }
    var repeatCountText by remember { mutableStateOf("") }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val horizontalScrollState = remember { ScrollState(0) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val locale = Locale.forLanguageTag("id-ID")
    val addTitleLabel = if (initialTask == null) "Tugas Baru" else "Edit Tugas"
    val saveLabel = stringResource(id = R.string.simpan)
    val cancelLabel = stringResource(id = R.string.batal)
    val clearDeadlineLabel = stringResource(id = R.string.hapus_deadline)

    val dateText = remember(selectedDateMillis) {
        selectedDateMillis?.let { millis ->
            SimpleDateFormat("yyyy-MM-dd", locale).format(millis)
        } ?: ""
    }

    val timeText = selectedHourMinute?.let { (hour, minute) ->
        String.format(locale, "%02d:%02d", hour, minute)
    } ?: ""

    val daysOfWeekMap = remember {
        mapOf(
            Calendar.SUNDAY to "Sun",
            Calendar.MONDAY to "Mon",
            Calendar.TUESDAY to "Tue",
            Calendar.WEDNESDAY to "Wed",
            Calendar.THURSDAY to "Thu",
            Calendar.FRIDAY to "Fri",
            Calendar.SATURDAY to "Sat"
        )
    }

    val daysOfWeekIndonesian = remember {
        mapOf(
            "Sun" to "Minggu",
            "Mon" to "Senin",
            "Tue" to "Selasa",
            "Wed" to "Rabu",
            "Thu" to "Kamis",
            "Fri" to "Jumat",
            "Sat" to "Sabtu"
        )
    }

    LaunchedEffect(selectedDateMillis, repeatMode) {
        if (repeatMode == com.example.todolist.domain.model.RepeatMode.CUSTOM_DAYS && selectedDateMillis != null) {
            val cal = Calendar.getInstance().apply { timeInMillis = selectedDateMillis!! }
            val dayStr = daysOfWeekMap[cal.get(Calendar.DAY_OF_WEEK)]
            if (dayStr != null && !customDays.contains(dayStr)) {
                customDays = customDays + dayStr
            }
        }
    }

    // Helper function to close keyboard
    val closeKeyboard = {
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    // Delete confirmation dialog
    if (showDeleteConfirmation && initialTask != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = {
                Text(
                    text = "Konfirmasi Hapus",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin menghapus Tugas ini?",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        initialTask.let { task -> onDelete?.invoke(task) }
                        onDismiss()
                    },
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text("Batal", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = addTitleLabel,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Delete button (only shown when editing)
                    if (initialTask != null && onDelete != null) {
                        IconButton(
                            onClick = { showDeleteConfirmation = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus Tugas",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .clickable(
                        enabled = true,
                        onClick = { closeKeyboard() },
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Task Title
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskSectionHeader(icon = "✎", title = "Apa yang perlu Anda lakukan?")
                    OutlinedTextField(
                        value = title,
                        onValueChange = { if (it.length <= 100) title = it },
                        placeholder = { Text("Masukkan judul tugas", color = MaterialTheme.colorScheme.outlineVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${title.length}/100",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }

                // Section 2: Deadline
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskSectionHeader(icon = "📅", title = "Kapan Anda ingin menyelesaikannya?")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Date button
                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable {
                                    closeKeyboard()
                                    val calendar = Calendar.getInstance()
                                    selectedDateMillis?.let { calendar.timeInMillis = it }
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            val picked = Calendar.getInstance().apply {
                                                set(year, month, dayOfMonth, 0, 0, 0)
                                                set(Calendar.MILLISECOND, 0)
                                            }
                                            selectedDateMillis = picked.timeInMillis
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = dateText.ifEmpty { "Tanggal" },
                                fontSize = 13.sp,
                                color = if (dateText.isEmpty()) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Time button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selectedDateMillis != null) MaterialTheme.colorScheme.surface
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable(enabled = selectedDateMillis != null) {
                                    closeKeyboard()
                                    val now = Calendar.getInstance()
                                    TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            selectedHourMinute = hourOfDay to minute
                                        },
                                        selectedHourMinute?.first ?: now.get(Calendar.HOUR_OF_DAY),
                                        selectedHourMinute?.second ?: now.get(Calendar.MINUTE),
                                        true
                                    ).show()
                                }
                                .border(
                                    width = 1.dp,
                                    color = if (selectedDateMillis != null) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = timeText.ifEmpty { "Waktu" },
                                fontSize = 13.sp,
                                color = if (timeText.isEmpty()) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Clear deadline button (X icon)
                        androidx.compose.material3.IconButton(
                            onClick = {
                                closeKeyboard()
                                selectedDateMillis = null
                                selectedHourMinute = null
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selectedDateMillis != null) MaterialTheme.colorScheme.surfaceVariant
                                    else MaterialTheme.colorScheme.surface
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            enabled = selectedDateMillis != null
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Hapus Deadline",
                                tint = if (selectedDateMillis != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = "Ketuk tanggal/waktu untuk menetapkan (tanggal diperlukan untuk waktu)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }

                // Section 3: Repeat Pattern (only if deadline is set)
                if (selectedDateMillis != null && initialTask == null) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        TaskSectionHeader(icon = "🔁", title = "Pola Pengulangan")

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { 
                                    closeKeyboard()
                                    repeatDropdownExpanded = true 
                                }
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(14.dp)
                        ) {
                            val modeTranslate = when (repeatMode) {
                                com.example.todolist.domain.model.RepeatMode.NONE -> "Tidak Ada"
                                com.example.todolist.domain.model.RepeatMode.DAILY -> "Harian"
                                com.example.todolist.domain.model.RepeatMode.WEEKLY -> "Mingguan"
                                com.example.todolist.domain.model.RepeatMode.MONTHLY -> "Bulanan"
                                com.example.todolist.domain.model.RepeatMode.CUSTOM_DAYS -> "Hari Khusus"
                            }
                            Text(
                                text = modeTranslate,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        DropdownMenu(
                            expanded = repeatDropdownExpanded,
                            onDismissRequest = { repeatDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            com.example.todolist.domain.model.RepeatMode.values().forEach { mode ->
                                val modeName = when (mode) {
                                    com.example.todolist.domain.model.RepeatMode.NONE -> "Tidak Ada"
                                    com.example.todolist.domain.model.RepeatMode.DAILY -> "Harian"
                                    com.example.todolist.domain.model.RepeatMode.WEEKLY -> "Mingguan"
                                    com.example.todolist.domain.model.RepeatMode.MONTHLY -> "Bulanan"
                                    com.example.todolist.domain.model.RepeatMode.CUSTOM_DAYS -> "Hari Khusus"
                                }
                                DropdownMenuItem(
                                    text = { Text(text = modeName) },
                                    onClick = {
                                        repeatMode = mode
                                        repeatDropdownExpanded = false
                                    }
                                )
                            }
                        }

                        // Repeat count input
                        if (repeatMode != com.example.todolist.domain.model.RepeatMode.NONE) {
                            OutlinedTextField(
                                value = repeatCountText,
                                onValueChange = { repeatCountText = it },
                                placeholder = { Text("Kosongkan untuk tidak terbatas", fontSize = 12.sp) },
                                label = { Text("Berapa kali?", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                ),
                                textStyle = MaterialTheme.typography.bodyMedium
                            )

                            // Custom days selector
                            if (repeatMode == com.example.todolist.domain.model.RepeatMode.CUSTOM_DAYS) {
                                val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                                val daysInitials = listOf("S", "S", "R", "K", "J", "S", "M")
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        daysOfWeek.forEachIndexed { index, day ->
                                            val dayInitial = daysInitials[index]
                                            val isSelected = customDays.contains(day)
                                            val isSunday = (day == "Sun")
                                            DaySelectionCircleButton(
                                                dayInitial = dayInitial,
                                                isSelected = isSelected,
                                                isSunday = isSunday,
                                                modifier = Modifier.weight(1f),
                                                onToggle = {
                                                    closeKeyboard()
                                                    if (isSelected) {
                                                        val newCustomDays = customDays - day
                                                        customDays = newCustomDays

                                                        selectedDateMillis?.let { dateMillis ->
                                                            val cal = Calendar.getInstance().apply { timeInMillis = dateMillis }
                                                            val currentDayStr = daysOfWeekMap[cal.get(Calendar.DAY_OF_WEEK)]

                                                            if (currentDayStr == day && newCustomDays.isNotEmpty()) {
                                                                val searchCal = Calendar.getInstance().apply { timeInMillis = dateMillis }
                                                                for (i in 0..6) {
                                                                    if (newCustomDays.contains(daysOfWeekMap[searchCal.get(Calendar.DAY_OF_WEEK)])) {
                                                                        selectedDateMillis = searchCal.timeInMillis
                                                                        break
                                                                    }
                                                                    searchCal.add(Calendar.DAY_OF_YEAR, 1)
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        customDays = customDays + day
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "Pilih hari pengulangan",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    }
                }

                // Section 4: Add Notes
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskSectionHeader(icon = "📝", title = "Tambah Catatan (Opsional)")
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { if (it.length <= 500) notes = it },
                        placeholder = { Text("Tambahkan catatan tambahan...", color = MaterialTheme.colorScheme.outlineVariant) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${notes.length}/500",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            Button(
                enabled = title.isNotBlank(),
                onClick = {
                    val deadlineMillis = selectedDateMillis?.let { dateMillis ->
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = dateMillis
                            val time = selectedHourMinute
                            set(Calendar.HOUR_OF_DAY, time?.first ?: 0)
                            set(Calendar.MINUTE, time?.second ?: 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        calendar.timeInMillis
                    }
                    val deadlineHasTime = selectedDateMillis != null && selectedHourMinute != null
                    val repeatDaysStr = if (repeatMode == com.example.todolist.domain.model.RepeatMode.CUSTOM_DAYS) {
                        customDays.joinToString(",")
                    } else null

                    val repeatCountNum = repeatCountText.toIntOrNull()

                    onSave(
                        title,
                        notes.ifBlank { null },
                        deadlineMillis,
                        deadlineHasTime,
                        repeatMode,
                        repeatDaysStr,
                        repeatCountNum
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = saveLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = cancelLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun TaskSectionHeader(icon: String, title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = icon,
            fontSize = 18.sp
        )
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun DaySelectionCircleButton(
    dayInitial: String,
    isSelected: Boolean,
    isSunday: Boolean,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit
) {
    val buttonSize = 38.dp
    
    // Text color: white when selected, dark when unselected
    val textColor = when {
        isSelected -> Color.White
        isSunday -> Color(0xFF8B0000)  // Dark maroon for visibility
        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)  // Dark primary
    }
    
    // Background color: soft/muda using primary and maroon colors
    val backgroundColor = if (isSelected) {
        if (isSunday) Color(0xFFC41C3B) else MaterialTheme.colorScheme.primary  // Maroon and primary when selected
    } else {
        if (isSunday) Color(0xFFFCE4EC) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)  // Very light maroon and primary when unselected
    }
    
    // Border color: softer shades matching the theme
    val borderColor = if (isSunday) Color(0xFFE91E63) else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .heightIn(max = buttonSize)
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .border(
                width = 1.2.dp,
                color = borderColor,
                shape = CircleShape
            )
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dayInitial,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DaySelectionButton(
    day: String,
    dayDisplay: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit
) {
    OutlinedButton(
        onClick = onToggle,
        modifier = modifier
            .height(40.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
    ) {
        Text(
            text = dayDisplay,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            textAlign = TextAlign.Center,
            lineHeight = 13.sp
        )
    }
}