package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.PeopleOutline
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.QuadrantDelegate
import com.example.ui.theme.QuadrantDelegateLight
import com.example.ui.theme.QuadrantDoFirst
import com.example.ui.theme.QuadrantDoFirstLight
import com.example.ui.theme.QuadrantEliminate
import com.example.ui.theme.QuadrantEliminateLight
import com.example.ui.theme.QuadrantSchedule
import com.example.ui.theme.QuadrantScheduleLight

enum class Quadrant(
  val title: String,
  val subtitle: String,
  val color: Color,
  val lightBg: Color,
  val icon: ImageVector,
  val tag: String
) {
  DO_FIRST(
    title = "Do First",
    subtitle = "Urgent & Important",
    color = QuadrantDoFirst,
    lightBg = QuadrantDoFirstLight,
    icon = Icons.Rounded.Bolt,
    tag = "Q1"
  ),
  SCHEDULE(
    title = "Schedule",
    subtitle = "Not Urgent & Important",
    color = QuadrantSchedule,
    lightBg = QuadrantScheduleLight,
    icon = Icons.Rounded.CalendarMonth,
    tag = "Q2"
  ),
  DELEGATE(
    title = "Delegate",
    subtitle = "Urgent & Not Important",
    color = QuadrantDelegate,
    lightBg = QuadrantDelegateLight,
    icon = Icons.Rounded.PeopleOutline,
    tag = "Q3"
  ),
  ELIMINATE(
    title = "Eliminate",
    subtitle = "Not Urgent & Not Important",
    color = QuadrantEliminate,
    lightBg = QuadrantEliminateLight,
    icon = Icons.Rounded.DeleteOutline,
    tag = "Q4"
  );

  companion object {
    /**
     * Determines quadrant based on Urgency (1..10) and Importance (1..10).
     * Urgency >= 6 is considered Urgent.
     * Importance >= 6 is considered Important.
     */
    fun fromScores(urgency: Int, importance: Int): Quadrant {
      val isUrgent = urgency >= 6
      val isImportant = importance >= 6
      return when {
        isUrgent && isImportant -> DO_FIRST
        !isUrgent && isImportant -> SCHEDULE
        isUrgent && !isImportant -> DELEGATE
        else -> ELIMINATE
      }
    }
  }
}

