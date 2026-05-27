package br.com.laboratoriodecircuitos.lablink.features.connection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
fun ConnectionScreen(
    bluetoothState: BluetoothUiState,
    developmentNotes: List<String>,
    onRequestPermissions: () -> Unit,
    onRefresh: () -> Unit,
    onLoadPairedDevices: () -> Unit,
    onSelectDevice: (BluetoothDeviceInfo) -> Unit,
    onConnectSelectedDevice: () -> Unit,
    onSendPing: () -> Unit,
    onBack: () -> Unit,
) {
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
                text = "Conexão Bluetooth",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            StatusCard(bluetoothState = bluetoothState)

            Spacer(modifier = Modifier.height(16.dp))

            SelectedDeviceCard(selectedDevice = bluetoothState.selectedDevice)

            Spacer(modifier = Modifier.height(16.dp))

            PairedDevicesCard(
                devices = bluetoothState.pairedDevices,
                selectedDevice = bluetoothState.selectedDevice,
                onSelectDevice = onSelectDevice,
            )

            Spacer(modifier = Modifier.height(16.dp))

            DevelopmentNotesCard(developmentNotes = developmentNotes)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRequestPermissions,
                modifier = Modifier.fillMaxWidth(),
                enabled = bluetoothState.status == BluetoothConnectionStatus.PermissionRequired,
            ) {
                Text(text = "Solicitar permissões Bluetooth")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onLoadPairedDevices,
                modifier = Modifier.fillMaxWidth(),
                enabled = bluetoothState.status == BluetoothConnectionStatus.Ready,
            ) {
                Text(text = "Buscar dispositivos pareados")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onConnectSelectedDevice,
                modifier = Modifier.fillMaxWidth(),
                enabled = bluetoothState.selectedDevice != null &&
                    bluetoothState.status != BluetoothConnectionStatus.Connecting &&
                    bluetoothState.status != BluetoothConnectionStatus.Connected,
            ) {
                Text(text = "Conectar ao dispositivo selecionado")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSendPing,
                modifier = Modifier.fillMaxWidth(),
                enabled = bluetoothState.status == BluetoothConnectionStatus.Connected,
            ) {
                Text(text = "Enviar PING")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onRefresh,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Atualizar status")
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
private fun StatusCard(bluetoothState: BluetoothUiState) {
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
}

@Composable
private fun SelectedDeviceCard(selectedDevice: BluetoothDeviceInfo?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Dispositivo selecionado",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
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
        }
    }
}

@Composable
private fun PairedDevicesCard(
    devices: List<BluetoothDeviceInfo>,
    selectedDevice: BluetoothDeviceInfo?,
    onSelectDevice: (BluetoothDeviceInfo) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Dispositivos pareados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (devices.isEmpty()) {
                Text(text = "Nenhum dispositivo listado ainda.")
            } else {
                devices.forEach { device ->
                    val isSelected = selectedDevice?.address == device.address

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { onSelectDevice(device) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start,
                        ) {
                            Text(
                                text = if (isSelected) "${device.name}  ✓" else device.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = device.address,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DevelopmentNotesCard(developmentNotes: List<String>) {
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
        ConnectionScreen(
            bluetoothState = BluetoothUiState(
                status = BluetoothConnectionStatus.Connected,
                message = "Conectado a HC-06. Comunicação serial pronta.",
                selectedDevice = BluetoothDeviceInfo(
                    name = "HC-06",
                    address = "20:16:05:11:38:71",
                ),
                pairedDevices = listOf(
                    BluetoothDeviceInfo(
                        name = "HC-06",
                        address = "20:16:05:11:38:71",
                    ),
                ),
            ),
            developmentNotes = listOf(
                "Conexão RFCOMM/SPP validada com HC-06.",
                "Envio inicial de comando serial em teste.",
            ),
            onRequestPermissions = {},
            onRefresh = {},
            onLoadPairedDevices = {},
            onSelectDevice = {},
            onConnectSelectedDevice = {},
            onSendPing = {},
            onBack = {},
        )
    }
}
