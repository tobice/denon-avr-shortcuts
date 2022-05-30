package cz.tobice.denonavrshortcuts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.tobice.denonavrshortcuts.settings.enums.audio.AudysseyDynamicVolume
import cz.tobice.denonavrshortcuts.settings.ui.ReceiverSettingUiState
import cz.tobice.denonavrshortcuts.ui.theme.DenonAVRShortcutsTheme
import dagger.hilt.android.AndroidEntryPoint

enum class Screen(val route: String) {
    MAIN("main"),
    DYNAMIC_VOLUME("dynamic_volume")
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
                composable(Screen.DYNAMIC_VOLUME.route) {
                    DynamicVolumeScreen(
                        viewModel.settings.dynamicVolumeUiState.collectAsState().value,
                        viewModel.settings.setDynamicVolume)
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
                navController.navigate(Screen.DYNAMIC_VOLUME.route)
            }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Dynamic Volume (${settings.dynamicVolumeUiState.collectAsState().value.value.label})")
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
fun DynamicVolumeScreen(
    uiState: ReceiverSettingUiState<AudysseyDynamicVolume>,
    setValue: (AudysseyDynamicVolume) -> Unit)
{
    Column {
        AudysseyDynamicVolume.values().forEach {
            Text(
                modifier = Modifier.clickable { setValue(it) }.padding(all = 16.dp),
                text = it.label,
                fontWeight = if (it == uiState.value)
                    FontWeight.Bold else FontWeight.Normal,
                color = if (uiState.isAvailable()) Color.Black else Color.Gray
            )
        }
    }
}