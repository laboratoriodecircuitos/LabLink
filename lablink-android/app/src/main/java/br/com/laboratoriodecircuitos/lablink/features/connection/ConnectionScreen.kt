package br.com.laboratoriodecircuitos.lablink.features.connection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.BluetoothSearching
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothConnectionStatus
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothDeviceInfo
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothDiscoveredDevice
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothDiscoveryStatus
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothPairingGuide
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothPairingStatus
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothUiState
import br.com.laboratoriodecircuitos.lablink.ui.components.LabLinkDrawer
import br.com.laboratoriodecircuitos.lablink.ui.components.LabLinkTopAppBar
import br.com.laboratoriodecircuitos.lablink.ui.theme.LabLinkTheme

private val BgDeep = Color(0xFF000000)
private val CardDark = Color(0xFF151515)
private val AccentYellow = Color(0xFFFFE382)
private val AccentPurple = Color(0xFFE5BEFF)
private val AccentGreen = Color(0xFFC4FA8C)
private val TextDim = Color(0xFF8A8A8A)
private val WhiteSoft = Color(0xFFFFFFFF)
private val BorderSoft = Color.White.copy(alpha = 0.06f)

@Composable
fun ConnectionScreen(
    bluetoothState: BluetoothUiState,
    discoveryStatus: BluetoothDiscoveryStatus = BluetoothDiscoveryStatus.Idle,
    discoveredDevices: List<BluetoothDiscoveredDevice> = emptyList(),
    developmentNotes: List<String>,
    onRequestPermissions: () -> Unit,
    onRefresh: () -> Unit,
    onLoadPairedDevices: () -> Unit,
    onSelectDevice: (BluetoothDeviceInfo) -> Unit,
    onConnectSelectedDevice: () -> Unit,
    onDisconnectSelectedDevice: () -> Unit,
    onStartPairingGuide: () -> Unit = {},
    onPairDiscoveredDevice: (BluetoothDiscoveredDevice) -> Unit = {},
    onOpenHome: () -> Unit = {},
    onOpenConnection: () -> Unit = {},
    onOpenTerminal: () -> Unit = {},
    onOpenControls: () -> Unit,
    onBack: () -> Unit,
) {
    val scrollState = rememberScrollState()
    var drawerOpen by remember { mutableStateOf(false) }

    val isConnected = bluetoothState.status == BluetoothConnectionStatus.Connected
    val selectedDevice = bluetoothState.selectedDevice
    val hasDevices = bluetoothState.pairedDevices.isNotEmpty()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BgDeep,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .background(BgDeep),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(top = 112.dp, bottom = 40.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    ConnectionHeader(
                        isConnected = isConnected,
                        selectedDevice = selectedDevice,
                        hasDevices = hasDevices,
                    )

                    when {
                        bluetoothState.status == BluetoothConnectionStatus.PermissionRequired -> {
                            StepCard(
                                step = "Permissão necessária",
                                title = "Libere o acesso ao Bluetooth",
                                description = "O Android precisa permitir que o LabLink encontre e conecte ao seu módulo HC-05 ou HC-06.",
                                icon = Icons.Rounded.Bluetooth,
                                iconColor = AccentYellow,
                            )

                            PrimaryActionButton(
                                title = "Permitir Bluetooth",
                                subtitle = "Solicitar permissões do Android",
                                icon = Icons.Rounded.Check,
                                backgroundColor = AccentYellow,
                                onClick = onRequestPermissions,
                            )
                        }

                        bluetoothState.status == BluetoothConnectionStatus.BluetoothDisabled -> {
                            StepCard(
                                step = "Bluetooth desligado",
                                title = "Ative o Bluetooth do celular",
                                description = "Ligue o Bluetooth nas configurações do Android e depois atualize esta tela.",
                                icon = Icons.Rounded.Bluetooth,
                                iconColor = TextDim,
                            )

                            PrimaryActionButton(
                                title = "Atualizar status",
                                subtitle = "Verificar novamente",
                                icon = Icons.Rounded.Refresh,
                                backgroundColor = AccentPurple,
                                onClick = onRefresh,
                            )
                        }

                        isConnected && selectedDevice != null -> {
                            ConnectedContent(
                                device = selectedDevice,
                                onDisconnect = onDisconnectSelectedDevice,
                                onOpenControls = onOpenControls,
                            )
                        }

                        selectedDevice != null -> {
                            SelectedDeviceContent(
                                device = selectedDevice,
                                status = bluetoothState.status,
                                onConnect = onConnectSelectedDevice,
                                onSearchAgain = onLoadPairedDevices,
                            )
                        }

                        hasDevices -> {
                            DeviceSelectionContent(
                                devices = bluetoothState.pairedDevices,
                                onSelectDevice = onSelectDevice,
                                onSearchAgain = onLoadPairedDevices,
                                discoveryStatus = discoveryStatus,
                                discoveredDevices = discoveredDevices,
                                onStartPairingGuide = onStartPairingGuide,
                                onPairDiscoveredDevice = onPairDiscoveredDevice,
                            )
                        }

                        else -> {
                            StartSearchContent(
                                onSearch = onLoadPairedDevices,
                                onRefresh = onRefresh,
                                discoveryStatus = discoveryStatus,
                                discoveredDevices = discoveredDevices,
                                onStartPairingGuide = onStartPairingGuide,
                                onPairDiscoveredDevice = onPairDiscoveredDevice,
                            )
                        }
                    }

                    if (bluetoothState.status == BluetoothConnectionStatus.Error) {
                        SimpleMessageCard(
                            title = "Não foi possível conectar",
                            message = "Confira se o HC-05/HC-06 está ligado, próximo do celular e corretamente pareado.",
                        )
                    }
                }
            }

            LabLinkTopAppBar(
                title = "Bluetooth",
                isBluetoothConnected = isConnected,
                onOpenDrawer = { drawerOpen = true },
            )

            AnimatedVisibility(
                visible = drawerOpen,
                enter = fadeIn(animationSpec = tween(180)) + slideInHorizontally(
                    animationSpec = tween(260),
                    initialOffsetX = { -it },
                ),
                exit = fadeOut(animationSpec = tween(160)) + slideOutHorizontally(
                    animationSpec = tween(220),
                    targetOffsetX = { -it },
                ),
            ) {
                LabLinkDrawer(
                    onClose = { drawerOpen = false },
                    onOpenHome = {
                        drawerOpen = false
                        onOpenHome()
                    },
                    onOpenBluetooth = {
                        drawerOpen = false
                        onOpenConnection()
                    },
                    onOpenControls = {
                        drawerOpen = false
                        onOpenControls()
                    },
                    onOpenTerminal = {
                        drawerOpen = false
                        onOpenTerminal()
                    },
                )
            }
        }
    }
}

