package cz.tobice.denonavrshortcuts.api

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStream
import java.util.Properties
import javax.inject.Qualifier

/** Provides the URL of the receiver that the app should connect to. */
@Module
@InstallIn(ActivityRetainedComponent::class)
object  ReceiverUrlProviderModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ReceiverUrl

    @Provides
    @ReceiverUrl
    fun provideReceiverUrl(@ApplicationContext context: Context): String {
        // Load the receiver URL from app/src/main/assets/config.local.properties
        // TODO: Load it from a proper place once receiver discovery is implemented
        val inputStream: InputStream = context.assets.open("config.local.properties")
        val properties = Properties()
        properties.load(inputStream)
        return properties.getProperty("RECEIVER_BASE_URL")
    }
}
