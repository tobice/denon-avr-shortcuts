package cz.tobice.denonavrshortcuts.settings.ui

import cz.tobice.denonavrshortcuts.settings.ui.ReceiverSettingUiState.*
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.Test

class ReceiverSettingUiStateTest {
    @Test
    fun `Available setting`() {
        ReceiverSettingUiState(true, Status.READY).isAvailable() shouldBe true
        ReceiverSettingUiState(true, Status.UPDATING).isAvailable() shouldBe true
    }

    @Test
    fun `Not available setting`() {
        ReceiverSettingUiState(true, Status.NOT_AVAILABLE).isAvailable() shouldNotBe true
    }

    @Test
    fun `Changeable setting`() {
        ReceiverSettingUiState(true, Status.READY).isChangeable() shouldBe true
    }

    @Test
    fun `Not changeable setting`() {
        ReceiverSettingUiState(true, Status.UPDATING).isChangeable() shouldNotBe true
        ReceiverSettingUiState(true, Status.NOT_AVAILABLE).isChangeable() shouldNotBe true
    }
}