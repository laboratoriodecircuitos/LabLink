package br.com.laboratoriodecircuitos.lablink.core.bluetooth

data class BluetoothUiState(
    val status: BluetoothConnectionStatus = BluetoothConnectionStatus.Disconnected,
    val selectedDevice: BluetoothDeviceInfo? = null,
    val pairedDevices: List<BluetoothDeviceInfo> = emptyList(),
    val message: String = "Nenhum dispositivo conectado.",
    val lastReceivedMessage: String = "",
)
