package cz.tobice.denonavrshortcuts.api

import cz.tobice.denonavrshortcuts.api.entities.audio.SurroundParameterConfig
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * A Retrofit2 interface for the HTTP API exposed by the receiver.
 *
 * Specifically, this interface describes the HTTP API of Denon AVR-X1700H.
 *
 * The actual client is instantiated through Hilt in [ReceiverApiProviderModule].
 */
interface ReceiverApi {

    @GET("ajax/audio/get_config?type=4")
    suspend fun getSurroundParameterConfig(): SurroundParameterConfig

    @GET("ajax/audio/set_config?type=4")
    suspend fun setSurroundParameterConfig(
        @Query("data") config: SurroundParameterConfig
    ): ResponseBody
}
