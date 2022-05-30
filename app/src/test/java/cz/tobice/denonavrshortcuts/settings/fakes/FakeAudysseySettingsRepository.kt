package cz.tobice.denonavrshortcuts.settings.fakes

import cz.tobice.denonavrshortcuts.settings.enums.audio.AudysseyDynamicVolume
import cz.tobice.denonavrshortcuts.settings.repositories.AudysseySettings
import cz.tobice.denonavrshortcuts.settings.repositories.AudysseySettingsRepository
import cz.tobice.denonavrshortcuts.settings.repositories.SurroundParameterSettings
import cz.tobice.denonavrshortcuts.settings.repositories.SurroundParameterSettingsRepository

/**
 * Fake repository that holds its own state, pretending to be a real receiver.
 *
 * While setting up failure scenarios in tests is a bit cumbersome (see [nextShouldFail]), the
 * overall experience is much more natural than with a standard mock. When a setting value is
 * changed, we can simply inspect the fake's internal state the same way we would check the state
 * of the actual receiver in a real world.
 */
class FakeAudysseySettingsRepository : AudysseySettingsRepository {

    private var _nextShouldFail = false

    /** Call this for the next suspend operation to fail. */
    fun nextShouldFail() {
        _nextShouldFail = true
    }

    var dynamicVolume: AudysseyDynamicVolume? = null

    override suspend fun getSettings(): Result<AudysseySettings> = maybeFail {
        AudysseySettings(dynamicVolume = dynamicVolume)
    }

    override suspend fun setDynamicVolume(value: AudysseyDynamicVolume): Result<Unit> = maybeFail {
        dynamicVolume = value
    }

    private fun <T, R> T.maybeFail(block: T.() -> R): Result<R> {
        return if (_nextShouldFail) {
            _nextShouldFail = false
            Result.failure(Exception())
        } else {
            Result.success(block())
        }
    }
}
