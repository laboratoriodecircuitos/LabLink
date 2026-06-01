package br.com.laboratoriodecircuitos.lablink.features.customcontrol

import java.util.UUID

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardPin
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardPinValidator
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardProfile
import br.com.laboratoriodecircuitos.lablink.core.controls.ControlType
import br.com.laboratoriodecircuitos.lablink.core.controls.LabLinkControl
import br.com.laboratoriodecircuitos.lablink.ui.components.LabLinkScreenChrome

private val CardDark = Color(0xFF151515)
private val FieldDark = Color(0xFF242424)
private val WhiteSoft = Color(0xFFFFFFFF)
private val TextDim = Color(0xFF8A8A8A)
private val BorderSoft = Color.White.copy(alpha = 0.08f)
private val AccentGreen = Color(0xFFC4FA8C)
private val AccentPurple = Color(0xFFE5BEFF)
private val AccentYellow = Color(0xFFFFE382)

data class WidgetSizeOption(
    val width: Int,
    val height: Int,
)

@Composable
fun WidgetSettingsScreen(
    board: BoardProfile,
    widgetType: ControlType,
    existingWidgets: List<LabLinkControl>,
    isBluetoothConnected: Boolean,
    onBack: () -> Unit,
    onSaveWidget: (LabLinkControl) -> Unit,
    onOpenHome: () -> Unit,
    onOpenConnection: () -> Unit,
    onOpenControls: () -> Unit,
    onOpenTerminal: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val availablePins = BoardPinValidator.availablePinsFor(
        board = board,
        controlType = widgetType,
        usedPins = existingWidgets.map { BoardPinValidator.normalizePin(it.pin) }.toSet(),
    )
    val allowedSizes = widgetType.allowedSizes()
    var name by remember(widgetType) { mutableStateOf(widgetType.defaultName()) }
    var selectedPin by remember(widgetType) { mutableStateOf("") }
    var mode by remember(widgetType) { mutableStateOf("SWITCH") }
    var size by remember(widgetType) { mutableStateOf(allowedSizes.first()) }
    var minValue by remember(widgetType) { mutableStateOf(if (widgetType == ControlType.ServoSlider) "0" else "0") }
    var maxValue by remember(widgetType) { mutableStateOf(if (widgetType == ControlType.ServoSlider) "180" else "255") }
    var durationMs by remember(widgetType) { mutableStateOf("500") }
    val isValid = name.isNotBlank() && selectedPin.isNotBlank()

    LabLinkScreenChrome(
        isBluetoothConnected = isBluetoothConnected,
        onOpenHome = onOpenHome,
        onOpenConnection = onOpenConnection,
        onOpenControls = onOpenControls,
        onOpenTerminal = onOpenTerminal,
        title = "Configurar widget",
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 104.dp, start = 16.dp, end = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = WhiteSoft,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onBack() },
                )

                Text(
                    text = widgetType.settingsTitle(),
                    color = WhiteSoft,
                    fontSize = 28.sp,
                    lineHeight = 34.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = "PREVIEW",
                color = TextDim,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
            )

            PreviewCard(type = widgetType)

            TextInput(
                label = "NOME",
                value = name,
                placeholder = widgetType.defaultName(),
                keyboardType = KeyboardType.Text,
                onChange = { name = it },
            )

            SectionLabel(text = "PINO")
            PinWheelPicker(
                pins = availablePins,
                selectedPin = selectedPin,
                onSelectPin = { selectedPin = it },
            )

            if (widgetType == ControlType.DigitalToggle) {
                PushSwitchPicker(
                    selected = mode,
                    onSelect = { mode = it },
                )
            }

            if (widgetType == ControlType.PwmSlider || widgetType == ControlType.ServoSlider) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    TextInput(
                        label = "MIN",
                        value = minValue,
                        placeholder = "0",
                        keyboardType = KeyboardType.Number,
                        onChange = { minValue = it },
                        modifier = Modifier.weight(1f),
                    )
                    TextInput(
                        label = "MAX",
                        value = maxValue,
                        placeholder = if (widgetType == ControlType.ServoSlider) "180" else "255",
                        keyboardType = KeyboardType.Number,
                        onChange = { maxValue = it },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            if (widgetType == ControlType.PulseButton) {
                TextInput(
                    label = "DURAÇÃO DO PULSO (MS)",
                    value = durationMs,
                    placeholder = "500",
                    keyboardType = KeyboardType.Number,
                    onChange = { durationMs = it },
                )
            }

            SectionLabel(text = "TAMANHO")
            SizePicker(
                options = allowedSizes,
                selected = size,
                onSelect = { size = it },
            )

            SaveButton(
                enabled = isValid,
                onClick = {
                    val normalizedPin = BoardPinValidator.normalizePin(selectedPin)
                    val gridIndex = existingWidgets.size
                    onSaveWidget(
                        LabLinkControl(
                            id = "${widgetType.name.lowercase()}_${normalizedPin.lowercase()}_${UUID.randomUUID()}",
                            type = widgetType,
                            name = name.ifBlank { widgetType.defaultName() },
                            pin = normalizedPin.removePrefix("D"),
                            minValue = when (widgetType) {
                                ControlType.PwmSlider,
                                ControlType.ServoSlider -> minValue.toIntOrNull() ?: 0
                                else -> null
                            },
                            maxValue = when (widgetType) {
                                ControlType.PwmSlider -> maxValue.toIntOrNull() ?: 255
                                ControlType.ServoSlider -> maxValue.toIntOrNull() ?: 180
                                else -> null
                            },
                            currentValue = when (widgetType) {
                                ControlType.PwmSlider -> minValue.toIntOrNull() ?: 0
                                ControlType.ServoSlider -> 90
                                else -> null
                            },
                            widthUnits = size.width,
                            heightUnits = size.height,
                            gridX = gridIndex % 3,
                            gridY = gridIndex / 3,
                            mode = if (widgetType == ControlType.DigitalToggle) mode else null,
                            durationMs = if (widgetType == ControlType.PulseButton) {
                                durationMs.toIntOrNull() ?: 500
                            } else {
                                null
                            },
                        ),
                    )
                },
            )
        }
    }
}

