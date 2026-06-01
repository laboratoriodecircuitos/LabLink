package br.com.laboratoriodecircuitos.lablink.features.createcontrol

import java.util.UUID

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Speed
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardPin
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardPinValidator
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardProfile
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardProfiles
import br.com.laboratoriodecircuitos.lablink.core.boards.PinValidationResult
import br.com.laboratoriodecircuitos.lablink.core.controls.ControlType
import br.com.laboratoriodecircuitos.lablink.core.controls.LabLinkControl
import br.com.laboratoriodecircuitos.lablink.ui.theme.LabLinkTheme

private val BgDeep = Color(0xFF000000)
private val TopBarDark = Color(0xFF050505)
private val PanelDark = Color(0xFF151515)
private val FieldDark = Color(0xFF2A2C2D)
private val TileDark = Color(0xFF202124)
private val AccentYellow = Color(0xFFFFE382)
private val AccentPurple = Color(0xFFE5BEFF)
private val AccentGreen = Color(0xFFC4FA8C)
private val AccentDanger = Color(0xFFFF8A8A)
private val TextDim = Color(0xFF8A8A8A)
private val WhiteSoft = Color(0xFFFFFFFF)
private val BorderSoft = Color.White.copy(alpha = 0.08f)

@Composable
fun CreateControlScreen(
    isBluetoothConnected: Boolean,
    initialBoard: BoardProfile? = null,
    initialControlType: ControlType? = null,
    existingControls: List<LabLinkControl> = emptyList(),
    onOpenHome: () -> Unit,
    onOpenConnection: () -> Unit,
    onOpenTerminal: () -> Unit,
    onOpenControls: () -> Unit,
    onBack: () -> Unit = onOpenControls,
    onSaveControls: (List<LabLinkControl>) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    val selectedBoard = initialBoard ?: BoardProfiles.defaultBoard
    val selectedType = initialControlType ?: ControlType.DigitalToggle
    val defaultName = selectedType.defaultName()

    var moduleName by remember(initialControlType) { mutableStateOf("") }
    var selectedPin by remember(initialControlType) { mutableStateOf("") }

    val usedPinsFromPanel = existingControls
        .map { BoardPinValidator.normalizePin(it.pin) }
        .filter { it.isNotBlank() }
        .toSet()

    val availablePins = BoardPinValidator.availablePinsFor(
        board = selectedBoard,
        controlType = selectedType,
        usedPins = usedPinsFromPanel,
    )

    val validationResult = BoardPinValidator.validatePinForControl(
        board = selectedBoard,
        controlType = selectedType,
        pin = selectedPin,
        usedPins = usedPinsFromPanel,
    )

    fun createUniqueModuleId(
        type: ControlType,
        pin: String,
    ): String {
        val normalizedType = type.name.lowercase()
        val normalizedPin = BoardPinValidator.normalizePin(pin).lowercase()

        return "${normalizedType}_${normalizedPin}_${UUID.randomUUID()}"
    }

    fun buildControl(): LabLinkControl {
        val normalizedPin = BoardPinValidator.normalizePin(selectedPin)

        return LabLinkControl(
            id = createUniqueModuleId(selectedType, normalizedPin),
            type = selectedType,
            name = moduleName.ifBlank { defaultName },
            pin = normalizedPin.removePrefix("D"),
            minValue = when (selectedType) {
                ControlType.PwmSlider -> 0
                ControlType.ServoSlider -> 0
                else -> null
            },
            maxValue = when (selectedType) {
                ControlType.PwmSlider -> 255
                ControlType.ServoSlider -> 180
                else -> null
            },
            currentValue = when (selectedType) {
                ControlType.PwmSlider -> 0
                ControlType.ServoSlider -> 90
                else -> null
            },
            isOn = false,
        )
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
                    .navigationBarsPadding()
                    .verticalScroll(scrollState)
                    .padding(top = 96.dp, bottom = 28.dp),
            ) {
                ModulePreviewHeader(
                    type = selectedType,
                    pin = selectedPin,
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PanelDark)
                        .padding(horizontal = 20.dp, vertical = 22.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    SettingsTextField(
                        value = moduleName,
                        placeholder = defaultName,
                        onValueChange = { moduleName = it },
                    )

                    SettingsDivider()

                    SectionLabel(text = if (selectedType == ControlType.AnalogRead) "PINO" else "OUTPUT")

                    PinSelector(
                        selectedPin = selectedPin,
                        availablePins = availablePins,
                        validationResult = validationResult,
                        onSelectPin = { selectedPin = BoardPinValidator.normalizePin(it) },
                    )

                    SettingsDivider()

                    SectionLabel(text = selectedType.parametersTitle())

                    ModuleParameters(type = selectedType)

                    SaveSettingsButton(
                        enabled = validationResult == PinValidationResult.Valid,
                        onClick = { onSaveControls(listOf(buildControl())) },
                    )
                }
            }

            SettingsTopBar(
                title = "${selectedType.displayName} Settings",
                isBluetoothConnected = isBluetoothConnected,
                onBack = onBack,
            )
        }
    }
}

