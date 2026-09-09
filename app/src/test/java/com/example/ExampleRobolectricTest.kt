package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.Quadrant
import com.example.model.Task
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Eisenhower Task Matrix", appName)
  }

  @Test
  fun `verify weightage calculation and quadrant sorting`() {
    val task1 = Task(title = "Urgent Important", category = "Coding", urgency = 9, importance = 8)
    assertEquals(72, task1.weightage)
    assertEquals(Quadrant.DO_FIRST, task1.quadrant)

    val task2 = Task(title = "Schedule", category = "Academics", urgency = 4, importance = 9)
    assertEquals(36, task2.weightage)
    assertEquals(Quadrant.SCHEDULE, task2.quadrant)

    val task3 = Task(title = "Delegate", category = "Work", urgency = 8, importance = 3)
    assertEquals(24, task3.weightage)
    assertEquals(Quadrant.DELEGATE, task3.quadrant)

    val task4 = Task(title = "Eliminate", category = "Personal", urgency = 2, importance = 2)
    assertEquals(4, task4.weightage)
    assertEquals(Quadrant.ELIMINATE, task4.quadrant)
  }
}

