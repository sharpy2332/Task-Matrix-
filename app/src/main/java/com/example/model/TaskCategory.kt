package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.MovieFilter
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.CategoryAcademics
import com.example.ui.theme.CategoryCoding
import com.example.ui.theme.CategoryContent
import com.example.ui.theme.CategoryFitness
import com.example.ui.theme.CategoryPersonal
import com.example.ui.theme.CategoryWork

enum class TaskCategory(
  val displayName: String,
  val color: Color,
  val icon: ImageVector
) {
  ACADEMICS("Academics", CategoryAcademics, Icons.Rounded.School),
  CODING("Coding", CategoryCoding, Icons.Rounded.Code),
  FITNESS("Fitness", CategoryFitness, Icons.Rounded.FitnessCenter),
  CONTENT_CREATION("Content Creation", CategoryContent, Icons.Rounded.MovieFilter),
  WORK("Work", CategoryWork, Icons.Rounded.Work),
  PERSONAL("Personal", CategoryPersonal, Icons.Rounded.Person);

  companion object {
    fun fromName(name: String): TaskCategory {
      return entries.find { it.displayName.equals(name, ignoreCase = true) || it.name.equals(name, ignoreCase = true) }
        ?: CODING
    }
  }
}
