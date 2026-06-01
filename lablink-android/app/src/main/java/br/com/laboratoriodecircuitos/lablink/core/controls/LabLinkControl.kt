package br.com.laboratoriodecircuitos.lablink.core.controls

data class LabLinkControl(
    val id: String,
    val type: ControlType,
    val name: String,
    val pin: String,
    val minValue: Int? = null,
    val maxValue: Int? = null,
    val currentValue: Int? = null,
    val isOn: Boolean = false,
    val widthUnits: Int = 1,
    val heightUnits: Int = 1,
    val gridX: Int = 0,
    val gridY: Int = 0,
    val mode: String? = null,
    val durationMs: Int? = null,
) {
    val pinLabel: String
        get() = when {
            pin.isBlank() -> "Pino não definido"
            pin.startsWith("A", ignoreCase = true) -> "Pino ${pin.uppercase()}"
            else -> "Pino $pin"
        }
}
