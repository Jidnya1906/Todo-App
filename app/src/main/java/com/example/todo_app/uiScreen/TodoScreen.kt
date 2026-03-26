package com.example.todo_app.uiScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todo_app.presentation.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(todoViewModel: TodoViewModel) {

    var task by remember { mutableStateOf("") }
    val todoList by todoViewModel.todos.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todo List") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.DarkGray,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            // 🔹 Add Task Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = task,
                    onValueChange = { task = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Enter Task") },
                    shape = RoundedCornerShape(22.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.padding(4.dp))

                Button(
                    onClick = {
                        if (task.isNotBlank()) {
                            todoViewModel.addTodo(task)
                            task = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color.DarkGray)
                ) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.DarkGray)
            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 Todo List
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(
                    items = todoList,
                    key = { it.id }
                ) { todo ->

                    var isEditing by remember(todo.id) { mutableStateOf(false) }
                    var new by remember(todo.id) { mutableStateOf(todo.title) }

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Checkbox(
                                    checked = todo.isDone,
                                    onCheckedChange = {
                                        todoViewModel.toggleTodo(todo)
                                    },
                                    colors = CheckboxDefaults.colors(Color.DarkGray)
                                )

                                if (!isEditing) {
                                    Text(
                                        todo.title,
                                        modifier = Modifier.padding(start = 8.dp),
                                        style = if (todo.isDone)
                                            LocalTextStyle.current.copy(
                                                textDecoration = TextDecoration.LineThrough
                                            )
                                        else
                                            LocalTextStyle.current
                                    )
                                }
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        if (!isEditing) new = todo.title
                                        isEditing = !isEditing
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = Color.DarkGray
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        todoViewModel.deleteTodo(todo)
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.DarkGray
                                    )
                                }
                            }
                        }

                        // 🔥 EDIT MODE UI (Improved)
                        if (isEditing) {
                            Spacer(modifier = Modifier.height(6.dp))

                            Column {
                                OutlinedTextField(
                                    value = new,
                                    onValueChange = { new = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(22.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedIndicatorColor = Color.DarkGray,
                                        unfocusedIndicatorColor = Color.DarkGray,
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                    )
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Button(
                                    onClick = {
                                        if (new.isNotBlank()) {
                                            todoViewModel.editTodo(todo, new)
                                            isEditing = false
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.End),
                                    colors = ButtonDefaults.buttonColors(Color.DarkGray)
                                ) {
                                    Text("Save")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}