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
class AudysseyConfigTest {

    @Test
    fun `serialize empty config`() {
        AudysseyConfig().toString() shouldBe
            "<Audyssey/>"
    }

    @Test
    fun `serialize Dynamic Volume`() {
        AudysseyConfig(dynamicVolume = "value").toString() shouldBe
            "<Audyssey><DynamicVolume>value</DynamicVolume></Audyssey>"
    }

    @Test
    fun `parse Dynamic Volume`() {
        parse("<Audyssey><DynamicVolume>value</DynamicVolume></Audyssey>") shouldBe
            AudysseyConfig(dynamicVolume = "value")
    }

    @Test
    fun `parse empty Dynamic Volume`() {
        parse("<Audyssey><DynamicVolume /></Audyssey>") shouldBe
            AudysseyConfig(dynamicVolume = null)
    }

    @Test
    fun `parse missing Dynamic Volume`() {
        parse("<Audyssey></Audyssey>") shouldBe
            AudysseyConfig(dynamicVolume = null)
    }

    @Test
    fun `parsing unsupported root element succeeds`() {
        parse("<UnsupportedElement />") shouldBe
            AudysseyConfig()
    }

    @Test
    fun `parsing unsupported nested elements succeeds`() {
        parse("<Audyssey><UnsupportedSetting /><DynamicVolume>value</DynamicVolume></Audyssey>") shouldBe
            AudysseyConfig(dynamicVolume = "value")
    }

    @Test
    fun `parsing unsupported attributes succeeds`() {
        parse("<Audyssey><DynamicVolume unsupportedAttr=\"value\">value</DynamicVolume></Audyssey>") shouldBe
            AudysseyConfig(dynamicVolume = "value")
    }

    @Test
    fun `parsing empty string fails`() {
        shouldThrow<XMLStreamException>{
            parse("")
        }
    }

    private fun parse(xml: String): AudysseyConfig {
        val serializer: Serializer = Persister()
        return serializer.read(AudysseyConfig::class.java, xml)
    }
}