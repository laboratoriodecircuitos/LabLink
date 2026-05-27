package br.com.laboratoriodecircuitos.lablink.features.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Check
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.laboratoriodecircuitos.lablink.ui.components.LabLinkDrawer
import br.com.laboratoriodecircuitos.lablink.ui.components.LabLinkTopAppBar
import br.com.laboratoriodecircuitos.lablink.ui.theme.LabLinkTheme

private val BgDeep = Color(0xFF000000)
private val CardDark = Color(0xFF151515)
private val AccentYellow = Color(0xFFFFE382)
private val AccentPurple = Color(0xFFE5BEFF)
private val AccentGreen = Color(0xFFC4FA8C)
private val TextDim = Color(0xFF8A8A8A)
private val WhiteSoft = Color(0xFFFFFFFF)
private val BorderSoft = Color.White.copy(alpha = 0.06f)

@Composable
fun LabLinkHomeScreen(
    isBluetoothConnected: Boolean,
    connectedDeviceName: String?,
    onOpenHome: () -> Unit,
    onOpenConnection: () -> Unit,
    onOpenTerminal: () -> Unit,
    onOpenControls: () -> Unit,
) {
    val scrollState = rememberScrollState()
    var drawerOpen by remember { mutableStateOf(false) }

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
                    .verticalScroll(scrollState)
                    .padding(top = 112.dp, bottom = 40.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    GreetingSection()

                    HomeActionCard(
                        title = if (isBluetoothConnected) "Dispositivo conectado" else "Conectar dispositivo",
                        subtitle = if (isBluetoothConnected) {
                            connectedDeviceName ?: "Módulo Bluetooth conectado"
                        } else {
                            "Buscar e conectar o módulo Bluetooth do seu projeto."
                        },
                        icon = if (isBluetoothConnected) Icons.Rounded.Check else Icons.Rounded.Bluetooth,
                        backgroundColor = if (isBluetoothConnected) AccentGreen else AccentPurple,
                        contentColor = Color.Black,
                        onClick = onOpenConnection,
                    )

                    HomeActionCard(
                        title = "Meu controle",
                        subtitle = "Controlar a saída configurada no Arduino.",
                        icon = Icons.Rounded.Tune,
                        backgroundColor = AccentYellow,
                        contentColor = Color.Black,
                        onClick = onOpenControls,
                    )

                    SmallHintCard(
                        text = if (isBluetoothConnected) {
                            "Tudo pronto para controlar seu projeto."
                        } else {
                            "Primeiro conecte o dispositivo. Depois acesse seu controle."
                        },
                    )
                }
            }

            LabLinkTopAppBar(
                title = "LabLink",
                isBluetoothConnected = isBluetoothConnected,
                onOpenDrawer = { drawerOpen = true },
            )

            AnimatedVisibility(
                visible = drawerOpen,
                enter = fadeIn(animationSpec = tween(180)) + slideInHorizontally(
                    animationSpec = tween(260),
                    initialOffsetX = { -it },
                ),
                exit = fadeOut(animationSpec = tween(160)) + slideOutHorizontally(
                    animationSpec = tween(220),
                    targetOffsetX = { -it },
                ),
            ) {
                LabLinkDrawer(
                    onClose = { drawerOpen = false },
                    onOpenHome = {
                        drawerOpen = false
                        onOpenHome()
                    },
                    onOpenBluetooth = {
                        drawerOpen = false
                        onOpenConnection()
                    },
                    onOpenControls = {
                        drawerOpen = false
                        onOpenControls()
                    },
                    onOpenTerminal = {
                        drawerOpen = false
                        onOpenTerminal()
                    },
                )
            }
        }
    }
}

@Composable
private fun GreetingSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp),
    ) {
        Text(
            text = "Oi Rafael,",
            color = WhiteSoft,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.4).sp,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Vamos dar mais controle ao seu projeto.",
            color = WhiteSoft.copy(alpha = 0.78f),
            fontSize = 16.sp,
            lineHeight = 24.sp,
        )
    }
}

@Composable
private fun HomeActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(178.dp)
            .background(backgroundColor, RoundedCornerShape(28.dp))
            .clickable { onClick() }
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(contentColor.copy(alpha = 0.10f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp),
            )
        }

        Column {
            Text(
                text = title,
                color = contentColor,
                fontSize = 28.sp,
                lineHeight = 33.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.45).sp,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = subtitle,
                color = contentColor.copy(alpha = 0.70f),
                fontSize = 16.sp,
                lineHeight = 22.sp,
            )
        }
    }
}

@Composable
private fun SmallHintCard(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(0.72f)
            .background(CardDark, RoundedCornerShape(20.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            color = TextDim,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LabLinkHomeScreenPreview() {
    LabLinkTheme {
        LabLinkHomeScreen(
            isBluetoothConnected = true,
            connectedDeviceName = "HC-06",
            onOpenHome = {},
            onOpenConnection = {},
            onOpenTerminal = {},
            onOpenControls = {},
        )
    }
}