@Composable
private fun PreviewCard(type: ControlType) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(192.dp)
            .background(CardDark, RoundedCornerShape(24.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(24.dp))
            .previewGrid(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(92.dp)
                .background(Color(0xFF1A1A1A), RoundedCornerShape(20.dp))
                .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = type.icon(),
                    contentDescription = null,
                    tint = AccentYellow,
                    modifier = Modifier.size(34.dp),
                )
                Text(
                    text = "D3",
                    color = AccentYellow,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

private fun Modifier.previewGrid(): Modifier {
    return drawBehind {
        val step = 20.dp.toPx()
        val dotColor = Color.White.copy(alpha = 0.08f)
        var x = 0f
        while (x <= size.width) {
            var y = 0f
            while (y <= size.height) {
                drawCircle(dotColor, 1.1f, Offset(x, y))
                y += step
            }
            x += step
        }
    }
}

@Composable
private fun PinWheelPicker(
    pins: List<BoardPin>,
    selectedPin: String,
    onSelectPin: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(174.dp)
            .background(CardDark, RoundedCornerShape(8.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(8.dp)),
    ) {
        if (pins.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Nenhum pino disponível",
                    color = TextDim,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 56.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(pins, key = { it.id }) { pin ->
                    val selected = BoardPinValidator.normalizePin(selectedPin) == BoardPinValidator.normalizePin(pin.id)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .background(if (selected) AccentPurple.copy(alpha = 0.18f) else Color.Transparent)
                            .clickable { onSelectPin(pin.id) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = pin.label,
                            color = if (selected) AccentPurple else WhiteSoft,
                            fontSize = if (selected) 24.sp else 18.sp,
                            lineHeight = 28.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PushSwitchPicker(
    selected: String,
    onSelect: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        listOf("PUSH", "SWITCH").forEach { option ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .background(if (selected == option) Color(0xFF2A2A2A) else Color(0xFF353434), RoundedCornerShape(10.dp))
                    .border(1.dp, if (selected == option) Color.White.copy(alpha = 0.10f) else Color.Transparent, RoundedCornerShape(10.dp))
                    .clickable { onSelect(option) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = option,
                    color = if (selected == option) WhiteSoft else TextDim,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun SizePicker(
    options: List<WidgetSizeOption>,
    selected: WidgetSizeOption,
    onSelect: (WidgetSizeOption) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.chunked(3).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { option ->
                    val selectedOption = selected == option
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .background(if (selectedOption) AccentYellow.copy(alpha = 0.10f) else CardDark, RoundedCornerShape(12.dp))
                            .border(1.dp, if (selectedOption) AccentYellow else BorderSoft, RoundedCornerShape(12.dp))
                            .clickable { onSelect(option) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "${option.width}x${option.height}",
                            color = if (selectedOption) AccentYellow else TextDim,
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TextInput(
    label: String,
    value: String,
    placeholder: String,
    keyboardType: KeyboardType,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SectionLabel(text = label)
        BasicTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            textStyle = TextStyle(
                color = WhiteSoft,
                fontSize = 17.sp,
                lineHeight = 22.sp,
            ),
            cursorBrush = SolidColor(AccentPurple),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = keyboardType,
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .background(FieldDark, RoundedCornerShape(8.dp))
                        .border(1.dp, BorderSoft, RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isBlank()) {
                        Text(
                            text = placeholder,
                            color = TextDim,
                            fontSize = 17.sp,
                            lineHeight = 22.sp,
                        )
                    }
                    innerTextField()
                }
            },
        )
    }
}

@Composable
private fun SaveButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(if (enabled) WhiteSoft else CardDark, RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "SAVE",
            color = if (enabled) Color.Black else TextDim,
            fontSize = 20.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = TextDim,
        fontSize = 12.sp,
        lineHeight = 15.sp,
        fontWeight = FontWeight.Bold,
    )
}

private fun ControlType.allowedSizes(): List<WidgetSizeOption> {
    val all = listOf(
        WidgetSizeOption(1, 1),
        WidgetSizeOption(2, 1),
        WidgetSizeOption(3, 1),
        WidgetSizeOption(1, 2),
        WidgetSizeOption(2, 2),
        WidgetSizeOption(3, 2),
    )

    return when (this) {
        ControlType.PwmSlider,
        ControlType.ServoSlider -> all.filter { it.width >= 2 }
        else -> all
    }
}

private fun ControlType.defaultName(): String {
    return when (this) {
        ControlType.DigitalToggle -> "Botão"
        ControlType.PwmSlider -> "Slider PWM"
        ControlType.ServoSlider -> "Servo"
        ControlType.PulseButton -> "Pulso"
        ControlType.AnalogRead -> "Leitura"
    }
}

private fun ControlType.settingsTitle(): String {
    return when (this) {
        ControlType.DigitalToggle -> "Button settings"
        ControlType.PwmSlider -> "PWM settings"
        ControlType.ServoSlider -> "Servo settings"
        ControlType.PulseButton -> "Pulse settings"
        ControlType.AnalogRead -> "Analog read settings"
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
