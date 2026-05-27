package br.com.laboratoriodecircuitos.lablink.features.connection

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.BluetoothConnected
import androidx.compose.material.icons.rounded.BluetoothSearching
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.SettingsInputComponent
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothConnectionStatus
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothDeviceInfo
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothUiState
import br.com.laboratoriodecircuitos.lablink.ui.theme.LabLinkTheme

private val BgDeep = Color(0xFF000000)
private val CardDark = Color(0xFF151515)
private val AccentYellow = Color(0xFFFFE382)
private val AccentPurple = Color(0xFFE5BEFF)
private val AccentGreen = Color(0xFFC4FA8C)
private val AccentOrange = Color(0xFFFFB87A)
private val TextDim = Color(0xFF8A8A8A)
private val WhiteSoft = Color(0xFFFFFFFF)
private val BorderSoft = Color.White.copy(alpha = 0.05f)
private val BorderMedium = Color.White.copy(alpha = 0.10f)

@Composable
fun ConnectionScreen(
    bluetoothState: BluetoothUiState,
    developmentNotes: List<String>,
    onRequestPermissions: () -> Unit,
    onRefresh: () -> Unit,
    onLoadPairedDevices: () -> Unit,
    onSelectDevice: (BluetoothDeviceInfo) -> Unit,
    onConnectSelectedDevice: () -> Unit,
    onOpenControls: () -> Unit,
    onBack: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val isConnected = bluetoothState.status == BluetoothConnectionStatus.Connected
    val selectedDevice = bluetoothState.selectedDevice

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BgDeep,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDeep),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(top = 96.dp, bottom = 96.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    IntroSection()

                    StatusCard(bluetoothState = bluetoothState)

                    SelectedDeviceCard(selectedDevice = selectedDevice)

                    PairedDevicesSection(
                        devices = bluetoothState.pairedDevices,
                        selectedDevice = selectedDevice,
                        onSelectDevice = onSelectDevice,
                    )

                    ActionsGrid(
                        bluetoothState = bluetoothState,
                        onRequestPermissions = onRequestPermissions,
                        onLoadPairedDevices = onLoadPairedDevices,
                        onConnectSelectedDevice = onConnectSelectedDevice,
                        onOpenControls = onOpenControls,
                        onRefresh = onRefresh,
                    )

                    TechnicalNotesCard(developmentNotes = developmentNotes)
                }
            }

            TopAppBar(isConnected = isConnected)

            BottomNavBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                onHome = onBack,
                onBluetooth = onRefresh,
                onMore = {},
            )
        }
    }
}

