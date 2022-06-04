package cz.tobice.denonavrshortcuts.settings.ui

import android.util.Log
import cz.tobice.denonavrshortcuts.core.DispatcherProviderModule.IODispatcher
import cz.tobice.denonavrshortcuts.core.ErrorMessage
import cz.tobice.denonavrshortcuts.settings.repositories.AudysseySettings
import cz.tobice.denonavrshortcuts.settings.repositories.AudysseySettingsRepository
import cz.tobice.denonavrshortcuts.settings.repositories.SurroundParameterSettings
import cz.tobice.denonavrshortcuts.settings.repositories.SurroundParameterSettingsRepository
import cz.tobice.denonavrshortcuts.utils.combineStates
import cz.tobice.denonavrshortcuts.utils.flatMap
import cz.tobice.denonavrshortcuts.utils.mapState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private val TAG = DefaultReceiverSettings::class.simpleName!!

class DefaultReceiverSettings @Inject constructor(
    @IODispatcher private val coroutineScope: CoroutineScope,
    private val surroundParameterSettingsRepository: SurroundParameterSettingsRepository,
    private val audysseySettingsRepository: AudysseySettingsRepository,
) : ReceiverSettings {

    /** State of Audio > Surround Parameter settings as fetched from the receiver */
    private val surroundParameterSettings = MutableStateFlow<SurroundParameterSettings?>(null)

    /** State of Audio > Audyssey settings as fetched from the receiver */
    private val audysseySettings = MutableStateFlow<AudysseySettings?>(null)

    //
    // Center Spread
    //

    private val centerSpreadControl = SettingControl(
        receiverValueFlow = surroundParameterSettings.mapState { it?.centerSpread },
        setReceiverValue = { value ->
            surroundParameterSettingsRepository
                .setCenterSpread(value)
                .flatMap { loadSurroundParameterSettings() }
        }
    )

    override val centerSpreadUiState = centerSpreadControl.uiState
    override val setCenterSpread = centerSpreadControl.setValue

    //
    // Audyssey Dynamic Volume
    //

    private val dynamicVolumeControl = SettingControl(
        receiverValueFlow = audysseySettings.mapState { it?.dynamicVolume },
        setReceiverValue = { value ->
            audysseySettingsRepository
                .setDynamicVolume(value)
                .flatMap { loadAudysseySettings() }
        }
    )

    override val dynamicVolumeUiState = dynamicVolumeControl.uiState
    override val setDynamicVolume = dynamicVolumeControl.setValue

    //
    // All settings
    //

    /** (Re)load all settings from the receiver. */
    override fun loadSettings() {
        coroutineScope.launch {
            awaitAll(
                async { loadSurroundParameterSettings() },
                async { loadAudysseySettings() }
            ).forEach {
                // TODO: Avoid showing multiple messages
                it.recoverCatching(handleError("Sorry, failed to load the settings"))
            }
        }
    }

    private suspend fun loadSurroundParameterSettings(): Result<Unit> {
        return surroundParameterSettingsRepository
            .getSettings()
            .map { surroundParameterSettings.value = it }
    }

    private suspend fun loadAudysseySettings(): Result<Unit> {
        return audysseySettingsRepository
            .getSettings()
            .map { audysseySettings.value = it }
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

    /**
     * Internal helper class that exposes APIs for rendering (via [ReceiverSettingUiState]) and
     * changing (via [setValue]) a single receiver setting.
     *
     * When a caller attempts to change the receiver's value via [setValue], the class holds
     * the target value as dirty value and exposes it via the [uiState] until the operation finishes
     * and a fresh value (changed or not changed) is obtained from the receiver.
     *
     * This means that the new value is immediately reflected in the UI (good for the user), together
     * with the [ReceiverSettingUiState.Status.UPDATING] status.
     */
    private inner class SettingControl<T>(
        /** The latest setting value obtained from the receiver */
        receiverValueFlow: StateFlow<T?>,

        /** A callback to change the setting's value on the receiver */
        private val setReceiverValue: suspend (value: T) -> Result<Unit>
    ) {
        private val dirtyValueFlow = MutableStateFlow<T?>(null)

        val uiState: StateFlow<ReceiverSettingUiState<T>> =
            combineStates(
                receiverValueFlow,
                dirtyValueFlow,
            ) { value, dirtyValue ->
                ReceiverSettingUiState(
                    value = dirtyValue ?: value,
                    status = if (value == null) {
                        ReceiverSettingUiState.Status.NOT_AVAILABLE
                    } else if (dirtyValue != null) {
                        ReceiverSettingUiState.Status.UPDATING
                    } else {
                        ReceiverSettingUiState.Status.READY
                    }
                )
            }

        val setValue: (value: T) -> Unit = { safeSetValue(it) }

        // Using @Synchronized to avoid potential race conditions. Coroutine Mutex would probably be
        // a better fit, but for an unknown reason the Result object returned by setReceiverValue was
        // always successful regardless of the actual outcome when using a Mutex ¯\_(ツ)_/¯
        @Synchronized
        private fun safeSetValue(value: T) {
            dirtyValueFlow.value = value
            coroutineScope.launch {
                setReceiverValue(value).recover(handleError())
                dirtyValueFlow.value = null
            }
        }
    }
}
