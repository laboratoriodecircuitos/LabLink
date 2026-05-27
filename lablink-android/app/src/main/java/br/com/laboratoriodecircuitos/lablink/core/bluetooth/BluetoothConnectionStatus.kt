package br.com.laboratoriodecircuitos.lablink.core.bluetooth

enum class BluetoothConnectionStatus {
    Disconnected,
    CheckingPermissions,
    PermissionRequired,
    BluetoothUnavailable,
    BluetoothDisabled,
    Ready,
    Connecting,
    Connected,
    Error,
}
