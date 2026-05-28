package br.com.laboratoriodecircuitos.lablink.core.bluetooth

object BluetoothForegroundServiceConfig {
    const val NOTIFICATION_CHANNEL_ID = "lablink_bluetooth_connection"
    const val NOTIFICATION_CHANNEL_NAME = "Conexão Bluetooth"
    const val NOTIFICATION_ID = 1001

    const val ACTION_START = "br.com.laboratoriodecircuitos.lablink.bluetooth.ACTION_START"
    const val ACTION_STOP = "br.com.laboratoriodecircuitos.lablink.bluetooth.ACTION_STOP"

    const val ACTION_CONNECT = "br.com.laboratoriodecircuitos.lablink.bluetooth.ACTION_CONNECT"
    const val ACTION_DISCONNECT = "br.com.laboratoriodecircuitos.lablink.bluetooth.ACTION_DISCONNECT"
    const val ACTION_SEND_COMMAND = "br.com.laboratoriodecircuitos.lablink.bluetooth.ACTION_SEND_COMMAND"

    const val EXTRA_DEVICE_NAME = "extra_device_name"
    const val EXTRA_DEVICE_ADDRESS = "extra_device_address"
    const val EXTRA_COMMAND = "extra_command"
}

