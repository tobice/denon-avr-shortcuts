package cz.tobice.denonavrshortcuts.settings.repositories

import cz.tobice.denonavrshortcuts.settings.enums.audio.AudysseyDynamicVolume

/**
 * State of Audio > Audyssey receiver settings.
 *
 * If a value is null, it means that the setting is at the moment not supported / available.
 */
data class AudysseySettings(
    /** Audio > Audyssey > Dynamic Volume setting */
    val dynamicVolume: AudysseyDynamicVolume?
)
