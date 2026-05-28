package br.com.laboratoriodecircuitos.lablink.core.bluetooth

enum class BluetoothDiscoveryStatus {
    Idle,
    PermissionRequired,
    BluetoothDisabled,
    Scanning,
    Finished,
    Error,
}
