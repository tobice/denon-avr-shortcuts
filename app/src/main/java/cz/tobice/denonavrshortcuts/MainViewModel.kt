package cz.tobice.denonavrshortcuts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var booleanSettingValue by mutableStateOf(false)

    enum class EnumSettingOptions(val label: String) {
        OPTION_1("Option 1"),
        OPTION_2("Option 2"),
        OPTION_3("Option 3")
    }
    var enumSettingValue by mutableStateOf(EnumSettingOptions.OPTION_1)
}