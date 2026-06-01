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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothConnectionStatus
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothDeviceInfo
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothUiState
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardPinValidator
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardProfile
import br.com.laboratoriodecircuitos.lablink.core.controls.ControlType
import br.com.laboratoriodecircuitos.lablink.core.controls.DefaultControls
import br.com.laboratoriodecircuitos.lablink.core.controls.LabLinkControl
import br.com.laboratoriodecircuitos.lablink.ui.components.LabLinkDrawer
import br.com.laboratoriodecircuitos.lablink.ui.theme.LabLinkTheme

private val BgDeep = Color(0xFF000000)
private val TopBarDark = Color(0xFF050505)
private val IconButtonDark = Color(0xFF151515)
private val WidgetDark = Color(0xFF171818)
private val WidgetPanel = Color(0xFF202124)
private val AccentYellow = Color(0xFFFFE382)
private val AccentGreen = Color(0xFFC4FA8C)
private val AccentPurple = Color(0xFFE5BEFF)
private val TextDim = Color(0xFF8A8A8A)
private val WhiteSoft = Color(0xFFFFFFFF)
private val BorderSoft = Color.White.copy(alpha = 0.09f)

@Composable
fun ControlsScreen(
    bluetoothState: BluetoothUiState,
    selectedBoard: BoardProfile? = null,
    controls: List<LabLinkControl> = emptyList(),
    controlsRefreshKey: Int = 0,
    onSendPing: () -> Unit,
    onToggleDigitalControl: (LabLinkControl, Boolean) -> Unit = { _, _ -> },
    onSendPwmControl: (LabLinkControl, Int) -> Unit = { _, _ -> },
    onSendServoControl: (LabLinkControl, Int) -> Unit = { _, _ -> },
    onSendPulseControl: (LabLinkControl) -> Unit = {},
    onReadAnalogControl: (LabLinkControl) -> Unit = {},
    onTurnLedOn: () -> Unit,
    onTurnLedOff: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenConnection: () -> Unit,
    onOpenTerminal: () -> Unit,
    onOpenControls: () -> Unit,
    onCreateControl: () -> Unit = {},
    onClearControls: () -> Unit = {},
) {
    var drawerOpen by remember { mutableStateOf(false) }
    val isConnected = bluetoothState.status == BluetoothConnectionStatus.Connected
    val digitalStates = remember { mutableStateMapOf<String, Boolean>() }
    val sliderStates = remember { mutableStateMapOf<String, Int>() }
    val analogReadStates = remember { mutableStateMapOf<String, Int>() }
    val sliderLastSentAt = remember { mutableStateMapOf<String, Long>() }
    val sliderLastSentValues = remember { mutableStateMapOf<String, Int>() }

    LaunchedEffect(controlsRefreshKey, controls) {
        digitalStates.clear()
        sliderStates.clear()
        analogReadStates.clear()
        sliderLastSentAt.clear()
        sliderLastSentValues.clear()

        controls.forEach { control ->
            when (control.type) {
                ControlType.DigitalToggle -> digitalStates[control.id] = control.isOn
                ControlType.PwmSlider -> sliderStates[control.id] = control.currentValue ?: 0
                ControlType.ServoSlider -> sliderStates[control.id] = control.currentValue ?: 90
                ControlType.AnalogRead -> analogReadStates[control.id] = control.currentValue ?: 0
                ControlType.PulseButton -> Unit
            }
        }
    }

    LaunchedEffect(bluetoothState.lastReceivedMessage) {
        when (bluetoothState.lastReceivedMessage) {
            "OK:LED_ON" -> controls
                .filter { it.type == ControlType.DigitalToggle && BoardPinValidator.normalizePin(it.pin) == "D13" }
                .forEach { digitalStates[it.id] = true }

            "OK:LED_OFF" -> controls
                .filter { it.type == ControlType.DigitalToggle && BoardPinValidator.normalizePin(it.pin) == "D13" }
                .forEach { digitalStates[it.id] = false }

            else -> {
                val message = bluetoothState.lastReceivedMessage.orEmpty()

                if (message.startsWith("OK:READ:")) {
                    val parts = message.split(":")
                    val pin = parts.getOrNull(2)?.let(BoardPinValidator::normalizePin)
                    val value = parts.getOrNull(3)?.toIntOrNull()

                    if (pin != null && value != null) {
                        controls
                            .filter {
                                it.type == ControlType.AnalogRead &&
                                    BoardPinValidator.normalizePin(it.pin) == pin
                            }
                            .forEach { control ->
                                analogReadStates[control.id] = value
                            }
                    }
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BgDeep,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .background(BgDeep)
                .dashboardGridBackground(),
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    top = 116.dp,
                    end = 20.dp,
                    bottom = 32.dp,
                ),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                itemsIndexed(
                    items = controls,
                    key = { index, control -> "${control.id.ifBlank { "control" }}_$index" },
                    span = { _, control -> GridItemSpan(control.type.gridSpan()) },
                ) { _, control ->
                    when (control.type) {
                        ControlType.DigitalToggle -> ButtonWidget(
                            control = control,
                            isOn = digitalStates[control.id] == true,
                            enabled = isConnected,
                            onToggle = {
                                val newState = !(digitalStates[control.id] ?: false)
                                digitalStates[control.id] = newState
                                onToggleDigitalControl(control, newState)
                            },
                        )

                        ControlType.PwmSlider -> SliderWidget(
                            control = control,
                            value = sliderStates[control.id] ?: control.currentValue ?: 0,
                            enabled = isConnected,
                            valueRange = 0f..255f,
                            valueLabel = { it.toString() },
                            onValueChanged = { value ->
                                sendThrottledSliderValue(
                                    control = control,
                                    value = value,
                                    sliderStates = sliderStates,
                                    sliderLastSentAt = sliderLastSentAt,
                                    sliderLastSentValues = sliderLastSentValues,
                                    onSendValue = onSendPwmControl,
                                )
                            },
                            onValueFinished = {
                                sendFinalSliderValue(
                                    control = control,
                                    fallbackValue = control.currentValue ?: 0,
                                    sliderStates = sliderStates,
                                    sliderLastSentAt = sliderLastSentAt,
                                    sliderLastSentValues = sliderLastSentValues,
                                    onSendValue = onSendPwmControl,
                                )
                            },
                        )

                        ControlType.ServoSlider -> SliderWidget(
                            control = control,
                            value = sliderStates[control.id] ?: control.currentValue ?: 90,
                            enabled = isConnected,
                            valueRange = 0f..180f,
                            valueLabel = { "${it}°" },
                            onValueChanged = { value ->
                                sendThrottledSliderValue(
                                    control = control,
                                    value = value,
                                    sliderStates = sliderStates,
                                    sliderLastSentAt = sliderLastSentAt,
                                    sliderLastSentValues = sliderLastSentValues,
                                    onSendValue = onSendServoControl,
                                )
                            },
                            onValueFinished = {
                                sendFinalSliderValue(
                                    control = control,
                                    fallbackValue = control.currentValue ?: 90,
                                    sliderStates = sliderStates,
                                    sliderLastSentAt = sliderLastSentAt,
                                    sliderLastSentValues = sliderLastSentValues,
                                    onSendValue = onSendServoControl,
                                )
                            },
                        )

                        ControlType.PulseButton -> PulseWidget(
                            control = control,
                            enabled = isConnected,
                            onPulse = { onSendPulseControl(control) },
                        )

                        ControlType.AnalogRead -> ReadWidget(
                            control = control,
                            value = analogReadStates[control.id],
                            enabled = isConnected,
                            onRead = { onReadAnalogControl(control) },
                        )
                    }
                }
            }

            DashboardTopBar(
                title = "LabLink",
                subtitle = selectedBoard?.displayName ?: "Painel",
                isConnected = isConnected,
                onOpenDrawer = { drawerOpen = true },
                onAddModule = onCreateControl,
                onPing = onSendPing,
                onSettings = onOpenControls,
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
private fun DashboardTopBar(
    title: String,
    subtitle: String,
    isConnected: Boolean,
    onOpenDrawer: () -> Unit,
    onAddModule: () -> Unit,
    onPing: () -> Unit,
    onSettings: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TopBarDark.copy(alpha = 0.98f))
            .border(1.dp, BorderSoft)
            .statusBarsPadding()
            .height(104.dp)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.Menu,
            contentDescription = null,
            tint = WhiteSoft,
            modifier = Modifier
                .size(31.dp)
                .clickable { onOpenDrawer() },
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            Text(
                text = title,
                color = WhiteSoft,
                fontSize = 23.sp,
                lineHeight = 27.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )

            Text(
                text = subtitle,
                color = TextDim,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                maxLines = 1,
            )
        }

        ToolbarIcon(
            icon = Icons.Rounded.Settings,
            onClick = onSettings,
        )

        ToolbarIcon(
            icon = Icons.Rounded.AddCircleOutline,
            onClick = onAddModule,
        )

        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = IconButtonDark,
                    shape = CircleShape,
                )
                .border(
                    width = 1.dp,
                    color = if (isConnected) AccentGreen.copy(alpha = 0.65f) else BorderSoft,
                    shape = CircleShape,
                )
                .clickable { onPing() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = null,
                tint = if (isConnected) AccentGreen else TextDim,
                modifier = Modifier.size(39.dp),
            )
        }
    }
}

