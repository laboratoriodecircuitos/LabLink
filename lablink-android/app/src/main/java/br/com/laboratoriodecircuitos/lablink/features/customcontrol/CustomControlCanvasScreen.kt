package br.com.laboratoriodecircuitos.lablink.features.customcontrol

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardPinValidator
import br.com.laboratoriodecircuitos.lablink.core.controls.ControlType
import br.com.laboratoriodecircuitos.lablink.core.controls.CustomControl
import br.com.laboratoriodecircuitos.lablink.core.controls.LabLinkControl
import br.com.laboratoriodecircuitos.lablink.ui.components.LabLinkScreenChrome

private val BgDeep = Color(0xFF000000)
private val CardDark = Color(0xFF151515)
private val WidgetDark = Color(0xFF202124)
private val WhiteSoft = Color(0xFFFFFFFF)
private val TextDim = Color(0xFF8A8A8A)
private val BorderSoft = Color.White.copy(alpha = 0.08f)
private val AccentGreen = Color(0xFFC4FA8C)
private val AccentPurple = Color(0xFFE5BEFF)
private val AccentYellow = Color(0xFFFFE382)

@Composable
fun CustomControlCanvasScreen(
    control: CustomControl,
    isEditing: Boolean,
    isBluetoothConnected: Boolean,
    validationMessage: String?,
    lastReceivedMessage: String,
    onNameChange: (String) -> Unit,
    onAddWidget: (ControlType) -> Unit,
    onSaveControl: () -> Unit,
    onEditControl: () -> Unit,
    onMoveWidget: (LabLinkControl, Int, Int) -> Unit,
    onToggleDigitalControl: (LabLinkControl, Boolean) -> Unit,
    onSendPwmControl: (LabLinkControl, Int) -> Unit,
    onSendServoControl: (LabLinkControl, Int) -> Unit,
    onSendPulseControl: (LabLinkControl) -> Unit,
    onReadAnalogControl: (LabLinkControl) -> Unit,
    onOpenHome: () -> Unit,
    onOpenConnection: () -> Unit,
    onOpenControls: () -> Unit,
    onOpenTerminal: () -> Unit,
) {
    var widgetListOpen by remember { mutableStateOf(false) }
    val analogValues = remember(control.id) { mutableStateMapOf<String, Int>() }

    BackHandler(enabled = widgetListOpen) {
        widgetListOpen = false
    }

    LaunchedEffect(lastReceivedMessage, control.widgets) {
        parseAnalogRead(lastReceivedMessage)?.let { (pin, value) ->
            control.widgets
                .filter { it.type == ControlType.AnalogRead }
                .filter { BoardPinValidator.normalizePin(it.pin) == pin }
                .forEach { analogValues[it.id] = value }
        }
    }

    LabLinkScreenChrome(
        isBluetoothConnected = isBluetoothConnected,
        onOpenHome = onOpenHome,
        onOpenConnection = onOpenConnection,
        onOpenControls = onOpenControls,
        onOpenTerminal = onOpenTerminal,
        title = if (isEditing) "Interface" else control.name.ifBlank { "Interface" },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDeep)
                .canvasGridBackground(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 96.dp),
            ) {
                CanvasTopBar(
                    name = control.name,
                    isEditing = isEditing,
                    validationMessage = validationMessage,
                    onNameChange = onNameChange,
                    onAdd = { widgetListOpen = true },
                    onEdit = onEditControl,
                )

                CanvasGrid(
                    widgets = control.widgets,
                    isEditing = isEditing,
                    isBluetoothConnected = isBluetoothConnected,
                    analogValues = analogValues,
                    onMoveWidget = onMoveWidget,
                    onToggleDigitalControl = onToggleDigitalControl,
                    onSendPwmControl = onSendPwmControl,
                    onSendServoControl = onSendServoControl,
                    onSendPulseControl = onSendPulseControl,
                    onReadAnalogControl = onReadAnalogControl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
            }

            if (isEditing && control.widgets.isNotEmpty()) {
                SaveControlButton(
                    enabled = control.name.isNotBlank(),
                    onClick = onSaveControl,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                )
            }

            AnimatedVisibility(
                visible = widgetListOpen,
                enter = fadeIn(animationSpec = tween(120)) + slideInHorizontally(
                    animationSpec = tween(220),
                    initialOffsetX = { it },
                ),
                exit = fadeOut(animationSpec = tween(120)) + slideOutHorizontally(
                    animationSpec = tween(200),
                    targetOffsetX = { it },
                ),
            ) {
                WidgetPickerDrawer(
                    onClose = { widgetListOpen = false },
                    onSelect = { type ->
                        widgetListOpen = false
                        onAddWidget(type)
                    },
                )
            }
        }
    }
}

