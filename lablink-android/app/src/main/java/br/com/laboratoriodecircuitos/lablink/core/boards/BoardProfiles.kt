package br.com.laboratoriodecircuitos.lablink.core.boards

object BoardProfiles {
    val arduinoUnoNano = BoardProfile(
        type = BoardType.ArduinoUnoNano,
        description = "Perfil inicial para Arduino Uno e Arduino Nano. Neste firmware, D2 e D3 ficam reservados para o HC-06 via SoftwareSerial.",
        pins = buildList {
            addDigitalPin(id = "D4")
            addDigitalPin(id = "D5", supportsPwm = true)
            addDigitalPin(id = "D6", supportsPwm = true)
            addDigitalPin(id = "D7")
            addDigitalPin(id = "D8")
            addDigitalPin(id = "D9", supportsPwm = true, supportsServo = true)
            addDigitalPin(id = "D10", supportsPwm = true, supportsServo = true)
            addDigitalPin(id = "D11", supportsPwm = true)
            addDigitalPin(id = "D12")
            addDigitalPin(
                id = "D13",
                note = "LED interno em muitas placas Arduino.",
            )

            addAnalogPin(id = "A0")
            addAnalogPin(id = "A1")
            addAnalogPin(id = "A2")
            addAnalogPin(id = "A3")
            addAnalogPin(id = "A4")
            addAnalogPin(id = "A5")
        },
    )

    val supportedBoards: List<BoardProfile> = listOf(
        arduinoUnoNano,
    )

    val defaultBoard: BoardProfile = arduinoUnoNano

    fun findByType(type: BoardType): BoardProfile {
        return supportedBoards.firstOrNull { it.type == type } ?: defaultBoard
    }

    private fun MutableList<BoardPin>.addDigitalPin(
        id: String,
        supportsPwm: Boolean = false,
        supportsServo: Boolean = false,
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
                note = note,
            ),
        )
    }

    private fun MutableList<BoardPin>.addAnalogPin(
        id: String,
    ) {
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


