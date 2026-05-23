package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.core.app.ApplicationProvider
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ZenithViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Zenith", appName)
  }

  @Test
  fun `test main activity view presentation`() {
    composeTestRule.setContent {
      MyApplicationTheme {
        MainContentScreen()
      }
    }
    composeTestRule.waitForIdle()
  }

  @Test
  fun `test full app user flows`() {
    var vm: ZenithViewModel? = null
    composeTestRule.setContent {
      MyApplicationTheme {
        val viewModel: ZenithViewModel = viewModel()
        vm = viewModel
        MainContentScreen(viewModel)
      }
    }
    composeTestRule.waitForIdle()

    // 1. If onboarding is not completed, perform click on GET STARTED
    val onboardingCompleted = vm?.onboardingCompleted?.value == true
    if (!onboardingCompleted) {
      composeTestRule.onNodeWithTag("get_started_button").performClick()
      composeTestRule.waitForIdle()
    }

    // 2. Select a student profile to login
    val allStudents = vm?.allStudents?.value ?: emptyList()
    if (allStudents.isNotEmpty()) {
      val defaultStudent = allStudents.first()
      vm?.let {
        composeTestRule.runOnUiThread {
          it.selectStudent(defaultStudent)
        }
      }
      composeTestRule.waitForIdle()
    }

    // 3. Cycle through every screen tab to ensure no component throws a runtime exception when rendering
    val tabs = listOf("Hub", "Routine", "Attendance", "Notes", "Focus", "CGPA", "AI", "Social")
    tabs.forEach { tab ->
      vm?.let {
        composeTestRule.runOnUiThread {
          it.setTab(tab)
        }
      }
      composeTestRule.waitForIdle()
    }
  }
}
