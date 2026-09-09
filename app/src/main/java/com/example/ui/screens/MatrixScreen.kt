package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Quadrant
import com.example.model.Task
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.TaskCard
import com.example.ui.theme.LightBg
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceSubtle
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMedium
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TickTickBlue
import com.example.viewmodel.TaskFilter

@Composable
fun MatrixScreen(
  tasks: List<Task>,
  searchQuery: String,
  selectedFilter: TaskFilter,
  focusedQuadrant: Quadrant?,
  onSearchChange: (String) -> Unit,
  onFilterChange: (TaskFilter) -> Unit,
  onQuadrantFocus: (Quadrant?) -> Unit,
  onToggleComplete: (Task) -> Unit,
  onEditTask: (Task) -> Unit,
  onDeleteTask: (Task) -> Unit,
  onAddNewTask: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(LightBg)
      .padding(horizontal = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(8.dp))

    // Search Bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = onSearchChange,
      placeholder = { Text("Search tasks or categories...", color = TextSubtle, fontSize = 14.sp) },
      leadingIcon = {
        Icon(
          imageVector = Icons.Rounded.Search,
          contentDescription = "Search",
          tint = TextSubtle,
          modifier = Modifier.size(18.dp)
        )
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { onSearchChange("") }) {
            Icon(
              imageVector = Icons.Rounded.Clear,
              contentDescription = "Clear search",
              tint = TextSubtle,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      },
      singleLine = true,
      colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = LightSurface,
        unfocusedContainerColor = LightSurface,
        focusedBorderColor = TickTickBlue,
        unfocusedBorderColor = LightBorder,
        focusedTextColor = TextDark,
        unfocusedTextColor = TextDark,
        cursorColor = TickTickBlue
      ),
      shape = RoundedCornerShape(10.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("search_tasks_input")
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Filter Pills Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      FilterPill(
        label = "All",
        isSelected = focusedQuadrant == null,
        accentColor = TickTickBlue,
        onClick = { onQuadrantFocus(null) },
        testTag = "quadrant_chip_all"
      )

      Quadrant.entries.forEach { q ->
        FilterPill(
          label = q.title,
          isSelected = focusedQuadrant == q,
          accentColor = q.color,
          onClick = { onQuadrantFocus(if (focusedQuadrant == q) null else q) },
          testTag = "quadrant_chip_${q.name}"
        )
      }

      Spacer(modifier = Modifier.width(6.dp))
      Box(modifier = Modifier.width(1.dp).height(16.dp).background(LightBorder))
      Spacer(modifier = Modifier.width(6.dp))

      TaskFilter.entries.forEach { filter ->
        FilterPill(
          label = filter.displayName,
          isSelected = selectedFilter == filter,
          accentColor = TickTickBlue,
          onClick = { onFilterChange(filter) },
          testTag = "filter_chip_${filter.name}"
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Main Content: Empty State or Organized Quadrant Lists
    if (tasks.isEmpty()) {
      EmptyStateView(
        isFiltered = searchQuery.isNotEmpty() || selectedFilter != TaskFilter.ALL || focusedQuadrant != null,
        onClearFilters = {
          onSearchChange("")
          onFilterChange(TaskFilter.ALL)
          onQuadrantFocus(null)
        }
      )
    } else {
      val quadrantsToShow = if (focusedQuadrant != null) {
        listOf(focusedQuadrant)
      } else {
        Quadrant.entries
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .testTag("matrix_quadrants_list"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
      ) {
        quadrantsToShow.forEach { quadrant ->
          val quadrantTasks = tasks.filter { it.quadrant == quadrant }
            .sortedByDescending { it.weightage }
          val pendingWeightage = quadrantTasks.filter { !it.isCompleted }.sumOf { it.weightage }

          item(key = "header_${quadrant.name}") {
            QuadrantSectionHeader(
              quadrant = quadrant,
              taskCount = quadrantTasks.size,
              pendingWeightage = pendingWeightage,
              isFocused = focusedQuadrant == quadrant,
              onToggleFocus = {
                onQuadrantFocus(if (focusedQuadrant == quadrant) null else quadrant)
              }
            )
          }

          if (quadrantTasks.isEmpty()) {
            item(key = "empty_${quadrant.name}") {
              EmptyQuadrantNotice(quadrant = quadrant)
            }
          } else {
            items(
              items = quadrantTasks,
              key = { it.id }
            ) { task ->
              TaskCard(
                task = task,
                onToggleComplete = { onToggleComplete(task) },
                onEdit = { onEditTask(task) },
                onDelete = { onDeleteTask(task) }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun EmptyStateView(
  isFiltered: Boolean,
  onClearFilters: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(bottom = 60.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.padding(horizontal = 32.dp)
    ) {
      // Friendly Minimalist Illustration Box
      Box(
        modifier = Modifier
          .size(72.dp)
          .clip(CircleShape)
          .background(Color(0xFFEFF6FF)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isFiltered) Icons.Rounded.Search else Icons.Rounded.CheckCircleOutline,
          contentDescription = null,
          tint = TickTickBlue,
          modifier = Modifier.size(36.dp)
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      if (isFiltered) {
        Text(
          text = "No matching tasks found",
          color = TextDark,
          fontSize = 17.sp,
          fontWeight = FontWeight.SemiBold,
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Try adjusting your search query or filter pills.",
          color = TextMutedLight,
          fontSize = 13.sp,
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(14.dp))
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFEFF6FF))
            .clickable { onClearFilters() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
          Text(
            text = "Reset Filters",
            color = TickTickBlue,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      } else {
        // EXACT prompt requirement: "No tasks yet. Tap the + to add your first task."
        Text(
          text = "No tasks yet. Tap the + to add your first task.",
          color = TextDark,
          fontSize = 16.sp,
          fontWeight = FontWeight.SemiBold,
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Tasks will automatically organize into the 4 Eisenhower quadrants based on your urgency and importance scores.",
          color = TextMutedLight,
          fontSize = 13.sp,
          textAlign = TextAlign.Center,
          lineHeight = 18.sp
        )
      }
    }
  }
}

@Composable
private fun FilterPill(
  label: String,
  isSelected: Boolean,
  accentColor: Color,
  onClick: () -> Unit,
  testTag: String
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (isSelected) accentColor.copy(alpha = 0.12f) else LightSurface)
      .border(
        width = 1.dp,
        color = if (isSelected) accentColor else LightBorder,
        shape = RoundedCornerShape(8.dp)
      )
      .clickable { onClick() }
      .padding(horizontal = 10.dp, vertical = 6.dp)
      .testTag(testTag)
  ) {
    Text(
      text = label,
      color = if (isSelected) accentColor else TextMedium,
      fontSize = 12.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
    )
  }
}

@Composable
private fun QuadrantSectionHeader(
  quadrant: Quadrant,
  taskCount: Int,
  pendingWeightage: Int,
  isFocused: Boolean,
  onToggleFocus: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      // Clean Accent Dot
      Box(
        modifier = Modifier
          .size(10.dp)
          .clip(CircleShape)
          .background(quadrant.color)
      )

      Spacer(modifier = Modifier.width(8.dp))

      Text(
        text = quadrant.title,
        color = TextDark,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.width(6.dp))

      Text(
        text = "(${quadrant.subtitle})",
        color = TextMutedLight,
        fontSize = 12.sp
      )
    }

    // Task count badge
    Row(
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .background(LightSurfaceSubtle)
        .border(1.dp, LightBorder, RoundedCornerShape(12.dp))
        .padding(horizontal = 8.dp, vertical = 3.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "$taskCount",
        color = if (taskCount > 0) TextDark else TextSubtle,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
      )
      if (pendingWeightage > 0) {
        Text(
          text = " • $pendingWeightage pts",
          color = quadrant.color,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
    }
  }
}

@Composable
private fun EmptyQuadrantNotice(quadrant: Quadrant) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(LightSurface)
      .border(1.dp, LightBorder.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
      .padding(vertical = 12.dp, horizontal = 16.dp),
    contentAlignment = Alignment.CenterStart
  ) {
    Text(
      text = "No tasks in ${quadrant.title}",
      color = TextSubtle,
      fontSize = 13.sp
    )
  }
}

