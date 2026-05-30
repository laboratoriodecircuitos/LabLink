package br.com.laboratoriodecircuitos.lablink.features.boardselection

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Memory
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardProfile
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardProfiles

private val BgDeep = Color(0xFF000000)
private val CardDark = Color(0xFF151515)
private val WhiteSoft = Color(0xFFF7F7F7)
private val TextDim = Color(0xFF8A8A8A)
private val BorderSoft = Color.White.copy(alpha = 0.08f)
private val AccentGreen = Color(0xFFC4FA8C)
private val AccentPurple = Color(0xFFE5BEFF)

@Composable
fun BoardSelectionScreen(
    isBluetoothConnected: Boolean,
    connectedDeviceName: String?,
    initialBoard: BoardProfile?,
    onSaveBoard: (BoardProfile) -> Unit,
    onBack: () -> Unit,
) {
    val scrollState = rememberScrollState()
    var selectedBoard by remember { mutableStateOf(initialBoard) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BgDeep,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                BoardSelectionTopBar(
                    isBluetoothConnected = isBluetoothConnected,
                    onBack = onBack,
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Escolha sua placa",
                        color = WhiteSoft,
                        fontSize = 30.sp,
                        lineHeight = 34.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = if (connectedDeviceName.isNullOrBlank()) {
                            "A placa define quais pinos e controles o LabLink vai liberar para o seu projeto."
                        } else {
                            "Conectado ao $connectedDeviceName. Agora selecione a placa ligada ao módulo Bluetooth."
                        },
                        color = TextDim,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                    )
                }

                BoardProfiles.supportedBoards.forEach { board ->
                    BoardOptionCard(
                        board = board,
                        selected = selectedBoard?.type == board.type,
                        onClick = { selectedBoard = board },
                    )
                }

                ContinueBoardCard(
                    enabled = selectedBoard != null,
                    onClick = {
                        selectedBoard?.let { board ->
                            onSaveBoard(board)
                        }
                    },
                )

                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun BoardSelectionTopBar(
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
                text = "Voltar",
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
private fun BoardOptionCard(
    board: BoardProfile,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (selected) AccentGreen else CardDark,
                shape = RoundedCornerShape(26.dp),
            )
            .border(
                width = 1.dp,
                color = if (selected) AccentGreen else BorderSoft,
                shape = RoundedCornerShape(26.dp),
            )
            .clickable { onClick() }
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BoardIcon(
            icon = Icons.Rounded.Memory,
            selected = selected,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(
                text = board.displayName,
                color = if (selected) Color.Black else WhiteSoft,
                fontSize = 18.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                text = board.description,
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
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun BoardIcon(
    icon: ImageVector,
    selected: Boolean,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = if (selected) Color.Black.copy(alpha = 0.10f) else AccentPurple.copy(alpha = 0.14f),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) Color.Black else AccentPurple,
            modifier = Modifier.size(25.dp),
        )
    }
}

@Composable
private fun ContinueBoardCard(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor = if (enabled) AccentPurple else CardDark
    val textColor = if (enabled) Color.Black else TextDim

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(24.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = if (enabled) "Continuar para controles" else "Selecione uma placa",
                color = textColor,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = if (enabled) {
                    "O LabLink usará esse perfil para liberar os pinos corretos."
                } else {
                    "Nesta versão, começaremos com Arduino Uno / Nano."
                },
                color = textColor.copy(alpha = 0.68f),
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
        }
    }
}
