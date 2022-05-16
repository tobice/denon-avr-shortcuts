package cz.tobice.denonavrshortcuts

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class MainActivityIntegrationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        composeTestRule.setContent {
            AppRoot()
        }
    }

    @Test
    fun toggleEnumSetting() {
        // Open the setting screen
        composeTestRule
            .onNode(hasText("Sample enum setting: Option 1"))
            .assertIsDisplayed()
            .performClick()

        // Select the second option
        composeTestRule
            .onNode(hasText("Option 2"))
            .assertIsDisplayed()
            .performClick()

        // Go back to the main screen
        Espresso.pressBack()

        // Check the value has changed
        composeTestRule
            .onNode(hasText("Sample enum setting: Option 2"))
            .assertIsDisplayed()
    }
}