@Composable
private fun ConnectionHeader(
    isConnected: Boolean,
    selectedDevice: BluetoothDeviceInfo?,
    hasDevices: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp),
    ) {
        Text(
            text = "Conectar dispositivo",
            color = WhiteSoft,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.4).sp,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = when {
                isConnected -> "Tudo pronto. Agora você pode controlar seu projeto Arduino."
                selectedDevice != null -> "Dispositivo escolhido. Agora é só conectar."
                hasDevices -> "Escolha o módulo Bluetooth do seu projeto."
                else -> "Escolha como deseja preparar seu módulo Bluetooth."
            },
            color = WhiteSoft.copy(alpha = 0.78f),
            fontSize = 16.sp,
            lineHeight = 24.sp,
        )
    }
}

@Composable
private fun StartSearchContent(
    onSearch: () -> Unit,
    onRefresh: () -> Unit,
    discoveryStatus: BluetoothDiscoveryStatus,
    discoveredDevices: List<BluetoothDiscoveredDevice>,
    onStartPairingGuide: () -> Unit,
    onPairDiscoveredDevice: (BluetoothDiscoveredDevice) -> Unit,
) {
    val isPairingFlowOpen =
        discoveryStatus != BluetoothDiscoveryStatus.Idle || discoveredDevices.isNotEmpty()

    if (isPairingFlowOpen) {
        PairNewDeviceGuideCard(
            discoveryStatus = discoveryStatus,
            discoveredDevices = discoveredDevices,
            onStartPairingGuide = onStartPairingGuide,
            onPairDiscoveredDevice = onPairDiscoveredDevice,
        )
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            BluetoothChoiceButton(
                title = "Buscar módulos pareados",
                icon = Icons.Rounded.Bluetooth,
                backgroundColor = AccentPurple,
                contentColor = Color.Black,
                onClick = onSearch,
            )

            BluetoothChoiceButton(
                title = "Parear novo módulo",
                icon = Icons.Rounded.BluetoothSearching,
                backgroundColor = AccentYellow,
                contentColor = Color.Black,
                onClick = onStartPairingGuide,
            )
        }
    }

    SecondaryActionButton(
        title = "Atualizar status do Bluetooth",
        onClick = onRefresh,
    )
}

