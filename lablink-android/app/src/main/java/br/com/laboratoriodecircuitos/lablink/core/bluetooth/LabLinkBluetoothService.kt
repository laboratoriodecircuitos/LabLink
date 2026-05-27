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
                    message = "Permissões concedidas. Bluetooth pronto para a próxima etapa.",
                )
            }
        } catch (exception: SecurityException) {
            BluetoothUiState(
                status = BluetoothConnectionStatus.PermissionRequired,
                message = "O Android bloqueou o acesso ao Bluetooth por falta de permissão.",
            )
        }
    }

    fun getDevelopmentNotes(): List<String> {
        return listOf(
            "Permissões Bluetooth adicionadas ao AndroidManifest.xml.",
            "Solicitação de permissões em runtime preparada.",
            "Listagem de dispositivos pareados ainda não implementada.",
            "Conexão real com HC-05/HC-06 ainda não implementada.",
        )
    }
}
