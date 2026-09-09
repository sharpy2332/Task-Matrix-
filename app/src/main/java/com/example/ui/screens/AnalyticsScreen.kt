package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DonutLarge
import androidx.compose.material.icons.rounded.ElectricBolt
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Quadrant
import com.example.model.TaskCategory
import com.example.ui.components.GlassmorphicCard
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
import com.example.viewmodel.AnalyticsData
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnalyticsScreen(
  analytics: AnalyticsData,
  onCategorySelected: (String?) -> Unit,
  modifier: Modifier = Modifier
) {
  var animationPlayed by remember { mutableStateOf(false) }
  val animProgress by animateFloatAsState(
    targetValue = if (animationPlayed) 1f else 0f,
    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
    label = "chartAnim"
  )

  LaunchedEffect(Unit) {
    animationPlayed = true
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(LightBg)
      .padding(horizontal = 16.dp)
      .testTag("analytics_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Productivity Analytics",
            color = TextDark,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "3D visual analytics & workload weightage",
            color = TextMutedLight,
            fontSize = 12.sp
          )
        }

        Box(
          modifier = Modifier
            .clip(CircleShape)
            .background(TickTickBlueLight)
            .padding(8.dp)
        ) {
          Icon(
            imageVector = Icons.Rounded.BarChart,
            contentDescription = null,
            tint = TickTickBlue,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    if (analytics.totalTasksCount == 0) {
      item {
        AnalyticsEmptyState()
      }
    } else {
      // 4 KPI Summary Cards
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            CleanKpiCard(
              title = "Pending Weight",
              value = "${analytics.totalPendingWeightage}",
              subtitle = "${analytics.pendingTasksCount} tasks pending",
              icon = Icons.Rounded.ElectricBolt,
              accentColor = TickTickBlue,
              modifier = Modifier.weight(1f)
            )
            CleanKpiCard(
              title = "Completion Rate",
              value = if (analytics.totalTasksCount > 0) "${(analytics.completedTasksCount * 100) / analytics.totalTasksCount}%" else "0%",
              subtitle = "${analytics.completedTasksCount} of ${analytics.totalTasksCount} done",
              icon = Icons.Rounded.CheckCircle,
              accentColor = Color(0xFF16A34A),
              modifier = Modifier.weight(1f)
            )
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            CleanKpiCard(
              title = "Avg Urgency",
              value = String.format("%.1f", analytics.avgUrgency),
              subtitle = "Scale 1 - 10",
              icon = Icons.Rounded.Speed,
              accentColor = Color(0xFFEA580C),
              modifier = Modifier.weight(1f)
            )
            CleanKpiCard(
              title = "Avg Importance",
              value = String.format("%.1f", analytics.avgImportance),
              subtitle = "Scale 1 - 10",
              icon = Icons.Rounded.PriorityHigh,
              accentColor = Color(0xFF7C3AED),
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      // 1. Tilted 3D Isometric Donut / Pie Chart for Category Weightage
      item {
        GlassmorphicCard(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          backgroundColor = LightSurface,
          borderColor = LightBorder,
          elevation = 1.dp
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Rounded.DonutLarge,
                  contentDescription = null,
                  tint = TickTickBlue,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "3D Category Weightage",
                  color = TextDark,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Text(
                text = "Tilted 3D Isometric View",
                color = TextMutedLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // The 3D Donut Chart Canvas with depth extrusion & realistic drop shadows
            Tilted3DDonutChart(
              categoryWeightage = analytics.categoryWeightage,
              totalWeightage = analytics.totalPendingWeightage,
              selectedCategory = analytics.selectedCategoryForDetail,
              onCategoryTap = onCategorySelected,
              animProgress = animProgress
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Clean Category Chips Legend
            CategoryLegendRow(
              categoryWeightage = analytics.categoryWeightage,
              categoryTaskCount = analytics.categoryTaskCount,
              selectedCategory = analytics.selectedCategoryForDetail,
              onCategorySelected = onCategorySelected
            )
          }
        }
      }

      // 2. 3D Isometric Bar Graph Showing Workload Across Quadrants
      item {
        GlassmorphicCard(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          backgroundColor = LightSurface,
          borderColor = LightBorder,
          elevation = 1.dp
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Rounded.BarChart,
                  contentDescription = null,
                  tint = TickTickBlue,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "3D Quadrant Workload",
                  color = TextDark,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Text(
                text = "Extruded 3D Prisms",
                color = TextMutedLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3D Bar Graph Canvas
            Isometric3DBarChart(
              quadrantWeightage = analytics.quadrantWeightage,
              quadrantTaskCount = analytics.quadrantTaskCount,
              animProgress = animProgress
            )
          }
        }
      }
    }
  }
}

@Composable
private fun AnalyticsEmptyState() {
  GlassmorphicCard(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 20.dp),
    shape = RoundedCornerShape(12.dp),
    backgroundColor = LightSurface,
    borderColor = LightBorder,
    elevation = 1.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(CircleShape)
          .background(TickTickBlueLight),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Rounded.PieChart,
          contentDescription = null,
          tint = TickTickBlue,
          modifier = Modifier.size(32.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "No task data to analyze yet",
        color = TextDark,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Add tasks with Urgency & Importance ratings to generate 3D workload visualizations.",
        color = TextMutedLight,
        fontSize = 13.sp,
        textAlign = TextAlign.Center,
        lineHeight = 18.sp
      )
    }
  }
}

@Composable
private fun CleanKpiCard(
  title: String,
  value: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  GlassmorphicCard(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    backgroundColor = LightSurface,
    borderColor = LightBorder,
    elevation = 1.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          color = TextMutedLight,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium
        )
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(accentColor.copy(alpha = 0.12f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(14.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = value,
        color = TextDark,
        fontSize = 19.sp,
        fontWeight = FontWeight.Bold
      )

      Text(
        text = subtitle,
        color = TextSubtle,
        fontSize = 10.sp
      )
    }
  }
}

/**
 * Renders a true 3D tilted isometric donut/pie chart with:
 * 1. Soft elliptical cast drop shadow on the ground plane.
 * 2. 3D cylindrical side walls (extruded downward with realistic ambient light shading).
 * 3. Tilted elliptical top face with radial specular lighting.
 * 4. Interactive slice inspection with dynamic 3D explosion/lift.
 */
@Composable
private fun Tilted3DDonutChart(
  categoryWeightage: Map<String, Int>,
  totalWeightage: Int,
  selectedCategory: String?,
  onCategoryTap: (String?) -> Unit,
  animProgress: Float
) {
  val nonZeroCategories = categoryWeightage.filter { it.value > 0 }.toList()

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(230.dp),
    contentAlignment = Alignment.Center
  ) {
    if (totalWeightage == 0 || nonZeroCategories.isEmpty()) {
      Text(
        text = "No pending task weightage to display",
        color = TextMutedLight,
        fontSize = 13.sp
      )
    } else {
      Canvas(
        modifier = Modifier
          .size(240.dp, 210.dp)
          .pointerInput(nonZeroCategories, totalWeightage) {
            detectTapGestures { tapOffset ->
              val centerX = size.width / 2f
              val centerY = size.height / 2f - 8.dp.toPx()
              // Account for the isometric vertical compression factor (0.58f)
              val dx = tapOffset.x - centerX
              val dy = (tapOffset.y - centerY) / 0.58f
              val distance = kotlin.math.sqrt(dx * dx + dy * dy)
              val outerRadius = 88.dp.toPx()
              val innerRadius = 42.dp.toPx()

              if (distance in (innerRadius * 0.7f)..(outerRadius * 1.3f)) {
                var angle = (Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat() + 360f) % 360f
                val normalizedAngle = (angle + 90f) % 360f

                var accumulated = 0f
                var tappedCat: String? = null
                for ((cat, weight) in nonZeroCategories) {
                  val sweep = (weight.toFloat() / totalWeightage.toFloat()) * 360f
                  if (normalizedAngle in accumulated..(accumulated + sweep)) {
                    tappedCat = cat
                    break
                  }
                  accumulated += sweep
                }
                onCategoryTap(if (selectedCategory == tappedCat) null else tappedCat)
              } else {
                onCategoryTap(null)
              }
            }
          }
          .testTag("interactive_3d_donut_chart")
      ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f - 6.dp.toPx()
        val outerRadius = 86.dp.toPx()
        val innerRadius = 44.dp.toPx()
        val depthPx = 22.dp.toPx() // Height of the 3D cylinder extrusion
        val tiltRatio = 0.58f // Isometric vertical foreshortening

        // STEP 1: Realistic 3D Soft Drop Shadow on the table below
        val shadowCenterY = centerY + depthPx + 10.dp.toPx()
        drawOval(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0x28000000),
              Color(0x14000000),
              Color.Transparent
            ),
            center = Offset(centerX, shadowCenterY),
            radius = outerRadius * 1.05f
          ),
          topLeft = Offset(centerX - outerRadius * 1.05f, shadowCenterY - outerRadius * tiltRatio * 0.95f),
          size = Size(outerRadius * 2.1f, outerRadius * 2f * tiltRatio * 0.95f)
        )

        // Precompute slice angles
        var accumulatedAngle = -90f
        val slices = nonZeroCategories.map { (catName, weight) ->
          val category = TaskCategory.fromName(catName)
          val sweep = ((weight.toFloat() / totalWeightage.toFloat()) * 360f) * animProgress
          val sliceStart = accumulatedAngle
          accumulatedAngle += sweep
          SliceInfo(
            categoryName = catName,
            color = category.color,
            startAngle = sliceStart,
            sweepAngle = sweep,
            isSelected = selectedCategory.equals(catName, ignoreCase = true)
          )
        }

        // STEP 2: Draw the 3D Extruded Cylinder Side Walls for slices facing the viewer (angles 0..180 deg)
        // Cylinder wall extends from Y to Y + depthPx
        slices.forEach { slice ->
          draw3DCylinderSideWall(
            slice = slice,
            centerX = centerX,
            centerY = centerY,
            outerRadius = outerRadius,
            innerRadius = innerRadius,
            depth = depthPx,
            tiltRatio = tiltRatio
          )
        }

        // STEP 3: Draw the 3D Top Faces of the donut
        slices.forEach { slice ->
          val isSelected = slice.isSelected
          val bisectorAngle = (slice.startAngle + slice.sweepAngle / 2f) * (PI.toFloat() / 180f)
          val explodeOffset = if (isSelected) 8.dp.toPx() else 0f
          val offsetX = cos(bisectorAngle) * explodeOffset
          val offsetY = (sin(bisectorAngle) * explodeOffset * tiltRatio) - (if (isSelected) 4.dp.toPx() else 0f)

          val effectiveCenterX = centerX + offsetX
          val effectiveCenterY = centerY + offsetY

          // Draw slice top face
          scale(scaleX = 1f, scaleY = tiltRatio, pivot = Offset(effectiveCenterX, effectiveCenterY)) {
            val strokeWidth = outerRadius - innerRadius
            val midRadius = innerRadius + strokeWidth / 2f
            val rectSize = midRadius * 2f
            val topLeft = Offset(effectiveCenterX - midRadius, effectiveCenterY - midRadius)

            // Specular highlighted top face
            val topColor = if (selectedCategory != null && !isSelected) {
              slice.color.copy(alpha = 0.35f)
            } else {
              slice.color
            }

            drawArc(
              color = topColor,
              startAngle = slice.startAngle,
              sweepAngle = slice.sweepAngle,
              useCenter = false,
              topLeft = topLeft,
              size = Size(rectSize, rectSize),
              style = Stroke(width = strokeWidth)
            )

            // Subtle inner & outer edge highlights for 3D bevel look
            drawArc(
              color = Color.White.copy(alpha = if (isSelected) 0.5f else 0.25f),
              startAngle = slice.startAngle,
              sweepAngle = slice.sweepAngle,
              useCenter = false,
              topLeft = Offset(effectiveCenterX - outerRadius, effectiveCenterY - outerRadius),
              size = Size(outerRadius * 2f, outerRadius * 2f),
              style = Stroke(width = 1.5.dp.toPx())
            )
          }
        }
      }

      // Center Overlay: Clean minimalist readout of Selected Category or Total Weightage
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 12.dp)
      ) {
        if (selectedCategory != null) {
          val weight = categoryWeightage[selectedCategory] ?: 0
          val percentage = if (totalWeightage > 0) (weight * 100) / totalWeightage else 0
          val catEnum = TaskCategory.fromName(selectedCategory)

          Text(
            text = selectedCategory,
            color = catEnum.color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "$weight pts",
            color = TextDark,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
          )
          Text(
            text = "$percentage% of total",
            color = TextMutedLight,
            fontSize = 11.sp
          )
        } else {
          Text(
            text = "Total Weight",
            color = TextMutedLight,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "$totalWeightage",
            color = TickTickBlue,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "score points",
            color = TextSubtle,
            fontSize = 10.sp
          )
        }
      }
    }
  }
}