@Composable
private fun BluetoothChoiceButton(
    title: String,
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 76.dp)
            .background(backgroundColor, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(contentColor.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp),
            )
        }

        Text(
            text = title,
            color = contentColor,
            fontSize = 17.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
    }
}
@Composable
private fun DeviceSelectionContent(
    devices: List<BluetoothDeviceInfo>,
    onSelectDevice: (BluetoothDeviceInfo) -> Unit,
    onSearchAgain: () -> Unit,
    discoveryStatus: BluetoothDiscoveryStatus,
    discoveredDevices: List<BluetoothDiscoveredDevice>,
    onStartPairingGuide: () -> Unit,
    onPairDiscoveredDevice: (BluetoothDiscoveredDevice) -> Unit,
) {
    StepCard(
        step = "Módulos pareados",
        title = "Escolha o módulo já pareado",
        description = "Toque no HC-05/HC-06 que você quer conectar ao Arduino.",
        icon = Icons.Rounded.Devices,
        iconColor = AccentYellow,
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        devices.forEach { device ->
            DeviceItem(
                device = device,
                onClick = { onSelectDevice(device) },
            )
        }
    }

    SecondaryActionButton(
        title = "Buscar novamente",
        onClick = onSearchAgain,
    )
}

@Composable
private fun SelectedDeviceContent(
    device: BluetoothDeviceInfo,
    status: BluetoothConnectionStatus,
    onConnect: () -> Unit,
    onSearchAgain: () -> Unit,
) {
    StepCard(
        step = "Dispositivo selecionado",
        title = "${device.name} selecionado",
        description = "Agora abra a comunicação Bluetooth com o Arduino.",
        icon = Icons.Rounded.Link,
        iconColor = AccentYellow,
    )

    ResponsiveActionButtons(
        firstAction = {
            PrimaryActionButton(
                title = if (status == BluetoothConnectionStatus.Connecting) "Conectando..." else "Conectar",
                subtitle = device.name,
                icon = Icons.Rounded.Link,
                backgroundColor = AccentYellow,
                enabled = status != BluetoothConnectionStatus.Connecting,
                onClick = onConnect,
            )
        },
        secondAction = {
            SecondaryActionButton(
                title = "Escolher outro",
                onClick = onSearchAgain,
            )
        },
    )
}

@Composable
private fun ConnectedContent(
    device: BluetoothDeviceInfo,
    onDisconnect: () -> Unit,
    onOpenControls: () -> Unit,
) {
    StepCard(
        step = "Conectado",
        title = "${device.name} conectado",
        description = "Pronto para enviar comandos para o Arduino.",
        icon = Icons.Rounded.Check,
        iconColor = AccentGreen,
    )

    ResponsiveActionButtons(
        firstAction = {
            PrimaryActionButton(
                title = "Desconectar",
                subtitle = "Encerrar conexão",
                icon = Icons.Rounded.Bluetooth,
                backgroundColor = CardDark,
                contentColor = WhiteSoft,
                border = true,
                onClick = onDisconnect,
            )
        },
        secondAction = {
            PrimaryActionButton(
                title = "Ir para Controles",
                subtitle = "Controlar projeto",
                icon = Icons.Rounded.Tune,
                backgroundColor = AccentGreen,
                onClick = onOpenControls,
            )
        },
    )
}

