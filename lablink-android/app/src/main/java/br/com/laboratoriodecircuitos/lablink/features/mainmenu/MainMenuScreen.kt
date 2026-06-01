package br.com.laboratoriodecircuitos.lablink.features.mainmenu

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddBox
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardProfile
import br.com.laboratoriodecircuitos.lablink.ui.components.LabLinkScreenChrome

private val CardDark = Color(0xFF151515)
private val WhiteSoft = Color(0xFFFFFFFF)
private val TextDim = Color(0xFFC4C7C8)
private val Muted = Color(0xFF8E9192)
private val BorderSoft = Color.White.copy(alpha = 0.10f)
private val AccentGreen = Color(0xFFC4FA8C)
private val AccentPurple = Color(0xFFE5BEFF)
private val AccentYellow = Color(0xFFFFE382)
private val AccentBlue = Color(0xFF82CFFF)

@Composable
fun MainMenuScreen(
    isBluetoothConnected: Boolean,
    selectedBoard: BoardProfile?,
    onNewCustomControl: () -> Unit,
    onMyControls: () -> Unit,
    onJoystick: () -> Unit,
    onCommunity: () -> Unit,
    onChangeBoard: () -> Unit,
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
        title = "Controles",
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 104.dp, start = 16.dp, end = 16.dp, bottom = 86.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Control your project",
                    color = WhiteSoft,
                    fontSize = 30.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "${selectedBoard?.displayName ?: "Arduino"} selected",
                        color = TextDim,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                    )

                    Text(
                        text = "Change board",
                        color = AccentBlue,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onChangeBoard() },
                    )
                }
            }

            MenuCard(
                title = "New Custom Control",
                subtitle = "Build your own UI",
                icon = Icons.Rounded.AddBox,
                accent = AccentPurple,
                onClick = onNewCustomControl,
            )

            MenuCard(
                title = "My Controls",
                subtitle = "Your saved projects",
                icon = Icons.Rounded.Dashboard,
                accent = AccentYellow,
                onClick = onMyControls,
            )

            MenuCard(
                title = "Joystick",
                subtitle = "Drive your robot",
                icon = Icons.Rounded.SportsEsports,
                accent = AccentGreen,
                onClick = onJoystick,
            )

            MenuCard(
                title = "Community",
                subtitle = "Join the makers",
                icon = Icons.Rounded.Groups,
                accent = AccentBlue,
                onClick = onCommunity,
            )
        }

        StitchBottomNav(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun MenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(CardDark, RoundedCornerShape(24.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(accent.copy(alpha = 0.12f), CircleShape)
                .border(1.dp, accent.copy(alpha = 0.30f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(28.dp),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = title,
                color = accent,
                fontSize = 21.sp,
                lineHeight = 27.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                text = subtitle,
                color = TextDim,
                fontSize = 15.sp,
                lineHeight = 20.sp,
            )
        }
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
