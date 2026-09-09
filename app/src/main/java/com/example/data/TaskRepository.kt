package com.example.data

import com.example.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(private val taskDao: TaskDao) {

  val allTasks: Flow<List<Task>> = taskDao.getAllTasksFlow().map { entities ->
    entities.map { it.toDomain() }
  }

  suspend fun insertTask(task: Task): Long {
    return taskDao.insertTask(TaskEntity.fromDomain(task))
  }

  suspend fun updateTask(task: Task) {
    taskDao.updateTask(TaskEntity.fromDomain(task))
  }

  suspend fun deleteTask(task: Task) {
    taskDao.deleteTask(TaskEntity.fromDomain(task))
  }

  suspend fun toggleTaskCompletion(task: Task) {
    val updated = task.copy(isCompleted = !task.isCompleted)
    taskDao.updateTask(TaskEntity.fromDomain(updated))
  }
}
