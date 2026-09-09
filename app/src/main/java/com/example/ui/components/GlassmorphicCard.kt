package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightSurface

@Composable
fun GlassmorphicCard(
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(12.dp),
  backgroundColor: Color = LightSurface,
  borderColor: Color = LightBorder,
  glowColor: Color = Color.Transparent, // Ignored in clean flat design
  elevation: Dp = 1.dp,
  content: @Composable BoxScope.() -> Unit
) {
  Box(
    modifier = modifier
      .shadow(
        elevation = elevation,
        shape = shape,
        spotColor = Color(0x14000000),
        ambientColor = Color(0x0A000000)
      )
      .clip(shape)
      .background(backgroundColor)
      .border(1.dp, borderColor, shape = shape),
    content = content
  )
}

