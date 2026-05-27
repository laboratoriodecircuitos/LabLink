package br.com.laboratoriodecircuitos.lablink.core.controls

object ControlCommandMapper {
    fun digitalToggleCommand(
        control: LabLinkControl,
        turnOn: Boolean,
        useLegacyLedCommandForPin13: Boolean = true,
    ): String {
        require(control.type == ControlType.DigitalToggle) {
            "digitalToggleCommand só aceita controles do tipo DigitalToggle."
        }

        val normalizedPin = control.pin.trim()

        if (useLegacyLedCommandForPin13 && normalizedPin == "13") {
            return if (turnOn) "LED:ON" else "LED:OFF"
        }

        return "DIGITAL:$normalizedPin:${if (turnOn) "ON" else "OFF"}"
    }

    fun pwmCommand(
        control: LabLinkControl,
        value: Int,
    ): String {
        require(control.type == ControlType.PwmSlider) {
            "pwmCommand só aceita controles do tipo PwmSlider."
        }

        val normalizedPin = control.pin.trim()
        return "PWM:$normalizedPin:$value"
    }

    fun servoCommand(
        control: LabLinkControl,
        angle: Int,
    ): String {
        require(control.type == ControlType.ServoSlider) {
            "servoCommand só aceita controles do tipo ServoSlider."
        }

        val normalizedPin = control.pin.trim()
        return "SERVO:$normalizedPin:$angle"
    }

    fun pulseCommand(
        control: LabLinkControl,
        durationMs: Int,
    ): String {
        require(control.type == ControlType.PulseButton) {
            "pulseCommand só aceita controles do tipo PulseButton."
        }

        val normalizedPin = control.pin.trim()
        return "PULSE:$normalizedPin:$durationMs"
    }

    fun analogReadCommand(control: LabLinkControl): String {
        require(control.type == ControlType.AnalogRead) {
            "analogReadCommand só aceita controles do tipo AnalogRead."
        }

        val normalizedPin = control.pin.trim().uppercase()
        return "READ:$normalizedPin"
    }
}
