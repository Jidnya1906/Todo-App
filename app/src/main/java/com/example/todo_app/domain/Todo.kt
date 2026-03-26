package com.example.todo_app.domain



data class Todo (
   val id: Int = 0,
   val title: String,
   val isDone: Boolean = false
)