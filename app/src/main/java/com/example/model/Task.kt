package com.example.model

data class Task(
  val id: Long = 0L,
  val title: String,
  val category: String,
  val urgency: Int, // 1 to 10
  val importance: Int, // 1 to 10
  val isCompleted: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
) {
  val weightage: Int
    get() = urgency * importance

  val quadrant: Quadrant
    get() = Quadrant.fromScores(urgency, importance)

  val categoryEnum: TaskCategory
    get() = TaskCategory.fromName(category)
}
