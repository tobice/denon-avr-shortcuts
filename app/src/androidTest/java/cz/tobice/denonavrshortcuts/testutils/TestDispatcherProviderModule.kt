package cz.tobice.denonavrshortcuts.testutils

import cz.tobice.denonavrshortcuts.core.DispatcherProviderModule
import cz.tobice.denonavrshortcuts.core.DispatcherProviderModule.DefaultDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Provides an instance of [TrackedDispatcher] that allows detecting (in)activity in tests via an
 * exposed IdlingResource.
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DispatcherProviderModule::class]
)
object TestDispatcherProviderModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class TrackedDefaultDispatcher

    @Singleton
    @Provides
    @TrackedDefaultDispatcher
    fun providesTrackedDefaultDispatcher(): TrackedDispatcher =
        TrackedDispatcher(Dispatchers.Default)

    @Singleton
    @Provides
    @DefaultDispatcher
    fun providesDefaultDispatcher(@TrackedDefaultDispatcher trackedDispatcher: TrackedDispatcher):
        CoroutineDispatcher = trackedDispatcher
}
