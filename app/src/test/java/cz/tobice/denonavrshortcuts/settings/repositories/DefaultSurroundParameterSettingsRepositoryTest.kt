package cz.tobice.denonavrshortcuts.settings.repositories

import cz.tobice.denonavrshortcuts.api.ReceiverApi
import cz.tobice.denonavrshortcuts.api.entities.audio.SurroundParameterConfig
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

@ExperimentalCoroutinesApi
class DefaultSurroundParameterSettingsRepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var receiverApi: ReceiverApi

    lateinit var repository: DefaultSurroundParameterSettingsRepository

    @Before
    fun setUp() {
        repository = DefaultSurroundParameterSettingsRepository(receiverApi)
    }

    @Test
    fun `get all Surround Parameter settings`() = runTest {
        coEvery {
            receiverApi.getSurroundParameterConfig()
        } returns SurroundParameterConfig(centerSpread = "1")

        repository.getSettings() shouldBe
            Result.success(SurroundParameterSettings(centerSpread = true))
    }

    @Test
    fun `get all Surround Parameter settings but receiver call fails`() = runTest {
        coEvery {
            receiverApi.getSurroundParameterConfig()
        } throws RECEIVER_ERROR

        repository.getSettings() shouldBe Result.failure(RECEIVER_ERROR)
    }

    @Test
    fun `enable Center Spread`() = runTest {
        coEvery {
            receiverApi.setSurroundParameterConfig(any())
        } returns EMPTY_SUCCESSFUL_RESPONSE

        repository.setCenterSpread(true) shouldBe Result.success(Unit)

        coVerify {
            receiverApi.setSurroundParameterConfig(SurroundParameterConfig(centerSpread = "1"))
        }
    }

    @Test
    fun `disable Center Spread`() = runTest {
        coEvery {
            receiverApi.setSurroundParameterConfig(any())
        } returns EMPTY_SUCCESSFUL_RESPONSE

        repository.setCenterSpread(false) shouldBe Result.success(Unit)

        coVerify {
            receiverApi.setSurroundParameterConfig(SurroundParameterConfig(centerSpread = "0"))
        }
    }

    @Test
    fun `enable Center Spread but receiver call fails`() = runTest {
        coEvery {
            receiverApi.setSurroundParameterConfig(any())
        } throws RECEIVER_ERROR

        repository.setCenterSpread(true) shouldBe Result.failure(RECEIVER_ERROR)
    }

    companion object {
        private val RECEIVER_ERROR = Exception()

        private val EMPTY_SUCCESSFUL_RESPONSE = "".toResponseBody()
    }
}
