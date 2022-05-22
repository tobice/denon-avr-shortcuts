package cz.tobice.denonavrshortcuts.api.entities.audio

import io.kotest.matchers.shouldBe
import org.junit.Test

class SurroundParameterConfigTest() {
    // TODO: Implement proper tests

    @Test
    fun `convert to string`() {
        SurroundParameterConfig(centerSpread = "1").toString() shouldBe
            "<SurroundParameter><CenterSpread>1</CenterSpread></SurroundParameter>"
    }
}