package com.example.todo_app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mytodos")
data class  TodoEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val isDone : Boolean = false
    )