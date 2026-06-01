package br.com.laboratoriodecircuitos.lablink.features.mycontrols

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.laboratoriodecircuitos.lablink.core.controls.CustomControl
import br.com.laboratoriodecircuitos.lablink.ui.components.LabLinkScreenChrome

private val CardDark = Color(0xFF151515)
private val WhiteSoft = Color(0xFFFFFFFF)
private val TextDim = Color(0xFFC4C7C8)
private val Muted = Color(0xFF8E9192)
private val BorderSoft = Color.White.copy(alpha = 0.10f)
private val AccentPurple = Color(0xFFE5BEFF)

@Composable
fun MyControlsScreen(
    controls: List<CustomControl>,
    isBluetoothConnected: Boolean,
    onOpenControl: (CustomControl) -> Unit,
    onBack: () -> Unit,
    onCreateControl: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenConnection: () -> Unit,
    onOpenControls: () -> Unit,
    onOpenTerminal: () -> Unit,
) {
    LabLinkScreenChrome(
        isBluetoothConnected = isBluetoothConnected,
        onOpenHome = onOpenHome,
        onOpenConnection = onOpenConnection,
        onOpenControls = onOpenControls,
        onOpenTerminal = onOpenTerminal,
        title = "Meus controles",
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 104.dp, start = 16.dp, end = 16.dp, bottom = 84.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    text = "MY CONTROLS",
                    color = TextDim,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp),
                )
            }

            items(controls, key = { it.id }) { control ->
                ControlCard(
                    control = control,
                    onClick = { onOpenControl(control) },
                )
            }

            item {
                CreateControlCard(onClick = onCreateControl)
            }
        }

        StitchBottomNav(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun ControlCard(
    control: CustomControl,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(196.dp)
            .background(CardDark, RoundedCornerShape(20.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(28.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(Color.White.copy(alpha = 0.14f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Memory,
                    contentDescription = null,
                    tint = WhiteSoft,
                    modifier = Modifier.size(28.dp),
                )
            }

            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = null,
                tint = TextDim,
                modifier = Modifier.size(27.dp),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = control.name.ifBlank { "Untitled Control" },
                color = WhiteSoft,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "Arduino Uno • ${control.widgets.size} widget${if (control.widgets.size == 1) "" else "s"}",
                color = TextDim,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun CreateControlCard(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(196.dp)
            .dashedBorder(AccentPurple.copy(alpha = 0.52f), 20.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = null,
            tint = AccentPurple,
            modifier = Modifier.size(34.dp),
        )
        Text(
            text = "Create Control",
            color = AccentPurple,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun Modifier.dashedBorder(color: Color, radius: androidx.compose.ui.unit.Dp): Modifier {
    return drawBehind {
        drawRoundRect(
            color = color,
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = CornerRadius(radius.toPx(), radius.toPx()),
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12.dp.toPx(), 8.dp.toPx())),
            ),
        )
    }
}

@Composable
private fun StitchBottomNav(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xFF141313))
            .border(1.dp, BorderSoft)
            .padding(horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        BottomNavItem(Icons.Rounded.Dashboard, "Dashboard", WhiteSoft)
        BottomNavItem(Icons.Rounded.Bluetooth, "Devices", Muted)
        BottomNavItem(Icons.Rounded.Person, "Profile", Muted)
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    tint: Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(25.dp),
        )
        Text(
            text = label,
            color = tint,
            fontSize = 12.sp,
            lineHeight = 15.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
        )
    }
}
