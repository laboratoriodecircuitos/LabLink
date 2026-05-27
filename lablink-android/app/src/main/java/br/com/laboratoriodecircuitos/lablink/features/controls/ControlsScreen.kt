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
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
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
import br.com.laboratoriodecircuitos.lablink.core.controls.ControlType
import br.com.laboratoriodecircuitos.lablink.core.controls.DefaultControls
import br.com.laboratoriodecircuitos.lablink.core.controls.LabLinkControl
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
    controls: List<LabLinkControl> = listOf(DefaultControls.pin13DigitalOutput),
    controlsRefreshKey: Int = 0,
    onSendPing: () -> Unit,
    onToggleDigitalControl: (LabLinkControl, Boolean) -> Unit = { _, _ -> },
    onSendPwmControl: (LabLinkControl, Int) -> Unit = { _, _ -> },
    onSendPulseControl: (LabLinkControl) -> Unit = {},
    onTurnLedOn: () -> Unit,
    onTurnLedOff: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenConnection: () -> Unit,
    onOpenTerminal: () -> Unit,
    onOpenControls: () -> Unit,
    onCreateControl: () -> Unit = {},
    onClearControls: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    var drawerOpen by remember { mutableStateOf(false) }

    val displayedControls = remember {
        mutableStateListOf<LabLinkControl>().apply {
            addAll(controls)
        }
    }

    val isConnected = bluetoothState.status == BluetoothConnectionStatus.Connected
    val hasCustomControls = displayedControls.any { it.id != DefaultControls.pin13DigitalOutput.id } ||
        displayedControls.size != 1
    val digitalStates = remember { mutableStateMapOf<String, Boolean>() }
    val pwmStates = remember { mutableStateMapOf<String, Int>() }
    val pwmLastSentAt = remember { mutableStateMapOf<String, Long>() }
    val pwmLastSentValues = remember { mutableStateMapOf<String, Int>() }

    LaunchedEffect(controlsRefreshKey, controls) {
        displayedControls.clear()
        displayedControls.addAll(controls)

        digitalStates.clear()
        pwmStates.clear()
        pwmLastSentAt.clear()
        pwmLastSentValues.clear()

        displayedControls.forEach { control ->
            when (control.type) {
                ControlType.DigitalToggle -> digitalStates[control.id] = control.isOn
                ControlType.PwmSlider -> pwmStates[control.id] = control.currentValue ?: 0
                else -> Unit
            }
        }
    }

    LaunchedEffect(bluetoothState.lastReceivedMessage) {
        when (bluetoothState.lastReceivedMessage) {
            "OK:LED_ON" -> displayedControls
                .filter { it.type == ControlType.DigitalToggle && it.pin == "13" }
                .forEach { digitalStates[it.id] = true }

            "OK:LED_OFF" -> displayedControls
                .filter { it.type == ControlType.DigitalToggle && it.pin == "13" }
                .forEach { digitalStates[it.id] = false }
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
                    HeaderSection(
                        isConnected = isConnected,
                        hasCustomControls = hasCustomControls,
                    )

                    if (displayedControls.isEmpty()) {
                        EmptyControlsCard(onCreateControl = onCreateControl)
                    } else {
                        displayedControls.forEach { control ->
                            when (control.type) {
                                ControlType.DigitalToggle -> {
                                    DigitalOutputControlCard(
                                        control = control,
                                        isOn = digitalStates[control.id] == true,
                                        enabled = isConnected,
                                        onToggle = {
                                            val newState = !(digitalStates[control.id] ?: false)
                                            digitalStates[control.id] = newState

                                            onToggleDigitalControl(control, newState)
                                        },
                                    )
                                }

                                ControlType.PwmSlider -> {
                                    PwmSliderControlCard(
                                        control = control,
                                        value = pwmStates[control.id] ?: control.currentValue ?: 0,
                                        enabled = isConnected,
                                        onValueChange = { newValue ->
                                            pwmStates[control.id] = newValue

                                            val now = System.currentTimeMillis()
                                            val lastSentAt = pwmLastSentAt[control.id] ?: 0L
                                            val shouldSendNow = now - lastSentAt >= 45L

                                            if (shouldSendNow) {
                                                pwmLastSentAt[control.id] = now
                                                pwmLastSentValues[control.id] = newValue
                                                onSendPwmControl(control, newValue)
                                            }
                                        },
                                        onValueChangeFinished = {
                                            val finalValue = pwmStates[control.id] ?: control.currentValue ?: 0
                                            val lastSentValue = pwmLastSentValues[control.id]

                                            if (lastSentValue != finalValue) {
                                                pwmLastSentAt[control.id] = System.currentTimeMillis()
                                                pwmLastSentValues[control.id] = finalValue
                                                onSendPwmControl(control, finalValue)
                                            }
                                        },
                                    )
                                }

                                ControlType.PulseButton -> {
                                    PulseControlCard(
                                        control = control,
                                        enabled = isConnected,
                                        onPulse = {
                                            onSendPulseControl(control)
                                        },
                                    )
                                }

                                ControlType.ServoSlider,
                                ControlType.AnalogRead -> {
                                    FutureConfiguredControlCard(control = control)
                                }
                            }
                        }
                    }

                    if (!isConnected) {
                        NotConnectedCard(
                            onOpenConnection = onOpenConnection,
                        )
                    }

                    CreateControlCard(onCreateControl = onCreateControl)

                    if (hasCustomControls) {
                        ResetControlsCard(
                            onClearControls = {
                                displayedControls.clear()
                                displayedControls.add(DefaultControls.pin13DigitalOutput.copy(isOn = false))

                                digitalStates.clear()
                                digitalStates[DefaultControls.pin13DigitalOutput.id] = false

                                onClearControls()
                            },
                        )
                    }
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
private fun HeaderSection(
    isConnected: Boolean,
    hasCustomControls: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp),
    ) {
        Text(
            text = if (hasCustomControls) "Meus controles" else "Controle seu projeto",
            color = WhiteSoft,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.4).sp,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isConnected) {
                "Use os controles abaixo para acionar as saídas configuradas no Arduino."
            } else {
                "Conecte um dispositivo Bluetooth para controlar o Arduino."
            },
            color = WhiteSoft.copy(alpha = 0.78f),
            fontSize = 16.sp,
            lineHeight = 24.sp,
        )
    }
}

