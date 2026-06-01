package br.com.laboratoriodecircuitos.lablink

import android.Manifest
import android.content.Intent
import android.net.Uri

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
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardProfiles
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardSelectionStorage
import br.com.laboratoriodecircuitos.lablink.core.controls.ControlCommandMapper
import br.com.laboratoriodecircuitos.lablink.core.controls.ControlType
import br.com.laboratoriodecircuitos.lablink.core.controls.CustomControl
import br.com.laboratoriodecircuitos.lablink.core.controls.CustomControlStorage
import br.com.laboratoriodecircuitos.lablink.core.controls.LabLinkControl
import br.com.laboratoriodecircuitos.lablink.features.boardselection.BoardSelectionScreen
import br.com.laboratoriodecircuitos.lablink.features.connection.ConnectionScreen
import br.com.laboratoriodecircuitos.lablink.features.customcontrol.CustomControlCanvasScreen
import br.com.laboratoriodecircuitos.lablink.features.customcontrol.WidgetSettingsScreen
import br.com.laboratoriodecircuitos.lablink.features.home.LabLinkHomeScreen
import br.com.laboratoriodecircuitos.lablink.features.joystick.JoystickScreen
import br.com.laboratoriodecircuitos.lablink.features.mainmenu.MainMenuScreen
import br.com.laboratoriodecircuitos.lablink.features.mycontrols.MyControlsScreen
import br.com.laboratoriodecircuitos.lablink.features.terminal.TerminalScreen
import br.com.laboratoriodecircuitos.lablink.ui.theme.LabLinkTheme
import java.util.UUID

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
    MainMenu,
    MyControls,
    CustomControlCanvas,
    WidgetSettings,
    Joystick,
    Terminal,
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

    var customControls by remember {
        mutableStateOf(CustomControlStorage.loadControls(context))
    }

    var activeCustomControl by remember {
        mutableStateOf<CustomControl?>(null)
    }

    var isEditingCustomControl by remember {
        mutableStateOf(false)
    }

    var customControlNameError by remember {
        mutableStateOf<String?>(null)
    }

    var selectedBoard by remember {
        mutableStateOf(BoardSelectionStorage.loadBoard(context))
    }

    var selectedModuleType by remember {
        mutableStateOf<ControlType?>(null)
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

    fun openMainMenuRespectingBoard() {
        currentScreen = if (
            bluetoothState.status == BluetoothConnectionStatus.Connected &&
            selectedBoard == null
        ) {
            LabLinkScreen.BoardSelection
        } else {
            LabLinkScreen.MainMenu
        }
    }

    fun createNewCustomControl() {
        activeCustomControl = CustomControl(
            id = UUID.randomUUID().toString(),
            name = "",
            widgets = emptyList(),
            isSaved = false,
        )
        selectedModuleType = null
        customControlNameError = null
        isEditingCustomControl = true
        currentScreen = LabLinkScreen.CustomControlCanvas
    }

    fun saveCustomControls(updatedControls: List<CustomControl>) {
        customControls = updatedControls
        CustomControlStorage.saveControls(context, updatedControls)
    }

    fun saveActiveCustomControl() {
        val control = activeCustomControl ?: return
        val normalizedName = control.name.trim().lowercase()

        if (normalizedName.isBlank()) {
            customControlNameError = "Dê um nome para salvar o controle."
            return
        }

        val duplicatedName = customControls.any { savedControl ->
            savedControl.id != control.id &&
                savedControl.name.trim().lowercase() == normalizedName
        }

        if (duplicatedName) {
            customControlNameError = "Já existe um controle com esse nome."
            return
        }

        val savedControl = control.copy(
            name = control.name.trim(),
            isSaved = true,
        )
        val updatedControls = customControls
            .filterNot { it.id == savedControl.id } + savedControl

        activeCustomControl = savedControl
        isEditingCustomControl = false
        customControlNameError = null
        saveCustomControls(updatedControls)
    }

    fun openCommunity() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/circuiteiros"))
        context.startActivity(intent)
    }

    fun widgetsOverlap(
        moving: LabLinkControl,
        targetX: Int,
        targetY: Int,
        other: LabLinkControl,
    ): Boolean {
        val movingWidth = moving.widthUnits.coerceIn(1, 3)
        val movingHeight = moving.heightUnits.coerceIn(1, 2)
        val otherWidth = other.widthUnits.coerceIn(1, 3)
        val otherHeight = other.heightUnits.coerceIn(1, 2)

        return targetX < other.gridX + otherWidth &&
            targetX + movingWidth > other.gridX &&
            targetY < other.gridY + otherHeight &&
            targetY + movingHeight > other.gridY
    }

    fun firstAvailableWidgetPosition(
        moving: LabLinkControl,
        widgets: List<LabLinkControl>,
        preferredX: Int,
        preferredY: Int,
    ): Pair<Int, Int>? {
        val width = moving.widthUnits.coerceIn(1, 3)
        val maxX = 3 - width
        val maxY = (widgets.maxOfOrNull { it.gridY + it.heightUnits.coerceIn(1, 2) } ?: 0) + 6
        val candidates = buildList {
            add(preferredX to preferredY)
            for (y in preferredY..maxY) {
                for (x in 0..maxX) {
                    add(x to y)
                }
            }
            for (y in 0 until preferredY) {
                for (x in 0..maxX) {
                    add(x to y)
                }
            }
        }

        return candidates.firstOrNull { (x, y) ->
            x in 0..maxX &&
                y >= 0 &&
                widgets.none { existing ->
                    existing.id != moving.id && widgetsOverlap(moving, x, y, existing)
                }
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
                        LabLinkScreen.MainMenu
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

            LabLinkScreen.MainMenu -> {
                currentScreen = LabLinkScreen.Home
            }

            LabLinkScreen.MyControls,
            LabLinkScreen.CustomControlCanvas,
            LabLinkScreen.Joystick,
            LabLinkScreen.Terminal -> {
                currentScreen = LabLinkScreen.MainMenu
            }

            LabLinkScreen.WidgetSettings -> {
                currentScreen = LabLinkScreen.CustomControlCanvas
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
            onOpenControls = { openMainMenuRespectingBoard() },
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
            onOpenControls = { openMainMenuRespectingBoard() },
            onBack = { currentScreen = LabLinkScreen.Home },
        )

        LabLinkScreen.BoardSelection -> BoardSelectionScreen(
            isBluetoothConnected = isBluetoothConnectedForUi,
            connectedDeviceName = bluetoothState.selectedDevice?.name,
            initialBoard = selectedBoard,
            onSaveBoard = { board ->
                selectedBoard = board
                BoardSelectionStorage.saveBoard(context, board)
                currentScreen = LabLinkScreen.MainMenu
            },
            onBack = {
                currentScreen = LabLinkScreen.Connection
            },
            onOpenHome = { currentScreen = LabLinkScreen.Home },
            onOpenConnection = {
                refreshBluetoothState()
                currentScreen = LabLinkScreen.Connection
            },
            onOpenControls = { openMainMenuRespectingBoard() },
            onOpenTerminal = { currentScreen = LabLinkScreen.Terminal },
        )

        LabLinkScreen.Terminal -> TerminalScreen(
            isBluetoothConnected = isBluetoothConnectedForUi,
            onOpenHome = { currentScreen = LabLinkScreen.Home },
            onOpenConnection = {
                refreshBluetoothState()
                currentScreen = LabLinkScreen.Connection
            },
            onOpenTerminal = { currentScreen = LabLinkScreen.Terminal },
            onOpenControls = { openMainMenuRespectingBoard() },
        )

        LabLinkScreen.MainMenu -> MainMenuScreen(
            isBluetoothConnected = isBluetoothConnectedForUi,
            selectedBoard = selectedBoard,
            onNewCustomControl = { createNewCustomControl() },
            onMyControls = { currentScreen = LabLinkScreen.MyControls },
            onJoystick = { currentScreen = LabLinkScreen.Joystick },
            onCommunity = { openCommunity() },
            onChangeBoard = { currentScreen = LabLinkScreen.BoardSelection },
            onOpenHome = { currentScreen = LabLinkScreen.Home },
            onOpenConnection = {
                refreshBluetoothState()
                currentScreen = LabLinkScreen.Connection
            },
            onOpenControls = { openMainMenuRespectingBoard() },
            onOpenTerminal = { currentScreen = LabLinkScreen.Terminal },
        )

        LabLinkScreen.MyControls -> MyControlsScreen(
            controls = customControls,
            isBluetoothConnected = isBluetoothConnectedForUi,
            onOpenControl = { control ->
                activeCustomControl = control
                customControlNameError = null
                isEditingCustomControl = false
                currentScreen = LabLinkScreen.CustomControlCanvas
            },
            onBack = { currentScreen = LabLinkScreen.MainMenu },
            onCreateControl = { createNewCustomControl() },
            onOpenHome = { currentScreen = LabLinkScreen.Home },
            onOpenConnection = {
                refreshBluetoothState()
                currentScreen = LabLinkScreen.Connection
            },
            onOpenControls = { openMainMenuRespectingBoard() },
            onOpenTerminal = { currentScreen = LabLinkScreen.Terminal },
        )

        LabLinkScreen.CustomControlCanvas -> activeCustomControl?.let { control ->
            CustomControlCanvasScreen(
                control = control,
                isEditing = isEditingCustomControl,
                isBluetoothConnected = isBluetoothConnectedForUi,
                validationMessage = customControlNameError,
                lastReceivedMessage = bluetoothState.lastReceivedMessage,
                onNameChange = { name ->
                    customControlNameError = null
                    activeCustomControl = activeCustomControl?.copy(name = name)
                },
                onAddWidget = { type ->
                    selectedModuleType = type
                    currentScreen = LabLinkScreen.WidgetSettings
                },
                onSaveControl = { saveActiveCustomControl() },
                onEditControl = { isEditingCustomControl = true },
                onMoveWidget = { widget, deltaX, deltaY ->
                    val currentControl = activeCustomControl
                    val widgets = currentControl?.widgets.orEmpty()
                    val latestWidget = widgets.firstOrNull { it.id == widget.id }

                    if (latestWidget != null) {
                        val targetX = (latestWidget.gridX + deltaX).coerceIn(
                            0,
                            3 - latestWidget.widthUnits.coerceIn(1, 3),
                        )
                        val targetY = (latestWidget.gridY + deltaY).coerceAtLeast(0)
                        val position = firstAvailableWidgetPosition(
                            moving = latestWidget,
                            widgets = widgets,
                            preferredX = targetX,
                            preferredY = targetY,
                        )

                        if (position != null) {
                            activeCustomControl = currentControl?.copy(
                                widgets = widgets.map { existing ->
                                    if (existing.id == latestWidget.id) {
                                        existing.copy(gridX = position.first, gridY = position.second)
                                    } else {
                                        existing
                                    }
                                },
                            )
                        }
                    }
                },
                onToggleDigitalControl = { controlWidget, turnOn ->
                    sendCommand(
                        ControlCommandMapper.digitalToggleCommand(
                            control = controlWidget,
                            turnOn = turnOn,
                        ),
                    )
                },
                onSendPwmControl = { controlWidget, value ->
                    sendCommand(
                        ControlCommandMapper.pwmCommand(
                            control = controlWidget,
                            value = value,
                        ),
                    )
                },
                onSendServoControl = { controlWidget, value ->
                    sendCommand(
                        ControlCommandMapper.servoCommand(
                            control = controlWidget,
                            angle = value,
                        ),
                    )
                },
                onSendPulseControl = { controlWidget ->
                    sendCommand(
                        ControlCommandMapper.pulseCommand(
                            control = controlWidget,
                            durationMs = controlWidget.durationMs ?: 500,
                        ),
                    )
                },
                onReadAnalogControl = { controlWidget ->
                    sendCommand(ControlCommandMapper.analogReadCommand(controlWidget))
                },
                onOpenHome = { currentScreen = LabLinkScreen.Home },
                onOpenConnection = {
                    refreshBluetoothState()
                    currentScreen = LabLinkScreen.Connection
                },
                onOpenControls = { openMainMenuRespectingBoard() },
                onOpenTerminal = { currentScreen = LabLinkScreen.Terminal },
            )
        } ?: MainMenuScreen(
            isBluetoothConnected = isBluetoothConnectedForUi,
            selectedBoard = selectedBoard,
            onNewCustomControl = { createNewCustomControl() },
            onMyControls = { currentScreen = LabLinkScreen.MyControls },
            onJoystick = { currentScreen = LabLinkScreen.Joystick },
            onCommunity = { openCommunity() },
            onChangeBoard = { currentScreen = LabLinkScreen.BoardSelection },
            onOpenHome = { currentScreen = LabLinkScreen.Home },
            onOpenConnection = {
                refreshBluetoothState()
                currentScreen = LabLinkScreen.Connection
            },
            onOpenControls = { openMainMenuRespectingBoard() },
            onOpenTerminal = { currentScreen = LabLinkScreen.Terminal },
        )

        LabLinkScreen.WidgetSettings -> WidgetSettingsScreen(
            board = selectedBoard ?: BoardProfiles.defaultBoard,
            widgetType = selectedModuleType ?: ControlType.DigitalToggle,
            existingWidgets = activeCustomControl?.widgets.orEmpty(),
            isBluetoothConnected = isBluetoothConnectedForUi,
            onBack = { currentScreen = LabLinkScreen.CustomControlCanvas },
            onSaveWidget = { widget ->
                activeCustomControl = activeCustomControl?.copy(
                    widgets = activeCustomControl?.widgets.orEmpty() + widget,
                )
                currentScreen = LabLinkScreen.CustomControlCanvas
            },
            onOpenHome = { currentScreen = LabLinkScreen.Home },
            onOpenConnection = {
                refreshBluetoothState()
                currentScreen = LabLinkScreen.Connection
            },
            onOpenControls = { openMainMenuRespectingBoard() },
            onOpenTerminal = { currentScreen = LabLinkScreen.Terminal },
        )

        LabLinkScreen.Joystick -> JoystickScreen(
            isBluetoothConnected = isBluetoothConnectedForUi,
            onBack = { currentScreen = LabLinkScreen.MainMenu },
            onSendCommand = { command -> sendCommand(command) },
            onOpenHome = { currentScreen = LabLinkScreen.Home },
            onOpenConnection = {
                refreshBluetoothState()
                currentScreen = LabLinkScreen.Connection
            },
            onOpenControls = { openMainMenuRespectingBoard() },
            onOpenTerminal = { currentScreen = LabLinkScreen.Terminal },
        )
    }
}


