@Composable
private fun SettingsTopBar(
    title: String,
    isBluetoothConnected: Boolean,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TopBarDark.copy(alpha = 0.98f))
            .border(1.dp, BorderSoft)
            .statusBarsPadding()
            .height(104.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.ArrowBack,
            contentDescription = null,
            tint = WhiteSoft,
            modifier = Modifier
                .size(31.dp)
                .clickable { onBack() },
        )

        Text(
            text = title,
            color = WhiteSoft,
            fontSize = 25.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )

        Box(
            modifier = Modifier
                .size(38.dp)
                .background(PanelDark, RoundedCornerShape(5.dp))
                .border(1.dp, BorderSoft, RoundedCornerShape(5.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = null,
                tint = if (isBluetoothConnected) AccentGreen else TextDim,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun ModulePreviewHeader(
    type: ControlType,
    pin: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .background(BgDeep)
            .settingsGridBackground(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(132.dp)
                    .background(TileDark.copy(alpha = 0.88f), RoundedCornerShape(8.dp))
                    .border(2.dp, type.accentColor().copy(alpha = 0.52f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = type.icon(),
                    contentDescription = null,
                    tint = type.accentColor(),
                    modifier = Modifier.size(62.dp),
                )
            }

            Text(
                text = if (pin.isBlank()) "PIN" else BoardPinValidator.normalizePin(pin),
                color = type.accentColor(),
                fontSize = 13.sp,
                lineHeight = 15.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun SettingsTextField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            color = WhiteSoft,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.Normal,
        ),
        cursorBrush = SolidColor(AccentPurple),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text,
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
                    .background(FieldDark, RoundedCornerShape(2.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isBlank()) {
                    Text(
                        text = placeholder,
                        color = WhiteSoft.copy(alpha = 0.52f),
                        fontSize = 28.sp,
                        lineHeight = 34.sp,
                    )
                }

                innerTextField()
            }
        },
    )
}

@Composable
private fun PinSelector(
    selectedPin: String,
    availablePins: List<BoardPin>,
    validationResult: PinValidationResult,
    onSelectPin: (String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (availablePins.isEmpty()) {
            Text(
                text = "Não há pinos disponíveis para este módulo.",
                color = AccentDanger,
                fontSize = 13.sp,
                lineHeight = 17.sp,
            )
            return
        }

        availablePins.chunked(3).forEach { rowPins ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                rowPins.forEach { pin ->
                    PinChoice(
                        pin = pin,
                        selected = BoardPinValidator.normalizePin(selectedPin) == BoardPinValidator.normalizePin(pin.id),
                        onClick = { onSelectPin(pin.id) },
                        modifier = Modifier.weight(1f),
                    )
                }

                repeat(3 - rowPins.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        BoardPinValidator.validationMessage(validationResult)?.let { message ->
            Text(
                text = message,
                color = if (validationResult == PinValidationResult.EmptyPin) TextDim else AccentDanger,
                fontSize = 12.sp,
                lineHeight = 15.sp,
            )
        }
    }
}

@Composable
private fun PinChoice(
    pin: BoardPin,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(80.dp)
            .background(
                color = if (selected) AccentPurple.copy(alpha = 0.16f) else FieldDark,
                shape = RoundedCornerShape(2.dp),
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) AccentPurple else BorderSoft,
                shape = RoundedCornerShape(2.dp),
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = pin.label,
            color = if (selected) AccentPurple else WhiteSoft,
            fontSize = 23.sp,
            lineHeight = 27.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Composable
private fun ModuleParameters(
    type: ControlType,
) {
    when (type) {
        ControlType.DigitalToggle -> ToggleParameterRow()
        ControlType.PwmSlider -> RangeParameterRow(min = "0", max = "255")
        ControlType.ServoSlider -> RangeParameterRow(min = "0°", max = "180°")
        ControlType.PulseButton -> SingleParameterBox(label = "DURAÇÃO", value = "500 ms")
        ControlType.AnalogRead -> SingleParameterBox(label = "ESCALA", value = "0 - 1023")
    }
}

@Composable
private fun ToggleParameterRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "PUSH",
            color = WhiteSoft,
            fontSize = 22.sp,
            lineHeight = 26.sp,
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .size(width = 92.dp, height = 46.dp)
                .border(3.dp, WhiteSoft, RoundedCornerShape(999.dp))
                .padding(4.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(WhiteSoft, CircleShape),
            )
        }

        Text(
            text = "SWITCH",
            color = TextDim,
            fontSize = 22.sp,
            lineHeight = 26.sp,
        )
    }
}

@Composable
private fun RangeParameterRow(
    min: String,
    max: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SingleParameterBox(
            label = "MIN",
            value = min,
            modifier = Modifier.weight(1f),
        )

        SingleParameterBox(
            label = "MAX",
            value = max,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SingleParameterBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(76.dp)
            .background(FieldDark, RoundedCornerShape(2.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            color = TextDim,
            fontSize = 11.sp,
            lineHeight = 13.sp,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = value,
            color = WhiteSoft,
            fontSize = 24.sp,
            lineHeight = 28.sp,
        )
    }
}

@Composable
private fun SaveSettingsButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .alpha(if (enabled) 1f else 0.46f)
            .background(if (enabled) AccentPurple else FieldDark, RoundedCornerShape(3.dp))
            .clickable(enabled = enabled) { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.Check,
            contentDescription = null,
            tint = if (enabled) Color.Black.copy(alpha = 0.78f) else TextDim,
            modifier = Modifier.size(24.dp),
        )

        Text(
            text = "Salvar",
            color = if (enabled) Color.Black.copy(alpha = 0.78f) else TextDim,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 10.dp),
        )
    }
}

