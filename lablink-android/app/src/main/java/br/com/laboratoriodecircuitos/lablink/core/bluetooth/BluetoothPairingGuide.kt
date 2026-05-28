package br.com.laboratoriodecircuitos.lablink.core.bluetooth

object BluetoothPairingGuide {
    val preparationSteps = listOf(
        "Ligue o Arduino.",
        "Confira se o HC-05 ou HC-06 está alimentado.",
        "O LED do módulo Bluetooth deve estar piscando.",
        "Mantenha o celular próximo ao módulo.",
        "Se o Android pedir senha, tente 1234. Se não funcionar, tente 0000.",
    )

    const val primaryPin = "1234"
    const val fallbackPin = "0000"

    const val pairingButtonLabel = "Parear dispositivo"
    const val searchButtonLabel = "Encontrar módulos próximos"
}
