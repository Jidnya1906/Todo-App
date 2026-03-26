package com.example.todo_app.domain

class AddTodoUseCase(private val todoRepository: TodoRepository) {

    suspend fun execute(title : String){
        todoRepository.addTodo(Todo(title = title, isDone = false))
    }
}