package cz.tobice.denonavrshortcuts.settings.repositories

import cz.tobice.denonavrshortcuts.api.ReceiverApi
import cz.tobice.denonavrshortcuts.api.entities.audio.AudysseyConfig
import cz.tobice.denonavrshortcuts.settings.enums.audio.AudysseyDynamicVolume
import cz.tobice.denonavrshortcuts.settings.enums.audio.AudysseyDynamicVolume.*
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.stream.Stream

@ExperimentalCoroutinesApi
class DefaultAudysseySettingsRepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var receiverApi: ReceiverApi

    lateinit var repository: DefaultAudysseySettingsRepository

    @Before
    fun setUp() {
        repository = DefaultAudysseySettingsRepository(receiverApi)
    }

    @Test
    fun `get all Audyssey settings`() = runTest {
        coEvery {
            receiverApi.getAudysseyConfig()
        } returns AudysseyConfig(dynamicVolume = HEAVY.receiverValue)

        repository.getSettings() shouldBe
            Result.success(AudysseySettings(dynamicVolume = HEAVY))
    }

    @Test
    fun `get all Audyssey settings but receiver call fails`() = runTest {
        coEvery {
            receiverApi.getAudysseyConfig()
        } throws RECEIVER_ERROR

        repository.getSettings() shouldBe Result.failure(RECEIVER_ERROR)
    }

    @Test
    fun `get all Audyssey settings but receiver returns unsupported setting value`() = runTest {
        coEvery {
            receiverApi.getAudysseyConfig()
        } returns AudysseyConfig(dynamicVolume = "unsupported_dynamic_volume_value")

        repository.getSettings() shouldBe
            Result.success(AudysseySettings(dynamicVolume = null))    }

    @Test
    fun `set Dynamic Volume`() = runTest {
        coEvery {
            receiverApi.setAudysseyConfig(any())
        } returns EMPTY_SUCCESSFUL_RESPONSE

        AudysseyDynamicVolume.values().forEach {
            repository.setDynamicVolume(it) shouldBe Result.success(Unit)

            coVerify {
                receiverApi.setAudysseyConfig("<DynamicVolume>${it.receiverValue}</DynamicVolume>")
            }
        }
    }

    @Test
    fun `set Dynamic Volume but receiver call fails`() = runTest {
        coEvery {
            receiverApi.setAudysseyConfig(any())
        } throws RECEIVER_ERROR

        repository.setDynamicVolume(HEAVY) shouldBe Result.failure(RECEIVER_ERROR)
    }

    companion object {
        private val RECEIVER_ERROR = Exception()

        private val EMPTY_SUCCESSFUL_RESPONSE = "".toResponseBody()
    }
}