private data class SliceInfo(
  val categoryName: String,
  val color: Color,
  val startAngle: Float,
  val sweepAngle: Float,
  val isSelected: Boolean
)

/**
 * Draws the curved, shaded 3D cylinder side wall for a donut slice with vertical extrusion.
 */
private fun DrawScope.draw3DCylinderSideWall(
  slice: SliceInfo,
  centerX: Float,
  centerY: Float,
  outerRadius: Float,
  innerRadius: Float,
  depth: Float,
  tiltRatio: Float
) {
  val stepDeg = 2f
  var angle = slice.startAngle
  val endAngle = slice.startAngle + slice.sweepAngle

  // Shading color: darken by 35% for depth wall
  val wallBaseColor = Color(
    red = (slice.color.red * 0.65f).coerceIn(0f, 1f),
    green = (slice.color.green * 0.65f).coerceIn(0f, 1f),
    blue = (slice.color.blue * 0.65f).coerceIn(0f, 1f),
    alpha = slice.color.alpha
  )

  while (angle < endAngle) {
    val nextAngle = (angle + stepDeg).coerceAtMost(endAngle)
    // Normalize to 0..360
    val normAngle = (angle % 360f + 360f) % 360f

    // Outer wall is visible when angle is between 0 and 180 degrees (facing bottom/viewer)
    if (normAngle in 0f..180f) {
      val rad1 = angle * (PI.toFloat() / 180f)
      val rad2 = nextAngle * (PI.toFloat() / 180f)

      val x1 = centerX + cos(rad1) * outerRadius
      val y1 = centerY + sin(rad1) * outerRadius * tiltRatio
      val x2 = centerX + cos(rad2) * outerRadius
      val y2 = centerY + sin(rad2) * outerRadius * tiltRatio

      val path = Path().apply {
        moveTo(x1, y1)
        lineTo(x2, y2)
        lineTo(x2, y2 + depth)
        lineTo(x1, y1 + depth)
        close()
      }

      // Ambient lighting modulation: direct front (90deg) is brightest, sides are darker
      val frontFactor = sin(normAngle * (PI.toFloat() / 180f)).coerceIn(0.4f, 1.0f)
      val segmentColor = Color(
        red = (wallBaseColor.red * frontFactor).coerceIn(0f, 1f),
        green = (wallBaseColor.green * frontFactor).coerceIn(0f, 1f),
        blue = (wallBaseColor.blue * frontFactor).coerceIn(0f, 1f),
        alpha = 1f
      )

      drawPath(path = path, color = segmentColor, style = Fill)
    }

    angle = nextAngle
  }
}

