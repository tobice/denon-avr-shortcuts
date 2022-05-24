package cz.tobice.denonavrshortcuts.core

import cz.tobice.denonavrshortcuts.core.DispatcherProviderModule.IODispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopeDispatcherModule {

    @Singleton
    @IODispatcher
    @Provides
    fun providesCoroutineScopeWithIODispatcher(
        @IODispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob() + dispatcher)
    }
}
