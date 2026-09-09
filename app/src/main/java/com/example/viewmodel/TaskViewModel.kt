package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.TaskRepository
import com.example.model.Quadrant
import com.example.model.Task
import com.example.model.TaskCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilter(val displayName: String) {
  ALL("All"),
  PENDING("Active"),
  COMPLETED("Done")
}

data class AnalyticsData(
  val totalPendingWeightage: Int = 0,
  val totalTasksCount: Int = 0,
  val pendingTasksCount: Int = 0,
  val completedTasksCount: Int = 0,
  val avgUrgency: Float = 0f,
  val avgImportance: Float = 0f,
  val categoryWeightage: Map<String, Int> = emptyMap(),
  val categoryTaskCount: Map<String, Int> = emptyMap(),
  val quadrantWeightage: Map<Quadrant, Int> = emptyMap(),
  val quadrantTaskCount: Map<Quadrant, Int> = emptyMap(),
  val selectedCategoryForDetail: String? = null
)

class TaskViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: TaskRepository
  val allTasks: StateFlow<List<Task>>

  private val _searchQuery = MutableStateFlow("")
  val searchQuery = _searchQuery.asStateFlow()

  private val _selectedFilter = MutableStateFlow(TaskFilter.ALL)
  val selectedFilter = _selectedFilter.asStateFlow()

  private val _focusedQuadrant = MutableStateFlow<Quadrant?>(null)
  val focusedQuadrant = _focusedQuadrant.asStateFlow()

  private val _activeTab = MutableStateFlow(0) // 0 = Matrix, 1 = Analytics
  val activeTab = _activeTab.asStateFlow()

  // Task Input Sheet State
  private val _isSheetOpen = MutableStateFlow(false)
  val isSheetOpen = _isSheetOpen.asStateFlow()

  private val _editingTaskId = MutableStateFlow<Long?>(null)
  val editingTaskId = _editingTaskId.asStateFlow()

  private val _inputTitle = MutableStateFlow("")
  val inputTitle = _inputTitle.asStateFlow()

  private val _inputCategory = MutableStateFlow(TaskCategory.CODING.displayName)
  val inputCategory = _inputCategory.asStateFlow()

  private val _inputUrgency = MutableStateFlow(5)
  val inputUrgency = _inputUrgency.asStateFlow()

  private val _inputImportance = MutableStateFlow(5)
  val inputImportance = _inputImportance.asStateFlow()

  // Interactive Analytics Selected Category Slice
  private val _selectedAnalyticsCategory = MutableStateFlow<String?>(null)
  val selectedAnalyticsCategory = _selectedAnalyticsCategory.asStateFlow()

  init {
    val db = AppDatabase.getDatabase(application)
    repository = TaskRepository(db.taskDao())
    allTasks = repository.allTasks.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )
  }

  // Filtered and Sorted Tasks
  val filteredTasks: StateFlow<List<Task>> = combine(
    allTasks,
    _searchQuery,
    _selectedFilter
  ) { tasks, query, filter ->
    tasks.filter { task ->
      val matchesQuery = query.isBlank() ||
        task.title.contains(query, ignoreCase = true) ||
        task.category.contains(query, ignoreCase = true)

      val matchesFilter = when (filter) {
        TaskFilter.ALL -> true
        TaskFilter.PENDING -> !task.isCompleted
        TaskFilter.COMPLETED -> task.isCompleted
      }

      matchesQuery && matchesFilter
    }.sortedByDescending { it.weightage }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  // Computed Analytics
  val analyticsData: StateFlow<AnalyticsData> = combine(
    allTasks,
    _selectedAnalyticsCategory
  ) { tasks, selectedCategory ->
    val pending = tasks.filter { !it.isCompleted }
    val totalPendingWeight = pending.sumOf { it.weightage }
    val completedCount = tasks.count { it.isCompleted }

    val catWeight = mutableMapOf<String, Int>()
    val catCount = mutableMapOf<String, Int>()
    pending.forEach { t ->
      catWeight[t.category] = (catWeight[t.category] ?: 0) + t.weightage
      catCount[t.category] = (catCount[t.category] ?: 0) + 1
    }

    val quadWeight = mutableMapOf<Quadrant, Int>()
    val quadCount = mutableMapOf<Quadrant, Int>()
    Quadrant.entries.forEach { q ->
      quadWeight[q] = 0
      quadCount[q] = 0
    }
    pending.forEach { t ->
      quadWeight[t.quadrant] = (quadWeight[t.quadrant] ?: 0) + t.weightage
      quadCount[t.quadrant] = (quadCount[t.quadrant] ?: 0) + 1
    }

    val avgUrg = if (pending.isNotEmpty()) pending.map { it.urgency }.average().toFloat() else 0f
    val avgImp = if (pending.isNotEmpty()) pending.map { it.importance }.average().toFloat() else 0f

    AnalyticsData(
      totalPendingWeightage = totalPendingWeight,
      totalTasksCount = tasks.size,
      pendingTasksCount = pending.size,
      completedTasksCount = completedCount,
      avgUrgency = avgUrg,
      avgImportance = avgImp,
      categoryWeightage = catWeight,
      categoryTaskCount = catCount,
      quadrantWeightage = quadWeight,
      quadrantTaskCount = quadCount,
      selectedCategoryForDetail = selectedCategory
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = AnalyticsData()
  )

  fun setActiveTab(index: Int) {
    _activeTab.value = index
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setFilter(filter: TaskFilter) {
    _selectedFilter.value = filter
  }

  fun setFocusedQuadrant(quadrant: Quadrant?) {
    _focusedQuadrant.value = quadrant
  }

  fun selectAnalyticsCategory(category: String?) {
    _selectedAnalyticsCategory.value = if (_selectedAnalyticsCategory.value == category) null else category
  }

  fun openAddTask() {
    _editingTaskId.value = null
    _inputTitle.value = ""
    _inputCategory.value = TaskCategory.CODING.displayName
    _inputUrgency.value = 5
    _inputImportance.value = 5
    _isSheetOpen.value = true
  }

  fun openEditTask(task: Task) {
    _editingTaskId.value = task.id
    _inputTitle.value = task.title
    _inputCategory.value = task.category
    _inputUrgency.value = task.urgency
    _inputImportance.value = task.importance
    _isSheetOpen.value = true
  }

  fun closeSheet() {
    _isSheetOpen.value = false
    _editingTaskId.value = null
  }

  fun updateInputTitle(title: String) {
    _inputTitle.value = title
  }

  fun updateInputCategory(category: String) {
    _inputCategory.value = category
  }

  fun updateInputUrgency(urgency: Int) {
    _inputUrgency.value = urgency.coerceIn(1, 10)
  }

  fun updateInputImportance(importance: Int) {
    _inputImportance.value = importance.coerceIn(1, 10)
  }

  fun saveTask() {
    val title = _inputTitle.value.trim()
    if (title.isBlank()) return

    val urgency = _inputUrgency.value
    val importance = _inputImportance.value
    val category = _inputCategory.value
    val editId = _editingTaskId.value

    viewModelScope.launch {
      if (editId != null) {
        val existing = allTasks.value.find { it.id == editId }
        val updated = (existing ?: Task(id = editId, title = title, category = category, urgency = urgency, importance = importance))
          .copy(title = title, category = category, urgency = urgency, importance = importance)
        repository.updateTask(updated)
      } else {
        val newTask = Task(
          title = title,
          category = category,
          urgency = urgency,
          importance = importance
        )
        repository.insertTask(newTask)
      }
      closeSheet()
    }
  }

  fun toggleTaskCompletion(task: Task) {
    viewModelScope.launch {
      repository.toggleTaskCompletion(task)
    }
  }

  fun deleteTask(task: Task) {
    viewModelScope.launch {
      repository.deleteTask(task)
    }
  }
}
