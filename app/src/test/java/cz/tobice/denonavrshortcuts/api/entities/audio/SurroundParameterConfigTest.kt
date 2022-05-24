package cz.tobice.denonavrshortcuts.api.entities.audio

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.Test
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import javax.xml.stream.XMLStreamException

/**
 * Explicitly test the XML conversion to make sure that the receiver responses will be properly
 * parsed. The receiver may be sending additional attributes and tags (settings) that we don't care
 * about and that needs to be silently ignored.
 **/
class SurroundParameterConfigTest() {

    @Test
    fun `serialize empty config`() {
        SurroundParameterConfig().toString() shouldBe
            "<SurroundParameter/>"
    }

    @Test
    fun `serialize enabled Center Spread`() {
        SurroundParameterConfig(centerSpread = "1").toString() shouldBe
            "<SurroundParameter><CenterSpread>1</CenterSpread></SurroundParameter>"
    }

    @Test
    fun `serialize disabled Center Spread`() {
        SurroundParameterConfig(centerSpread = "0").toString() shouldBe
            "<SurroundParameter><CenterSpread>0</CenterSpread></SurroundParameter>"
    }

    @Test
    fun `parse enabled Center Spread`() {
        parse("<SurroundParameter><CenterSpread>1</CenterSpread></SurroundParameter>") shouldBe
            SurroundParameterConfig(centerSpread = "1")
    }

    @Test
    fun `parse disabled Center Spread`() {
        parse("<SurroundParameter><CenterSpread>0</CenterSpread></SurroundParameter>") shouldBe
            SurroundParameterConfig(centerSpread = "0")
    }

    @Test
    fun `parse empty Center Spread`() {
        parse("<SurroundParameter><CenterSpread /></SurroundParameter>") shouldBe
            SurroundParameterConfig(centerSpread = null)
    }

    @Test
    fun `parse missing Center Spread`() {
        parse("<SurroundParameter></SurroundParameter>") shouldBe
            SurroundParameterConfig(centerSpread = null)
    }

    @Test
    fun `parsing unsupported root element succeeds`() {
        parse("<UnsupportedElement />") shouldBe
            SurroundParameterConfig()
    }

    @Test
    fun `parsing unsupported nested elements succeeds`() {
        parse("<SurroundParameter><UnsupportedSetting /><CenterSpread>0</CenterSpread></SurroundParameter>") shouldBe
            SurroundParameterConfig(centerSpread = "0")
    }

    @Test
    fun `parsing unsupported attributes succeeds`() {
        parse("<SurroundParameter><CenterSpread unsupportedAttr=\"value\">0</CenterSpread></SurroundParameter>") shouldBe
            SurroundParameterConfig(centerSpread = "0")
    }

    @Test
    fun `parsing empty string fails`() {
        shouldThrow<XMLStreamException>{
            parse("")
        }
    }

    private fun parse(xml: String): SurroundParameterConfig {
        val serializer: Serializer = Persister()
        return serializer.read(SurroundParameterConfig::class.java, xml)
    }
}
