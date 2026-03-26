package com.example.todo_app.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.todo_app.data.TodoDataRepository
import com.example.todo_app.data.TodoDatabase
import com.example.todo_app.domain.AddTodoUseCase
import com.example.todo_app.domain.Todo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class TodoViewModel(application : Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
       application,
        TodoDatabase::class.java,
        "my_todo_db"
    ).build()

    private val todoRepository = TodoDataRepository(db.todoDao())

    private val addUseCase = AddTodoUseCase(todoRepository)

    open val todos = todoRepository.getTodos().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(),
        emptyList()
    )

    fun addTodo(title: String) {
        viewModelScope.launch {
            addUseCase.execute(title)
        }
    }

    fun toggleTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.updateTodo(todo.copy(isDone = !todo.isDone))
        }
    }

    fun editTodo(todo: Todo, newTitle: String) {
        viewModelScope.launch {
            if (newTitle.isNotBlank()) {
                todoRepository.updateTodo(todo.copy(title = newTitle))
            }
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.deleteTodo(todo)
        }
    }
//SELECTING Multiple
    fun deleteMultipleTodos(ids: Set<Int>) {
        viewModelScope.launch {
            todos.value.forEach { todo ->
                if (ids.contains(todo.id)) {
                    todoRepository.deleteTodo(todo)
                }
            }
        }
    }

}