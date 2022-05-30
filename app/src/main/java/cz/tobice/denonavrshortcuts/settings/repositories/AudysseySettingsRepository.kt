package cz.tobice.denonavrshortcuts.settings.repositories

import cz.tobice.denonavrshortcuts.settings.enums.audio.AudysseyDynamicVolume

/** Fetch and update Audio > Audyssey settings on the receiver. */
interface AudysseySettingsRepository {
    /** Fetch the current values from the receiver. */
    suspend fun getSettings(): Result<AudysseySettings>

    /** Set the Dynamic Volume setting. */
    suspend fun setDynamicVolume(value: AudysseyDynamicVolume): Result<Unit>
}
