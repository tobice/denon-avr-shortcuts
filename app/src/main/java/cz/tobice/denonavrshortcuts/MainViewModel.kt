package cz.tobice.denonavrshortcuts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cz.tobice.denonavrshortcuts.settings.ui.ReceiverSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val settings: ReceiverSettings
) : ViewModel() {
    init {
        settings.loadSettings()
    }

    enum class EnumSettingOptions(val label: String) {
        OPTION_1("Option 1"),
        OPTION_2("Option 2"),
        OPTION_3("Option 3")
    }
    var enumSettingValue by mutableStateOf(EnumSettingOptions.OPTION_1)
}
