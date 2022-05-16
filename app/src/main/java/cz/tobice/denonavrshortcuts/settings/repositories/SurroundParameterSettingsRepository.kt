package cz.tobice.denonavrshortcuts.settings.repositories

/** Fetch and update Audio > Surround Parameter settings on the receiver. */
interface SurroundParameterSettingsRepository {
    /** Fetch the current values from the receiver. */
    suspend fun getSettings(): Result<SurroundParameterSettings>

    /** Set the Center Spread setting. */
    suspend fun setCenterSpread(value: Boolean): Result<Unit>
}
