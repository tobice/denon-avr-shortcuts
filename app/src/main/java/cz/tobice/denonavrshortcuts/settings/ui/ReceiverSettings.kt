package cz.tobice.denonavrshortcuts.settings.ui

import cz.tobice.denonavrshortcuts.core.ErrorMessage
import kotlinx.coroutines.flow.StateFlow

/**
 * Exposes receiver settings and control over them to the UI.
 *
 * Call [loadSettings] to initialize the setting values from the receiver (and any time later
 * when you want fresh values).
 *
 * This class creates an abstraction over the receiver and its settings hierarchy (this hierarchy is
 * reflected even in the underlying receiver APIs). The purpose of this app is to give a quick
 * access to only a few most used settings, which is why the chosen abstraction exposes a simple
 * flat list of settings, whose value can be obtained and set.
 *
 * Should there ever arise a need to support other receiver models with vastly different APIs, this
 * functionality can be contained in additional implementations of this interface. However, at that
 * moment, it may make sense to push this logic further down from the UI layer.
 */
interface ReceiverSettings {

    /** UI state of the Center Spread setting */
    val centerSpreadUiState: StateFlow<ReceiverSettingUiState<Boolean>>

    /** Set the Center Spread setting value. */
    val setCenterSpread: (Boolean) -> Unit

    /** (Re)load all settings from the receiver. */
    fun loadSettings()

    /** List of errors that occurred during performed operations. */
    val errorMessages: StateFlow<List<ErrorMessage>>
}
