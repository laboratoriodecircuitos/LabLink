package br.com.laboratoriodecircuitos.lablink.features.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothConnectionStatus
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothDeviceInfo
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothUiState
import br.com.laboratoriodecircuitos.lablink.ui.theme.LabLinkTheme

@Composable
fun ControlsScreen(
    bluetoothState: BluetoothUiState,
    onSendPing: () -> Unit,
    onTurnLedOn: () -> Unit,
    onTurnLedOff: () -> Unit,
    onOpenConnection: () -> Unit,
    onBack: () -> Unit,
) {
    val isConnected = bluetoothState.status == BluetoothConnectionStatus.Connected

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Controles",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Envie comandos para o Arduino pelo Bluetooth",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            ConnectionSummaryCard(bluetoothState = bluetoothState)

            Spacer(modifier = Modifier.height(16.dp))

            if (!isConnected) {
                NotConnectedCard(onOpenConnection = onOpenConnection)

                Spacer(modifier = Modifier.height(16.dp))
            }

            LastResponseCard(lastReceivedMessage = bluetoothState.lastReceivedMessage)

            Spacer(modifier = Modifier.height(16.dp))

            PingControlCard(
                isConnected = isConnected,
                onSendPing = onSendPing,
            )

            Spacer(modifier = Modifier.height(16.dp))

            LedControlCard(
                isConnected = isConnected,
                onTurnLedOn = onTurnLedOn,
                onTurnLedOff = onTurnLedOff,
            )

            Spacer(modifier = Modifier.height(16.dp))

            UpcomingControlsCard()

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onOpenConnection,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Ir para conexão Bluetooth")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Voltar")
            }
        }
    }
}

@Composable
private fun ConnectionSummaryCard(bluetoothState: BluetoothUiState) {
    val selectedDevice = bluetoothState.selectedDevice

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Conexão atual",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = bluetoothState.status.toDisplayText(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedDevice == null) {
                Text(text = "Nenhum dispositivo selecionado.")
            } else {
                Text(
                    text = selectedDevice.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = selectedDevice.address,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = bluetoothState.message)
        }
    }
}

@Composable
private fun NotConnectedCard(onOpenConnection: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Nenhum módulo conectado",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Conecte primeiro ao HC-05 ou HC-06 para liberar os comandos desta tela.")

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onOpenConnection,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Conectar Bluetooth")
            }
        }
    }
}

@Composable
private fun LastResponseCard(lastReceivedMessage: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Última resposta recebida",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (lastReceivedMessage.isBlank()) {
                Text(text = "Nenhuma resposta recebida ainda.")
            } else {
                Text(
                    text = lastReceivedMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun PingControlCard(
    isConnected: Boolean,
    onSendPing: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Teste de comunicação",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Use este botão para confirmar que o Arduino responde ao app.")

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSendPing,
                modifier = Modifier.fillMaxWidth(),
                enabled = isConnected,
            ) {
                Text(text = "Enviar PING")
            }
        }
    }
}

@Composable
private fun LedControlCard(
    isConnected: Boolean,
    onTurnLedOn: () -> Unit,
    onTurnLedOff: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Controle de LED",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Controla o LED 13 do Arduino usando LED:ON e LED:OFF.")

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onTurnLedOn,
                    modifier = Modifier.weight(1f),
                    enabled = isConnected,
                ) {
                    Text(text = "Ligar LED")
                }

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedButton(
                    onClick = onTurnLedOff,
                    modifier = Modifier.weight(1f),
                    enabled = isConnected,
                ) {
                    Text(text = "Desligar LED")
                }
            }
        }
    }
}

@Composable
private fun UpcomingControlsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Próximos controles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "• PWM por slider")
            Text(text = "• Servo motor")
            Text(text = "• Motores com direção")
        }
    }
}

private fun BluetoothConnectionStatus.toDisplayText(): String {
    return when (this) {
        BluetoothConnectionStatus.Disconnected -> "Desconectado"
        BluetoothConnectionStatus.CheckingPermissions -> "Verificando permissões"
        BluetoothConnectionStatus.PermissionRequired -> "Permissão necessária"
        BluetoothConnectionStatus.BluetoothUnavailable -> "Bluetooth indisponível"
        BluetoothConnectionStatus.BluetoothDisabled -> "Bluetooth desligado"
        BluetoothConnectionStatus.Ready -> "Pronto para conectar"
        BluetoothConnectionStatus.Connecting -> "Conectando"
        BluetoothConnectionStatus.Connected -> "Conectado"
        BluetoothConnectionStatus.Error -> "Erro"
    }
}

@Preview(showBackground = true)
@Composable
private fun ControlsScreenPreview() {
    LabLinkTheme {
        ControlsScreen(
            bluetoothState = BluetoothUiState(
                status = BluetoothConnectionStatus.Connected,
                selectedDevice = BluetoothDeviceInfo(
                    name = "HC-06",
                    address = "20:16:05:11:38:71",
                ),
                message = "Comando enviado: LED:ON. Resposta recebida.",
                lastReceivedMessage = "OK:LED_ON",
            ),
            onSendPing = {},
            onTurnLedOn = {},
            onTurnLedOff = {},
            onOpenConnection = {},
            onBack = {},
        )
    }
}
