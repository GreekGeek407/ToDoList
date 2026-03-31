package com.example.todolist

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class ToDoViewModel : ViewModel() {

    val taskList = mutableStateListOf<Task>()

    fun populateTaskList() {
        addTask("Task 1")
        addTask("Task 2")
        addTask("Task 3")
        addTask("Task 4")
        addTask("Task 5")
        addTask("Task 6")
        addTask("Task 7")
        addTask("Task 8")
    }

    fun clearCompletedTasks() {
        taskList.removeAll {it.completed}
    }

    fun uncheckAllTasks() {
        for (task in taskList) {
            if (task.completed) {
                toggleTaskCompleted(task)
            }
        }
    }
    fun addTask(body: String) {
        taskList.add(Task(body = body))
    }

    fun deleteTask(task: Task) {
        taskList.remove(task)
    }

    fun toggleTaskCompleted(task: Task) {
        val index = taskList.indexOf(task)
        taskList[index] = taskList[index].copy(completed = !task.completed)
    }

}