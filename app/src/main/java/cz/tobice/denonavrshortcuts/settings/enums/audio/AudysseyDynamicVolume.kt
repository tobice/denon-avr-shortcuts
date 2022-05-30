package cz.tobice.denonavrshortcuts.settings.enums.audio

import cz.tobice.denonavrshortcuts.settings.enums.EnumSetting
import cz.tobice.denonavrshortcuts.settings.enums.EnumSettingCompanion

enum class AudysseyDynamicVolume(
    override val receiverValue: String,
    override val label: String
) : EnumSetting {
    HEAVY("1", "Heavy"),
    MEDIUM("2", "Medium"),
    LIGHT("3", "Light"),
    OFF("4", "Off");

    companion object : EnumSettingCompanion<AudysseyDynamicVolume>
}
