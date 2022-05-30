package cz.tobice.denonavrshortcuts.settings.repositories

import cz.tobice.denonavrshortcuts.api.ReceiverApi
import cz.tobice.denonavrshortcuts.api.entities.audio.AudysseyConfig
import cz.tobice.denonavrshortcuts.settings.enums.audio.AudysseyDynamicVolume
import cz.tobice.denonavrshortcuts.settings.enums.fromReceiverValue
import javax.inject.Inject

/**
 * The default implementation of [AudysseySettingsRepository] that uses a Retrofit2 API
 * client to communicate with the receiver.
 */
class DefaultAudysseySettingsRepository @Inject constructor(
    private val receiverApi: ReceiverApi
) : AudysseySettingsRepository {

    override suspend fun getSettings(): Result<AudysseySettings> = runCatching {
        val config = receiverApi.getAudysseyConfig()

        AudysseySettings(
            dynamicVolume = AudysseyDynamicVolume.fromReceiverValue(config.dynamicVolume)
        )
    }

    override suspend fun setDynamicVolume(value: AudysseyDynamicVolume): Result<Unit> =
        runCatching {
            // Unfortunately, we cannot use the AudysseyConfig XML object because the receiver does
            // not expect the root <Audyssey /> element. This is the simplest workaround.
            receiverApi.setAudysseyConfig("<DynamicVolume>${value.receiverValue}</DynamicVolume>")
        }
}
