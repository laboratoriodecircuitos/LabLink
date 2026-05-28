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