@Composable
private fun ToolbarIcon(
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = WhiteSoft.copy(alpha = 0.86f),
        modifier = Modifier
            .background(IconButtonDark, CircleShape)
            .border(1.dp, BorderSoft, CircleShape)
            .padding(5.dp)
            .size(31.dp)
            .clickable { onClick() },
    )
}

private fun sendThrottledSliderValue(
    control: LabLinkControl,
    value: Int,
    sliderStates: MutableMap<String, Int>,
    sliderLastSentAt: MutableMap<String, Long>,
    sliderLastSentValues: MutableMap<String, Int>,
    onSendValue: (LabLinkControl, Int) -> Unit,
) {
    sliderStates[control.id] = value

    val now = System.currentTimeMillis()
    val lastSentAt = sliderLastSentAt[control.id] ?: 0L

    if (now - lastSentAt >= 45L) {
        sliderLastSentAt[control.id] = now
        sliderLastSentValues[control.id] = value
        onSendValue(control, value)
    }
}

private fun sendFinalSliderValue(
    control: LabLinkControl,
    fallbackValue: Int,
    sliderStates: MutableMap<String, Int>,
    sliderLastSentAt: MutableMap<String, Long>,
    sliderLastSentValues: MutableMap<String, Int>,
    onSendValue: (LabLinkControl, Int) -> Unit,
) {
    val finalValue = sliderStates[control.id] ?: fallbackValue

    if (sliderLastSentValues[control.id] != finalValue) {
        sliderLastSentAt[control.id] = System.currentTimeMillis()
        sliderLastSentValues[control.id] = finalValue
        onSendValue(control, finalValue)
    }
}

