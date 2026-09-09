package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Quadrant
import com.example.model.TaskCategory
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceSubtle
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMedium
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TickTickBlue
import com.example.ui.theme.TickTickBlueLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(
  isOpen: Boolean,
  isEditing: Boolean,
  title: String,
  selectedCategory: String,
  urgency: Int,
  importance: Int,
  onTitleChange: (String) -> Unit,
  onCategoryChange: (String) -> Unit,
  onUrgencyChange: (Int) -> Unit,
  onImportanceChange: (Int) -> Unit,
  onSave: () -> Unit,
  onDismiss: () -> Unit
) {
  if (!isOpen) return

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val weightage = urgency * importance
  val targetQuadrant = Quadrant.fromScores(urgency, importance)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = LightSurface,
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(top = 12.dp, bottom = 6.dp)
          .width(36.dp)
          .height(4.dp)
          .clip(CircleShape)
          .background(Color(0xFFCBD5E1))
      )
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp)
        .navigationBarsPadding()
        .padding(bottom = 24.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = if (isEditing) "Edit Task" else "New Task",
            color = TextDark,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Set priority via Eisenhower criteria",
            color = TextMutedLight,
            fontSize = 12.sp
          )
        }

        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("close_task_sheet")
        ) {
          Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = "Close",
            tint = TextMutedLight
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Task Name Input
      Text(
        text = "Task Name",
        color = TextMedium,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(6.dp))

      OutlinedTextField(
        value = title,
        onValueChange = onTitleChange,
        placeholder = {
          Text("What would you like to accomplish?", color = TextSubtle, fontSize = 14.sp)
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = LightSurface,
          unfocusedContainerColor = LightSurfaceSubtle,
          focusedBorderColor = TickTickBlue,
          unfocusedBorderColor = LightBorder,
          focusedTextColor = TextDark,
          unfocusedTextColor = TextDark,
          cursorColor = TickTickBlue
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("task_name_input")
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Category Selection Chips
      Text(
        text = "Category",
        color = TextMedium,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TaskCategory.entries.forEach { category ->
          val isSelected = selectedCategory.equals(category.displayName, ignoreCase = true)
          val chipColor = category.color

          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) chipColor.copy(alpha = 0.14f) else LightSurfaceSubtle)
              .border(
                width = 1.dp,
                color = if (isSelected) chipColor else LightBorder,
                shape = RoundedCornerShape(8.dp)
              )
              .clickable { onCategoryChange(category.displayName) }
              .padding(horizontal = 12.dp, vertical = 7.dp)
              .testTag("category_chip_${category.name}"),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(if (isSelected) chipColor else TextSubtle)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = category.displayName,
              color = if (isSelected) chipColor else TextMedium,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Urgency Slider (1-10)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Urgency",
            color = TextDark,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = if (urgency >= 6) "Pressing / Time-sensitive" else "Flexible deadline",
            color = TextMutedLight,
            fontSize = 11.sp
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(LightSurfaceSubtle)
            .border(1.dp, LightBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = "$urgency / 10",
            color = TickTickBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Slider(
        value = urgency.toFloat(),
        onValueChange = { onUrgencyChange(it.toInt()) },
        valueRange = 1f..10f,
        steps = 8,
        colors = SliderDefaults.colors(
          thumbColor = TickTickBlue,
          activeTrackColor = TickTickBlue,
          inactiveTrackColor = Color(0xFFE2E8F0)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("urgency_slider")
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Importance Slider (1-10)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Importance",
            color = TextDark,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = if (importance >= 6) "High impact on goals" else "Lower long-term significance",
            color = TextMutedLight,
            fontSize = 11.sp
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(LightSurfaceSubtle)
            .border(1.dp, LightBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = "$importance / 10",
            color = TickTickBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Slider(
        value = importance.toFloat(),
        onValueChange = { onImportanceChange(it.toInt()) },
        valueRange = 1f..10f,
        steps = 8,
        colors = SliderDefaults.colors(
          thumbColor = TickTickBlue,
          activeTrackColor = TickTickBlue,
          inactiveTrackColor = Color(0xFFE2E8F0)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("importance_slider")
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Clean Minimalist Summary Banner
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(targetQuadrant.lightBg)
          .border(1.dp, targetQuadrant.color.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
          .padding(horizontal = 14.dp, vertical = 10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Target: ${targetQuadrant.title}",
              color = targetQuadrant.color,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = targetQuadrant.subtitle,
              color = TextMutedLight,
              fontSize = 11.sp
            )
          }

          Text(
            text = "Score: $weightage",
            color = targetQuadrant.color,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Save Button
      val isEnabled = title.isNotBlank()
      Button(
        onClick = onSave,
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
          containerColor = TickTickBlue,
          disabledContainerColor = Color(0xFF93C5FD)
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("save_task_button")
      ) {
        Text(
          text = if (isEditing) "Save Changes" else "Add Task",
          color = Color.White,
          fontWeight = FontWeight.SemiBold,
          fontSize = 15.sp
        )
      }
    }
  }
}

