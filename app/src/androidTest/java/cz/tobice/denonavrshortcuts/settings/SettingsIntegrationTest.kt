package cz.tobice.denonavrshortcuts.settings

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import cz.tobice.denonavrshortcuts.AppRoot
import cz.tobice.denonavrshortcuts.MainActivity
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class SettingsIntegrationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        composeTestRule.setContent {
            AppRoot()
        }
    }

    @Test
    fun centerSpread_turnOn() {
        composeTestRule
            .onNode(hasText("Center Spread"))
            .onSiblings()
            .onFirst()
            .assertIsDisplayed()
            .assertIsToggleable()
            .performClick()
            .assertIsOn()
    }

    // TODO: Add a failure scenario

    // TODO: Add rotation
}