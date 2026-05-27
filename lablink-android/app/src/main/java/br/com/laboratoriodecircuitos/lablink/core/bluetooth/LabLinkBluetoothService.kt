package br.com.laboratoriodecircuitos.lablink.core.bluetooth

import android.bluetooth.BluetoothManager
import android.content.Context

class LabLinkBluetoothService {
    fun evaluateInitialState(context: Context): BluetoothUiState {
        if (!BluetoothPermissionHelper.hasRequiredPermissions(context)) {
            return BluetoothUiState(
                status = BluetoothConnectionStatus.PermissionRequired,
                message = "Permissões Bluetooth necessárias para continuar.",
            )
        }

        return try {
            val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
            val bluetoothAdapter = bluetoothManager?.adapter

            when {
                bluetoothAdapter == null -> BluetoothUiState(
                    status = BluetoothConnectionStatus.BluetoothUnavailable,
                    message = "Este dispositivo não possui Bluetooth disponível.",
                )

                !bluetoothAdapter.isEnabled -> BluetoothUiState(
                    status = BluetoothConnectionStatus.BluetoothDisabled,
                    message = "Bluetooth desligado. Ative o Bluetooth do celular para continuar.",
                )

                else -> BluetoothUiState(
                    status = BluetoothConnectionStatus.Ready,
                    message = "Permissões concedidas. Bluetooth pronto para buscar dispositivos pareados.",
                )
            }
        } catch (exception: SecurityException) {
            BluetoothUiState(
                status = BluetoothConnectionStatus.PermissionRequired,
                message = "O Android bloqueou o acesso ao Bluetooth por falta de permissão.",
            )
        }
    }

    fun loadPairedDevices(context: Context): BluetoothUiState {
        val initialState = evaluateInitialState(context)

        if (initialState.status != BluetoothConnectionStatus.Ready) {
            return initialState
        }

        return try {
            val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
            val bluetoothAdapter = bluetoothManager?.adapter

            if (bluetoothAdapter == null) {
                BluetoothUiState(
                    status = BluetoothConnectionStatus.BluetoothUnavailable,
                    message = "Este dispositivo não possui Bluetooth disponível.",
                )
            } else {
                val devices = bluetoothAdapter.bondedDevices
                    .map { device ->
                        BluetoothDeviceInfo(
                            name = device.name ?: "Dispositivo sem nome",
                            address = device.address ?: "Endereço indisponível",
                        )
                    }
                    .sortedBy { device -> device.name.lowercase() }

                if (devices.isEmpty()) {
                    BluetoothUiState(
                        status = BluetoothConnectionStatus.Ready,
                        pairedDevices = emptyList(),
                        message = "Nenhum dispositivo Bluetooth pareado encontrado. Pareie o HC-05/HC-06 nas configurações do Android.",
                    )
                } else {
                    BluetoothUiState(
                        status = BluetoothConnectionStatus.Ready,
                        pairedDevices = devices,
                        message = "Dispositivos pareados encontrados: ${devices.size}. Toque em um dispositivo para selecionar.",
                    )
                }
            }
        } catch (exception: SecurityException) {
            BluetoothUiState(
                status = BluetoothConnectionStatus.PermissionRequired,
                message = "Permissão Bluetooth necessária para listar dispositivos pareados.",
            )
        }
    }

    fun selectDevice(
        currentState: BluetoothUiState,
        device: BluetoothDeviceInfo,
    ): BluetoothUiState {
        return currentState.copy(
            selectedDevice = device,
            message = "Dispositivo selecionado: ${device.name}. Próxima etapa: conectar ao módulo Bluetooth.",
        )
    }

    fun getDevelopmentNotes(): List<String> {
        return listOf(
            "Permissões Bluetooth adicionadas ao AndroidManifest.xml.",
            "Solicitação de permissões em runtime validada.",
            "Listagem de dispositivos pareados validada.",
            "Seleção de dispositivo pareado preparada.",
            "Conexão real com HC-05/HC-06 ainda não implementada.",
        )
    }
}
