package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Task
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceSubtle
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMedium
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TickTickBlue

@Composable
fun TaskCard(
  task: Task,
  onToggleComplete: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val quadrantColor = task.quadrant.color
  val categoryColor = task.categoryEnum.color

  val animatedBg by animateColorAsState(
    targetValue = if (task.isCompleted) Color(0xFFF8FAFC) else LightSurface,
    label = "taskCardBg"
  )

  GlassmorphicCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("task_card_${task.id}")
      .clickable { onEdit() },
    shape = RoundedCornerShape(12.dp),
    backgroundColor = animatedBg,
    borderColor = if (task.isCompleted) LightBorder.copy(alpha = 0.6f) else LightBorder,
    elevation = if (task.isCompleted) 0.dp else 1.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
      // Row 1: Checkbox + Title + Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // TickTick-style circular checkbox
        Box(
          modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(if (task.isCompleted) TickTickBlue else Color.Transparent)
            .border(
              width = if (task.isCompleted) 0.dp else 1.5.dp,
              color = if (task.isCompleted) Color.Transparent else Color(0xFF94A3B8),
              shape = CircleShape
            )
            .clickable { onToggleComplete() }
            .testTag("checkbox_task_${task.id}"),
          contentAlignment = Alignment.Center
        ) {
          if (task.isCompleted) {
            Icon(
              imageVector = Icons.Rounded.Check,
              contentDescription = "Completed",
              tint = Color.White,
              modifier = Modifier.size(15.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Task Title
        Text(
          text = task.title,
          color = if (task.isCompleted) TextSubtle else TextDark,
          fontSize = 15.sp,
          fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Medium,
          textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f)
        )

        // Subtle edit & delete icons
        IconButton(
          onClick = onEdit,
          modifier = Modifier
            .size(30.dp)
            .testTag("edit_task_${task.id}")
        ) {
          Icon(
            imageVector = Icons.Rounded.Edit,
            contentDescription = "Edit task",
            tint = TextSubtle,
            modifier = Modifier.size(16.dp)
          )
        }

        IconButton(
          onClick = onDelete,
          modifier = Modifier
            .size(30.dp)
            .testTag("delete_task_${task.id}")
        ) {
          Icon(
            imageVector = Icons.Rounded.DeleteOutline,
            contentDescription = "Delete task",
            tint = Color(0xFFEF4444).copy(alpha = 0.75f),
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Row 2: Metadata tags (Category, Urgency & Importance, Weightage)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 34.dp), // Align nicely with the text
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Category Chip
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(categoryColor.copy(alpha = 0.12f))
              .padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(categoryColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = task.category,
              color = categoryColor,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          // Urgency & Importance badges
          CleanTag(label = "U", value = "${task.urgency}")
          CleanTag(label = "I", value = "${task.importance}")
        }

        // Weightage Score Pill
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(quadrantColor.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Score ",
            color = TextMutedLight,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "${task.weightage}",
            color = quadrantColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
private fun CleanTag(label: String, value: String) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(LightSurfaceSubtle)
      .border(1.dp, LightBorder, RoundedCornerShape(6.dp))
      .padding(horizontal = 6.dp, vertical = 2.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = "$label:",
        color = TextSubtle,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.width(2.dp))
      Text(
        text = value,
        color = TextMedium,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

