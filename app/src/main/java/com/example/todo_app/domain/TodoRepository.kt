package com.example.todo_app.domain

import kotlinx.coroutines.flow.Flow

interface TodoRepository {

    fun getTodos(): Flow<List<Todo>>
    suspend fun addTodo(todo: Todo)
    suspend fun updateTodo(todo: Todo)
    suspend fun deleteTodo(todo: Todo)
}