@Composable
private fun CanvasTopBar(
    name: String,
    isEditing: Boolean,
    validationMessage: String?,
    onNameChange: (String) -> Unit,
    onAdd: () -> Unit,
    onEdit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (isEditing) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(88.dp)
                        .background(Color(0xFF242424), RoundedCornerShape(44.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(44.dp))
                        .padding(start = 20.dp, end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Tune,
                        contentDescription = null,
                        tint = TextDim.copy(alpha = 0.55f),
                        modifier = Modifier.size(23.dp),
                    )

                    BasicTextField(
                        value = name,
                        onValueChange = onNameChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            color = WhiteSoft,
                            fontSize = 20.sp,
                            lineHeight = 26.sp,
                            fontWeight = FontWeight.Normal,
                        ),
                        cursorBrush = SolidColor(AccentPurple),
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            if (name.isBlank()) {
                                Text(
                                    text = "New Interface",
                                    color = WhiteSoft.copy(alpha = 0.85f),
                                    fontSize = 20.sp,
                                    lineHeight = 26.sp,
                                )
                            }
                            innerTextField()
                        },
                    )

                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .background(AccentYellow, CircleShape)
                            .clickable { onAdd() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp),
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .height(88.dp)
                        .background(Color(0xFF242424), RoundedCornerShape(44.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(44.dp))
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.GraphicEq,
                        contentDescription = null,
                        tint = TextDim,
                        modifier = Modifier.size(24.dp),
                    )

                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = null,
                        tint = TextDim,
                        modifier = Modifier.size(23.dp),
                    )
                }
            }

            if (!validationMessage.isNullOrBlank()) {
                Text(
                    text = validationMessage,
                    color = AccentYellow,
                    fontSize = 12.sp,
                    lineHeight = 15.sp,
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = "CANVAS NAME",
                        color = TextDim,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = name.ifBlank { "Robot Base v1" },
                        color = WhiteSoft,
                        fontSize = 24.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                    )

                    Text(
                        text = "Modo de uso",
                        color = TextDim,
                        fontSize = 11.sp,
                        lineHeight = 13.sp,
                    )
                }

                IconCircle(
                    icon = Icons.Rounded.Settings,
                    tint = AccentPurple,
                    onClick = onEdit,
                )
            }
        }
    }
}

