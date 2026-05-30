package br.com.laboratoriodecircuitos.lablink

import android.Manifest

import android.os.Bundle
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothConnectionStatus
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothDeviceInfo
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothDiscoveredDevice
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothDiscoveryController
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothDiscoveryStatus
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothPermissionHelper
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothPermissionRequirements
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothPairingStatus
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.LabLinkBluetoothConnectionManager
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.LabLinkBluetoothForegroundService
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardSelectionStorage
import br.com.laboratoriodecircuitos.lablink.core.controls.ControlCommandMapper
import br.com.laboratoriodecircuitos.lablink.core.controls.ControlStorage
import br.com.laboratoriodecircuitos.lablink.core.controls.DefaultControls
import br.com.laboratoriodecircuitos.lablink.core.controls.LabLinkControl
import br.com.laboratoriodecircuitos.lablink.features.boardselection.BoardSelectionScreen
import br.com.laboratoriodecircuitos.lablink.features.connection.ConnectionScreen
import br.com.laboratoriodecircuitos.lablink.features.controls.ControlsScreen
import br.com.laboratoriodecircuitos.lablink.features.createcontrol.CreateControlScreen
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
    BoardSelection,
    Terminal,
    Controls,
    CreateControl,
}

@Composable
private fun LabLinkApp() {
    val context = LocalContext.current
    val bluetoothService = remember { LabLinkBluetoothConnectionManager }
    val bluetoothDiscoveryController = remember { BluetoothDiscoveryController() }
    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    var currentScreen by remember { mutableStateOf(LabLinkScreen.Home) }
    var bluetoothState by remember {
        mutableStateOf(bluetoothService.evaluateInitialState(context))
    }

    var configuredControls by remember {
        mutableStateOf(ControlStorage.loadControls(context))
    }

    var selectedBoard by remember {
        mutableStateOf(BoardSelectionStorage.loadBoard(context))
    }

    var controlsRefreshKey by remember {
        mutableIntStateOf(0)
    }

    var discoveryStatus by remember {
        mutableStateOf(BluetoothDiscoveryStatus.Idle)
    }

    val discoveredDevices = remember {
        mutableStateListOf<BluetoothDiscoveredDevice>()
    }

    DisposableEffect(Unit) {
        onDispose {
            bluetoothDiscoveryController.stopDiscovery(context.applicationContext)
        }
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

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        LabLinkBluetoothForegroundService.start(context.applicationContext)

        bluetoothState = bluetoothState.copy(
            message = if (granted) {
                "Conexão protegida ativada. O LabLink mostrará uma notificação enquanto mantiver o Bluetooth ativo."
            } else {
                "Conexão protegida ativada. Como a notificação foi negada, o Android pode limitar a visibilidade do serviço."
            },
        )
    }

    fun startBluetoothForegroundServiceWithGuide() {
        if (hasNotificationPermission()) {
            LabLinkBluetoothForegroundService.start(context.applicationContext)

            bluetoothState = bluetoothState.copy(
                message = "Conexão protegida ativada. O LabLink manterá um serviço Bluetooth em primeiro plano.",
            )
        } else {
            bluetoothState = bluetoothState.copy(
                message = "Para manter a conexão Bluetooth ativa fora da tela, permita a notificação do LabLink.",
            )

            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun openCreateControlRespectingBoard() {
        val storedBoard = selectedBoard ?: BoardSelectionStorage.loadBoard(context)

        if (storedBoard != null) {
            selectedBoard = storedBoard
            currentScreen = LabLinkScreen.CreateControl
        } else {
            currentScreen = LabLinkScreen.BoardSelection
        }
    }

    fun openControlsRespectingBoard() {
        currentScreen = if (
            bluetoothState.status == BluetoothConnectionStatus.Connected &&
            selectedBoard == null
        ) {
            LabLinkScreen.BoardSelection
        } else {
            LabLinkScreen.Controls
        }
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

                if (resultState.status == BluetoothConnectionStatus.Connected) {
                    startBluetoothForegroundServiceWithGuide()

                    currentScreen = if (selectedBoard == null) {
                        LabLinkScreen.BoardSelection
                    } else {
                        LabLinkScreen.Controls
                    }
                }
            }
        }.start()
    }

    fun disconnectSelectedDevice() {
        bluetoothState = bluetoothService.disconnectDevice(bluetoothState)
        LabLinkBluetoothForegroundService.stop(context.applicationContext)
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

    fun hasDiscoveryPermissions(): Boolean {
        return BluetoothPermissionRequirements.discoveryPermissions().all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission,
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun startBluetoothDiscovery() {
        discoveredDevices.clear()
        discoveryStatus = BluetoothDiscoveryStatus.Scanning

        bluetoothDiscoveryController.startDiscovery(
            context = context.applicationContext,
            onDeviceFound = { discoveredDevice ->
                mainHandler.post {
                    val existingIndex = discoveredDevices.indexOfFirst { existing ->
                        existing.address == discoveredDevice.address
                    }

                    if (existingIndex >= 0) {
                        discoveredDevices[existingIndex] = discoveredDevice
                    } else {
                        discoveredDevices.add(discoveredDevice)
                    }
                }
            },
            onFinished = {
                mainHandler.post {
                    discoveryStatus = BluetoothDiscoveryStatus.Finished
                }
            },
            onError = { message ->
                mainHandler.post {
                    discoveryStatus = BluetoothDiscoveryStatus.Error
                    bluetoothState = bluetoothState.copy(message = message)
                }
            },
        )
    }

    val discoveryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val allGranted = permissions.values.all { granted -> granted }

        if (allGranted) {
            startBluetoothDiscovery()
        } else {
            discoveryStatus = BluetoothDiscoveryStatus.PermissionRequired
            bluetoothState = bluetoothState.copy(
                message = "Permissão Bluetooth necessária para encontrar módulos próximos.",
            )
        }
    }

    fun requestDiscoveryPermissionOrStartSearch() {
        if (hasDiscoveryPermissions()) {
            startBluetoothDiscovery()
        } else {
            discoveryStatus = BluetoothDiscoveryStatus.PermissionRequired
            discoveryPermissionLauncher.launch(
                BluetoothPermissionRequirements.discoveryPermissions(),
            )
        }
    }
    fun pairDiscoveredDevice(discoveredDevice: BluetoothDiscoveredDevice) {
        bluetoothDiscoveryController.startPairing(
            context = context.applicationContext,
            discoveredDevice = discoveredDevice,
            onPairingStarted = {
                mainHandler.post {
                    discoveryStatus = BluetoothDiscoveryStatus.Finished

                    val existingIndex = discoveredDevices.indexOfFirst { existing ->
                        existing.address == discoveredDevice.address
                    }

                    val pairingDevice = discoveredDevice.copy(
                        pairingStatus = BluetoothPairingStatus.Pairing,
                    )

                    if (existingIndex >= 0) {
                        discoveredDevices[existingIndex] = pairingDevice
                    }

                    bluetoothState = bluetoothState.copy(
                        message = "Pareando ${discoveredDevice.name}. Se o Android pedir senha, use 1234. Se não funcionar, tente 0000.",
                    )
                }
            },
            onPairingSucceeded = {
                mainHandler.post {
                    val pairedDevice = discoveredDevice.copy(
                        pairingStatus = BluetoothPairingStatus.Paired,
                    )

                    val existingIndex = discoveredDevices.indexOfFirst { existing ->
                        existing.address == discoveredDevice.address
                    }

                    if (existingIndex >= 0) {
                        discoveredDevices[existingIndex] = pairedDevice
                    }

                    bluetoothState = bluetoothState.copy(
                        message = "${discoveredDevice.name} pareado com sucesso.",
                    )

                    mainHandler.postDelayed(
                        {
                            val refreshedState = bluetoothService.loadPairedDevices(context)
                            val matchedDevice = refreshedState.pairedDevices.firstOrNull { paired ->
                                paired.address == discoveredDevice.address
                            }

                            bluetoothState = if (matchedDevice != null) {
                                bluetoothService.selectDevice(
                                    currentState = refreshedState,
                                    device = matchedDevice,
                                ).copy(
                                    message = "${discoveredDevice.name} pareado com sucesso. Toque em Conectar para controlar seu projeto.",
                                )
                            } else {
                                refreshedState.copy(
                                    message = "${discoveredDevice.name} pareado com sucesso. Agora ele já pode ser selecionado para conexão.",
                                )
                            }
                        },
                        500L,
                    )
                }
            },
            onPairingFailed = {
                mainHandler.post {
                    val existingIndex = discoveredDevices.indexOfFirst { existing ->
                        existing.address == discoveredDevice.address
                    }

                    val notPairedDevice = discoveredDevice.copy(
                        pairingStatus = BluetoothPairingStatus.NotPaired,
                    )

                    if (existingIndex >= 0) {
                        discoveredDevices[existingIndex] = notPairedDevice
                    }

                    bluetoothState = bluetoothState.copy(
                        message = "Pareamento não concluído com ${discoveredDevice.name}. Confira se o módulo está piscando e tente 1234 ou 0000.",
                    )
                }
            },
            onAlreadyPaired = {
                mainHandler.post {
                    val existingIndex = discoveredDevices.indexOfFirst { existing ->
                        existing.address == discoveredDevice.address
                    }

                    val pairedDevice = discoveredDevice.copy(
                        pairingStatus = BluetoothPairingStatus.Paired,
                    )

                    if (existingIndex >= 0) {
                        discoveredDevices[existingIndex] = pairedDevice
                    }

                    bluetoothState = bluetoothState.copy(
                        message = "${discoveredDevice.name} já está pareado. Toque em Conectar ou busque os módulos pareados.",
                    )

                    loadPairedDevices()
                }
            },
            onError = { message ->
                mainHandler.post {
                    discoveryStatus = BluetoothDiscoveryStatus.Error
                    bluetoothState = bluetoothState.copy(message = message)
                }
            },
        )
    }
    fun isInsideBluetoothFlow(): Boolean {
        return bluetoothState.selectedDevice != null ||
            bluetoothState.pairedDevices.isNotEmpty() ||
            discoveredDevices.isNotEmpty() ||
            discoveryStatus != BluetoothDiscoveryStatus.Idle
    }

    fun resetBluetoothFlowToStart() {
        bluetoothDiscoveryController.stopDiscovery(context.applicationContext)
        discoveredDevices.clear()
        discoveryStatus = BluetoothDiscoveryStatus.Idle

        if (bluetoothState.status != BluetoothConnectionStatus.Connected) {
            bluetoothState = bluetoothService.evaluateInitialState(context)
        }
    }
    fun handleAndroidBack() {
        when (currentScreen) {
            LabLinkScreen.Home -> Unit

            LabLinkScreen.Connection -> {
                if (
                    bluetoothState.status != BluetoothConnectionStatus.Connected &&
                    isInsideBluetoothFlow()
                ) {
                    resetBluetoothFlowToStart()
                } else {
                    currentScreen = LabLinkScreen.Home
                }
            }

            LabLinkScreen.BoardSelection -> {
                currentScreen = LabLinkScreen.Connection
            }

            LabLinkScreen.Terminal -> {
                currentScreen = LabLinkScreen.Home
            }

            LabLinkScreen.Controls -> {
                currentScreen = LabLinkScreen.Home
            }

            LabLinkScreen.CreateControl -> {
                currentScreen = LabLinkScreen.Controls
            }
        }
    }

    BackHandler(
        enabled = currentScreen != LabLinkScreen.Home,
    ) {
        handleAndroidBack()
    }
    when (currentScreen) {
        LabLinkScreen.Home -> LabLinkHomeScreen(
            isBluetoothConnected = isBluetoothConnectedForUi,
            connectedDeviceName = bluetoothState.selectedDevice?.name,
            onOpenHome = { currentScreen = LabLinkScreen.Home },
            onOpenConnection = {
                refreshBluetoothState()
                currentScreen = LabLinkScreen.Connection
            },
            onOpenTerminal = { currentScreen = LabLinkScreen.Terminal },
            onOpenControls = { openControlsRespectingBoard() },
        )

        LabLinkScreen.Connection -> ConnectionScreen(
            bluetoothState = bluetoothState,
            discoveryStatus = discoveryStatus,
            discoveredDevices = discoveredDevices,
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
            onDisconnectSelectedDevice = {
                disconnectSelectedDevice()
            },
            onStartPairingGuide = {
                requestDiscoveryPermissionOrStartSearch()
            },
            onPairDiscoveredDevice = { discoveredDevice ->
                pairDiscoveredDevice(discoveredDevice)
            },
            onOpenHome = { currentScreen = LabLinkScreen.Home },
            onOpenConnection = {
                refreshBluetoothState()
                currentScreen = LabLinkScreen.Connection
            },
            onOpenTerminal = { currentScreen = LabLinkScreen.Terminal },
            onOpenControls = { openControlsRespectingBoard() },
            onBack = { currentScreen = LabLinkScreen.Home },
        )

        LabLinkScreen.BoardSelection -> BoardSelectionScreen(
            isBluetoothConnected = isBluetoothConnectedForUi,
            connectedDeviceName = bluetoothState.selectedDevice?.name,
            initialBoard = selectedBoard,
            onSaveBoard = { board ->
                selectedBoard = board
                BoardSelectionStorage.saveBoard(context, board)
                currentScreen = LabLinkScreen.Controls
            },
            onBack = {
                currentScreen = LabLinkScreen.Connection
            },
        )

        LabLinkScreen.Terminal -> TerminalScreen(
            isBluetoothConnected = isBluetoothConnectedForUi,
            onOpenHome = { currentScreen = LabLinkScreen.Home },
            onOpenConnection = {
                refreshBluetoothState()
                currentScreen = LabLinkScreen.Connection
            },
            onOpenTerminal = { currentScreen = LabLinkScreen.Terminal },
            onOpenControls = { openControlsRespectingBoard() },
        )

        LabLinkScreen.CreateControl -> CreateControlScreen(
            isBluetoothConnected = isBluetoothConnectedForUi,
            initialBoard = selectedBoard,
            onOpenHome = { currentScreen = LabLinkScreen.Home },
            onOpenConnection = {
                refreshBluetoothState()
                currentScreen = LabLinkScreen.Connection
            },
            onOpenTerminal = { currentScreen = LabLinkScreen.Terminal },
            onOpenControls = { openControlsRespectingBoard() },
            onSaveControls = { controls ->
                configuredControls = controls.toList()
                controlsRefreshKey++
                ControlStorage.saveControls(context, controls)
                currentScreen = LabLinkScreen.Controls
            },
        )

        LabLinkScreen.Controls -> key(controlsRefreshKey) {
            ControlsScreen(
                bluetoothState = bluetoothState,
                selectedBoard = selectedBoard,
                controls = configuredControls,
                controlsRefreshKey = controlsRefreshKey,
                onSendPing = { sendCommand("PING") },
            onToggleDigitalControl = { control, turnOn ->
                sendCommand(
                    ControlCommandMapper.digitalToggleCommand(
                        control = control,
                        turnOn = turnOn,
                    )
                )
            },
            onSendPwmControl = { control, value ->
                sendCommand(
                    ControlCommandMapper.pwmCommand(
                        control = control,
                        value = value,
                    )
                )
            },
            onSendPulseControl = { control ->
                sendCommand(
                    ControlCommandMapper.pulseCommand(
                        control = control,
                        durationMs = 500,
                    )
                )
            },
            onReadAnalogControl = { control ->
                sendCommand(
                    ControlCommandMapper.analogReadCommand(
                        control = control,
                    )
                )
            },
            onTurnLedOn = {
                sendCommand(
                    ControlCommandMapper.digitalToggleCommand(
                        control = DefaultControls.pin13DigitalOutput,
                        turnOn = true,
                    )
                )
            },
            onTurnLedOff = {
                sendCommand(
                    ControlCommandMapper.digitalToggleCommand(
                        control = DefaultControls.pin13DigitalOutput,
                        turnOn = false,
                    )
                )
            },
            onOpenHome = { currentScreen = LabLinkScreen.Home },
            onOpenConnection = {
                refreshBluetoothState()
                currentScreen = LabLinkScreen.Connection
            },
            onOpenTerminal = { currentScreen = LabLinkScreen.Terminal },
            onOpenControls = { openControlsRespectingBoard() },
            onCreateControl = { openCreateControlRespectingBoard() },
            onClearControls = {
                val defaultControls = listOf(
                    DefaultControls.pin13DigitalOutput.copy(
                        isOn = false,
                    )
                )

                configuredControls = defaultControls.toList()
                ControlStorage.saveControls(context, defaultControls)
                controlsRefreshKey++

                currentScreen = LabLinkScreen.Controls
            },
        )
    }
}
}



























