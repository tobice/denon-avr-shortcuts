package cz.tobice.denonavrshortcuts.settings.repositories

import javax.inject.Inject

// Temporary implementation which holds its own state.
class DefaultSurroundParameterSettingsRepository @Inject constructor() :
    SurroundParameterSettingsRepository {

    // TODO: Remove
    private var tmpCenterSpread = false

    override suspend fun getSettings(): Result<SurroundParameterSettings> = runCatching {
        // TODO: Implement
        SurroundParameterSettings(centerSpread = tmpCenterSpread)
    }

    override suspend fun setCenterSpread(value: Boolean): Result<Unit> = runCatching {
        // TODO: Implement
        tmpCenterSpread = value
    }
}