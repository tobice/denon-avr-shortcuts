package cz.tobice.denonavrshortcuts.settings.ui

import app.cash.turbine.test
import cz.tobice.denonavrshortcuts.core.ErrorMessage
import cz.tobice.denonavrshortcuts.settings.fakes.FakeSurroundParameterSettingsRepository
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import cz.tobice.denonavrshortcuts.settings.ui.ReceiverSettingUiState.Status as Status

@ExperimentalCoroutinesApi
class DefaultReceiverSettingsTest {

    private lateinit var surroundParameterSettingsRepository:
        FakeSurroundParameterSettingsRepository

    @Before
    fun init() {
        surroundParameterSettingsRepository = FakeSurroundParameterSettingsRepository()
    }

    //
    // General
    //

    @Test
    fun `Add error message when loading fails`() = runTest {
        val settings = getInstance(this)
        surroundParameterSettingsRepository.nextShouldFail()

        settings.loadSettings()
        advanceUntilIdle()

        settings.errorMessages.test {
            getSingletonError(awaitItem()).message shouldBe "Sorry, failed to load the settings"
        }
    }

    // TODO: Consider test parametrization when more settings are implemented
    // This should help: https://stackoverflow.com/a/48710398

    //
    // Center Spread
    //

    @Test
    fun `Center Spread is off and not available on init`() = runTest {
        val settings = getInstance(this)

        settings.centerSpreadUiState.test {
            awaitItem() shouldBe ReceiverSettingUiState(
                value = false, status = Status.NOT_AVAILABLE
            )
        }
    }

    @Test
    fun `Center Spread is on and ready when loaded`() {
        surroundParameterSettingsRepository.centerSpread = true

        runWithLoadedSettings { settings ->
            settings.centerSpreadUiState.test {
                awaitItem() shouldBe ReceiverSettingUiState(
                    value = true, status = Status.READY
                )
            }
        }
    }

    @Test
    fun `Center Spread is off and unavailable when loaded but reported as unavailable`() {
        surroundParameterSettingsRepository.centerSpread = null

        runWithLoadedSettings { settings ->
            settings.centerSpreadUiState.test {
                awaitItem() shouldBe ReceiverSettingUiState(
                    value = false, Status.NOT_AVAILABLE
                )
            }
        }
    }

    @Test
    fun `Center Spread is updating when changing value`()  {
        surroundParameterSettingsRepository.centerSpread = false

        runWithLoadedSettings { settings ->
            settings.centerSpreadUiState.test { awaitItem().status shouldBe Status.READY }
            settings.setCenterSpread(true)
            settings.centerSpreadUiState.test { awaitItem().status shouldBe Status.UPDATING }
        }
    }

    @Test
    fun `Center Spread turns on`() {
        surroundParameterSettingsRepository.centerSpread = false

        runWithLoadedSettings { settings ->
            settings.setCenterSpread(true)
            advanceUntilIdle()

            settings.centerSpreadUiState.test { awaitItem().value shouldBe true }
            surroundParameterSettingsRepository.centerSpread shouldBe true
        }
    }

    @Test
    fun `Center Spread fails to turn on and adds an error message`() {
        surroundParameterSettingsRepository.centerSpread = false

        runWithLoadedSettings { settings ->
            surroundParameterSettingsRepository.nextShouldFail()

            settings.setCenterSpread(true)
            advanceUntilIdle()

            settings.centerSpreadUiState.test { awaitItem().value shouldBe false }
            settings.errorMessages.test {
                getSingletonError(awaitItem()).message shouldBe "Sorry, operation failed"
            }
        }
    }

    private fun runWithLoadedSettings(testBody: suspend TestScope.(ReceiverSettings) -> Unit) {
        runTest {
            val settings = getInstance(this)
            settings.loadSettings()
            advanceUntilIdle()

            testBody(settings)
        }
    }

    private fun getInstance(coroutineScope: CoroutineScope) =
        DefaultReceiverSettings(coroutineScope, surroundParameterSettingsRepository)

    companion object {
        private fun getSingletonError(messages: List<ErrorMessage>): ErrorMessage {
            messages shouldHaveSize 1
            return messages[0]
        }
    }
}
