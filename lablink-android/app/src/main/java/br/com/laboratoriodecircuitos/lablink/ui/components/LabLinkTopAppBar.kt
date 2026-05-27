package br.com.laboratoriodecircuitos.lablink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BgDeep = Color(0xFF000000)
private val CardDark = Color(0xFF151515)
private val AccentGreen = Color(0xFFC4FA8C)
private val TextDim = Color(0xFF8A8A8A)
private val WhiteSoft = Color(0xFFFFFFFF)

@Composable
fun LabLinkTopAppBar(
    title: String = "LabLink",
    isBluetoothConnected: Boolean,
    onOpenDrawer: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .background(BgDeep.copy(alpha = 0.98f))
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(38.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = null,
                    tint = WhiteSoft,
                    modifier = Modifier
                        .size(26.dp)
                        .clickable { onOpenDrawer() },
                )

                Text(
                    text = title,
                    color = WhiteSoft,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.2).sp,
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = if (isBluetoothConnected) AccentGreen else CardDark,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Bluetooth,
                    contentDescription = null,
                    tint = if (isBluetoothConnected) BgDeep else TextDim,
                    modifier = Modifier.size(23.dp),
                )
            }
        }
    }
}

