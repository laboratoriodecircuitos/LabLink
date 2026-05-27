package br.com.laboratoriodecircuitos.lablink.features.createcontrol

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
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.Speed
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
import br.com.laboratoriodecircuitos.lablink.core.controls.ControlType
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
fun CreateControlScreen(
    isBluetoothConnected: Boolean,
    onOpenHome: () -> Unit,
    onOpenConnection: () -> Unit,
    onOpenTerminal: () -> Unit,
    onOpenControls: () -> Unit,
) {
    val scrollState = rememberScrollState()
    var drawerOpen by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf<ControlType?>(null) }

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
                    HeaderSection()

                    ControlType.values().forEach { type ->
                        ControlTypeCard(
                            type = type,
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                        )
                    }

                    NextStepCard(selectedType = selectedType)
                }
            }

            LabLinkTopAppBar(
                title = "Criar controle",
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
private fun HeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp),
    ) {
        Text(
            text = "Que controle você quer criar?",
            color = WhiteSoft,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.4).sp,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Escolha o tipo de controle que será usado no seu projeto Arduino.",
            color = WhiteSoft.copy(alpha = 0.78f),
            fontSize = 16.sp,
            lineHeight = 24.sp,
        )
    }
}

@Composable
private fun ControlTypeCard(
    type: ControlType,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val accent = when (type) {
        ControlType.DigitalToggle -> AccentYellow
        ControlType.PwmSlider -> AccentPurple
        ControlType.ServoSlider -> AccentGreen
        ControlType.PulseButton -> AccentYellow
        ControlType.AnalogRead -> AccentPurple
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (selected) accent else CardDark,
                shape = RoundedCornerShape(24.dp),
            )
            .border(
                width = 1.dp,
                color = if (selected) accent.copy(alpha = 0.75f) else BorderSoft,
                shape = RoundedCornerShape(24.dp),
            )
            .clickable { onClick() }
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(
                    color = if (selected) Color.Black.copy(alpha = 0.10f) else accent.copy(alpha = 0.16f),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = type.icon(),
                contentDescription = null,
                tint = if (selected) Color.Black else accent,
                modifier = Modifier.size(25.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = type.displayName,
                color = if (selected) Color.Black else WhiteSoft,
                fontSize = 18.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = type.description,
                color = if (selected) Color.Black.copy(alpha = 0.68f) else TextDim,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
        }

        if (selected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun NextStepCard(selectedType: ControlType?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (selectedType == null) 0.52f else 1f)
            .background(CardDark, RoundedCornerShape(22.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(22.dp))
            .padding(18.dp),
    ) {
        Text(
            text = if (selectedType == null) {
                "Selecione um tipo para continuar"
            } else {
                "Próxima etapa"
            },
            color = WhiteSoft,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (selectedType == null) {
                "Depois disso, o LabLink vai perguntar a quantidade de controles e os pinos usados."
            } else {
                "Na próxima etapa, vamos definir quantos controles ${selectedType.displayName} você quer criar."
            },
            color = TextDim,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
    }
}

private fun ControlType.icon(): ImageVector {
    return when (this) {
        ControlType.DigitalToggle -> Icons.Rounded.PowerSettingsNew
        ControlType.PwmSlider -> Icons.Rounded.Tune
        ControlType.ServoSlider -> Icons.Rounded.Speed
        ControlType.PulseButton -> Icons.Rounded.Bolt
        ControlType.AnalogRead -> Icons.Rounded.GraphicEq
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateControlScreenPreview() {
    LabLinkTheme {
        CreateControlScreen(
            isBluetoothConnected = true,
            onOpenHome = {},
            onOpenConnection = {},
            onOpenTerminal = {},
            onOpenControls = {},
        )
    }
}
