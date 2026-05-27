package br.com.laboratoriodecircuitos.lablink.core.controls

object DefaultControls {
    val pin13DigitalOutput = LabLinkControl(
        id = "digital_output_13",
        type = ControlType.DigitalToggle,
        name = "Controle principal",
        pin = "13",
        isOn = false,
    )
}
