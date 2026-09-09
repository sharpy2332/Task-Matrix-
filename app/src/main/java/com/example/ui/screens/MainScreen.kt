package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddTaskBottomSheet
import com.example.ui.theme.LightBg
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceSubtle
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMedium
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TickTickBlue
import com.example.ui.theme.TickTickBlueLight
import com.example.viewmodel.TaskViewModel

@Composable
fun MainScreen(
  taskViewModel: TaskViewModel = viewModel()
) {
  val tasks by taskViewModel.filteredTasks.collectAsState()
  val searchQuery by taskViewModel.searchQuery.collectAsState()
  val selectedFilter by taskViewModel.selectedFilter.collectAsState()
  val focusedQuadrant by taskViewModel.focusedQuadrant.collectAsState()
  val activeTab by taskViewModel.activeTab.collectAsState()
  val analyticsData by taskViewModel.analyticsData.collectAsState()

  // Sheet State
  val isSheetOpen by taskViewModel.isSheetOpen.collectAsState()
  val editingTaskId by taskViewModel.editingTaskId.collectAsState()
  val inputTitle by taskViewModel.inputTitle.collectAsState()
  val inputCategory by taskViewModel.inputCategory.collectAsState()
  val inputUrgency by taskViewModel.inputUrgency.collectAsState()
  val inputImportance by taskViewModel.inputImportance.collectAsState()

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(LightBg),
    containerColor = LightBg,
    topBar = {
      TopNavBar(activeTab = activeTab, onTabSelect = { taskViewModel.setActiveTab(it) })
    },
    floatingActionButton = {
      // Clean TickTick Minimalist FAB
      FloatingActionButton(
        onClick = { taskViewModel.openAddTask() },
        containerColor = TickTickBlue,
        contentColor = Color.White,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(
          defaultElevation = 3.dp,
          pressedElevation = 6.dp
        ),
        modifier = Modifier
          .padding(bottom = 8.dp)
          .testTag("fab_add_task")
      ) {
        Icon(
          imageVector = Icons.Rounded.Add,
          contentDescription = "Add Task",
          tint = Color.White,
          modifier = Modifier.size(24.dp)
        )
      }
    },
    bottomBar = {
      CleanBottomBar(
        activeTab = activeTab,
        onTabSelect = { taskViewModel.setActiveTab(it) }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Smooth Page Transitions between Matrix View (Tab 0) and Analytics View (Tab 1)
      AnimatedContent(
        targetState = activeTab,
        transitionSpec = {
          if (targetState > initialState) {
            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
              slideOutHorizontally { width -> -width } + fadeOut()
            )
          } else {
            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
              slideOutHorizontally { width -> width } + fadeOut()
            )
          }
        },
        label = "tabTransition"
      ) { tabIndex ->
        when (tabIndex) {
          0 -> MatrixScreen(
            tasks = tasks,
            searchQuery = searchQuery,
            selectedFilter = selectedFilter,
            focusedQuadrant = focusedQuadrant,
            onSearchChange = { taskViewModel.setSearchQuery(it) },
            onFilterChange = { taskViewModel.setFilter(it) },
            onQuadrantFocus = { taskViewModel.setFocusedQuadrant(it) },
            onToggleComplete = { taskViewModel.toggleTaskCompletion(it) },
            onEditTask = { taskViewModel.openEditTask(it) },
            onDeleteTask = { taskViewModel.deleteTask(it) },
            onAddNewTask = { taskViewModel.openAddTask() }
          )

          1 -> AnalyticsScreen(
            analytics = analyticsData,
            onCategorySelected = { taskViewModel.selectAnalyticsCategory(it) }
          )
        }
      }
    }

    // Modal Bottom Sheet for Smart Task Entry
    AddTaskBottomSheet(
      isOpen = isSheetOpen,
      isEditing = editingTaskId != null,
      title = inputTitle,
      selectedCategory = inputCategory,
      urgency = inputUrgency,
      importance = inputImportance,
      onTitleChange = { taskViewModel.updateInputTitle(it) },
      onCategoryChange = { taskViewModel.updateInputCategory(it) },
      onUrgencyChange = { taskViewModel.updateInputUrgency(it) },
      onImportanceChange = { taskViewModel.updateInputImportance(it) },
      onSave = { taskViewModel.saveTask() },
      onDismiss = { taskViewModel.closeSheet() }
    )
  }
}

@Composable
private fun TopNavBar(
  activeTab: Int,
  onTabSelect: (Int) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .statusBarsPadding()
      .background(LightSurface)
      .border(width = 0.5.dp, color = LightBorder)
      .padding(horizontal = 16.dp, vertical = 10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Clean App Branding
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(TickTickBlue),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Rounded.CheckCircle,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Text(
            text = "Eisenhower Matrix",
            color = TextDark,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Prioritize with Urgency × Importance",
            color = TextMutedLight,
            fontSize = 11.sp
          )
        }
      }

      // Segmented Tabs Pill
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(LightSurfaceSubtle)
          .border(1.dp, LightBorder, RoundedCornerShape(8.dp))
          .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        TopTabItem(
          title = "Matrix",
          icon = Icons.Rounded.GridView,
          isSelected = activeTab == 0,
          onClick = { onTabSelect(0) },
          testTag = "top_tab_matrix"
        )
        TopTabItem(
          title = "Analytics",
          icon = Icons.Rounded.BarChart,
          isSelected = activeTab == 1,
          onClick = { onTabSelect(1) },
          testTag = "top_tab_analytics"
        )
      }
    }
  }
}

@Composable
private fun TopTabItem(
  title: String,
  icon: ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  testTag: String
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(if (isSelected) LightSurface else Color.Transparent)
      .clickable { onClick() }
      .padding(horizontal = 10.dp, vertical = 5.dp)
      .testTag(testTag)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = if (isSelected) TickTickBlue else TextMutedLight,
        modifier = Modifier.size(14.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = title,
        color = if (isSelected) TextDark else TextMutedLight,
        fontSize = 11.sp,
        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
      )
    }
  }
}

@Composable
private fun CleanBottomBar(
  activeTab: Int,
  onTabSelect: (Int) -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .background(LightSurface)
      .border(width = 0.5.dp, color = LightBorder)
      .padding(horizontal = 24.dp, vertical = 6.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      BottomNavItem(
        title = "Tasks Matrix",
        icon = Icons.Rounded.GridView,
        isSelected = activeTab == 0,
        onClick = { onTabSelect(0) },
        testTag = "nav_item_matrix"
      )

      BottomNavItem(
        title = "3D Analytics",
        icon = Icons.Rounded.BarChart,
        isSelected = activeTab == 1,
        onClick = { onTabSelect(1) },
        testTag = "nav_item_analytics"
      )
    }
  }
}

@Composable
private fun BottomNavItem(
  title: String,
  icon: ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  testTag: String
) {
  Row(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (isSelected) TickTickBlueLight else Color.Transparent)
      .clickable { onClick() }
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = title,
      tint = if (isSelected) TickTickBlue else TextMutedLight,
      modifier = Modifier.size(18.dp)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = title,
      color = if (isSelected) TickTickBlue else TextMedium,
      fontSize = 12.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
    )
  }
}

