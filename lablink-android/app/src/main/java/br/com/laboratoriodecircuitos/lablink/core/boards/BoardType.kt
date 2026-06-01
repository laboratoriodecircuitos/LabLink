package br.com.laboratoriodecircuitos.lablink.core.boards

enum class BoardType(
    val displayName: String,
) {
    ArduinoUno(
        displayName = "Arduino Uno",
    ),

    ArduinoNano(
        displayName = "Arduino Nano",
    ),

    ArduinoProMini(
        displayName = "Arduino Pro Mini",
    ),

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
