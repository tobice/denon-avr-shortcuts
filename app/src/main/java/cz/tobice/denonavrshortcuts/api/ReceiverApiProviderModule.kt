package cz.tobice.denonavrshortcuts.api

import cz.tobice.denonavrshortcuts.api.ReceiverUrlProviderModule.ReceiverUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.*
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

@Module
@InstallIn(ActivityRetainedComponent::class)
object ReceiverApiProviderModule {

    @Provides
    fun provideDenonReceiverApi(
        @ReceiverUrl receiverUrl: String
    ): ReceiverApi = Retrofit.Builder()
        .baseUrl(receiverUrl)
        // The receiver uses a self-signed certificate for HTTPs. The default OkHttp client will
        // refuse to send request.
        .client(UnsafeOkHttpClientFactory.getInstance(listOf(
            // Add an interceptor to log all requests for debugging purposes.
            HttpLoggingInterceptor().apply { level = Level.BODY }
        )))
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build()
        .create(ReceiverApi::class.java)
}
