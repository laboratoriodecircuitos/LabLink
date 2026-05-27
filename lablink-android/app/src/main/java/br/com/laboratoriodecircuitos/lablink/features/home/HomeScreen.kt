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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Terminal
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
import br.com.laboratoriodecircuitos.lablink.ui.theme.LabLinkTheme
import br.com.laboratoriodecircuitos.lablink.ui.components.LabLinkTopAppBar
import br.com.laboratoriodecircuitos.lablink.ui.components.LabLinkDrawer

private val BgDeep = Color(0xFF000000)
private val DrawerBg = Color(0xFF202126)
private val CardDark = Color(0xFF151515)
private val AccentYellow = Color(0xFFFFE382)
private val AccentPurple = Color(0xFFE5BEFF)
private val AccentGreen = Color(0xFFC4FA8C)
private val AccentBlue = Color(0xFF4CD6FB)
private val TextDim = Color(0xFF8A8A8A)
private val WhiteSoft = Color(0xFFFFFFFF)
private val BorderSoft = Color.White.copy(alpha = 0.06f)

@Composable
fun LabLinkHomeScreen(
    isBluetoothConnected: Boolean,
    onOpenHome: () -> Unit,
    onOpenConnection: () -> Unit,
    onOpenTerminal: () -> Unit,
    onOpenControls: () -> Unit,
) {
    var drawerOpen by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

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
                    .padding(top = 112.dp, bottom = 96.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    GreetingSection()

                    HomeActionCard(
                        title = "Conectar dispositivo",
                        subtitle = "Buscar módulos HC-05 ou HC-06 pareados e abrir a comunicação.",
                        icon = Icons.Rounded.Bluetooth,
                        backgroundColor = AccentPurple,
                        contentColor = Color.Black,
                        onClick = onOpenConnection,
                    )

                    HomeActionCard(
                        title = "Início rápido",
                        subtitle = "Conecte, envie PING e teste o LED do Arduino em poucos passos.",
                        icon = Icons.Rounded.Tune,
                        backgroundColor = AccentYellow,
                        contentColor = Color.Black,
                        onClick = onOpenControls,
                    )

                    HomeActionCard(
                        title = "Explorar exemplos",
                        subtitle = "Veja ideias como PING/PONG, LED ON/OFF, PWM, servo e motores.",
                        icon = Icons.Rounded.Terminal,
                        backgroundColor = CardDark,
                        contentColor = WhiteSoft,
                        border = true,
                        onClick = onOpenTerminal,
                    )

                    HomeActionCard(
                        title = "Criar novo controle",
                        subtitle = "Em breve, monte interfaces próprias para controlar seus projetos.",
                        icon = Icons.Rounded.Add,
                        backgroundColor = AccentGreen,
                        contentColor = Color.Black,
                        faded = true,
                        onClick = onOpenControls,
                    )
                }
            }

            LabLinkTopAppBar(
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
            .padding(top = 4.dp, bottom = 6.dp),
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
            fontWeight = FontWeight.Normal,
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
    border: Boolean = false,
    faded: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(172.dp)
            .alpha(if (faded) 0.82f else 1f)
            .background(backgroundColor, RoundedCornerShape(24.dp))
            .then(
                if (border) {
                    Modifier.border(1.dp, BorderSoft, RoundedCornerShape(24.dp))
                } else {
                    Modifier
                }
            )
            .clickable { onClick() }
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor.copy(alpha = 0.88f),
            modifier = Modifier.size(34.dp),
        )

        Column {
            Text(
                text = title,
                color = contentColor,
                fontSize = 26.sp,
                lineHeight = 31.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.35).sp,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = subtitle,
                color = contentColor.copy(alpha = if (contentColor == Color.Black) 0.70f else 0.78f),
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LabLinkHomeScreenPreview() {
    LabLinkTheme {
        LabLinkHomeScreen(
            isBluetoothConnected = true,
            onOpenHome = {},
            onOpenConnection = {},
            onOpenTerminal = {},
            onOpenControls = {},
        )
    }
}




