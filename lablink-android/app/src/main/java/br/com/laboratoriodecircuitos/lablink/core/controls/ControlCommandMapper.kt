package br.com.laboratoriodecircuitos.lablink.core.controls

object ControlCommandMapper {
    fun digitalToggleCommand(
        control: LabLinkControl,
        turnOn: Boolean,
    ): String {
        val normalizedPin = normalizeDigitalPin(control.pin)
        val state = if (turnOn) "ON" else "OFF"

        return if (normalizedPin == "D13") {
            if (turnOn) "LED:ON" else "LED:OFF"
        } else {
            "DIGITAL:$normalizedPin:$state"
        }
    }

    fun pwmCommand(
        control: LabLinkControl,
        value: Int,
    ): String {
        val normalizedPin = normalizeDigitalPin(control.pin)
        val safeValue = value.coerceIn(0, 255)

        return "PWM:$normalizedPin:$safeValue"
    }

    fun servoCommand(
        control: LabLinkControl,
        angle: Int,
    ): String {
        val normalizedPin = normalizeDigitalPin(control.pin)
        val safeAngle = angle.coerceIn(0, 180)

        return "SERVO:$normalizedPin:$safeAngle"
    }

    fun pulseCommand(
        control: LabLinkControl,
        durationMs: Int = 500,
    ): String {
        val normalizedPin = normalizeDigitalPin(control.pin)
        val safeDuration = durationMs.coerceIn(1, 5000)

        return "PULSE:$normalizedPin:$safeDuration"
    }

    fun analogReadCommand(control: LabLinkControl): String {
        val normalizedPin = normalizeAnalogPin(control.pin)

        return "READ:$normalizedPin"
    }

    private fun normalizeDigitalPin(pin: String): String {
        val cleaned = pin.trim().uppercase()

        return when {
            cleaned.isBlank() -> "D13"
            cleaned.startsWith("D") -> cleaned
            cleaned.startsWith("A") -> cleaned
            cleaned.all { it.isDigit() } -> "D$cleaned"
            else -> cleaned
        }
    }

    private fun normalizeAnalogPin(pin: String): String {
        val cleaned = pin.trim().uppercase()

        return when {
            cleaned.isBlank() -> "A0"
            cleaned.startsWith("A") -> cleaned
            else -> cleaned
        }
    }
}
