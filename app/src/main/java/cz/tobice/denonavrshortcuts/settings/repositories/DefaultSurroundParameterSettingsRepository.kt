package cz.tobice.denonavrshortcuts.settings.repositories

import cz.tobice.denonavrshortcuts.api.ReceiverApi
import cz.tobice.denonavrshortcuts.api.entities.audio.SurroundParameterConfig
import cz.tobice.denonavrshortcuts.utils.booleanToOneZero
import cz.tobice.denonavrshortcuts.utils.oneZeroToBoolean
import javax.inject.Inject

/**
 * The default implementation of [SurroundParameterSettingsRepository] that uses a Retrofit2 API
 * client to communicate with the receiver.
 */
class DefaultSurroundParameterSettingsRepository @Inject constructor(
    private val receiverApi: ReceiverApi
) : SurroundParameterSettingsRepository {

    override suspend fun getSettings(): Result<SurroundParameterSettings> = runCatching {
        val config = receiverApi.getSurroundParameterConfig()

        // Other settings are at the moment ignored.
        SurroundParameterSettings(centerSpread = oneZeroToBoolean(config.centerSpread))
    }

    override suspend fun setCenterSpread(value: Boolean): Result<Unit> = runCatching {
        val config = SurroundParameterConfig(centerSpread = booleanToOneZero(value))
        receiverApi.setSurroundParameterConfig(config)
    }
}
