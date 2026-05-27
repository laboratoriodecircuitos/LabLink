package br.com.laboratoriodecircuitos.lablink.core.boards

data class BoardPin(
    val id: String,
    val label: String,
    val supportsDigitalOutput: Boolean,
    val supportsPwm: Boolean,
    val supportsServo: Boolean,
    val supportsAnalogRead: Boolean,
    val isRecommended: Boolean = true,
    val note: String? = null,
)
