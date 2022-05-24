package cz.tobice.denonavrshortcuts.settings.ui

import android.util.Log
import cz.tobice.denonavrshortcuts.core.DispatcherProviderModule.IODispatcher
import cz.tobice.denonavrshortcuts.core.ErrorMessage
import cz.tobice.denonavrshortcuts.settings.repositories.SurroundParameterSettings
import cz.tobice.denonavrshortcuts.settings.repositories.SurroundParameterSettingsRepository
import cz.tobice.denonavrshortcuts.utils.combineStates
import cz.tobice.denonavrshortcuts.utils.mapState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

private val TAG = DefaultReceiverSettings::class.simpleName!!

class DefaultReceiverSettings @Inject constructor(
    @IODispatcher private val coroutineScope: CoroutineScope,
    private val surroundParameterSettingsRepository: SurroundParameterSettingsRepository
) : ReceiverSettings {

    /** Mutex to prevent simultaneous updates to settings. */
    private val mutex = Mutex()

    /** State of Audio > Surround Parameter settings as fetched from the receiver. */
    private val surroundParameterSettings = MutableStateFlow<SurroundParameterSettings?>(null)

    //
    // Center Spread
    //

    private val centerSpreadValue = surroundParameterSettings.mapState { it?.centerSpread }
    private val dirtyCenterSpreadValue = MutableStateFlow<Boolean?>(null)

    /** UI state of the Center Spread setting */
    override val centerSpreadUiState =
        createBooleanSettingUiState(centerSpreadValue, dirtyCenterSpreadValue)

    /** Set the Center Spread setting value. */
    override val setCenterSpread = { value: Boolean ->
        dirtyCenterSpreadValue.value = value

        safelyChangeSetting {
            surroundParameterSettingsRepository
                .setCenterSpread(value)
                .map { loadSurroundParameterSettings() }
                .recoverCatching(handleError())

            dirtyCenterSpreadValue.value = null
        }
    }

    //
    // All settings
    //

    /** (Re)load all settings from the receiver. */
    override fun loadSettings() {
        coroutineScope.launch {
            loadSurroundParameterSettings()
                .recoverCatching(handleError("Sorry, failed to load the settings"))
        }
    }

    private suspend fun loadSurroundParameterSettings(): Result<Unit> {
        return surroundParameterSettingsRepository
            .getSettings()
            .map { surroundParameterSettings.value = it }
    }

    //
    // Error messages
    //

    private val _errorMessages = MutableStateFlow<List<ErrorMessage>>(listOf())

    override val errorMessages: StateFlow<List<ErrorMessage>>
        get() = _errorMessages

    private fun handleError(message: String = "Sorry, operation failed"):
            (exception: Throwable) -> Unit =
        { exception ->
            Log.e(TAG, message, exception)
            _errorMessages.update { it + ErrorMessage(message = message) }

        }

    //
    // Helpers
    //

    private fun safelyChangeSetting(changeSetting: suspend () -> Unit) {
        coroutineScope.launch {
            mutex.withLock {
                changeSetting()
            }
        }
    }

    private fun createBooleanSettingUiState(
        valueFlow: StateFlow<Boolean?>,
        dirtyValueFlow: StateFlow<Boolean?>
    ): StateFlow<ReceiverSettingUiState<Boolean>> {
        return combineStates(
            valueFlow,
            dirtyValueFlow,
        ) { value, dirtyValue ->
            ReceiverSettingUiState(
                value = dirtyValue ?: value ?: false,
                status = if (value == null) {
                    ReceiverSettingUiState.Status.NOT_AVAILABLE
                } else if (dirtyValue != null) {
                    ReceiverSettingUiState.Status.UPDATING
                } else {
                    ReceiverSettingUiState.Status.READY
                }
            )
        }
    }
}
