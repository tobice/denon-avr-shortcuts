package cz.tobice.denonavrshortcuts.api.entities.audio

import cz.tobice.denonavrshortcuts.utils.toXml
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "Audyssey", strict = false)
data class AudysseyConfig  @JvmOverloads constructor(
    @field:Element(name = "DynamicVolume", required = false)
    @param:Element(name = "DynamicVolume")
    var dynamicVolume: String? = null,
) {
    override fun toString() = toXml(this)
}
