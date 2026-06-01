package br.com.laboratoriodecircuitos.lablink.core.boards

object BoardProfiles {
    private val unoPwmPins = setOf(3, 5, 6, 9, 10, 11)
    private val megaPwmPins = (2..13).toSet() + setOf(44, 45, 46)

    val arduinoUno = BoardProfile(
        type = BoardType.ArduinoUno,
        description = "Arduino Uno com digitais D0-D13, analógicos A0-A5 e PWM em D3, D5, D6, D9, D10 e D11.",
        pins = buildList {
            addDigitalRange(
                range = 0..13,
                pwmPins = unoPwmPins,
                servoPins = setOf(9, 10),
                reservedPins = setOf(0, 1, 2, 3),
            )
            addAnalogRange(0..5)
        },
    )

    val arduinoNano = BoardProfile(
        type = BoardType.ArduinoNano,
        description = "Arduino Nano com digitais D0-D13, analógicos A0-A7 e PWM em D3, D5, D6, D9, D10 e D11.",
        pins = buildList {
            addDigitalRange(
                range = 0..13,
                pwmPins = unoPwmPins,
                servoPins = setOf(9, 10),
                reservedPins = setOf(0, 1, 2, 3),
            )
            addAnalogRange(0..7)
        },
    )

    val arduinoProMini = BoardProfile(
        type = BoardType.ArduinoProMini,
        description = "Arduino Pro Mini com digitais D0-D13, analógicos A0-A5 e PWM em D3, D5, D6, D9, D10 e D11.",
        pins = buildList {
            addDigitalRange(
                range = 0..13,
                pwmPins = unoPwmPins,
                servoPins = setOf(9, 10),
                reservedPins = setOf(0, 1, 2, 3),
            )
            addAnalogRange(0..5)
        },
    )

    val arduinoMega = BoardProfile(
        type = BoardType.ArduinoMega,
        description = "Arduino Mega com digitais D0-D53, analógicos A0-A15 e PWM em D2-D13, D44, D45 e D46.",
        pins = buildList {
            addDigitalRange(
                range = 0..53,
                pwmPins = megaPwmPins,
                servoPins = megaPwmPins,
                reservedPins = setOf(0, 1),
            )
            addAnalogRange(0..15)
        },
    )

    val supportedBoards: List<BoardProfile> = listOf(
        arduinoUno,
        arduinoNano,
        arduinoProMini,
        arduinoMega,
    )

    val defaultBoard: BoardProfile = arduinoUno

    fun findByType(type: BoardType): BoardProfile {
        return when (type) {
            BoardType.ArduinoUnoNano -> arduinoUno
            else -> supportedBoards.firstOrNull { it.type == type } ?: defaultBoard
        }
    }

    private fun MutableList<BoardPin>.addDigitalRange(
        range: IntRange,
        pwmPins: Set<Int>,
        servoPins: Set<Int>,
        reservedPins: Set<Int>,
    ) {
        range.forEach { number ->
            addDigitalPin(
                id = "D$number",
                supportsPwm = number in pwmPins,
                supportsServo = number in servoPins,
                isRecommended = number !in reservedPins,
                note = when (number) {
                    0, 1 -> "Reservado para RX/TX serial em muitos projetos."
                    2, 3 -> "Reservado no firmware HC-05/HC-06 com SoftwareSerial."
                    13 -> "LED interno em muitas placas Arduino."
                    else -> null
                },
            )
        }
    }

    private fun MutableList<BoardPin>.addDigitalPin(
        id: String,
        supportsPwm: Boolean = false,
        supportsServo: Boolean = false,
        isRecommended: Boolean = true,
        note: String? = null,
    ) {
        add(
            BoardPin(
                id = id,
                label = id,
                supportsDigitalOutput = true,
                supportsPwm = supportsPwm,
                supportsServo = supportsServo,
                supportsAnalogRead = false,
                isRecommended = isRecommended,
                note = note,
            ),
        )
    }

    private fun MutableList<BoardPin>.addAnalogRange(range: IntRange) {
        range.forEach { number ->
            addAnalogPin(id = "A$number")
        }
    }

    private fun MutableList<BoardPin>.addAnalogPin(id: String) {
        add(
            BoardPin(
                id = id,
                label = id,
                supportsDigitalOutput = true,
                supportsPwm = false,
                supportsServo = false,
                supportsAnalogRead = true,
                note = "Também pode ser usado como entrada/saída digital em muitos projetos Arduino.",
            ),
        )
    }
}
