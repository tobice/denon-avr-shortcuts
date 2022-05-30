package cz.tobice.denonavrshortcuts.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsToggleable
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onSiblings
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import cz.tobice.denonavrshortcuts.AppRoot
import cz.tobice.denonavrshortcuts.MainActivity
import cz.tobice.denonavrshortcuts.fakes.FakeReceiver
import cz.tobice.denonavrshortcuts.settings.enums.audio.AudysseyDynamicVolume
import cz.tobice.denonavrshortcuts.testutils.TestDispatcherProviderModule.TrackedIODispatcher
import cz.tobice.denonavrshortcuts.testutils.TrackedDispatcher
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class SettingsIntegrationTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    @TrackedIODispatcher
    lateinit var trackedDispatcher: TrackedDispatcher

    private lateinit var fakeReceiver: FakeReceiver

    @Before
    fun setup() {
        // The app is automatically pointed to the fake receiver's URL when run under test. This is
        // achieved through a Hilt binding override.
        fakeReceiver = FakeReceiver()
        fakeReceiver.startHttpServer()

        hiltRule.inject()

        composeTestRule.registerIdlingResource(trackedDispatcher.getIdlingResource())
    }

    @After
    fun tearDown() {
        fakeReceiver.stopHttpServer()
    }

    @Test
    fun centerSpread_turnOn() {
        fakeReceiver.centerSpread = false

        launchApp()

        composeTestRule.onNode(hasText("Center Spread"))
            .onSiblings()
            .onFirst()
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOff()
            .performClick()
            .assertIsOn()

        fakeReceiver.centerSpread shouldBe true
    }

    @Test
    fun centerSpread_turnOff() {
        fakeReceiver.centerSpread = true

        launchApp()

        composeTestRule.onNode(hasText("Center Spread"))
            .onSiblings()
            .onFirst()
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOn()
            .performClick()
            .assertIsOff()

        fakeReceiver.centerSpread shouldBe false
    }

    @Test
    fun dynamicVolume_changeToHeavy() {
        fakeReceiver.dynamicVolume = AudysseyDynamicVolume.OFF

        launchApp()

        // Open the Dynamic Volume setting screen
        composeTestRule.onNode(hasText("Dynamic Volume (Off)"))
            .assertIsDisplayed()
            .performClick()

        composeTestRule
            .onNode(hasText("Heavy"))
            .assertIsDisplayed()
            .performClick()

        // Go back to the main screen
        Espresso.pressBack()

        composeTestRule
            .onNode(hasText("Dynamic Volume (Heavy)"))
            .assertIsDisplayed()

        fakeReceiver.dynamicVolume shouldBe AudysseyDynamicVolume.HEAVY
    }

    private fun launchApp() {
        composeTestRule.setContent { AppRoot() }
    }

    // TODO: Add a failure scenario

    // TODO: Add a rotation scenario
}