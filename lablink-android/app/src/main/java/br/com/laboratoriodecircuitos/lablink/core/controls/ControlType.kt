package br.com.laboratoriodecircuitos.lablink.core.controls

enum class ControlType(
    val displayName: String,
    val description: String,
) {
    DigitalToggle(
        displayName = "Liga / Desliga",
        description = "Controle digital para LED, relé, buzzer ou módulo simples.",
    ),

    PwmSlider(
        displayName = "Slider PWM",
        description = "Controle de intensidade, brilho ou velocidade usando saída PWM.",
    ),

    ServoSlider(
        displayName = "Servo",
        description = "Controle de posição angular para servo motor.",
    ),

    PulseButton(
        displayName = "Pulso",
        description = "Acionamento momentâneo por um tempo definido.",
    ),

    AnalogRead(
        displayName = "Leitura",
        description = "Exibição de valor lido de sensor analógico ou digital.",
    ),
}
