package cz.tobice.denonavrshortcuts.settings.fakes

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
class FakeSurroundParameterSettingsRepository : SurroundParameterSettingsRepository {

    private var _nextShouldFail = false

    /** Call this for the next suspend operation to fail. */
    fun nextShouldFail() {
        _nextShouldFail = true
    }

    var centerSpread: Boolean? = null

    override suspend fun getSettings(): Result<SurroundParameterSettings> = maybeFail {
        SurroundParameterSettings(centerSpread = centerSpread)
    }

    override suspend fun setCenterSpread(value: Boolean): Result<Unit> = maybeFail {
        centerSpread = value
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