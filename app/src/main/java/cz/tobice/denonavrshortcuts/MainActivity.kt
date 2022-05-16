package cz.tobice.denonavrshortcuts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.tobice.denonavrshortcuts.settings.ui.ReceiverSettingUiState
import cz.tobice.denonavrshortcuts.ui.theme.DenonAVRShortcutsTheme
import dagger.hilt.android.AndroidEntryPoint

enum class Screen(val route: String) {
    MAIN("main"),
    SAMPLE_ENUM_SETTING("sample_enum_setting")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppRoot()
        }
    }
}

@Composable
fun AppRoot() {
    DenonAVRShortcutsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            val navController = rememberNavController()
            val viewModel: MainViewModel = hiltViewModel()

            NavHost(navController = navController, startDestination = Screen.MAIN.route) {
                composable(Screen.MAIN.route) {
                    MainScreen(navController, viewModel)
                }
                composable(Screen.SAMPLE_ENUM_SETTING.route) {
                    SampleEnumSettingScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    Column {
        val settings = viewModel.settings

        settings.errorMessages.collectAsState().value.forEach {
            Text(text = it.message)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            BooleanSettingSwitch(
                label = "Center Spread",
                uiState = settings.centerSpreadUiState.collectAsState().value,
                setValue = settings.setCenterSpread
            )
        }
        Row(
            Modifier.clickable(onClick = {
                navController.navigate(Screen.SAMPLE_ENUM_SETTING.route)
            }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Sample enum setting: ${viewModel.enumSettingValue.label}")
        }
    }
}

@Composable
fun BooleanSettingSwitch(
    label: String, uiState: ReceiverSettingUiState<Boolean>, setValue: (Boolean) -> Unit
) {
    Text(text = label, color = if (uiState.isAvailable()) Color.Black else Color.Gray)
    Switch(
        checked = uiState.value,
        enabled = uiState.isChangeable(),
        onCheckedChange = { setValue(it) }
    )
}

@Composable
fun SampleEnumSettingScreen(viewModel: MainViewModel) {
    Column {
        MainViewModel.EnumSettingOptions.values().forEach {
            Text(
                it.label,
                modifier = Modifier.clickable {
                    viewModel.enumSettingValue = it
                },
                fontWeight = if (it == viewModel.enumSettingValue)
                    FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}