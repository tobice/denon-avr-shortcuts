package cz.tobice.denonavrshortcuts.utils

import cz.tobice.denonavrshortcuts.api.entities.audio.SurroundParameterConfig
import io.kotest.matchers.shouldBe
import org.junit.Test

class XmlConversionUtilsTest {

    @Test
    fun `toXML returns an unformatted XML string`() {
        toXml(SurroundParameterConfig(centerSpread = "1")) shouldBe
            "<SurroundParameter><CenterSpread>1</CenterSpread></SurroundParameter>"
    }

    @Test
    fun `oneZeroToBoolean converts 1 to true`() {
        oneZeroToBoolean("1") shouldBe true
    }

    @Test
    fun `oneZeroToBoolean converts 0 to false`() {
        oneZeroToBoolean("0") shouldBe false
    }

    @Test
    fun `oneZeroToBoolean converts unsupported value to nul`() {
        oneZeroToBoolean("unsupported value") shouldBe null
    }

    @Test
    fun `booleanToOneZero converts true to 1`() {
        booleanToOneZero(true) shouldBe "1"
    }

    @Test
    fun `booleanToOneZero converts false to 0`() {
        booleanToOneZero(false) shouldBe "0"
    }
}