@Composable
private fun DigitalOutputControlCard(
    control: LabLinkControl,
    isOn: Boolean,
    enabled: Boolean,
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
        Text(
            text = control.name,
            color = WhiteSoft,
            fontSize = 18.sp,
            lineHeight = 23.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(18.dp))

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
            text = control.pinLabel,
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
private fun PwmSliderControlCard(
    control: LabLinkControl,
    value: Int,
    enabled: Boolean,
    onValueChange: (Int) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    val safeValue = value.coerceIn(0, 255)
    val percentage = ((safeValue / 255f) * 100).toInt()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.52f)
            .background(CardDark, RoundedCornerShape(30.dp))
            .border(
                width = 1.dp,
                color = if (safeValue > 0) AccentPurple.copy(alpha = 0.55f) else BorderSoft,
                shape = RoundedCornerShape(30.dp),
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(AccentPurple.copy(alpha = 0.16f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Tune,
                    contentDescription = null,
                    tint = AccentPurple,
                    modifier = Modifier.size(28.dp),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = control.name,
                    color = WhiteSoft,
                    fontSize = 18.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "${control.pinLabel} • PWM",
                    color = TextDim,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontFamily = FontFamily.Monospace,
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = safeValue.toString(),
                color = WhiteSoft,
                fontSize = 44.sp,
                lineHeight = 50.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.8).sp,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$percentage%",
                color = TextDim,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
        }

        Slider(
            value = safeValue.toFloat(),
            onValueChange = { newValue ->
                if (enabled) {
                    onValueChange(newValue.toInt().coerceIn(0, 255))
                }
            },
            onValueChangeFinished = {
                if (enabled) {
                    onValueChangeFinished()
                }
            },
            enabled = enabled,
            valueRange = 0f..255f,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "0",
                color = TextDim,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
            )

            Text(
                text = "255",
                color = TextDim,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

@Composable
private fun PulseControlCard(
    control: LabLinkControl,
    enabled: Boolean,
    onPulse: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.52f)
            .background(CardDark, RoundedCornerShape(30.dp))
            .border(
                width = 1.dp,
                color = BorderSoft,
                shape = RoundedCornerShape(30.dp),
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(AccentYellow.copy(alpha = 0.16f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Bolt,
                    contentDescription = null,
                    tint = AccentYellow,
                    modifier = Modifier.size(28.dp),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = control.name,
                    color = WhiteSoft,
                    fontSize = 18.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "${control.pinLabel} • Pulso de 500 ms",
                    color = TextDim,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontFamily = FontFamily.Monospace,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .background(
                    color = if (enabled) AccentYellow else Color.White.copy(alpha = 0.06f),
                    shape = RoundedCornerShape(22.dp),
                )
                .border(
                    width = 1.dp,
                    color = if (enabled) AccentYellow.copy(alpha = 0.65f) else BorderSoft,
                    shape = RoundedCornerShape(22.dp),
                )
                .clickable(enabled = enabled) { onPulse() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Enviar pulso",
                color = if (enabled) Color.Black else TextDim,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Text(
            text = if (enabled) {
                "Ao tocar, o pino liga por 500 ms e desliga automaticamente."
            } else {
                "Conecte o Bluetooth para enviar o pulso."
            },
            color = TextDim,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun FutureConfiguredControlCard(control: LabLinkControl) {
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
                .background(control.type.accentColor().copy(alpha = 0.16f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = control.type.icon(),
                contentDescription = null,
                tint = control.type.accentColor(),
                modifier = Modifier.size(24.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = control.name,
                color = WhiteSoft,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "${control.type.displayName} • ${control.pinLabel}",
                color = TextDim,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontFamily = FontFamily.Monospace,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Controle visual preparado. Ação será ativada em etapa futura.",
                color = TextDim,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
        }
    }
}

@Composable
private fun EmptyControlsCard(
    onCreateControl: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(24.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(24.dp))
            .clickable { onCreateControl() }
            .padding(20.dp),
    ) {
        Text(
            text = "Nenhum controle criado",
            color = WhiteSoft,
            fontSize = 18.sp,
            lineHeight = 23.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Crie controles para acionar saídas, PWM, servo, pulso ou leitura de sensores.",
            color = TextDim,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
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
private fun CreateControlCard(onCreateControl: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(22.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(22.dp))
            .clickable { onCreateControl() }
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
                text = "Criar novo controle",
                color = WhiteSoft,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Escolha o tipo de controle, quantidade e pinos usados no seu projeto.",
            color = TextDim,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun ResetControlsCard(onClearControls: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(22.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(22.dp))
            .clickable { onClearControls() }
            .padding(18.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Tune,
                contentDescription = null,
                tint = TextDim,
                modifier = Modifier.size(18.dp),
            )

            Text(
                text = "Redefinir controles",
                color = WhiteSoft,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Limpa os controles criados e volta para o controle padrão do Pino 13.",
            color = TextDim,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
    }
}

private fun ControlType.icon(): ImageVector {
    return when (this) {
        ControlType.DigitalToggle -> Icons.Rounded.PowerSettingsNew
        ControlType.PwmSlider -> Icons.Rounded.Tune
        ControlType.ServoSlider -> Icons.Rounded.Speed
        ControlType.PulseButton -> Icons.Rounded.Bolt
        ControlType.AnalogRead -> Icons.Rounded.GraphicEq
    }
}

private fun ControlType.accentColor(): Color {
    return when (this) {
        ControlType.DigitalToggle -> AccentYellow
        ControlType.PwmSlider -> AccentPurple
        ControlType.ServoSlider -> AccentGreen
        ControlType.PulseButton -> AccentYellow
        ControlType.AnalogRead -> AccentPurple
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
            controls = listOf(DefaultControls.pin13DigitalOutput),
            controlsRefreshKey = 0,
            onSendPing = {},
            onToggleDigitalControl = { _, _ -> },
            onSendPwmControl = { _, _ -> },
            onSendPulseControl = {},
            onTurnLedOn = {},
            onTurnLedOff = {},
            onOpenHome = {},
            onOpenConnection = {},
            onOpenTerminal = {},
            onOpenControls = {},
            onCreateControl = {},
            onClearControls = {},
        )
    }
}









