package br.com.laboratoriodecircuitos.lablink.core.boards

import br.com.laboratoriodecircuitos.lablink.core.controls.ControlType

object BoardPinValidator {
    fun availablePinsFor(
        board: BoardProfile,
        controlType: ControlType,
        usedPins: Set<String>,
    ): List<BoardPin> {
        val normalizedUsedPins = usedPins.map { normalizePin(it) }.toSet()

        return board.pins.filter { pin ->
            supportsControlType(pin = pin, controlType = controlType) &&
                pin.isRecommended &&
                normalizePin(pin.id) !in normalizedUsedPins
        }
    }

    fun validatePinForControl(
        board: BoardProfile,
        controlType: ControlType,
        pin: String,
        usedPins: Set<String>,
    ): PinValidationResult {
        val normalizedPin = normalizePin(pin)

        if (normalizedPin.isBlank()) {
            return PinValidationResult.EmptyPin
        }

        val normalizedUsedPins = usedPins.map { normalizePin(it) }.toSet()

        if (normalizedPin in normalizedUsedPins) {
            return PinValidationResult.PinAlreadyUsed(pin = normalizedPin)
        }

        val boardPin = board.pins.firstOrNull {
            normalizePin(it.id) == normalizedPin
        } ?: return PinValidationResult.PinNotFound(
            pin = normalizedPin,
            boardName = board.displayName,
        )

        if (!supportsControlType(pin = boardPin, controlType = controlType)) {
            return PinValidationResult.UnsupportedFunction(
                pin = normalizedPin,
                functionName = controlType.displayName,
            )
        }

        return PinValidationResult.Valid
    }

    fun supportsControlType(
        pin: BoardPin,
        controlType: ControlType,
    ): Boolean {
        return when (controlType) {
            ControlType.DigitalToggle -> pin.supportsDigitalOutput
            ControlType.PwmSlider -> pin.supportsPwm
            ControlType.ServoSlider -> pin.supportsServo || pin.supportsDigitalOutput
            ControlType.PulseButton -> pin.supportsDigitalOutput
            ControlType.AnalogRead -> pin.supportsAnalogRead
        }
    }

    fun normalizePin(pin: String): String {
        val cleaned = pin.trim().uppercase()

        return when {
            cleaned.isBlank() -> ""
            cleaned.startsWith("D") -> cleaned
            cleaned.startsWith("A") -> cleaned
            cleaned.all { it.isDigit() } -> "D$cleaned"
            else -> cleaned
        }
    }

    fun validationMessage(result: PinValidationResult): String? {
        return when (result) {
            PinValidationResult.Valid -> null
            PinValidationResult.EmptyPin -> "Informe um pino."
            is PinValidationResult.PinNotFound -> "O pino ${result.pin} não existe na placa ${result.boardName}."
            is PinValidationResult.PinAlreadyUsed -> "O pino ${result.pin} já está sendo usado em outro controle."
            is PinValidationResult.UnsupportedFunction -> "O pino ${result.pin} não suporta ${result.functionName}."
        }
    }
}