@Composable
private fun IconCircle(
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(tint.copy(alpha = 0.13f), CircleShape)
            .border(1.dp, tint.copy(alpha = 0.45f), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CanvasGrid(
    widgets: List<LabLinkControl>,
    isEditing: Boolean,
    isBluetoothConnected: Boolean,
    analogValues: Map<String, Int>,
    onMoveWidget: (LabLinkControl, Int, Int) -> Unit,
    onToggleDigitalControl: (LabLinkControl, Boolean) -> Unit,
    onSendPwmControl: (LabLinkControl, Int) -> Unit,
    onSendServoControl: (LabLinkControl, Int) -> Unit,
    onSendPulseControl: (LabLinkControl) -> Unit,
    onReadAnalogControl: (LabLinkControl) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val rowHeight = 126.dp
    val spacing = 10.dp
    val maxRows = (widgets.maxOfOrNull { it.gridY + it.heightUnits.coerceIn(1, 2) } ?: 4).coerceAtLeast(4)

    BoxWithConstraints(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp),
    ) {
        val cellWidth = (maxWidth - (spacing * 2f)) / 3f
        val canvasHeight = (rowHeight * maxRows.toFloat()) + (spacing * (maxRows - 1).toFloat()) + 86.dp

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(canvasHeight),
        ) {
            if (widgets.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.GraphicEq,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.12f),
                        modifier = Modifier.size(40.dp),
                    )
                    Text(
                        text = "EMPTY CANVAS",
                        color = Color.White.copy(alpha = 0.12f),
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            widgets.forEach { widget ->
                val widthUnits = widget.widthUnits.coerceIn(1, 3)
                val heightUnits = widget.heightUnits.coerceIn(1, 2)
                val gridX = widget.gridX.coerceIn(0, 3 - widthUnits)
                val gridY = widget.gridY.coerceAtLeast(0)
                val width = (cellWidth * widthUnits.toFloat()) + (spacing * (widthUnits - 1).toFloat())
                val height = (rowHeight * heightUnits.toFloat()) + (spacing * (heightUnits - 1).toFloat())

                CanvasWidget(
                    widget = widget,
                    isEditing = isEditing,
                    isBluetoothConnected = isBluetoothConnected,
                    analogValue = analogValues[widget.id],
                    onMoveWidget = onMoveWidget,
                    onToggleDigitalControl = onToggleDigitalControl,
                    onSendPwmControl = onSendPwmControl,
                    onSendServoControl = onSendServoControl,
                    onSendPulseControl = onSendPulseControl,
                    onReadAnalogControl = onReadAnalogControl,
                    modifier = Modifier
                        .offset(
                            x = (cellWidth + spacing) * gridX.toFloat(),
                            y = (rowHeight + spacing) * gridY.toFloat(),
                        )
                        .width(width)
                        .height(height),
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CanvasWidget(
    widget: LabLinkControl,
    isEditing: Boolean,
    isBluetoothConnected: Boolean,
    analogValue: Int?,
    onMoveWidget: (LabLinkControl, Int, Int) -> Unit,
    onToggleDigitalControl: (LabLinkControl, Boolean) -> Unit,
    onSendPwmControl: (LabLinkControl, Int) -> Unit,
    onSendServoControl: (LabLinkControl, Int) -> Unit,
    onSendPulseControl: (LabLinkControl) -> Unit,
    onReadAnalogControl: (LabLinkControl) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isOn by remember(widget.id) { mutableStateOf(widget.isOn) }
    var sliderValue by remember(widget.id) { mutableFloatStateOf((widget.currentValue ?: widget.minValue ?: 0).toFloat()) }
    var dragX by remember(widget.id) { mutableFloatStateOf(0f) }
    var dragY by remember(widget.id) { mutableFloatStateOf(0f) }
    val accent = widget.type.accentColor()
    val isPushButton = widget.type == ControlType.DigitalToggle && widget.mode == "PUSH"

    Column(
        modifier = modifier
            .background(CardDark, RoundedCornerShape(20.dp))
            .border(1.dp, if (isEditing) accent.copy(alpha = 0.48f) else BorderSoft, RoundedCornerShape(20.dp))
            .pointerInput(isEditing, widget.id) {
                if (isEditing) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = {
                            dragX = 0f
                            dragY = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragX += dragAmount.x
                            dragY += dragAmount.y

                            if (dragX > 52f) {
                                dragX = 0f
                                onMoveWidget(widget, 1, 0)
                            } else if (dragX < -52f) {
                                dragX = 0f
                                onMoveWidget(widget, -1, 0)
                            }

                            if (dragY > 52f) {
                                dragY = 0f
                                onMoveWidget(widget, 0, 1)
                            } else if (dragY < -52f) {
                                dragY = 0f
                                onMoveWidget(widget, 0, -1)
                            }
                        },
                    )
                }
            }
            .pointerInput(isPushButton, isBluetoothConnected, isEditing, widget.id) {
                if (isPushButton && isBluetoothConnected && !isEditing) {
                    detectTapGestures(
                        onPress = {
                            isOn = true
                            onToggleDigitalControl(widget, true)
                            try {
                                tryAwaitRelease()
                            } finally {
                                isOn = false
                                onToggleDigitalControl(widget, false)
                            }
                        },
                    )
                }
            }
            .combinedClickable(
                enabled = isBluetoothConnected && !isEditing && !isPushButton,
                onClick = {
                    when (widget.type) {
                        ControlType.DigitalToggle -> {
                            val newState = !isOn
                            isOn = newState
                            onToggleDigitalControl(widget, newState)
                        }
                        ControlType.PulseButton -> onSendPulseControl(widget)
                        ControlType.AnalogRead -> onReadAnalogControl(widget)
                        else -> Unit
                    }
                },
                onLongClick = {},
            )
            .padding(18.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = BoardPinValidator.normalizePin(widget.pin),
                color = TextDim,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
            )

            if (widget.type == ControlType.DigitalToggle) {
                Icon(
                    imageVector = Icons.Rounded.PowerSettingsNew,
                    contentDescription = null,
                    tint = if (isOn) AccentGreen else TextDim.copy(alpha = 0.45f),
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        when (widget.type) {
            ControlType.DigitalToggle -> BigValue(text = if (isOn) "ON" else "OFF", color = accent)
            ControlType.PulseButton -> BigIcon(icon = Icons.Rounded.Bolt, color = accent)
            ControlType.AnalogRead -> BigValue(text = analogValue?.toString() ?: widget.currentValue?.toString() ?: "--", color = WhiteSoft)
            ControlType.PwmSlider,
            ControlType.ServoSlider -> {
                Slider(
                    value = sliderValue,
                    onValueChange = {
                        sliderValue = it
                        if (!isEditing && isBluetoothConnected) {
                            if (widget.type == ControlType.PwmSlider) {
                                onSendPwmControl(widget, it.toInt())
                            } else {
                                onSendServoControl(widget, it.toInt())
                            }
                        }
                    },
                    enabled = !isEditing && isBluetoothConnected,
                    valueRange = (widget.minValue ?: 0).toFloat()..(widget.maxValue ?: 255).toFloat(),
                )
                BigValue(text = sliderValue.toInt().toString(), color = accent)
            }
        }

        Text(
            text = widget.name,
            color = WhiteSoft,
            fontSize = 16.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

@Composable
private fun BigValue(text: String, color: Color) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 28.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun BigIcon(icon: ImageVector, color: Color) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(44.dp),
        )
    }
}

@Composable
private fun SaveControlButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(if (enabled) AccentYellow else CardDark, RoundedCornerShape(32.dp))
            .clickable(enabled = enabled) { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.Check,
            contentDescription = null,
            tint = if (enabled) Color.Black else TextDim,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = "SALVAR CONTROLE",
            color = if (enabled) Color.Black else TextDim,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
private fun WidgetPickerDrawer(
    onClose: () -> Unit,
    onSelect: (ControlType) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.62f))
            .clickable { onClose() },
        contentAlignment = Alignment.CenterEnd,
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.84f)
                .background(CardDark)
                .clickable(enabled = false) {}
                .padding(top = 46.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Widgets",
                    color = AccentYellow,
                    fontSize = 25.sp,
                    lineHeight = 31.sp,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = TextDim,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onClose() },
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSoft),
            ) {
                item { WidgetGroupLabel("OUTPUTS") }
                items(listOf(ControlType.DigitalToggle, ControlType.PwmSlider, ControlType.ServoSlider)) { type ->
                    WidgetPickerRow(
                        type = type,
                        onClick = { onSelect(type) },
                    )
                }
                item { WidgetGroupLabel("INPUTS") }
                items(listOf(ControlType.AnalogRead, ControlType.PulseButton)) { type ->
                    WidgetPickerRow(
                        type = type,
                        onClick = { onSelect(type) },
                    )
                }
            }
        }
    }
}

