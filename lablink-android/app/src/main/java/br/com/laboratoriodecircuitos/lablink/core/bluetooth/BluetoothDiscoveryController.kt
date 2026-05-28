package br.com.laboratoriodecircuitos.lablink.core.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build

class BluetoothDiscoveryController {
    private var discoveryReceiver: BroadcastReceiver? = null
    private var pairingReceiver: BroadcastReceiver? = null

    fun startDiscovery(
        context: Context,
        onDeviceFound: (BluetoothDiscoveredDevice) -> Unit,
        onFinished: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val appContext = context.applicationContext

        stopDiscovery(appContext)

        try {
            val bluetoothManager = appContext.getSystemService(BluetoothManager::class.java)
            val bluetoothAdapter = bluetoothManager?.adapter

            if (bluetoothAdapter == null) {
                onError("Este celular não possui Bluetooth disponível.")
                return
            }

            if (!bluetoothAdapter.isEnabled) {
                onError("Bluetooth desligado. Ative o Bluetooth do celular para buscar módulos próximos.")
                return
            }

            val receiver = object : BroadcastReceiver() {
                override fun onReceive(receiverContext: Context?, intent: Intent?) {
                    when (intent?.action) {
                        BluetoothDevice.ACTION_FOUND -> {
                            val bluetoothDevice = getBluetoothDeviceFromIntent(intent)

                            if (bluetoothDevice != null) {
                                val discoveredDevice = bluetoothDevice.toDiscoveredDevice()

                                if (discoveredDevice.address.isNotBlank()) {
                                    onDeviceFound(discoveredDevice)
                                }
                            }
                        }

                        BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                            onFinished()
                        }
                    }
                }
            }

            val filter = IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_FOUND)
                addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                appContext.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
            } else {
                appContext.registerReceiver(receiver, filter)
            }

            discoveryReceiver = receiver

            if (bluetoothAdapter.isDiscovering) {
                bluetoothAdapter.cancelDiscovery()
            }

            val started = bluetoothAdapter.startDiscovery()

            if (!started) {
                stopDiscovery(appContext)
                onError("Não foi possível iniciar a busca Bluetooth agora. Tente novamente.")
            }
        } catch (exception: SecurityException) {
            stopDiscovery(appContext)
            onError("Permissão Bluetooth necessária para buscar dispositivos próximos.")
        } catch (exception: Exception) {
            stopDiscovery(appContext)
            onError("Falha ao buscar dispositivos Bluetooth: ${exception.message ?: "erro sem detalhe"}.")
        }
    }

    fun startPairing(
        context: Context,
        discoveredDevice: BluetoothDiscoveredDevice,
        onPairingStarted: () -> Unit,
        onPairingSucceeded: () -> Unit,
        onPairingFailed: () -> Unit,
        onAlreadyPaired: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val appContext = context.applicationContext

        try {
            val bluetoothManager = appContext.getSystemService(BluetoothManager::class.java)
            val bluetoothAdapter = bluetoothManager?.adapter

            if (bluetoothAdapter == null) {
                onError("Este celular não possui Bluetooth disponível.")
                return
            }

            if (!bluetoothAdapter.isEnabled) {
                onError("Bluetooth desligado. Ative o Bluetooth do celular para parear.")
                return
            }

            if (discoveredDevice.address.isBlank()) {
                onError("Endereço Bluetooth inválido para pareamento.")
                return
            }

            stopDiscovery(appContext)
            stopPairingReceiver(appContext)

            val remoteDevice = bluetoothAdapter.getRemoteDevice(discoveredDevice.address)

            if (remoteDevice.bondState == BluetoothDevice.BOND_BONDED) {
                onAlreadyPaired()
                return
            }

            registerPairingReceiver(
                context = appContext,
                targetAddress = discoveredDevice.address,
                onPairingSucceeded = onPairingSucceeded,
                onPairingFailed = onPairingFailed,
            )

            val started = remoteDevice.createBond()

            if (started) {
                onPairingStarted()
            } else {
                stopPairingReceiver(appContext)
                onError("O Android não iniciou o pareamento. Tente novamente ou confira se o módulo está próximo.")
            }
        } catch (exception: SecurityException) {
            onError("Permissão Bluetooth necessária para iniciar o pareamento.")
        } catch (exception: IllegalArgumentException) {
            onError("Endereço Bluetooth inválido para ${discoveredDevice.name}.")
        } catch (exception: Exception) {
            onError("Falha ao iniciar pareamento: ${exception.message ?: "erro sem detalhe"}.")
        }
    }
    private fun registerPairingReceiver(
        context: Context,
        targetAddress: String,
        onPairingSucceeded: () -> Unit,
        onPairingFailed: () -> Unit,
    ) {
        val appContext = context.applicationContext

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(receiverContext: Context?, intent: Intent?) {
                if (intent?.action != BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                    return
                }

                val bluetoothDevice = getBluetoothDeviceFromIntent(intent) ?: return
                val address = try {
                    bluetoothDevice.address ?: ""
                } catch (_: SecurityException) {
                    ""
                }

                if (address != targetAddress) {
                    return
                }

                val bondState = intent.getIntExtra(
                    BluetoothDevice.EXTRA_BOND_STATE,
                    BluetoothDevice.ERROR,
                )

                when (bondState) {
                    BluetoothDevice.BOND_BONDED -> {
                        stopPairingReceiver(appContext)
                        onPairingSucceeded()
                    }

                    BluetoothDevice.BOND_NONE -> {
                        stopPairingReceiver(appContext)
                        onPairingFailed()
                    }
                }
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            appContext.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            appContext.registerReceiver(receiver, filter)
        }

        pairingReceiver = receiver
    }

    private fun stopPairingReceiver(context: Context) {
        val appContext = context.applicationContext

        pairingReceiver?.let { receiver ->
            try {
                appContext.unregisterReceiver(receiver)
            } catch (_: Exception) {
                // Receiver pode já ter sido removido.
            }
        }

        pairingReceiver = null
    }
    fun stopDiscovery(context: Context) {
        val appContext = context.applicationContext

        try {
            val bluetoothManager = appContext.getSystemService(BluetoothManager::class.java)
            val bluetoothAdapter = bluetoothManager?.adapter

            if (bluetoothAdapter?.isDiscovering == true) {
                bluetoothAdapter.cancelDiscovery()
            }
        } catch (_: SecurityException) {
            // Ignora nesta etapa. A UI orienta sobre permissões.
        }

        discoveryReceiver?.let { receiver ->
            try {
                appContext.unregisterReceiver(receiver)
            } catch (_: Exception) {
                // Receiver pode já ter sido removido.
            }
        }

        discoveryReceiver = null
    }

    private fun getBluetoothDeviceFromIntent(intent: Intent): BluetoothDevice? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        }
    }

    private fun BluetoothDevice.toDiscoveredDevice(): BluetoothDiscoveredDevice {
        val safeName = try {
            name ?: "Dispositivo sem nome"
        } catch (_: SecurityException) {
            "Dispositivo sem nome"
        }

        val safeAddress = try {
            address ?: ""
        } catch (_: SecurityException) {
            ""
        }

        val safePairingStatus = try {
            when (bondState) {
                BluetoothDevice.BOND_BONDED -> BluetoothPairingStatus.Paired
                BluetoothDevice.BOND_BONDING -> BluetoothPairingStatus.Pairing
                else -> BluetoothPairingStatus.NotPaired
            }
        } catch (_: SecurityException) {
            BluetoothPairingStatus.NotPaired
        }

        return BluetoothDiscoveredDevice(
            name = safeName,
            address = safeAddress,
            pairingStatus = safePairingStatus,
        )
    }
}


