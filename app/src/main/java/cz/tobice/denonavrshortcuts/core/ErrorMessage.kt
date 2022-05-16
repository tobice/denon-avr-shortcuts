package cz.tobice.denonavrshortcuts.core

import java.util.*

data class ErrorMessage(
    val id: Long = UUID.randomUUID().mostSignificantBits,
    val message: String)