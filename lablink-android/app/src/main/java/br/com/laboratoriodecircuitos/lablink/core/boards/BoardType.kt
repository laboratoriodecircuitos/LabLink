package br.com.laboratoriodecircuitos.lablink.core.boards

enum class BoardType(
    val displayName: String,
) {
    ArduinoUnoNano(
        displayName = "Arduino Uno / Nano",
    ),

    ArduinoMega(
        displayName = "Arduino Mega",
    ),

    Esp32DevKit(
        displayName = "ESP32 DevKit",
    ),
}
