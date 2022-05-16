package cz.tobice.denonavrshortcuts.settings

import cz.tobice.denonavrshortcuts.settings.repositories.DefaultSurroundParameterSettingsRepository
import cz.tobice.denonavrshortcuts.settings.repositories.SurroundParameterSettingsRepository
import cz.tobice.denonavrshortcuts.settings.ui.DefaultReceiverSettings
import cz.tobice.denonavrshortcuts.settings.ui.ReceiverSettings
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class SettingsModule {

    @Binds
    abstract fun bindReceiverSettings(
        defaultReceiverSettings: DefaultReceiverSettings
    ): ReceiverSettings

    @Binds
    abstract fun bindSurroundParameterSettingsRepository(
        defaultSurroundParameterSettingsRepository: DefaultSurroundParameterSettingsRepository
    ): SurroundParameterSettingsRepository
}
