package cz.tobice.denonavrshortcuts.settings.repositories

/**
 * State of Audio > Surround Parameter receiver settings.
 *
 * If a value is null, it means that the setting is at the moment not supported / available.
 */
data class SurroundParameterSettings(
    /** Audio > Surround Parameter > Center Spread setting */
    val centerSpread: Boolean?
)
