package cz.tobice.denonavrshortcuts.utils

import cz.tobice.denonavrshortcuts.api.entities.audio.SurroundParameterConfig
import io.kotest.matchers.shouldBe
import org.junit.Test

class XmlConversionUtilsTest {
    // TODO: Implement proper tests

    @Test
    fun `toXML returns an unformatted XML string`() {
        toXml(SurroundParameterConfig(centerSpread = "1")) shouldBe
            "<SurroundParameter><CenterSpread>1</CenterSpread></SurroundParameter>"
    }
}