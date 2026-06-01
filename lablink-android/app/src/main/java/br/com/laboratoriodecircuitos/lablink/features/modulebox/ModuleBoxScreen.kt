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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Info
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
private val TopBarDark = Color(0xFF050505)
private val PanelDark = Color(0xFF151515)
private val TileDark = Color(0xFF202124)
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDeep)
                .moduleBoxGridBackground(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .verticalScroll(scrollState)
                    .padding(top = 106.dp, start = 16.dp, end = 16.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                BoardBalanceStrip(
                    selectedBoard = selectedBoard,
                    isBluetoothConnected = isBluetoothConnected,
                )

                ModuleSectionTitle(title = "Saídas")

                ModuleOptionRow(
                    title = "Liga / Desliga",
                    subtitle = "Saída digital ON/OFF",
                    icon = Icons.Rounded.PowerSettingsNew,
                    accentColor = AccentGreen,
                    onClick = { onSelectModule(ControlType.DigitalToggle) },
                )

                ModuleOptionRow(
                    title = "Slider PWM",
                    subtitle = "Valor de 0 a 255",
                    icon = Icons.Rounded.Tune,
                    accentColor = AccentPurple,
                    onClick = { onSelectModule(ControlType.PwmSlider) },
                )

                ModuleOptionRow(
                    title = "Pulso",
                    subtitle = "Acionamento momentâneo",
                    icon = Icons.Rounded.Bolt,
                    accentColor = AccentYellow,
                    onClick = { onSelectModule(ControlType.PulseButton) },
                )

                ModuleOptionRow(
                    title = "Servo",
                    subtitle = "Ângulo de 0 a 180",
                    icon = Icons.Rounded.Speed,
                    accentColor = AccentPurple,
                    onClick = { onSelectModule(ControlType.ServoSlider) },
                )

                ModuleSectionTitle(title = "Entradas")

                ModuleOptionRow(
                    title = "Leitura analógica",
                    subtitle = "Entrada A0-A5",
                    icon = Icons.Rounded.GraphicEq,
                    accentColor = AccentGreen,
                    onClick = { onSelectModule(ControlType.AnalogRead) },
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            WidgetBoxTopBar(onBack = onBack)
        }
    }
}

@Composable
private fun WidgetBoxTopBar(
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TopBarDark.copy(alpha = 0.98f))
            .border(1.dp, BorderSoft)
            .statusBarsPadding()
            .height(104.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .height(58.dp)
                .size(82.dp)
                .background(PanelDark)
                .border(1.dp, BorderSoft)
                .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = null,
                tint = WhiteSoft,
                modifier = Modifier.size(33.dp),
            )
        }

        Text(
            text = "Caixa de módulos",
            color = WhiteSoft,
            fontSize = 26.sp,
            lineHeight = 31.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun BoardBalanceStrip(
    selectedBoard: BoardProfile?,
    isBluetoothConnected: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PanelDark.copy(alpha = 0.94f), RoundedCornerShape(2.dp))
            .border(1.dp, BorderSoft, RoundedCornerShape(2.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = "PLACA DO PAINEL",
                color = TextDim,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = selectedBoard?.displayName ?: "Arduino Uno/Nano",
                color = AccentGreen,
                fontSize = 17.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Text(
            text = if (isBluetoothConnected) "ONLINE" else "OFFLINE",
            color = if (isBluetoothConnected) AccentGreen else TextDim,
            fontSize = 12.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ModuleSectionTitle(
    title: String,
) {
    Text(
        text = title.uppercase(),
        color = TextDim,
        fontSize = 12.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp),
    )
}

@Composable
private fun ModuleOptionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .clickable { onClick() }
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .background(TileDark, RoundedCornerShape(4.dp))
                .border(1.dp, Color.Black.copy(alpha = 0.42f), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(34.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                color = WhiteSoft,
                fontSize = 25.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
            )

            Text(
                text = subtitle,
                color = accentColor,
                fontSize = 15.sp,
                lineHeight = 18.sp,
                maxLines = 1,
            )
        }

        Box(
            modifier = Modifier
                .size(34.dp)
                .border(1.dp, TextDim.copy(alpha = 0.70f), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = null,
                tint = TextDim,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

private fun Modifier.moduleBoxGridBackground(): Modifier {
    return drawBehind {
        val step = 14.dp.toPx()
        val dotColor = Color.White.copy(alpha = 0.035f)

        var x = 0f
        while (x <= size.width) {
            var y = 0f
            while (y <= size.height) {
                drawCircle(
                    color = dotColor,
                    radius = 0.9f,
                    center = Offset(x, y),
                )
                y += step
            }
            x += step
        }
    }
}
