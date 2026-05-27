package br.com.laboratoriodecircuitos.lablink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DrawerBg = Color(0xFF202126)
private val WhiteSoft = Color(0xFFFFFFFF)

@Composable
fun LabLinkDrawer(
    onClose: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenBluetooth: () -> Unit,
    onOpenControls: () -> Unit,
    onOpenTerminal: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.52f))
            .clickable { onClose() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.82f)
                .background(DrawerBg)
                .clickable(enabled = false) {}
                .padding(horizontal = 24.dp)
                .padding(top = 54.dp, bottom = 24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "LabLink",
                    color = WhiteSoft,
                    fontSize = 28.sp,
                    lineHeight = 34.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = WhiteSoft,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onClose() },
                )
            }

            Spacer(modifier = Modifier.height(54.dp))

            DrawerItem(Icons.Rounded.Home, "Início", onOpenHome)
            DrawerItem(Icons.Rounded.Bluetooth, "Bluetooth", onOpenBluetooth)
            DrawerItem(Icons.Rounded.Tune, "Controles", onOpenControls)
            DrawerItem(Icons.Rounded.Terminal, "Terminal", onOpenTerminal)
            DrawerItem(Icons.Rounded.Settings, "Configurações", onClose)
            DrawerItem(Icons.Rounded.HelpOutline, "Ajuda", onClose)
            DrawerItem(Icons.Rounded.Info, "Sobre", onClose)
        }
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = WhiteSoft.copy(alpha = 0.88f),
            modifier = Modifier.size(21.dp),
        )

        Text(
            text = label,
            color = WhiteSoft.copy(alpha = 0.92f),
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
