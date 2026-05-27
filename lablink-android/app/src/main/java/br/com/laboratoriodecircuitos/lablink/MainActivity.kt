package br.com.laboratoriodecircuitos.lablink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import br.com.laboratoriodecircuitos.lablink.features.connection.ConnectionScreen
import br.com.laboratoriodecircuitos.lablink.features.controls.ControlsScreen
import br.com.laboratoriodecircuitos.lablink.features.home.LabLinkHomeScreen
import br.com.laboratoriodecircuitos.lablink.features.terminal.TerminalScreen
import br.com.laboratoriodecircuitos.lablink.ui.theme.LabLinkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LabLinkTheme {
                LabLinkApp()
            }
        }
    }
}

private enum class LabLinkScreen {
    Home,
    Connection,
    Terminal,
    Controls,
}

@Composable
private fun LabLinkApp() {
    var currentScreen by remember { mutableStateOf(LabLinkScreen.Home) }

    when (currentScreen) {
        LabLinkScreen.Home -> LabLinkHomeScreen(
            onOpenConnection = { currentScreen = LabLinkScreen.Connection },
            onOpenTerminal = { currentScreen = LabLinkScreen.Terminal },
            onOpenControls = { currentScreen = LabLinkScreen.Controls },
        )

        LabLinkScreen.Connection -> ConnectionScreen(
            onBack = { currentScreen = LabLinkScreen.Home },
        )

        LabLinkScreen.Terminal -> TerminalScreen(
            onBack = { currentScreen = LabLinkScreen.Home },
        )

        LabLinkScreen.Controls -> ControlsScreen(
            onBack = { currentScreen = LabLinkScreen.Home },
        )
    }
}
