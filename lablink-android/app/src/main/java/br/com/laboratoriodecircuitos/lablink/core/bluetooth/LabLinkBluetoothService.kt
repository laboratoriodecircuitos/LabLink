package br.com.laboratoriodecircuitos.lablink.core.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import java.io.IOException
import java.util.UUID

class LabLinkBluetoothService {
    private var bluetoothSocket: BluetoothSocket? = null

    private val serialPortProfileUuid: UUID = UUID.fromString(
        "00001101-0000-1000-8000-00805F9B34FB",
    )

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
            message = "Dispositivo selecionado: ${device.name}. Toque em conectar para abrir a comunicação Bluetooth.",
        )
    }

    fun connectingState(currentState: BluetoothUiState): BluetoothUiState {
        val selectedDevice = currentState.selectedDevice

        return if (selectedDevice == null) {
            currentState.copy(
                status = BluetoothConnectionStatus.Error,
                message = "Nenhum dispositivo selecionado para conectar.",
            )
        } else {
            currentState.copy(
                status = BluetoothConnectionStatus.Connecting,
                message = "Conectando a ${selectedDevice.name}...",
            )
        }
    }

    fun connectToSelectedDevice(
        context: Context,
        currentState: BluetoothUiState,
    ): BluetoothUiState {
        val selectedDevice = currentState.selectedDevice

        if (selectedDevice == null) {
            return currentState.copy(
                status = BluetoothConnectionStatus.Error,
                message = "Nenhum dispositivo selecionado.",
            )
        }

        if (!BluetoothPermissionHelper.hasRequiredPermissions(context)) {
            return currentState.copy(
                status = BluetoothConnectionStatus.PermissionRequired,
                message = "Permissões Bluetooth necessárias para conectar.",
            )
        }

        return try {
            val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
            val bluetoothAdapter = bluetoothManager?.adapter

            when {
                bluetoothAdapter == null -> currentState.copy(
                    status = BluetoothConnectionStatus.BluetoothUnavailable,
                    message = "Este dispositivo não possui Bluetooth disponível.",
                )

                !bluetoothAdapter.isEnabled -> currentState.copy(
                    status = BluetoothConnectionStatus.BluetoothDisabled,
                    message = "Bluetooth desligado. Ative o Bluetooth para conectar.",
                )

                else -> connectWithAdapter(
                    bluetoothAdapter = bluetoothAdapter,
                    selectedDevice = selectedDevice,
                    currentState = currentState,
                )
            }
        } catch (exception: SecurityException) {
            currentState.copy(
                status = BluetoothConnectionStatus.PermissionRequired,
                selectedDevice = selectedDevice,
                message = "Permissão Bluetooth necessária para conectar ao dispositivo.",
            )
        } catch (exception: IllegalArgumentException) {
            closeConnection()

            currentState.copy(
                status = BluetoothConnectionStatus.Error,
                selectedDevice = selectedDevice,
                message = "Endereço Bluetooth inválido para ${selectedDevice.name}.",
            )
        }
    }

    private fun connectWithAdapter(
        bluetoothAdapter: BluetoothAdapter,
        selectedDevice: BluetoothDeviceInfo,
        currentState: BluetoothUiState,
    ): BluetoothUiState {
        closeConnection()
        bluetoothAdapter.cancelDiscovery()

        val remoteDevice = bluetoothAdapter.getRemoteDevice(selectedDevice.address)

        val secureError = tryConnect(
            remoteDevice = remoteDevice,
            secure = true,
        )

        if (secureError == null) {
            return currentState.copy(
                status = BluetoothConnectionStatus.Connected,
                selectedDevice = selectedDevice,
                message = "Conectado a ${selectedDevice.name} por RFCOMM seguro. Comunicação serial pronta.",
            )
        }

        val insecureError = tryConnect(
            remoteDevice = remoteDevice,
            secure = false,
        )

        if (insecureError == null) {
            return currentState.copy(
                status = BluetoothConnectionStatus.Connected,
                selectedDevice = selectedDevice,
                message = "Conectado a ${selectedDevice.name} por RFCOMM insecure. Comunicação serial pronta.",
            )
        }

        closeConnection()

        return currentState.copy(
            status = BluetoothConnectionStatus.Error,
            selectedDevice = selectedDevice,
            message = "Falha ao conectar em ${selectedDevice.name}. Seguro: ${secureError.message ?: "erro sem detalhe"} | Insecure: ${insecureError.message ?: "erro sem detalhe"}",
        )
    }

    private fun tryConnect(
        remoteDevice: BluetoothDevice,
        secure: Boolean,
    ): IOException? {
        return try {
            val socket = if (secure) {
                remoteDevice.createRfcommSocketToServiceRecord(serialPortProfileUuid)
            } else {
                remoteDevice.createInsecureRfcommSocketToServiceRecord(serialPortProfileUuid)
            }

            socket.connect()
            bluetoothSocket = socket
            null
        } catch (exception: IOException) {
            closeConnection()
            exception
        }
    }

    fun sendCommandAndReadResponse(
        currentState: BluetoothUiState,
        command: String,
    ): BluetoothUiState {
        if (currentState.status != BluetoothConnectionStatus.Connected) {
            return currentState.copy(
                message = "Conecte a um dispositivo antes de enviar comandos.",
            )
        }

        val socket = bluetoothSocket

        if (socket == null || !socket.isConnected) {
            return currentState.copy(
                status = BluetoothConnectionStatus.Error,
                message = "Socket Bluetooth não está conectado.",
            )
        }

        return try {
            val commandWithLineBreak = if (command.endsWith("\n")) command else "$command\n"
            socket.outputStream.write(commandWithLineBreak.toByteArray(Charsets.UTF_8))
            socket.outputStream.flush()

            val response = readLineFromSocket(socket)

            if (response.isBlank()) {
                currentState.copy(
                    message = "Comando enviado: $command. Nenhuma resposta recebida no tempo esperado.",
                    lastReceivedMessage = "",
                )
            } else {
                currentState.copy(
                    message = "Comando enviado: $command. Resposta recebida.",
                    lastReceivedMessage = response,
                )
            }
        } catch (exception: IOException) {
            closeConnection()

            currentState.copy(
                status = BluetoothConnectionStatus.Error,
                message = "Falha ao enviar ou ler resposta: ${exception.message ?: "erro sem detalhe"}.",
            )
        }
    }

    private fun readLineFromSocket(
        socket: BluetoothSocket,
        timeoutMs: Long = 1500L,
    ): String {
        val inputStream = socket.inputStream
        val startTime = System.currentTimeMillis()
        val responseBuilder = StringBuilder()

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            while (inputStream.available() > 0) {
                val receivedChar = inputStream.read().toChar()

                if (receivedChar == '\n') {
                    return responseBuilder.toString().trim()
                }

                responseBuilder.append(receivedChar)
            }

            Thread.sleep(10)
        }

        return responseBuilder.toString().trim()
    }

    fun closeConnection() {
        try {
            bluetoothSocket?.close()
        } catch (_: IOException) {
            // Ignora falhas ao fechar socket nesta fase inicial.
        } finally {
            bluetoothSocket = null
        }
    }

    fun disconnectDevice(currentState: BluetoothUiState): BluetoothUiState {
        closeConnection()

        return currentState.copy(
            status = BluetoothConnectionStatus.Ready,
            message = "Dispositivo desconectado. Você pode conectar novamente quando quiser.",
            lastReceivedMessage = "",
        )
    }

    fun getDevelopmentNotes(): List<String> {
        return listOf(
            "Permissões Bluetooth adicionadas ao AndroidManifest.xml.",
            "Conexão RFCOMM/SPP validada com HC-06.",
            "Envio de comando serial validado.",
            "Leitura de resposta do Arduino em teste.",
        )
    }
}

