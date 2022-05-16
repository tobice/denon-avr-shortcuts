package cz.tobice.denonavrshortcuts.settings.ui

/** UI state of a receiver setting toggle / control */
data class ReceiverSettingUiState<T>(
    /**
     * Current value of the setting to be shown in the UI (not necessarily the actual value on the
     * receiver. */
    val value: T,

    /** The setting's status */
    val status: Status,
) {
    enum class Status {
        /** Setting is loaded and can be changed. */
        READY,

        /** Setting value is updating. */
        UPDATING,

        /**
         * Setting is not available, either because it hasn't been loaded or it is not supported by
         * the current configuration.
         */
        NOT_AVAILABLE
    }

    fun isAvailable() = status != Status.NOT_AVAILABLE
    fun isChangeable() = status == Status.READY
}