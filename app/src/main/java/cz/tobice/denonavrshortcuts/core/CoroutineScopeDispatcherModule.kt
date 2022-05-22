package cz.tobice.denonavrshortcuts.core

import cz.tobice.denonavrshortcuts.core.DispatcherProviderModule.DefaultDispatcher
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
    @Provides
    fun providesCoroutineScope(@DefaultDispatcher dispatcher: CoroutineDispatcher): CoroutineScope {
        return CoroutineScope(SupervisorJob() + dispatcher)
    }
}
