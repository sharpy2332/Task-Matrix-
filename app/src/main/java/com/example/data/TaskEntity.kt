package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Task

@Entity(tableName = "tasks")
data class TaskEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0L,
  val title: String,
  val category: String,
  val urgency: Int,
  val importance: Int,
  val isCompleted: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
) {
  fun toDomain(): Task = Task(
    id = id,
    title = title,
    category = category,
    urgency = urgency,
    importance = importance,
    isCompleted = isCompleted,
    createdAt = createdAt
  )

  companion object {
    fun fromDomain(task: Task): TaskEntity = TaskEntity(
      id = task.id,
      title = task.title,
      category = task.category,
      urgency = task.urgency,
      importance = task.importance,
      isCompleted = task.isCompleted,
      createdAt = task.createdAt
    )
  }
}
