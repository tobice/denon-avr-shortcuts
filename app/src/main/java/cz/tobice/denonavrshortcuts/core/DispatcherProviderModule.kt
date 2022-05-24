package cz.tobice.denonavrshortcuts.core

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Provides a coroutine dispatcher for async operations.
 *
 * It's a standalone module so that tests can easily override this binding to inject a dispatcher
 * instance more suitable for testing.
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatcherProviderModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class IODispatcher

    @Singleton
    @Provides
    @IODispatcher
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
