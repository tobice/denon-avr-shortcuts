package cz.tobice.denonavrshortcuts

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainActivityIntegrationTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
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