/**
 * Renders a 3D Isometric Bar Chart where each bar is an extruded 3D rectangular prism:
 * - Front Face: Standard quadrant color with smooth gradient
 * - Right Side Face: Isometric depth face (30% darker) providing true geometric 3D volume
 * - Top Face: Lighted isometric top cap
 * - Soft base drop shadow under each 3D bar
 */
@Composable
private fun Isometric3DBarChart(
  quadrantWeightage: Map<Quadrant, Int>,
  quadrantTaskCount: Map<Quadrant, Int>,
  animProgress: Float
) {
  val maxWeight = (quadrantWeightage.values.maxOrNull() ?: 1).coerceAtLeast(1)

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("quadrant_bar_graph")
  ) {
    Canvas(
      modifier = Modifier
        .fillMaxWidth()
        .height(160.dp)
    ) {
      val totalWidth = size.width
      val chartHeight = size.height - 24.dp.toPx()
      val barCount = Quadrant.entries.size
      val slotWidth = totalWidth / barCount
      val barWidth = 32.dp.toPx()
      val depthX = 10.dp.toPx() // Isometric 3D depth to the right
      val depthY = 6.dp.toPx()  // Isometric 3D depth upward

      // Draw subtle horizontal reference guidelines
      val gridLines = 3
      for (i in 1..gridLines) {
        val y = chartHeight * (1f - (i.toFloat() / gridLines))
        drawLine(
          color = Color(0xFFE2E8F0),
          start = Offset(0f, y),
          end = Offset(totalWidth, y),
          strokeWidth = 1.dp.toPx()
        )
      }

      Quadrant.entries.forEachIndexed { index, quadrant ->
        val weight = quadrantWeightage[quadrant] ?: 0
        val fraction = (weight.toFloat() / maxWeight.toFloat()).coerceIn(0.06f, 1f) * animProgress
        val barHeight = chartHeight * fraction

        val startX = index * slotWidth + (slotWidth - barWidth - depthX) / 2f
        val baseY = chartHeight
        val topY = baseY - barHeight

        val quadColor = quadrant.color

        // 1. Soft Base Drop Shadow on ground plane
        val shadowPath = Path().apply {
          moveTo(startX - 2.dp.toPx(), baseY + 2.dp.toPx())
          lineTo(startX + barWidth, baseY + 2.dp.toPx())
          lineTo(startX + barWidth + depthX + 2.dp.toPx(), baseY - depthY + 2.dp.toPx())
          lineTo(startX + depthX, baseY - depthY + 2.dp.toPx())
          close()
        }
        drawPath(
          path = shadowPath,
          color = Color(0x18000000),
          style = Fill
        )

        // 2. Front Face of the 3D Prism
        val frontPath = Path().apply {
          moveTo(startX, baseY)
          lineTo(startX + barWidth, baseY)
          lineTo(startX + barWidth, topY)
          lineTo(startX, topY)
          close()
        }
        drawPath(
          path = frontPath,
          brush = Brush.verticalGradient(
            colors = listOf(
              quadColor,
              quadColor.copy(alpha = 0.85f)
            ),
            startY = topY,
            endY = baseY
          ),
          style = Fill
        )
        // Front face subtle border
        drawPath(
          path = frontPath,
          color = quadColor.copy(alpha = 0.9f),
          style = Stroke(width = 1.dp.toPx())
        )

        // 3. Right Depth Face (Shaded darker for 3D depth)
        val rightFaceColor = Color(
          red = (quadColor.red * 0.7f).coerceIn(0f, 1f),
          green = (quadColor.green * 0.7f).coerceIn(0f, 1f),
          blue = (quadColor.blue * 0.7f).coerceIn(0f, 1f),
          alpha = 1f
        )
        val rightPath = Path().apply {
          moveTo(startX + barWidth, baseY)
          lineTo(startX + barWidth + depthX, baseY - depthY)
          lineTo(startX + barWidth + depthX, topY - depthY)
          lineTo(startX + barWidth, topY)
          close()
        }
        drawPath(path = rightPath, color = rightFaceColor, style = Fill)
        drawPath(
          path = rightPath,
          color = rightFaceColor.copy(alpha = 0.8f),
          style = Stroke(width = 1.dp.toPx())
        )

        // 4. Top Face (Lightened cap receiving top lighting)
        val topFaceColor = Color(
          red = (quadColor.red + 0.25f).coerceIn(0f, 1f),
          green = (quadColor.green + 0.25f).coerceIn(0f, 1f),
          blue = (quadColor.blue + 0.25f).coerceIn(0f, 1f),
          alpha = 1f
        )
        val topPath = Path().apply {
          moveTo(startX, topY)
          lineTo(startX + barWidth, topY)
          lineTo(startX + barWidth + depthX, topY - depthY)
          lineTo(startX + depthX, topY - depthY)
          close()
        }
        drawPath(path = topPath, color = topFaceColor, style = Fill)
        drawPath(
          path = topPath,
          color = Color.White.copy(alpha = 0.5f),
          style = Stroke(width = 1.dp.toPx())
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Quadrant Labels Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly
    ) {
      Quadrant.entries.forEach { quadrant ->
        val weight = quadrantWeightage[quadrant] ?: 0
        val count = quadrantTaskCount[quadrant] ?: 0

        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "$weight pts",
            color = quadrant.color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = quadrant.title,
            color = TextDark,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = "$count tasks",
            color = TextSubtle,
            fontSize = 10.sp
          )
        }
      }
    }
  }
}

@Composable
private fun CategoryLegendRow(
  categoryWeightage: Map<String, Int>,
  categoryTaskCount: Map<String, Int>,
  selectedCategory: String?,
  onCategorySelected: (String?) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState()),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    TaskCategory.entries.forEach { category ->
      val weight = categoryWeightage[category.displayName] ?: 0
      val count = categoryTaskCount[category.displayName] ?: 0
      val isSelected = selectedCategory.equals(category.displayName, ignoreCase = true)

      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(if (isSelected) category.color.copy(alpha = 0.12f) else LightSurfaceSubtle)
          .border(
            width = 1.dp,
            color = if (isSelected) category.color else LightBorder,
            shape = RoundedCornerShape(8.dp)
          )
          .clickable { onCategorySelected(if (isSelected) null else category.displayName) }
          .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(7.dp)
            .clip(CircleShape)
            .background(category.color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = category.displayName,
          color = if (isSelected) category.color else TextDark,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "$weight",
          color = TextMutedLight,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

