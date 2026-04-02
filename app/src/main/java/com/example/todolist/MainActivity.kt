package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.ui.theme.ToDoListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                    ToDoScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoScreen(modifier: Modifier = Modifier, toDoViewModel: ToDoViewModel = viewModel()) {
    var taskBody by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("To Do List") },
                actions = {
                    IconButton(
                        onClick = { toDoViewModel.populateTaskList() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Populate List"
                        )
                    }
                    IconButton(
                        onClick = { toDoViewModel.uncheckAllTasks() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Remove all checks"
                        )
                    }
                    IconButton(
                        onClick = { toDoViewModel.clearCompletedTasks() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear list"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier.fillMaxSize().padding(innerPadding)
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                value = taskBody,
                onValueChange = { taskBody = it },
                label = { Text("Enter task") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        toDoViewModel.addTask(taskBody)
                        taskBody = ""
                    }
                )
            )
            LazyColumn(

            ) {
                items(
                    items = toDoViewModel.taskList,
                    key = { task -> task.id }
                ) { task ->
                    val currentTask by rememberUpdatedState(task)
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.StartToEnd) {
                                toDoViewModel.deleteTask(currentTask)
                                true
                            } else if (it == SwipeToDismissBoxValue.EndToStart) {
                                toDoViewModel.moveToBottom(currentTask)
                                false
                            } else
                                false
                        }
                    )
                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = { SwipeBackground(dismissState) },
                        content = { TaskCard(task, toDoViewModel::toggleTaskCompleted) },
                        modifier = Modifier.padding(vertical = 1.dp).animateItem()
                    )
                }
            }

        }
    }
}

@Composable
fun SwipeBackground(dismissState: SwipeToDismissBoxState, modifier: Modifier = Modifier) {
    val color =
        if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd)
            Color.Red
        else if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
            Color.Blue
        else
            Color.Transparent
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxSize()
            .background(color)
    ) {
        if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd)
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier.padding(8.dp)
            )
        else if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "Move item to bottom of list",
                modifier = Modifier.padding(start = 370.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)

            )
    }
}

@Composable
fun TaskCard(task: Task, toggleCompleted: (Task)->Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = task.body,
                modifier = modifier.padding(start = 12.dp)
            )
            Checkbox(
                checked = task.completed,
                onCheckedChange = { toggleCompleted(task) }
            )
        }
    }
}