package br.com.laboratoriodecircuitos.lablink

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothConnectionStatus
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothDeviceInfo
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothPermissionHelper
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.LabLinkBluetoothService
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
    val context = LocalContext.current
    val bluetoothService = remember { LabLinkBluetoothService() }
    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    var currentScreen by remember { mutableStateOf(LabLinkScreen.Home) }
    var bluetoothState by remember {
        mutableStateOf(bluetoothService.evaluateInitialState(context))
    }

    val isBluetoothConnectedForUi =
        bluetoothState.status == BluetoothConnectionStatus.Connected

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        if (bluetoothState.status != BluetoothConnectionStatus.Connected) {
            bluetoothState = bluetoothService.evaluateInitialState(context)
        }
    }

    fun refreshBluetoothState() {
        if (bluetoothState.status != BluetoothConnectionStatus.Connected) {
            bluetoothState = bluetoothService.evaluateInitialState(context)
        }
    }

    fun loadPairedDevices() {
        bluetoothState = bluetoothService.loadPairedDevices(context)
    }

    fun selectDevice(device: BluetoothDeviceInfo) {
        bluetoothState = bluetoothService.selectDevice(
            currentState = bluetoothState,
            device = device,
        )
    }

    fun connectSelectedDevice() {
        val stateBeforeConnection = bluetoothService.connectingState(bluetoothState)
        bluetoothState = stateBeforeConnection

        Thread {
            val resultState = bluetoothService.connectToSelectedDevice(
                context = context.applicationContext,
                currentState = stateBeforeConnection,
            )

            mainHandler.post {
                bluetoothState = resultState
            }
        }.start()
    }

    fun sendCommand(command: String) {
        Thread {
            val resultState = bluetoothService.sendCommandAndReadResponse(
                currentState = bluetoothState,
                command = command,
            )

            mainHandler.post {
                bluetoothState = resultState
            }
        }.start()
    }

    when (currentScreen) {
        LabLinkScreen.Home -> LabLinkHomeScreen(
            isBluetoothConnected = isBluetoothConnectedForUi,
            onOpenHome = { currentScreen = LabLinkScreen.Home },
            onOpenConnection = {
                refreshBluetoothState()
                currentScreen = LabLinkScreen.Connection
            },
            onOpenTerminal = { currentScreen = LabLinkScreen.Terminal },
            onOpenControls = { currentScreen = LabLinkScreen.Controls },
        )

        LabLinkScreen.Connection -> ConnectionScreen(
            bluetoothState = bluetoothState,
            developmentNotes = bluetoothService.getDevelopmentNotes(),
            onRequestPermissions = {
                permissionLauncher.launch(BluetoothPermissionHelper.requiredPermissions())
            },
            onRefresh = {
                refreshBluetoothState()
            },
            onLoadPairedDevices = {
                loadPairedDevices()
            },
            onSelectDevice = { device ->
                selectDevice(device)
            },
            onConnectSelectedDevice = {
                connectSelectedDevice()
            },
            onOpenControls = { currentScreen = LabLinkScreen.Controls },
            onBack = { currentScreen = LabLinkScreen.Home },
        )

        LabLinkScreen.Terminal -> TerminalScreen(
            isBluetoothConnected = isBluetoothConnectedForUi,
            onOpenHome = { currentScreen = LabLinkScreen.Home },
            onOpenConnection = {
                refreshBluetoothState()
                currentScreen = LabLinkScreen.Connection
            },
            onOpenTerminal = { currentScreen = LabLinkScreen.Terminal },
            onOpenControls = { currentScreen = LabLinkScreen.Controls },
        )

        LabLinkScreen.Controls -> ControlsScreen(
            bluetoothState = bluetoothState,
            onSendPing = { sendCommand("PING") },
            onTurnLedOn = { sendCommand("LED:ON") },
            onTurnLedOff = { sendCommand("LED:OFF") },
            onOpenHome = { currentScreen = LabLinkScreen.Home },
            onOpenConnection = {
                refreshBluetoothState()
                currentScreen = LabLinkScreen.Connection
            },
            onOpenTerminal = { currentScreen = LabLinkScreen.Terminal },
            onOpenControls = { currentScreen = LabLinkScreen.Controls },
        )
    }
}
