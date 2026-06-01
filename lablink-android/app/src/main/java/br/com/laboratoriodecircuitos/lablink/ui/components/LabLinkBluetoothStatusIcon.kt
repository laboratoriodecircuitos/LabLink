package br.com.laboratoriodecircuitos.lablink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val AccentGreen = Color(0xFFC4FA8C)
private val TextDim = Color(0xFF8E9192)

@Composable
fun LabLinkBluetoothStatusIcon(
    isBluetoothConnected: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 34.dp,
) {
    Row(
        modifier = modifier
            .size(width = if (isBluetoothConnected) size + 12.dp else size, height = size),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        if (isBluetoothConnected) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(AccentGreen, CircleShape),
            )
        }

        Icon(
            imageVector = Icons.Rounded.Bluetooth,
            contentDescription = null,
            tint = if (isBluetoothConnected) AccentGreen else TextDim,
            modifier = Modifier.size(size * 0.78f),
        )
    }
}
