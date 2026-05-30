package br.com.laboratoriodecircuitos.lablink.core.bluetooth

import android.content.Context

object LabLinkBluetoothConnectionManager {
    private val bluetoothService = LabLinkBluetoothService()

    @Volatile
    private var cachedState: BluetoothUiState = BluetoothUiState()

    @Synchronized
    fun currentState(): BluetoothUiState {
        return cachedState
    }

    @Synchronized
    fun evaluateInitialState(context: Context): BluetoothUiState {
        if (cachedState.status == BluetoothConnectionStatus.Connected) {
            return cachedState
        }

        cachedState = bluetoothService.evaluateInitialState(context)
        return cachedState
    }

    @Synchronized
    fun loadPairedDevices(context: Context): BluetoothUiState {
        if (cachedState.status == BluetoothConnectionStatus.Connected) {
            return cachedState.copy(
                message = "Bluetooth já conectado em ${cachedState.selectedDevice?.name ?: "módulo Bluetooth"}.",
            )
        }

        cachedState = bluetoothService.loadPairedDevices(context)
        return cachedState
    }

    @Synchronized
    fun selectDevice(
        currentState: BluetoothUiState,
        device: BluetoothDeviceInfo,
    ): BluetoothUiState {
        cachedState = bluetoothService.selectDevice(
            currentState = currentState,
            device = device,
        )

        return cachedState
    }

    @Synchronized
    fun connectingState(currentState: BluetoothUiState): BluetoothUiState {
        cachedState = bluetoothService.connectingState(currentState)
        return cachedState
    }

    @Synchronized
    fun connectToSelectedDevice(
        context: Context,
        currentState: BluetoothUiState,
    ): BluetoothUiState {
        cachedState = bluetoothService.connectToSelectedDevice(
            context = context,
            currentState = currentState,
        )

        return cachedState
    }

    @Synchronized
    fun sendCommandAndReadResponse(
        currentState: BluetoothUiState,
        command: String,
    ): BluetoothUiState {
        val effectiveState = if (cachedState.status == BluetoothConnectionStatus.Connected) {
            cachedState
        } else {
            currentState
        }

        cachedState = bluetoothService.sendCommandAndReadResponse(
            currentState = effectiveState,
            command = command,
        )

        return cachedState
    }

    @Synchronized
    fun disconnectDevice(currentState: BluetoothUiState): BluetoothUiState {
        val effectiveState = if (cachedState.selectedDevice != null) {
            cachedState
        } else {
            currentState
        }

        cachedState = bluetoothService.disconnectDevice(effectiveState)
        return cachedState
    }

    @Synchronized
    fun disconnectCurrentDevice(): BluetoothUiState {
        cachedState = bluetoothService.disconnectDevice(cachedState)
        return cachedState
    }

    fun getDevelopmentNotes(): List<String> {
        return bluetoothService.getDevelopmentNotes()
    }
}
