package cz.tobice.denonavrshortcuts.testutils

import cz.tobice.denonavrshortcuts.api.ReceiverUrlProviderModule
import cz.tobice.denonavrshortcuts.fakes.FakeReceiver
import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.testing.TestInstallIn

/** Provides a receiver URL pointing to the local instance of [FakeReceiver]. */
@Module
@TestInstallIn(
    components = [ActivityRetainedComponent::class],
    replaces = [ReceiverUrlProviderModule::class]
)
class TestReceiverUrlProviderModule {

    @Provides
    @ReceiverUrlProviderModule.ReceiverUrl
    fun provideReceiverUrl(): String {
        return "http://localhost:${FakeReceiver.PORT}/"
    }
}

