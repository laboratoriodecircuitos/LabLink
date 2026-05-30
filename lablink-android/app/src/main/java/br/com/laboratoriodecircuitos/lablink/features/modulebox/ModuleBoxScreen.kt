package br.com.laboratoriodecircuitos.lablink.features.modulebox

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardProfile
import br.com.laboratoriodecircuitos.lablink.core.controls.ControlType

private val BgDeep = Color(0xFF000000)
private val CardDark = Color(0xFF151515)
private val WhiteSoft = Color(0xFFF7F7F7)
private val TextDim = Color(0xFF8A8A8A)
private val BorderSoft = Color.White.copy(alpha = 0.08f)
private val AccentGreen = Color(0xFFC4FA8C)
private val AccentYellow = Color(0xFFFFE16A)
private val AccentPurple = Color(0xFFE5BEFF)

@Composable
fun ModuleBoxScreen(
    isBluetoothConnected: Boolean,
    selectedBoard: BoardProfile?,
    onBack: () -> Unit,
    onSelectModule: (ControlType) -> Unit,
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BgDeep,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .moduleBoxGridBackground()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            ModuleBoxTopBar(
                isBluetoothConnected = isBluetoothConnected,
                onBack = onBack,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "Caixa de módulos",
                    color = WhiteSoft,
                    fontSize = 30.sp,
                    lineHeight = 34.sp,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = selectedBoard?.displayName ?: "Selecione uma placa antes de montar o painel.",
                    color = if (selectedBoard != null) AccentGreen else TextDim,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            ModuleSectionTitle(title = "Saídas")

            ModuleOptionCard(
                title = "Liga / Desliga",
                subtitle = "Botão ON/OFF para saída digital",
                icon = Icons.Rounded.PowerSettingsNew,
                accentColor = AccentGreen,
                onClick = { onSelectModule(ControlType.DigitalToggle) },
            )

            ModuleOptionCard(
                title = "Slider PWM",
                subtitle = "Controle de intensidade de 0 a 255",
                icon = Icons.Rounded.Tune,
                accentColor = AccentPurple,
                onClick = { onSelectModule(ControlType.PwmSlider) },
            )

            ModuleOptionCard(
                title = "Pulso",
                subtitle = "Acionamento rápido por tempo definido",
                icon = Icons.Rounded.Bolt,
                accentColor = AccentYellow,
                onClick = { onSelectModule(ControlType.PulseButton) },
            )

            ModuleOptionCard(
                title = "Servo",
                subtitle = "Controle de ângulo para servo motor",
                icon = Icons.Rounded.Speed,
                accentColor = AccentPurple,
                onClick = { onSelectModule(ControlType.ServoSlider) },
            )

            ModuleSectionTitle(title = "Entradas")

            ModuleOptionCard(
                title = "Leitura analógica",
                subtitle = "Exibe o valor lido em uma entrada A0-A5",
                icon = Icons.Rounded.GraphicEq,
                accentColor = AccentGreen,
                onClick = { onSelectModule(ControlType.AnalogRead) },
            )

            Spacer(modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun ModuleBoxTopBar(
    isBluetoothConnected: Boolean,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier
                .background(CardDark, RoundedCornerShape(999.dp))
                .clickable { onBack() }
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = null,
                tint = WhiteSoft,
                modifier = Modifier.size(18.dp),
            )

            Text(
                text = "Painel",
                color = WhiteSoft,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Row(
            modifier = Modifier
                .background(CardDark, RoundedCornerShape(999.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(if (isBluetoothConnected) AccentGreen else TextDim, CircleShape),
            )

            Text(
                text = if (isBluetoothConnected) "Conectado" else "Desconectado",
                color = WhiteSoft,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun ModuleSectionTitle(
    title: String,
) {
    Text(
        text = title.uppercase(),
        color = TextDim,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
    )
}

@Composable
private fun ModuleOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark.copy(alpha = 0.94f), RoundedCornerShape(24.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(accentColor.copy(alpha = 0.16f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = title,
                color = WhiteSoft,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                text = subtitle,
                color = TextDim,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
        }
    }
}

private fun Modifier.moduleBoxGridBackground(): Modifier {
    return drawBehind {
        val step = 16.dp.toPx()
        val dotColor = Color.White.copy(alpha = 0.045f)

        var x = 0f
        while (x <= size.width) {
            var y = 0f
            while (y <= size.height) {
                drawCircle(
                    color = dotColor,
                    radius = 1.05f,
                    center = Offset(x, y),
                )
                y += step
            }
            x += step
        }
    }
}
