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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardProfile
import br.com.laboratoriodecircuitos.lablink.core.boards.BoardProfiles
import br.com.laboratoriodecircuitos.lablink.ui.components.LabLinkScreenChrome

private val CardDark = Color(0xFF151515)
private val WhiteSoft = Color(0xFFFFFFFF)
private val TextDim = Color(0xFFC4C7C8)
private val Muted = Color(0xFF8E9192)
private val BorderSoft = Color.White.copy(alpha = 0.10f)
private val AccentPurple = Color(0xFFE5BEFF)

@Composable
fun BoardSelectionScreen(
    isBluetoothConnected: Boolean,
    connectedDeviceName: String?,
    initialBoard: BoardProfile?,
    onSaveBoard: (BoardProfile) -> Unit,
    onBack: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenConnection: () -> Unit,
    onOpenControls: () -> Unit,
    onOpenTerminal: () -> Unit,
) {
    val scrollState = rememberScrollState()
    var selectedBoard by remember { mutableStateOf(initialBoard ?: BoardProfiles.supportedBoards.firstOrNull()) }

    LabLinkScreenChrome(
        isBluetoothConnected = isBluetoothConnected,
        onOpenHome = onOpenHome,
        onOpenConnection = onOpenConnection,
        onOpenControls = onOpenControls,
        onOpenTerminal = onOpenTerminal,
        title = "Escolher placa",
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 104.dp, start = 16.dp, end = 16.dp, bottom = 132.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Choose your board",
                    color = WhiteSoft,
                    fontSize = 30.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Text(
                    text = if (connectedDeviceName.isNullOrBlank()) {
                        "Select the Arduino model connected to your Bluetooth module."
                    } else {
                        "Select the Arduino model connected to $connectedDeviceName."
                    },
                    color = TextDim,
                    fontSize = 17.sp,
                    lineHeight = 25.sp,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BoardProfiles.supportedBoards.chunked(2).forEach { rowBoards ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        rowBoards.forEach { board ->
                            BoardOptionCard(
                                board = board,
                                selected = selectedBoard?.type == board.type,
                                onClick = { selectedBoard = board },
                                modifier = Modifier.weight(1f),
                            )
                        }

                        if (rowBoards.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.30f to Color.Black,
                        1f to Color.Black,
                    ),
                )
                .padding(start = 16.dp, end = 16.dp, top = 36.dp, bottom = 24.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(WhiteSoft, RoundedCornerShape(14.dp))
                    .clickable(enabled = selectedBoard != null) {
                        selectedBoard?.let(onSaveBoard)
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Continue",
                    color = Color(0xFF1A1C1C),
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun BoardOptionCard(
    board: BoardProfile,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (selected) AccentPurple.copy(alpha = 0.62f) else BorderSoft

    Column(
        modifier = modifier
            .height(160.dp)
            .background(CardDark, RoundedCornerShape(24.dp))
            .selectedBoardWash(selected)
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.Memory,
            contentDescription = null,
            tint = if (selected) AccentPurple else TextDim,
            modifier = Modifier.size(42.dp),
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = board.displayName,
            color = if (selected) WhiteSoft else TextDim,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
        )
    }
}

private fun Modifier.selectedBoardWash(selected: Boolean): Modifier {
    if (!selected) return this

    return drawBehind {
        drawRect(
            color = AccentPurple.copy(alpha = 0.06f),
            topLeft = Offset.Zero,
            size = size,
        )
    }
}