@Composable
private fun WidgetGroupLabel(text: String) {
    Text(
        text = text,
        color = TextDim,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 24.dp, top = 34.dp, bottom = 18.dp),
    )
}

@Composable
private fun WidgetPickerRow(type: ControlType, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .background(CardDark)
            .border(1.dp, Color.White.copy(alpha = 0.06f))
            .clickable { onClick() }
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(type.accentColor().copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                .border(1.dp, type.accentColor().copy(alpha = 0.30f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = type.icon(),
                contentDescription = null,
                tint = type.accentColor(),
                modifier = Modifier.size(29.dp),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = type.canvasName(),
                color = WhiteSoft,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Normal,
            )
            Text(
                text = type.stitchDescription(),
                color = TextDim,
                fontSize = 15.sp,
                lineHeight = 20.sp,
            )
        }

        Text(
            text = ">",
            color = TextDim,
            fontSize = 34.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.Light,
        )
    }
}

private fun Modifier.canvasGridBackground(): Modifier {
    return drawBehind {
        val step = 16.dp.toPx()
        val major = step * 5f
        val dotColor = Color.White.copy(alpha = 0.05f)
        val lineColor = Color.White.copy(alpha = 0.032f)

        var x = 0f
        while (x <= size.width) {
            drawLine(lineColor, Offset(x, 0f), Offset(x, size.height), 1f)
            x += major
        }

        var y = 0f
        while (y <= size.height) {
            drawLine(lineColor, Offset(0f, y), Offset(size.width, y), 1f)
            y += major
        }

        var dotX = 0f
        while (dotX <= size.width) {
            var dotY = 0f
            while (dotY <= size.height) {
                drawCircle(dotColor, 1f, Offset(dotX, dotY))
                dotY += step
            }
            dotX += step
        }
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

private fun ControlType.canvasName(): String {
    return when (this) {
        ControlType.DigitalToggle -> "BUTTON"
        ControlType.PwmSlider -> "SLIDER PWM"
        ControlType.ServoSlider -> "SERVO"
        ControlType.PulseButton -> "PULSE"
        ControlType.AnalogRead -> "ANALOG READ"
    }
}

private fun ControlType.stitchDescription(): String {
    return when (this) {
        ControlType.DigitalToggle -> "Digital On/Off"
        ControlType.PwmSlider -> "0-255 Analog output"
        ControlType.ServoSlider -> "Angle control 0-180"
        ControlType.PulseButton -> "Timed pulse"
        ControlType.AnalogRead -> "Monitor sensors"
    }
}

private fun parseAnalogRead(message: String): Pair<String, Int>? {
    if (!message.startsWith("OK:READ:")) {
        return null
    }

    val parts = message.split(":")
    if (parts.size < 4) {
        return null
    }

    val pin = BoardPinValidator.normalizePin(parts[2])
    val value = parts[3].toIntOrNull() ?: return null
    return pin to value
}
