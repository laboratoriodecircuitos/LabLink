package br.com.laboratoriodecircuitos.lablink.features.home

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.SettingsInputComponent
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.laboratoriodecircuitos.lablink.ui.theme.LabLinkTheme

private val BgDeep = Color(0xFF000000)
private val CardDark = Color(0xFF151515)
private val AccentYellow = Color(0xFFFFE382)
private val AccentPurple = Color(0xFFE5BEFF)
private val AccentGreen = Color(0xFFC4FA8C)
private val AccentOrange = Color(0xFFFFB87A)
private val TextDim = Color(0xFF8A8A8A)
private val WhiteSoft = Color(0xFFFFFFFF)
private val BorderSoft = Color.White.copy(alpha = 0.05f)
private val BorderMedium = Color.White.copy(alpha = 0.10f)

@Composable
fun LabLinkHomeScreen(
    onOpenConnection: () -> Unit,
    onOpenTerminal: () -> Unit,
    onOpenControls: () -> Unit,
) {
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
                    .padding(top = 96.dp, bottom = 96.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    HeroCard()

                    MainActionsSection(
                        onOpenConnection = onOpenConnection,
                        onOpenControls = onOpenControls,
                    )

                    ToolsSection(
                        onOpenTerminal = onOpenTerminal,
                    )

                    RoadmapSection()
                }
            }

            TopAppBar()

            BottomNavBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                onHome = {},
                onBluetooth = onOpenConnection,
                onMore = onOpenTerminal,
            )
        }
    }
}

@Composable
private fun TopAppBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .background(BgDeep.copy(alpha = 0.94f))
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Home,
                    contentDescription = null,
                    tint = WhiteSoft,
                    modifier = Modifier.size(24.dp),
                )

                Text(
                    text = "LabLink",
                    color = WhiteSoft,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.2).sp,
                )
            }

            Row(
                modifier = Modifier
                    .background(CardDark, RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(AccentGreen, CircleShape),
                )

                Text(
                    text = "MVP ativo",
                    color = WhiteSoft,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun HeroCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(28.dp))
            .padding(20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(AccentYellow, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.SettingsInputComponent,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp),
                )
            }

            Column {
                Text(
                    text = "Arduino via Bluetooth",
                    color = WhiteSoft,
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Controle projetos reais pelo celular",
                    color = TextDim,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "O LabLink conecta o Android a módulos HC-05/HC-06 para enviar comandos e receber respostas de projetos Arduino.",
            color = TextDim,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                .border(1.dp, BorderMedium, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = ">",
                color = AccentGreen,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
            )

            Text(
                text = "Bluetooth Classic • HC-06 • Arduino",
                color = TextDim,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

@Composable
private fun MainActionsSection(
    onOpenConnection: () -> Unit,
    onOpenControls: () -> Unit,
) {
    Column {
        SectionHeader(
            icon = Icons.Rounded.Tune,
            title = "Ações principais",
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ActionCard(
                title = "Conectar Bluetooth",
                subtitle = "Buscar, selecionar e conectar ao HC-06",
                icon = Icons.Rounded.Bluetooth,
                backgroundColor = AccentPurple,
                contentColor = Color.Black,
                iconBackground = Color.Black.copy(alpha = 0.10f),
                onClick = onOpenConnection,
            )

            ActionCard(
                title = "Abrir Controles",
                subtitle = "Enviar PING, ligar LED e desligar LED",
                icon = Icons.Rounded.Tune,
                backgroundColor = AccentYellow,
                contentColor = Color.Black,
                iconBackground = Color.Black.copy(alpha = 0.10f),
                onClick = onOpenControls,
            )
        }
    }
}

@Composable
private fun ToolsSection(
    onOpenTerminal: () -> Unit,
) {
    Column {
        SectionHeader(
            icon = Icons.Rounded.Terminal,
            title = "Ferramentas",
        )

        Spacer(modifier = Modifier.height(12.dp))

        ActionCard(
            title = "Terminal",
            subtitle = "Área futura para mensagens e comandos seriais",
            icon = Icons.Rounded.Terminal,
            backgroundColor = CardDark,
            contentColor = WhiteSoft,
            iconBackground = Color.White.copy(alpha = 0.05f),
            onClick = onOpenTerminal,
            faded = true,
        )
    }
}

@Composable
private fun RoadmapSection() {
    Column(
        modifier = Modifier.alpha(0.55f),
    ) {
        SectionHeader(
            icon = Icons.Rounded.MoreHoriz,
            title = "Próximos passos",
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardDark, RoundedCornerShape(20.dp))
                .border(1.dp, BorderSoft, RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "PWM, Servo e Motores",
                    color = WhiteSoft,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Novos blocos de controle serão adicionados após a base Bluetooth estar consolidada.",
                    color = TextDim,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                )
            }
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color,
    iconBackground: Color,
    onClick: () -> Unit,
    faded: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (faded) 0.65f else 1f)
            .background(backgroundColor, RoundedCornerShape(28.dp))
            .then(
                if (backgroundColor == CardDark) {
                    Modifier.border(1.dp, BorderSoft, RoundedCornerShape(28.dp))
                } else {
                    Modifier
                }
            )
            .clickable { onClick() }
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                color = contentColor,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                color = contentColor.copy(alpha = if (contentColor == Color.Black) 0.60f else 0.62f),
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconBackground, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun SectionHeader(
    icon: ImageVector,
    title: String,
) {
    Row(
        modifier = Modifier.padding(horizontal = 1.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextDim,
            modifier = Modifier.size(14.dp),
        )

        Text(
            text = title,
            color = TextDim,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun BottomNavBar(
    modifier: Modifier = Modifier,
    onHome: () -> Unit,
    onBluetooth: () -> Unit,
    onMore: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BgDeep.copy(alpha = 0.95f))
            .navigationBarsPadding(),
    ) {
        HorizontalDivider(
            color = Color.White.copy(alpha = 0.05f),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BottomNavItem(
                icon = Icons.Rounded.Home,
                label = "Início",
                selected = true,
                onClick = onHome,
            )

            BottomNavItem(
                icon = Icons.Rounded.Bluetooth,
                label = "Bluetooth",
                selected = false,
                onClick = onBluetooth,
            )

            BottomNavItem(
                icon = Icons.Rounded.MoreHoriz,
                label = "Mais",
                selected = false,
                onClick = onMore,
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .widthIn(min = 72.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(32.dp)
                .background(
                    color = if (selected) Color.White.copy(alpha = 0.10f) else Color.Transparent,
                    shape = RoundedCornerShape(999.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) WhiteSoft else TextDim,
                modifier = Modifier.size(24.dp),
            )
        }

        Text(
            text = label,
            color = if (selected) WhiteSoft else TextDim,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LabLinkHomeScreenPreview() {
    LabLinkTheme {
        LabLinkHomeScreen(
            onOpenConnection = {},
            onOpenTerminal = {},
            onOpenControls = {},
        )
    }
}
