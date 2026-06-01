package br.com.laboratoriodecircuitos.lablink.features.joystick

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.laboratoriodecircuitos.lablink.ui.components.LabLinkScreenChrome

private val CardDark = Color(0xFF151515)
private val WhiteSoft = Color(0xFFFFFFFF)
private val TextDim = Color(0xFF8A8A8A)
private val BorderSoft = Color.White.copy(alpha = 0.08f)
private val AccentGreen = Color(0xFFC4FA8C)
private val AccentPurple = Color(0xFFE5BEFF)
private val AccentYellow = Color(0xFFFFE382)

@Composable
fun JoystickScreen(
    isBluetoothConnected: Boolean,
    onBack: () -> Unit,
    onSendCommand: (String) -> Unit,
    onOpenHome: () -> Unit,
    onOpenConnection: () -> Unit,
    onOpenControls: () -> Unit,
    onOpenTerminal: () -> Unit,
) {
    val activity = LocalContext.current as? Activity

    DisposableEffect(activity) {
        val previousOrientation = activity?.requestedOrientation
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        onDispose {
            activity?.requestedOrientation = previousOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    var speed by remember { mutableFloatStateOf(128f) }
    var editingButton by remember { mutableIntStateOf(0) }
    var buttonIcons by remember {
        mutableStateOf(
            listOf(
                Icons.Rounded.PowerSettingsNew,
                Icons.Rounded.Lightbulb,
                Icons.Rounded.Bolt,
                Icons.Rounded.Star,
            ),
        )
    }

    LabLinkScreenChrome(
        isBluetoothConnected = isBluetoothConnected,
        onOpenHome = onOpenHome,
        onOpenConnection = onOpenConnection,
        onOpenControls = onOpenControls,
        onOpenTerminal = onOpenTerminal,
        title = "Joystick",
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 96.dp, start = 18.dp, end = 18.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                IconButtonCircle(
                    icon = Icons.Rounded.ArrowBack,
                    accent = WhiteSoft,
                    onClick = onBack,
                    size = 42,
                )

                Text(
                    text = "Joystick",
                    color = WhiteSoft,
                    fontSize = 21.sp,
                    lineHeight = 25.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DirectionPad(
                    onSendCommand = onSendCommand,
                    modifier = Modifier.weight(1f),
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SpeedPanel(
                        speed = speed,
                        onChange = {
                            speed = it
                            onSendCommand("JOY:SPEED:${it.toInt()}")
                        },
                    )

                    ActionButtons(
                        buttonIcons = buttonIcons,
                        onButton = { index -> onSendCommand("JOY:B$index") },
                        onEdit = { index -> editingButton = index },
                    )

                    if (editingButton != 0) {
                        IconPalette(
                            onSelect = { icon ->
                                buttonIcons = buttonIcons.toMutableList().also {
                                    it[editingButton - 1] = icon
                                }
                                editingButton = 0
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DirectionPad(
    onSendCommand: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(CardDark, RoundedCornerShape(14.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(14.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DirectionButton("UL", "JOY:UP_LEFT", onSendCommand)
            DirectionButton("UP", "JOY:UP", onSendCommand)
            DirectionButton("UR", "JOY:UP_RIGHT", onSendCommand)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(vertical = 12.dp)) {
            DirectionButton("LT", "JOY:LEFT", onSendCommand)
            IconButtonCircle(Icons.Rounded.DirectionsCar, AccentPurple, onClick = {}, size = 68)
            DirectionButton("RT", "JOY:RIGHT", onSendCommand)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DirectionButton("DL", "JOY:DOWN_LEFT", onSendCommand)
            DirectionButton("DN", "JOY:DOWN", onSendCommand)
            DirectionButton("DR", "JOY:DOWN_RIGHT", onSendCommand)
        }
    }
}

@Composable
private fun DirectionButton(
    label: String,
    command: String,
    onSendCommand: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .size(68.dp)
            .background(Color.White.copy(alpha = 0.05f), CircleShape)
            .border(1.dp, BorderSoft, CircleShape)
            .combinedClickable(
                onClick = { onSendCommand(command) },
                onLongClick = { onSendCommand(command) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = WhiteSoft,
            fontSize = 14.sp,
            lineHeight = 17.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun SpeedPanel(
    speed: Float,
    onChange: (Float) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(10.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "ACELERADOR PWM ${speed.toInt()}",
            color = WhiteSoft,
            fontSize = 12.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        Slider(
            value = speed,
            onValueChange = onChange,
            valueRange = 0f..255f,
        )
    }
}

@Composable
private fun ActionButtons(
    buttonIcons: List<ImageVector>,
    onButton: (Int) -> Unit,
    onEdit: (Int) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            JoystickActionButton(1, buttonIcons[0], { onButton(1) }, { onEdit(1) }, Modifier.weight(1f))
            JoystickActionButton(2, buttonIcons[1], { onButton(2) }, { onEdit(2) }, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            JoystickActionButton(3, buttonIcons[2], { onButton(3) }, { onEdit(3) }, Modifier.weight(1f))
            JoystickActionButton(4, buttonIcons[3], { onButton(4) }, { onEdit(4) }, Modifier.weight(1f))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun JoystickActionButton(
    index: Int,
    icon: ImageVector,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(82.dp)
            .background(CardDark, RoundedCornerShape(12.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentYellow,
            modifier = Modifier.size(29.dp),
        )
        Text(
            text = "B$index",
            color = TextDim,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun IconPalette(onSelect: (ImageVector) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(8.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        listOf(
            Icons.Rounded.PowerSettingsNew,
            Icons.Rounded.Lightbulb,
            Icons.Rounded.Bolt,
            Icons.Rounded.Star,
            Icons.Rounded.Speed,
            Icons.Rounded.WbSunny,
            Icons.Rounded.Circle,
        ).forEach { icon ->
            IconButtonCircle(
                icon = icon,
                accent = AccentPurple,
                onClick = { onSelect(icon) },
                size = 42,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun IconButtonCircle(
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
    size: Int,
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(accent.copy(alpha = 0.12f), CircleShape)
            .border(1.dp, accent.copy(alpha = 0.42f), CircleShape)
            .combinedClickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size((size * 0.50f).dp),
        )
    }
}
