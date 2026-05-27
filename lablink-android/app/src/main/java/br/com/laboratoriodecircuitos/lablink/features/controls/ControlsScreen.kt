package br.com.laboratoriodecircuitos.lablink.features.controls

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothUiState
import br.com.laboratoriodecircuitos.lablink.ui.components.LabLinkDrawer
import br.com.laboratoriodecircuitos.lablink.ui.components.LabLinkTopAppBar
import br.com.laboratoriodecircuitos.lablink.ui.theme.LabLinkTheme

private val BgDeep = Color(0xFF000000)
private val CardDark = Color(0xFF151515)
private val AccentYellow = Color(0xFFFFE382)
private val AccentGreen = Color(0xFFC4FA8C)
private val AccentPurple = Color(0xFFE5BEFF)
private val TextDim = Color(0xFF8A8A8A)
private val WhiteSoft = Color(0xFFFFFFFF)
private val BorderSoft = Color.White.copy(alpha = 0.06f)

@Composable
fun ControlsScreen(
    bluetoothState: BluetoothUiState,
    onSendPing: () -> Unit,
    onTurnLedOn: () -> Unit,
    onTurnLedOff: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenConnection: () -> Unit,
    onOpenTerminal: () -> Unit,
    onOpenControls: () -> Unit,
) {
    val scrollState = rememberScrollState()
    var drawerOpen by remember { mutableStateOf(false) }

    val isConnected = bluetoothState.status == BluetoothConnectionStatus.Connected

    var isOutputOn by remember {
        mutableStateOf(bluetoothState.lastReceivedMessage == "OK:LED_ON")
    }

    LaunchedEffect(bluetoothState.lastReceivedMessage) {
        when (bluetoothState.lastReceivedMessage) {
            "OK:LED_ON" -> isOutputOn = true
            "OK:LED_OFF" -> isOutputOn = false
        }
    }

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
                    .padding(top = 112.dp, bottom = 40.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    HeaderSection(isConnected = isConnected)

                    DigitalOutputControlCard(
                        isOn = isOutputOn,
                        enabled = isConnected,
                        pinLabel = "Pino 13",
                        onToggle = {
                            val newState = !isOutputOn
                            isOutputOn = newState

                            if (newState) {
                                onTurnLedOn()
                            } else {
                                onTurnLedOff()
                            }
                        },
                    )

                    if (!isConnected) {
                        NotConnectedCard(
                            onOpenConnection = onOpenConnection,
                        )
                    }

                    FutureControlsCard()
                }
            }

            LabLinkTopAppBar(
                title = "Controles",
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
private fun HeaderSection(isConnected: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp),
    ) {
        Text(
            text = "Controle seu projeto",
            color = WhiteSoft,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.4).sp,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isConnected) {
                "Use o controle abaixo para acionar a saída configurada no Arduino."
            } else {
                "Conecte um dispositivo Bluetooth para controlar a saída do Arduino."
            },
            color = WhiteSoft.copy(alpha = 0.78f),
            fontSize = 16.sp,
            lineHeight = 24.sp,
        )
    }
}

@Composable
private fun DigitalOutputControlCard(
    isOn: Boolean,
    enabled: Boolean,
    pinLabel: String,
    onToggle: () -> Unit,
) {
    val activeColor = AccentYellow
    val inactiveIconColor = TextDim
    val statusText = if (isOn) "Ligado" else "Desligado"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.52f)
            .background(CardDark, RoundedCornerShape(30.dp))
            .border(
                width = 1.dp,
                color = if (isOn) activeColor.copy(alpha = 0.55f) else BorderSoft,
                shape = RoundedCornerShape(30.dp),
            )
            .clickable(enabled = enabled) { onToggle() }
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(
                    color = if (isOn) activeColor.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.05f),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = null,
                tint = if (isOn) activeColor else inactiveIconColor,
                modifier = Modifier.size(52.dp),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = statusText,
            color = if (isOn) activeColor else WhiteSoft,
            fontSize = 34.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.6).sp,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = pinLabel,
            color = TextDim,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            fontFamily = FontFamily.Monospace,
        )

        Spacer(modifier = Modifier.height(28.dp))

        LargeToggle(
            isOn = isOn,
            enabled = enabled,
        )
    }
}

@Composable
private fun LargeToggle(
    isOn: Boolean,
    enabled: Boolean,
) {
    Box(
        modifier = Modifier
            .size(width = 116.dp, height = 58.dp)
            .background(
                color = if (isOn) AccentYellow else Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(999.dp),
            )
            .border(
                width = 1.dp,
                color = if (isOn) AccentYellow.copy(alpha = 0.65f) else BorderSoft,
                shape = RoundedCornerShape(999.dp),
            )
            .alpha(if (enabled) 1f else 0.70f),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset(x = if (isOn) 58.dp else 6.dp)
                .size(46.dp)
                .background(
                    color = if (isOn) Color.Black else WhiteSoft.copy(alpha = 0.88f),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.PowerSettingsNew,
                contentDescription = null,
                tint = if (isOn) AccentYellow else CardDark,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun NotConnectedCard(
    onOpenConnection: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(22.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(22.dp))
            .clickable { onOpenConnection() }
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(AccentPurple.copy(alpha = 0.16f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Tune,
                contentDescription = null,
                tint = AccentPurple,
                modifier = Modifier.size(22.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = "Conectar dispositivo",
                color = WhiteSoft,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Abra a tela Bluetooth para conectar o HC-05 ou HC-06.",
                color = TextDim,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
        }
    }
}

@Composable
private fun FutureControlsCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(0.52f)
            .background(CardDark, RoundedCornerShape(22.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(22.dp))
            .padding(18.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                tint = TextDim,
                modifier = Modifier.size(18.dp),
            )

            Text(
                text = "Novos controles em breve",
                color = WhiteSoft,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "PWM, servo, motor, buzzer e sensores poderão aparecer aqui nas próximas etapas.",
            color = TextDim,
            fontSize = 13.sp,
            lineHeight = 18.sp,
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
                lastReceivedMessage = "OK:LED_ON",
            ),
            onSendPing = {},
            onTurnLedOn = {},
            onTurnLedOff = {},
            onOpenHome = {},
            onOpenConnection = {},
            onOpenTerminal = {},
            onOpenControls = {},
        )
    }
}
