package cz.tobice.denonavrshortcuts.api.entities.audio

import cz.tobice.denonavrshortcuts.utils.toXml
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "SurroundParameter", strict = false)
data class SurroundParameterConfig @JvmOverloads constructor(
    @field:Element(name = "CenterSpread", required = false)
    @param:Element(name = "CenterSpread")
    var centerSpread: String? = null,
) {
    override fun toString() = toXml(this)
}
