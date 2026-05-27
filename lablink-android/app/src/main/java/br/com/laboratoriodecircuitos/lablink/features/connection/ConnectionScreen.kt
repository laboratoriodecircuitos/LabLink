package br.com.laboratoriodecircuitos.lablink.features.connection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.BluetoothConnectionStatus
import br.com.laboratoriodecircuitos.lablink.core.bluetooth.LabLinkBluetoothService
import br.com.laboratoriodecircuitos.lablink.ui.theme.LabLinkTheme

@Composable
fun ConnectionScreen(
    onBack: () -> Unit,
) {
    val bluetoothService = remember { LabLinkBluetoothService() }
    val bluetoothState = remember { bluetoothService.initialState() }
    val developmentNotes = remember { bluetoothService.getDevelopmentNotes() }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Conexão Bluetooth",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = bluetoothState.status.toDisplayText())

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = bluetoothState.message)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Preparação técnica",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    developmentNotes.forEach { note ->
                        Text(text = "• $note")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Buscar dispositivos pareados")
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
private fun ConnectionScreenPreview() {
    LabLinkTheme {
        ConnectionScreen(onBack = {})
    }
}
