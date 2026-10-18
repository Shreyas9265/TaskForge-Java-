package com.taskforge.mobile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taskforge.mobile.data.TaskItem
import com.taskforge.mobile.data.TaskPriority
import com.taskforge.mobile.data.TaskRequest
import com.taskforge.mobile.data.TaskStatus
import com.taskforge.mobile.data.pretty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(vm: TaskForgeViewModel) {
    // null = dialog closed, TaskItem = editing, NEW_TASK sentinel = creating
    var editing by remember { mutableStateOf<TaskItem?>(null) }
    var creating by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<TaskItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Tasks") },
                actions = {
                    TextButton(onClick = { vm.logout() }) {
                        Text("Logout (${vm.username})")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { creating = true }) {
                Icon(Icons.Default.Add, contentDescription = "New task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = vm.statusFilter == null,
                    onClick = { vm.setFilter(null) },
                    label = { Text("All") }
                )
                TaskStatus.all.forEach { status ->
                    FilterChip(
                        selected = vm.statusFilter == status,
                        onClick = { vm.setFilter(status) },
                        label = { Text(status.pretty()) }
                    )
                }
            }

            vm.error?.let {
                Text(
                    text = it,
                    color = Color(0xFFE63946),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    vm.loading && vm.tasks.isEmpty() -> CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                    vm.tasks.isEmpty() -> Text(
                        text = "No tasks yet. Tap + to add one.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                    else -> LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(vm.tasks, key = { it.id }) { task ->
                            TaskCard(
                                task = task,
                                onEdit = { editing = task },
                                onDelete = { pendingDelete = task }
                            )
                        }
                    }
                }
            }
        }
    }

    if (creating || editing != null) {
        TaskEditorDialog(
            existing = editing,
            onDismiss = {
                creating = false
                editing = null
            },
            onSave = { request ->
                vm.saveTask(editing?.id, request) {
                    creating = false
                    editing = null
                }
            }
        )
    }

    pendingDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete task") },
            text = { Text("Delete \"${task.title}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteTask(task.id)
                    pendingDelete = null
                }) { Text("Delete", color = Color(0xFFE63946)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun TaskCard(task: TaskItem, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(task.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            if (!task.description.isNullOrBlank()) {
                Text(task.description, style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                text = "${task.status.pretty()}  •  ${task.priority}" +
                    (task.dueDate?.let { "  •  Due $it" } ?: ""),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) { Text("Edit") }
                TextButton(onClick = onDelete) { Text("Delete", color = Color(0xFFE63946)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskEditorDialog(
    existing: TaskItem?,
    onDismiss: () -> Unit,
    onSave: (TaskRequest) -> Unit
) {
    var title by remember { mutableStateOf(existing?.title ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var status by remember { mutableStateOf(existing?.status ?: "TODO") }
    var priority by remember { mutableStateOf(existing?.priority ?: "MEDIUM") }
    var dueDate by remember { mutableStateOf(existing?.dueDate ?: "") }
    var validation by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "New Task" else "Edit Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                validation?.let { Text(it, color = Color(0xFFE63946)) }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Status", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskStatus.all.forEach {
                        FilterChip(selected = status == it, onClick = { status = it }, label = { Text(it.pretty()) })
                    }
                }
                Text("Priority", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskPriority.all.forEach {
                        FilterChip(selected = priority == it, onClick = { priority = it }, label = { Text(it) })
                    }
                }
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due date (YYYY-MM-DD, optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val trimmedDate = dueDate.trim()
                when {
                    title.isBlank() -> validation = "Title is required."
                    trimmedDate.isNotEmpty() && !Regex("""\d{4}-\d{2}-\d{2}""").matches(trimmedDate) ->
                        validation = "Due date must look like 2026-12-31."
                    else -> onSave(
                        TaskRequest(
                            title = title.trim(),
                            description = description.trim(),
                            status = status,
                            priority = priority,
                            dueDate = trimmedDate.ifEmpty { null }
                        )
                    )
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
