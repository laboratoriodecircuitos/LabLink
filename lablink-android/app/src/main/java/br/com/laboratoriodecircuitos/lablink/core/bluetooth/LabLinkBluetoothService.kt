package br.com.laboratoriodecircuitos.lablink.core.bluetooth

class LabLinkBluetoothService {
    fun initialState(): BluetoothUiState {
        return BluetoothUiState(
            status = BluetoothConnectionStatus.Disconnected,
            message = "Bluetooth ainda nao inicializado nesta versao.",
        )
    }

    fun getDevelopmentNotes(): List<String> {
        return listOf(
            "Permissoes Bluetooth adicionadas ao AndroidManifest.xml.",
            "Modelo de estado Bluetooth criado.",
            "Conexao real com HC-05/HC-06 ainda nao implementada.",
        )
    }
}
