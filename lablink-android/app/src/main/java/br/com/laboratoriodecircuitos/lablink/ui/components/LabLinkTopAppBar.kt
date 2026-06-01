package br.com.laboratoriodecircuitos.lablink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
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
            .height(80.dp)
            .background(BgDeep)
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = null,
                tint = WhiteSoft.copy(alpha = 0.82f),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(28.dp)
                    .clickable { onOpenDrawer() },
            )

            Text(
                text = title,
                color = WhiteSoft,
                fontSize = 20.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .height(48.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                LabLinkBluetoothStatusIcon(isBluetoothConnected)
            }
        }
    }
}