@Composable
private fun TopAppBar(isConnected: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .background(BgDeep.copy(alpha = 0.94f))
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Bluetooth,
                    contentDescription = null,
                    tint = WhiteSoft,
                    modifier = Modifier.size(24.dp),
                )

                Text(
                    text = "Bluetooth",
                    color = WhiteSoft,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.2).sp,
                )
            }

            Row(
                modifier = Modifier
                    .background(CardDark, RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = if (isConnected) Icons.Rounded.BluetoothConnected else Icons.Rounded.BluetoothSearching,
                    contentDescription = null,
                    tint = if (isConnected) AccentGreen else TextDim,
                    modifier = Modifier.size(14.dp),
                )

                Text(
                    text = if (isConnected) "Conectado" else "Aguardando",
                    color = WhiteSoft,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun IntroSection() {
    Text(
        text = "Conecte o app ao módulo HC-05 ou HC-06 pareado no Android.",
        color = TextDim,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal,
    )
}

@Composable
private fun StatusCard(bluetoothState: BluetoothUiState) {
    val isConnected = bluetoothState.status == BluetoothConnectionStatus.Connected

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.SettingsInputComponent,
                    contentDescription = null,
                    tint = if (isConnected) AccentGreen else AccentPurple,
                    modifier = Modifier.size(16.dp),
                )

                Text(
                    text = bluetoothState.status.toDisplayText(),
                    color = WhiteSoft,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = bluetoothState.message,
                color = TextDim,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        StatusPill(isConnected = isConnected)
    }
}

@Composable
private fun StatusPill(isConnected: Boolean) {
    Row(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
            .border(1.dp, BorderMedium, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    color = if (isConnected) AccentGreen else TextDim,
                    shape = CircleShape,
                ),
        )

        Text(
            text = if (isConnected) "Serial pronta" else "Sem conexão",
            color = TextDim,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Composable
private fun SelectedDeviceCard(selectedDevice: BluetoothDeviceInfo?) {
    Column {
        SectionHeader(
            icon = Icons.Rounded.Devices,
            title = "Dispositivo selecionado",
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardDark, RoundedCornerShape(20.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SettingsInputComponent,
                        contentDescription = null,
                        tint = if (selectedDevice != null) AccentGreen else TextDim,
                        modifier = Modifier.size(16.dp),
                    )

                    Text(
                        text = selectedDevice?.name ?: "Nenhum dispositivo",
                        color = WhiteSoft,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = selectedDevice?.address ?: "Selecione um módulo pareado abaixo",
                    color = TextDim,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontFamily = if (selectedDevice != null) FontFamily.Monospace else FontFamily.Default,
                )
            }

            if (selectedDevice != null) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(AccentGreen.copy(alpha = 0.16f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = AccentGreen,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun PairedDevicesSection(
    devices: List<BluetoothDeviceInfo>,
    selectedDevice: BluetoothDeviceInfo?,
    onSelectDevice: (BluetoothDeviceInfo) -> Unit,
) {
    Column {
        SectionHeader(
            icon = Icons.Rounded.BluetoothSearching,
            title = "Dispositivos pareados",
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (devices.isEmpty()) {
            EmptyDevicesCard()
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                devices.forEach { device ->
                    DeviceListItem(
                        device = device,
                        selected = selectedDevice?.address == device.address,
                        onClick = { onSelectDevice(device) },
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyDevicesCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(20.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(20.dp))
            .padding(16.dp),
    ) {
        Text(
            text = "Nenhum dispositivo listado ainda.",
            color = WhiteSoft,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Toque em Buscar dispositivos pareados para carregar módulos já pareados nas configurações do Android.",
            color = TextDim,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        )
    }
}

@Composable
private fun DeviceListItem(
    device: BluetoothDeviceInfo,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (selected) AccentYellow else CardDark,
                shape = RoundedCornerShape(24.dp),
            )
            .then(
                if (selected) {
                    Modifier
                } else {
                    Modifier.border(1.dp, BorderSoft, RoundedCornerShape(24.dp))
                }
            )
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = device.name,
                color = if (selected) Color.Black else WhiteSoft,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = device.address,
                color = if (selected) Color.Black.copy(alpha = 0.60f) else TextDim,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily.Monospace,
            )
        }

        if (selected) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.Black.copy(alpha = 0.10f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun ActionsGrid(
    bluetoothState: BluetoothUiState,
    onRequestPermissions: () -> Unit,
    onLoadPairedDevices: () -> Unit,
    onConnectSelectedDevice: () -> Unit,
    onOpenControls: () -> Unit,
    onRefresh: () -> Unit,
) {
    Column {
        SectionHeader(
            icon = Icons.Rounded.Tune,
            title = "Ações",
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ActionCard(
                title = "Permissões Bluetooth",
                subtitle = "Solicitar acesso quando necessário",
                icon = Icons.Rounded.Security,
                backgroundColor = CardDark,
                contentColor = WhiteSoft,
                iconBackground = Color.White.copy(alpha = 0.05f),
                enabled = bluetoothState.status == BluetoothConnectionStatus.PermissionRequired,
                onClick = onRequestPermissions,
            )

            ActionCard(
                title = "Buscar pareados",
                subtitle = "Carregar módulos HC-05 ou HC-06",
                icon = Icons.Rounded.BluetoothSearching,
                backgroundColor = AccentPurple,
                contentColor = Color.Black,
                iconBackground = Color.Black.copy(alpha = 0.10f),
                enabled = bluetoothState.status == BluetoothConnectionStatus.Ready,
                onClick = onLoadPairedDevices,
            )

            ActionCard(
                title = "Conectar",
                subtitle = "Abrir comunicação Bluetooth serial",
                icon = Icons.Rounded.Link,
                backgroundColor = AccentYellow,
                contentColor = Color.Black,
                iconBackground = Color.Black.copy(alpha = 0.10f),
                enabled = bluetoothState.selectedDevice != null &&
                    bluetoothState.status != BluetoothConnectionStatus.Connecting &&
                    bluetoothState.status != BluetoothConnectionStatus.Connected,
                onClick = onConnectSelectedDevice,
            )

            ActionCard(
                title = "Ir para Controles",
                subtitle = "Enviar PING, LED ON e LED OFF",
                icon = Icons.Rounded.Tune,
                backgroundColor = AccentGreen,
                contentColor = Color.Black,
                iconBackground = Color.Black.copy(alpha = 0.10f),
                enabled = bluetoothState.status == BluetoothConnectionStatus.Connected,
                onClick = onOpenControls,
            )

            ActionCard(
                title = "Atualizar status",
                subtitle = "Reavaliar Bluetooth e permissões",
                icon = Icons.Rounded.Refresh,
                backgroundColor = CardDark,
                contentColor = WhiteSoft,
                iconBackground = Color.White.copy(alpha = 0.05f),
                enabled = true,
                onClick = onRefresh,
            )
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color,
    iconBackground: Color,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.45f)
            .background(backgroundColor, RoundedCornerShape(28.dp))
            .then(
                if (backgroundColor == CardDark) {
                    Modifier.border(1.dp, BorderSoft, RoundedCornerShape(28.dp))
                } else {
                    Modifier
                }
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                color = contentColor,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                color = contentColor.copy(alpha = if (contentColor == Color.Black) 0.60f else 0.62f),
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconBackground, CircleShape),
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
private fun TechnicalNotesCard(developmentNotes: List<String>) {
    Column(
        modifier = Modifier.alpha(0.50f),
    ) {
        SectionHeader(
            icon = Icons.Rounded.Tune,
            title = "Preparação técnica",
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardDark, RoundedCornerShape(20.dp))
                .border(1.dp, BorderSoft, RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            developmentNotes.forEach { note ->
                Text(
                    text = "• $note",
                    color = TextDim,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: ImageVector,
    title: String,
) {
    Row(
        modifier = Modifier.padding(horizontal = 1.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextDim,
            modifier = Modifier.size(14.dp),
        )

        Text(
            text = title,
            color = TextDim,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun BottomNavBar(
    modifier: Modifier = Modifier,
    onHome: () -> Unit,
    onBluetooth: () -> Unit,
    onMore: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BgDeep.copy(alpha = 0.95f))
            .navigationBarsPadding(),
    ) {
        HorizontalDivider(
            color = Color.White.copy(alpha = 0.05f),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BottomNavItem(
                icon = Icons.Rounded.Home,
                label = "Início",
                selected = false,
                onClick = onHome,
            )

            BottomNavItem(
                icon = Icons.Rounded.Bluetooth,
                label = "Bluetooth",
                selected = true,
                onClick = onBluetooth,
            )

            BottomNavItem(
                icon = Icons.Rounded.MoreHoriz,
                label = "Mais",
                selected = false,
                onClick = onMore,
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .widthIn(min = 72.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(32.dp)
                .background(
                    color = if (selected) Color.White.copy(alpha = 0.10f) else Color.Transparent,
                    shape = RoundedCornerShape(999.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) WhiteSoft else TextDim,
                modifier = Modifier.size(24.dp),
            )
        }

        Text(
            text = label,
            color = if (selected) WhiteSoft else TextDim,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

private fun BluetoothConnectionStatus.toDisplayText(): String {
    return when (this) {
        BluetoothConnectionStatus.Disconnected -> "Desconectado"
        BluetoothConnectionStatus.CheckingPermissions -> "Verificando permissões"
        BluetoothConnectionStatus.PermissionRequired -> "Permissão necessária"
        BluetoothConnectionStatus.BluetoothUnavailable -> "Bluetooth indisponível"
        BluetoothConnectionStatus.BluetoothDisabled -> "Bluetooth desligado"
        BluetoothConnectionStatus.Ready -> "Pronto para conectar"
        BluetoothConnectionStatus.Connecting -> "Conectando"
        BluetoothConnectionStatus.Connected -> "Conectado"
        BluetoothConnectionStatus.Error -> "Erro"
    }
}

@Preview(showBackground = true)
@Composable
private fun ConnectionScreenPreview() {
    LabLinkTheme {
        ConnectionScreen(
            bluetoothState = BluetoothUiState(
                status = BluetoothConnectionStatus.Connected,
                message = "Conectado a HC-06 por RFCOMM insecure. Comunicação serial pronta.",
                selectedDevice = BluetoothDeviceInfo(
                    name = "HC-06",
                    address = "20:16:05:11:38:71",
                ),
                pairedDevices = listOf(
                    BluetoothDeviceInfo(
                        name = "HC-06",
                        address = "20:16:05:11:38:71",
                    ),
                    BluetoothDeviceInfo(
                        name = "Fone Bluetooth",
                        address = "AA:BB:CC:DD:EE:FF",
                    ),
                ),
            ),
            developmentNotes = listOf(
                "Permissões Bluetooth adicionadas ao AndroidManifest.xml.",
                "Conexão RFCOMM/SPP validada com HC-06.",
                "Comandos movidos para a tela Controles.",
            ),
            onRequestPermissions = {},
            onRefresh = {},
            onLoadPairedDevices = {},
            onSelectDevice = {},
            onConnectSelectedDevice = {},
            onOpenControls = {},
            onBack = {},
        )
    }
}

