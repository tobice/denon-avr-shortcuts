package cz.tobice.denonavrshortcuts.settings.ui

import android.util.Log
import cz.tobice.denonavrshortcuts.core.DispatcherProviderModule.IODispatcher
import cz.tobice.denonavrshortcuts.core.ErrorMessage
import cz.tobice.denonavrshortcuts.settings.enums.EnumSetting
import cz.tobice.denonavrshortcuts.settings.enums.audio.AudysseyDynamicVolume
import cz.tobice.denonavrshortcuts.settings.repositories.AudysseySettings
import cz.tobice.denonavrshortcuts.settings.repositories.AudysseySettingsRepository
import cz.tobice.denonavrshortcuts.settings.repositories.SurroundParameterSettings
import cz.tobice.denonavrshortcuts.settings.repositories.SurroundParameterSettingsRepository
import cz.tobice.denonavrshortcuts.utils.combineStates
import cz.tobice.denonavrshortcuts.utils.mapState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
    private val surroundParameterSettingsRepository: SurroundParameterSettingsRepository,
    private val audysseySettingsRepository: AudysseySettingsRepository,
) : ReceiverSettings {

    /** Mutex to prevent simultaneous updates to settings */
    private val mutex = Mutex()

    /** State of Audio > Surround Parameter settings as fetched from the receiver */
    private val surroundParameterSettings = MutableStateFlow<SurroundParameterSettings?>(null)

    /** State of Audio > Audyssey settings as fetched from the receiver */
    private val audysseySettings = MutableStateFlow<AudysseySettings?>(null)

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
    // Audyssey Dynamic Volume
    //

    private val dynamicVolumeValue = audysseySettings.mapState { it?.dynamicVolume }
    private val dirtyDynamicVolumeValue = MutableStateFlow<AudysseyDynamicVolume?>(null)

    /** UI state of the Audyssey Dynamic Volume setting */
    override val dynamicVolumeUiState: StateFlow<ReceiverSettingUiState<AudysseyDynamicVolume>>
        = createEnumSettingUiState(dynamicVolumeValue, dirtyDynamicVolumeValue)

    /** Set the Center Spread setting value. */
    override val setDynamicVolume = { value: AudysseyDynamicVolume ->
        dirtyDynamicVolumeValue.value = value

        safelyChangeSetting {
            audysseySettingsRepository
                .setDynamicVolume(value)
                .map { loadAudysseySettings() }
                .recoverCatching(handleError())

            dirtyDynamicVolumeValue.value = null
        }
    }

    // TODO: Figure out how to avoid duplicating logic for each setting

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

    private inline fun <reified T : Enum<T>>createEnumSettingUiState(
        valueFlow: StateFlow<T?>,
        dirtyValueFlow: StateFlow<T?>
    ): StateFlow<ReceiverSettingUiState<T>> {
        return combineStates(
            valueFlow,
            dirtyValueFlow,
        ) { value, dirtyValue ->
            ReceiverSettingUiState(
                value = dirtyValue ?: value ?: enumValues<T>()[0],
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