@Composable
private fun ButtonWidget(
    control: LabLinkControl,
    isOn: Boolean,
    enabled: Boolean,
    onToggle: () -> Unit,
) {
    SquareWidgetShell(
        control = control,
        accent = AccentGreen,
        enabled = enabled,
        onClick = onToggle,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(82.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .border(
                        width = 2.dp,
                        color = if (isOn) AccentGreen else AccentGreen.copy(alpha = 0.72f),
                        shape = CircleShape,
                    )
                    .background(
                        color = if (isOn) AccentGreen.copy(alpha = 0.14f) else Color.Transparent,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (isOn) "ON" else "OFF",
                    color = if (isOn) AccentGreen else AccentGreen.copy(alpha = 0.84f),
                    fontSize = 17.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun PulseWidget(
    control: LabLinkControl,
    enabled: Boolean,
    onPulse: () -> Unit,
) {
    SquareWidgetShell(
        control = control,
        accent = AccentYellow,
        enabled = enabled,
        onClick = onPulse,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(82.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(66.dp)
                    .background(AccentYellow.copy(alpha = 0.16f), CircleShape)
                    .border(2.dp, AccentYellow.copy(alpha = 0.82f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Bolt,
                    contentDescription = null,
                    tint = AccentYellow,
                    modifier = Modifier.size(31.dp),
                )
            }
        }
    }
}

@Composable
private fun ReadWidget(
    control: LabLinkControl,
    value: Int?,
    enabled: Boolean,
    onRead: () -> Unit,
) {
    SquareWidgetShell(
        control = control,
        accent = AccentPurple,
        enabled = enabled,
        onClick = onRead,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(82.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = value?.toString() ?: "--",
                color = WhiteSoft,
                fontSize = 33.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun SliderWidget(
    control: LabLinkControl,
    value: Int,
    enabled: Boolean,
    valueRange: ClosedFloatingPointRange<Float>,
    valueLabel: (Int) -> String,
    onValueChanged: (Int) -> Unit,
    onValueFinished: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(126.dp)
            .alpha(if (enabled) 1f else 0.52f)
            .background(WidgetPanel.copy(alpha = 0.98f), RoundedCornerShape(2.dp))
            .border(1.dp, Color.Black.copy(alpha = 0.55f), RoundedCornerShape(2.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ModuleIconBox(
            icon = control.type.icon(),
            accent = control.type.accentColor(),
            size = 54,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = control.name.uppercase(),
                        color = WhiteSoft.copy(alpha = 0.82f),
                        fontSize = 11.sp,
                        lineHeight = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )

                    Text(
                        text = control.pinLabel,
                        color = TextDim,
                        fontSize = 10.sp,
                        lineHeight = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                    )
                }

                Text(
                    text = valueLabel(value),
                    color = control.type.accentColor(),
                    fontSize = 22.sp,
                    lineHeight = 25.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChanged(it.toInt()) },
                onValueChangeFinished = onValueFinished,
                enabled = enabled,
                valueRange = valueRange,
            )
        }
    }
}

@Composable
private fun SquareWidgetShell(
    control: LabLinkControl,
    accent: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(126.dp)
            .alpha(if (enabled) 1f else 0.52f)
            .background(WidgetPanel.copy(alpha = 0.98f), RoundedCornerShape(2.dp))
            .border(1.dp, Color.Black.copy(alpha = 0.55f), RoundedCornerShape(2.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = control.name.uppercase(),
            color = WhiteSoft.copy(alpha = 0.78f),
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )

        content()

        Text(
            text = BoardPinValidator.normalizePin(control.pin),
            color = accent.copy(alpha = 0.72f),
            fontSize = 9.sp,
            lineHeight = 11.sp,
            fontFamily = FontFamily.Monospace,
            maxLines = 1,
        )
    }
}

@Composable
private fun ModuleIconBox(
    icon: ImageVector,
    accent: Color,
    size: Int,
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(Color.Black.copy(alpha = 0.22f), RoundedCornerShape(4.dp))
            .border(1.dp, accent.copy(alpha = 0.42f), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size((size * 0.48f).dp),
        )
    }
}

private fun ControlType.gridSpan(): Int {
    return when (this) {
        ControlType.PwmSlider,
        ControlType.ServoSlider -> 3

        ControlType.DigitalToggle,
        ControlType.PulseButton,
        ControlType.AnalogRead -> 1
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
        ControlType.DigitalToggle -> AccentGreen
        ControlType.PwmSlider -> AccentPurple
        ControlType.ServoSlider -> AccentGreen
        ControlType.PulseButton -> AccentYellow
        ControlType.AnalogRead -> AccentPurple
    }
}

private fun Modifier.dashboardGridBackground(): Modifier {
    return drawBehind {
        val dotStep = 14.dp.toPx()
        val majorStep = dotStep * 8f
        val dotColor = Color.White.copy(alpha = 0.055f)
        val lineColor = Color.White.copy(alpha = 0.036f)

        var x = 0f
        while (x <= size.width) {
            drawLine(
                color = lineColor,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1f,
            )
            x += majorStep
        }

        var y = 0f
        while (y <= size.height) {
            drawLine(
                color = lineColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f,
            )
            y += majorStep
        }

        var dotX = 0f
        while (dotX <= size.width) {
            var dotY = 0f
            while (dotY <= size.height) {
                drawCircle(
                    color = dotColor,
                    radius = 1f,
                    center = Offset(dotX, dotY),
                    style = Stroke(width = 1f),
                )
                dotY += dotStep
            }
            dotX += dotStep
        }
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
            controls = listOf(
                DefaultControls.pin13DigitalOutput,
                LabLinkControl(
                    id = "pwm_d5_preview",
                    type = ControlType.PwmSlider,
                    name = "PWM",
                    pin = "5",
                    minValue = 0,
                    maxValue = 255,
                    currentValue = 128,
                ),
            ),
            onSendPing = {},
            onToggleDigitalControl = { _, _ -> },
            onSendPwmControl = { _, _ -> },
            onSendServoControl = { _, _ -> },
            onSendPulseControl = {},
            onReadAnalogControl = {},
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