@Composable
private fun SectionLabel(
    text: String,
) {
    Text(
        text = text,
        color = TextDim,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(BorderSoft),
    )
}

private fun ControlType.defaultName(): String {
    return when (this) {
        ControlType.DigitalToggle -> "Lâmpada"
        ControlType.PwmSlider -> "PWM"
        ControlType.ServoSlider -> "Servo"
        ControlType.PulseButton -> "Pulso"
        ControlType.AnalogRead -> "Leitura"
    }
}

private fun ControlType.parametersTitle(): String {
    return when (this) {
        ControlType.DigitalToggle -> "MODE"
        ControlType.PwmSlider -> "RANGE"
        ControlType.ServoSlider -> "ANGLE"
        ControlType.PulseButton -> "PULSE"
        ControlType.AnalogRead -> "READ"
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

private fun Modifier.settingsGridBackground(): Modifier {
    return drawBehind {
        val step = 8.dp.toPx()
        val majorStep = step * 6f
        val dotColor = Color.White.copy(alpha = 0.045f)
        val lineColor = Color.White.copy(alpha = 0.032f)

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
                    radius = 0.8f,
                    center = Offset(dotX, dotY),
                )
                dotY += step
            }
            dotX += step
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateControlScreenPreview() {
    LabLinkTheme {
        CreateControlScreen(
            isBluetoothConnected = true,
            initialBoard = BoardProfiles.defaultBoard,
            initialControlType = ControlType.DigitalToggle,
            existingControls = emptyList(),
            onOpenHome = {},
            onOpenConnection = {},
            onOpenTerminal = {},
            onOpenControls = {},
            onSaveControls = {},
        )
    }
}
