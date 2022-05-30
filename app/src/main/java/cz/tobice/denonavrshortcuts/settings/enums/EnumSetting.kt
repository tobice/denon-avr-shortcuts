package cz.tobice.denonavrshortcuts.settings.enums

import android.util.Log

/**
 * A shared interface for multi-option receiver settings that are internally represented as enums
 */
interface EnumSetting {
    /** The actual value as reported by the receiver */
    val receiverValue: String

    /** Human-readable label for the setting */
    val label: String
}

interface EnumSettingCompanion<T : Enum<T>>

/**
 * Converts a string value provided by the receiver into an enum value.
 *
 * Code taken from: https://stackoverflow.com/a/57102199
 *
 * @return the enum value or null if not found
 */
inline fun <reified T> EnumSettingCompanion<T>.fromReceiverValue(
    receiverValue: String?
): T? where T : Enum<T>, T : EnumSetting {
    val value = enumValues<T>().find { it.receiverValue == receiverValue }

    if (value == null) {
        Log.w(this.javaClass.simpleName, "Unknown receiver value: $receiverValue")
    }

    return value
}