@Composable
private fun ResponsiveActionButtons(
    firstAction: @Composable () -> Unit,
    secondAction: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (maxWidth >= 600.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    firstAction()
                }

                Box(modifier = Modifier.weight(1f)) {
                    secondAction()
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                firstAction()
                secondAction()
            }
        }
    }
}

@Composable
private fun PairNewDeviceGuideCard(
    discoveryStatus: BluetoothDiscoveryStatus,
    discoveredDevices: List<BluetoothDiscoveredDevice>,
    onStartPairingGuide: () -> Unit,
    onPairDiscoveredDevice: (BluetoothDiscoveredDevice) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(24.dp))
            .border(1.dp, AccentPurple.copy(alpha = 0.32f), RoundedCornerShape(24.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(AccentPurple.copy(alpha = 0.16f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.BluetoothSearching,
                    contentDescription = null,
                    tint = AccentPurple,
                    modifier = Modifier.size(24.dp),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = "Parear novo módulo",
                    color = WhiteSoft,
                    fontSize = 17.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Encontrar HC-05/HC-06 próximo e iniciar pareamento.",
                    color = TextDim,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BluetoothPairingGuide.preparationSteps.forEach { step ->
                Text(
                    text = "• $step",
                    color = WhiteSoft.copy(alpha = 0.78f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                )
            }
        }

        Text(
            text = when (discoveryStatus) {
                BluetoothDiscoveryStatus.Idle -> "Ligue o módulo, aguarde o LED piscar e toque para procurar."
                BluetoothDiscoveryStatus.PermissionRequired -> "Permita a busca Bluetooth para o LabLink encontrar módulos próximos."
                BluetoothDiscoveryStatus.BluetoothDisabled -> "Ative o Bluetooth do celular para procurar o HC-05/HC-06."
                BluetoothDiscoveryStatus.Scanning -> "Procurando módulos Bluetooth próximos..."
                BluetoothDiscoveryStatus.Finished -> if (discoveredDevices.isEmpty()) {
                    "Busca concluída. Nenhum dispositivo encontrado. Confira se o LED do HC-05/HC-06 está piscando."
                } else {
                    "Busca concluída. Toque no módulo encontrado para iniciar o pareamento."
                }
                BluetoothDiscoveryStatus.Error -> "Não foi possível concluir a busca. Confira o Bluetooth, as permissões e se o módulo está ligado."
            },
            color = TextDim,
            fontSize = 12.sp,
            lineHeight = 17.sp,
        )

        if (discoveredDevices.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                discoveredDevices.forEach { discoveredDevice ->
                    DiscoveredDeviceItem(
                        device = discoveredDevice,
                        onPair = {
                            onPairDiscoveredDevice(discoveredDevice)
                        },
                    )
                }
            }
        }

        PrimaryActionButton(
            title = if (discoveryStatus == BluetoothDiscoveryStatus.Scanning) {
                "Buscando..."
            } else {
                BluetoothPairingGuide.pairingButtonLabel
            },
            subtitle = if (discoveryStatus == BluetoothDiscoveryStatus.Scanning) {
                "Procurando dispositivos"
            } else {
                "Procurar módulos próximos"
            },
            icon = Icons.Rounded.BluetoothSearching,
            backgroundColor = AccentPurple,
            contentColor = Color.Black,
            enabled = discoveryStatus != BluetoothDiscoveryStatus.Scanning,
            onClick = onStartPairingGuide,
        )
    }
}

@Composable
private fun DiscoveredDeviceItem(
    device: BluetoothDiscoveredDevice,
    onPair: () -> Unit,
) {
    val isLikelyModule = device.isLikelyLabLinkModule
    val isPairing = device.pairingStatus == BluetoothPairingStatus.Pairing
    val isPaired = device.pairingStatus == BluetoothPairingStatus.Paired

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.055f), RoundedCornerShape(18.dp))
            .border(
                width = 1.dp,
                color = if (isLikelyModule) AccentGreen.copy(alpha = 0.45f) else BorderSoft,
                shape = RoundedCornerShape(18.dp),
            )
            .clickable(enabled = !isPairing && !isPaired) { onPair() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = if (isLikelyModule) AccentGreen.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.08f),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Devices,
                contentDescription = null,
                tint = if (isLikelyModule) AccentGreen else TextDim,
                modifier = Modifier.size(22.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = device.name,
                color = WhiteSoft,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = device.address,
                color = TextDim,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                fontFamily = FontFamily.Monospace,
            )
        }

        Text(
            text = when {
                isPaired -> "Pareado"
                isPairing -> "Pareando"
                isLikelyModule -> "Parear"
                else -> "Parear"
            },
            color = when {
                isPaired -> AccentGreen
                isPairing -> AccentYellow
                isLikelyModule -> AccentGreen
                else -> AccentPurple
            },
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
@Composable
private fun StepCard(
    step: String,
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(24.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(24.dp))
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(iconColor.copy(alpha = 0.16f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = step,
                color = TextDim,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                color = WhiteSoft,
                fontSize = 18.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                color = TextDim,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
        }
    }
}

@Composable
private fun DeviceItem(
    device: BluetoothDeviceInfo,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(18.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color.White.copy(alpha = 0.06f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Bluetooth,
                contentDescription = null,
                tint = AccentPurple,
                modifier = Modifier.size(20.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = device.name,
                color = WhiteSoft,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = device.address,
                color = TextDim,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}


@Composable
private fun PrimaryActionButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit,
    contentColor: Color = Color.Black,
    enabled: Boolean = true,
    border: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.50f)
            .background(backgroundColor, RoundedCornerShape(24.dp))
            .then(
                if (border) {
                    Modifier.border(1.dp, BorderSoft, RoundedCornerShape(24.dp))
                } else {
                    Modifier
                }
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                color = contentColor,
                fontSize = 20.sp,
                lineHeight = 25.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                color = contentColor.copy(alpha = if (contentColor == Color.Black) 0.68f else 0.72f),
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .background(contentColor.copy(alpha = 0.10f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun SecondaryActionButton(
    title: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(18.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            color = WhiteSoft.copy(alpha = 0.84f),
            fontSize = 14.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun SimpleMessageCard(
    title: String,
    message: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(18.dp))
            .border(1.dp, AccentYellow.copy(alpha = 0.28f), RoundedCornerShape(18.dp))
            .padding(16.dp),
    ) {
        Text(
            text = title,
            color = WhiteSoft,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = message,
            color = TextDim,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ConnectionScreenPreview() {
    LabLinkTheme {
        ConnectionScreen(
            bluetoothState = BluetoothUiState(
                status = BluetoothConnectionStatus.Connected,
                message = "Conectado a HC-06.",
                selectedDevice = BluetoothDeviceInfo(
                    name = "HC-06",
                    address = "20:16:05:11:38:71",
                ),
                pairedDevices = listOf(
                    BluetoothDeviceInfo(
                        name = "HC-06",
                        address = "20:16:05:11:38:71",
                    ),
                ),
            ),
            developmentNotes = emptyList(),
            onRequestPermissions = {},
            onRefresh = {},
            onLoadPairedDevices = {},
            onSelectDevice = {},
            onConnectSelectedDevice = {},
            onDisconnectSelectedDevice = {},
            onStartPairingGuide = {},
            onPairDiscoveredDevice = {},
            onOpenHome = {},
            onOpenConnection = {},
            onOpenTerminal = {},
            onOpenControls = {},
            onBack = {},
        )
    }
}
















