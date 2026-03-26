package com.example.todo_app.uiScreen

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todo_app.presentation.TodoViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(todoViewModel: TodoViewModel) {

    var task by remember { mutableStateOf("") }
    val todoList by todoViewModel.todos.collectAsStateWithLifecycle()
    var selectedItems by remember { mutableStateOf(setOf<Int>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Tasks") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // 🔹 Input + Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = task,
                    onValueChange = { task = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Add a new task...") },
                    shape = RoundedCornerShape(16.dp)
                )

                Button(
                    onClick = {
                        if (selectedItems.isNotEmpty()) {
                            todoViewModel.deleteMultipleTodos(selectedItems)
                            selectedItems = emptySet()
                        } else if (task.isNotBlank()) {
                            todoViewModel.addTodo(task)
                            task = ""
                        }
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    // AND and DELETE BUTTON
                    Text(if (selectedItems.isNotEmpty()) "Delete" else "Add")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 Selection Info
            if (selectedItems.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${selectedItems.size} selected")

                    TextButton(onClick = {
                        selectedItems =
                            if (selectedItems.size == todoList.size) emptySet()
                            else todoList.map { it.id }.toSet()
                    }) {
                        Text(
                            if (selectedItems.size == todoList.size)
                                "Unselect All" else "Select All"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔥 FIXED EMPTY STATE
            if (todoList.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No tasks yet 🎉",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

            } else {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(todoList, key = { it.id }) { todo ->

                        var isVisible by remember { mutableStateOf(true) }
                        var isEditing by remember { mutableStateOf(false) }
                        var new by remember { mutableStateOf(todo.title) }

                        AnimatedVisibility(
                            visible = isVisible,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut() + slideOutVertically()
                        ) {

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedItems.contains(todo.id))
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    else Color.White
                                )
                            ) {

                                Column(modifier = Modifier.padding(12.dp)) {

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {

                                        Row(verticalAlignment = Alignment.CenterVertically) {

                                            Checkbox(
                                                checked = selectedItems.contains(todo.id),
                                                onCheckedChange = { isChecked ->
                                                    selectedItems = if (isChecked)
                                                        selectedItems + todo.id
                                                    else
                                                        selectedItems - todo.id
                                                }
                                            )

                                            Text(
                                                text = todo.title,
                                                modifier = Modifier.padding(start = 8.dp),
                                                style = if (todo.isDone)
                                                    LocalTextStyle.current.copy(
                                                        textDecoration = TextDecoration.LineThrough
                                                    )
                                                else LocalTextStyle.current
                                            )
                                        }

                                        Row {
                                            // 🔥 ROTATING EDIT ICON
                                            var rotation by remember { mutableStateOf(0f) }
                                            val animatedRotation by animateFloatAsState(rotation)

                                            IconButton(onClick = {
                                                rotation += 360f
                                                isEditing = !isEditing
                                                new = todo.title
                                            }) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    contentDescription = null,
                                                    modifier = Modifier.graphicsLayer {
                                                        rotationZ = animatedRotation
                                                    }
                                                )
                                            }

                                            // 🔥 DELETE ANIMATION
                                            IconButton(onClick = {
                                                isVisible = false
                                            }) {
                                                Icon(Icons.Default.Delete, contentDescription = null)
                                            }
                                        }
                                    }

                                    if (isEditing) {
                                        Spacer(modifier = Modifier.height(8.dp))

                                        OutlinedTextField(
                                            value = new,
                                            onValueChange = { new = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Button(
                                            onClick = {
                                                if (new.isNotBlank()) {
                                                    todoViewModel.editTodo(todo, new)
                                                    isEditing = false
                                                }
                                            },
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            Text("Save")
                                        }
                                    }
                                }
                            }

                            // 🔥 DELAY DELETE
                            LaunchedEffect(isVisible) {
                                if (!isVisible) {
                                    delay(300)
                                    todoViewModel.deleteTodo(todo)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}