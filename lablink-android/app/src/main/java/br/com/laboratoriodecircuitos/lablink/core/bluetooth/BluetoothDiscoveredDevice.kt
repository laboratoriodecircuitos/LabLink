package br.com.laboratoriodecircuitos.lablink.core.bluetooth

data class BluetoothDiscoveredDevice(
    val name: String,
    val address: String,
    val pairingStatus: BluetoothPairingStatus = BluetoothPairingStatus.NotPaired,
) {
    val isLikelyLabLinkModule: Boolean
        get() {
            val normalizedName = name.uppercase()

            return normalizedName.contains("HC-05") ||
                normalizedName.contains("HC-06")
        }
}
