package br.com.laboratoriodecircuitos.lablink.features.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.BluetoothConnected
import androidx.compose.material.icons.rounded.CompareArrows
import androidx.compose.material.icons.rounded.Construction
import androidx.compose.material.icons.rounded.Gamepad
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.rounded.LinearScale
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Terminal
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
private val TextDim = Color(0xFF8A8A8A)
private val WhiteSoft = Color(0xFFFFFFFF)
private val BorderSoft = Color.White.copy(alpha = 0.05f)
private val BorderMedium = Color.White.copy(alpha = 0.10f)

@Composable
fun ControlsScreen(
    bluetoothState: BluetoothUiState,
    onSendPing: () -> Unit,
    onTurnLedOn: () -> Unit,
    onTurnLedOff: () -> Unit,
    onOpenConnection: () -> Unit,
    onBack: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val isConnected = bluetoothState.status == BluetoothConnectionStatus.Connected
    val deviceName = bluetoothState.selectedDevice?.name ?: "HC-06"
    val deviceAddress = bluetoothState.selectedDevice?.address ?: "20:16:05:11:38:71"
    val lastResponse = bluetoothState.lastReceivedMessage.ifBlank { "OK:LED_ON" }

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
                    .padding(bottom = 132.dp),
            ) {
                TopAppBar(isConnected = isConnected)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    DeviceSection(
                        deviceName = deviceName,
                        deviceAddress = deviceAddress,
                        isConnected = isConnected,
                    )

                    LastResponseSection(lastResponse = lastResponse)

                    LedControlSection(
                        onTurnLedOn = onTurnLedOn,
                        onTurnLedOff = onTurnLedOff,
                    )

                    PingButton(onSendPing = onSendPing)

                    UpcomingControlsSection()
                }
            }

            TopAppBar(
                isConnected = isConnected,
            )

            BottomNavBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                onHome = onBack,
                onBluetooth = onOpenConnection,
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
            .background(BgDeep.copy(alpha = 0.90f))
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
                    imageVector = Icons.Rounded.Tune,
                    contentDescription = null,
                    tint = WhiteSoft,
                    modifier = Modifier.size(24.dp),
                )

                Text(
                    text = "Controles",
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
                    imageVector = Icons.Rounded.BluetoothConnected,
                    contentDescription = null,
                    tint = AccentGreen,
                    modifier = Modifier.size(14.dp),
                )

                Text(
                    text = if (isConnected) "Conectado" else "Desconectado",
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
private fun DeviceSection(
    deviceName: String,
    deviceAddress: String,
    isConnected: Boolean,
) {
    Column {
        Text(
            text = "Envie comandos para o Arduino pelo Bluetooth",
            color = TextDim,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal,
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                        imageVector = Icons.Rounded.Memory,
                        contentDescription = null,
                        tint = AccentGreen,
                        modifier = Modifier.size(14.dp),
                    )

                    Text(
                        text = deviceName,
                        color = WhiteSoft,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = deviceAddress,
                    color = TextDim,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily.Monospace,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Row(
                modifier = Modifier
                    .width(176.dp)
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
                    modifier = Modifier.weight(1f),
                    text = if (isConnected) "Comunicação serial\npronta." else "Sem comunicação",
                    color = TextDim,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

@Composable
private fun LastResponseSection(lastResponse: String) {
    Column {
        SectionHeader(
            icon = Icons.Rounded.Terminal,
            title = "Última resposta",
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardDark, RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "> ${lastResponse}_",
                color = AccentGreen,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun LedControlSection(
    onTurnLedOn: () -> Unit,
    onTurnLedOff: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 1.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.GridView,
                    contentDescription = null,
                    tint = TextDim,
                    modifier = Modifier.size(14.dp),
                )

                Text(
                    text = "Controle de LED",
                    color = TextDim,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Text(
                text = "LED 13 do Arduino",
                color = TextDim,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Normal,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LedActionCard(
                modifier = Modifier.weight(1f),
                backgroundColor = AccentYellow,
                contentColor = Color.Black,
                icon = Icons.Rounded.Lightbulb,
                iconColor = Color.Black,
                title = "Ligar\nLED",
                switchOn = true,
                border = null,
                onClick = onTurnLedOn,
            )

            LedActionCard(
                modifier = Modifier.weight(1f),
                backgroundColor = CardDark,
                contentColor = WhiteSoft,
                icon = Icons.Outlined.Lightbulb,
                iconColor = TextDim,
                title = "Desligar\nLED",
                switchOn = false,
                border = BorderSoft,
                onClick = onTurnLedOff,
            )
        }
    }
}

@Composable
private fun LedActionCard(
    modifier: Modifier,
    backgroundColor: Color,
    contentColor: Color,
    icon: ImageVector,
    iconColor: Color,
    title: String,
    switchOn: Boolean,
    border: Color?,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(28.dp)

    Column(
        modifier = modifier
            .height(128.dp)
            .background(backgroundColor, shape)
            .then(
                if (border != null) {
                    Modifier.border(1.dp, border, shape)
                } else {
                    Modifier
                }
            )
            .clickable { onClick() }
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp),
            )

            MiniSwitch(
                switchOn = switchOn,
                thumbColor = if (switchOn) AccentYellow else TextDim,
            )
        }

        Text(
            text = title,
            color = contentColor,
            fontSize = 18.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun MiniSwitch(
    switchOn: Boolean,
    thumbColor: Color,
) {
    Box(
        modifier = Modifier
            .background(
                color = if (switchOn) Color.Black.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(999.dp),
            )
            .padding(6.dp),
    ) {
        Box(
            modifier = Modifier
                .width(34.dp)
                .height(18.dp)
                .background(Color.Black, RoundedCornerShape(999.dp)),
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .align(if (switchOn) Alignment.CenterEnd else Alignment.CenterStart)
                    .background(thumbColor, CircleShape),
            )
        }
    }
}

@Composable
private fun PingButton(onSendPing: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AccentPurple, RoundedCornerShape(28.dp))
            .clickable { onSendPing() }
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "Enviar PING",
                color = Color.Black,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Espera OK:PONG do Arduino",
                color = Color.Black.copy(alpha = 0.60f),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Normal,
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.10f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.CompareArrows,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun UpcomingControlsSection() {
    Column {
        SectionHeader(
            icon = Icons.Rounded.Construction,
            title = "Próximos controles",
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            UpcomingControlCard(
                icon = Icons.Rounded.LinearScale,
                label = "PWM por slider",
            )

            UpcomingControlCard(
                icon = Icons.Rounded.Autorenew,
                label = "Servo motor",
            )

            UpcomingControlCard(
                icon = Icons.Rounded.Gamepad,
                label = "Motores",
            )
        }
    }
}

@Composable
private fun UpcomingControlCard(
    icon: ImageVector,
    label: String,
) {
    Column(
        modifier = Modifier
            .width(128.dp)
            .background(CardDark, RoundedCornerShape(16.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(16.dp))
            .alpha(0.50f)
            .padding(16.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextDim,
            modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = label,
            color = TextDim,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium,
        )
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
                selected = true,
                onClick = onHome,
            )

            BottomNavItem(
                icon = Icons.Rounded.Bluetooth,
                label = "Bluetooth",
                selected = false,
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

@Preview(showBackground = true)
@Composable
private fun ControlsScreenPreview() {
    LabLinkTheme {
        ControlsScreen(
            bluetoothState = BluetoothUiState(
                status = BluetoothConnectionStatus.Connected,
                selectedDevice = BluetoothDeviceInfo(
                    name = "HC-06",
                    address = "20:16:05:11:38:71",
                ),
                message = "Comunicação serial pronta.",
                lastReceivedMessage = "OK:LED_ON",
            ),
            onSendPing = {},
            onTurnLedOn = {},
            onTurnLedOff = {},
            onOpenConnection = {},
            onBack = {},
        )
    }
}






