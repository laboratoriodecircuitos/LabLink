# Protocolo inicial do LabLink

## Ideia

O aplicativo envia comandos de texto via Bluetooth para o Arduino.
O Arduino interpreta o comando e executa a acao correspondente.

## Comandos iniciais previstos

- PING
- LED:ON
- LED:OFF
- PWM:0
- PWM:128
- PWM:255
- SERVO:0
- SERVO:90
- SERVO:180
- MOTOR:FWD
- MOTOR:BACK
- MOTOR:LEFT
- MOTOR:RIGHT
- MOTOR:STOP
- BUZZER:ON
- BUZZER:OFF
- RELAY:ON
- RELAY:OFF

## Respostas sugeridas do Arduino

- OK
- OK:LED_ON
- OK:LED_OFF
- OK:PWM_SET
- OK:SERVO_SET
- OK:MOTOR
- ERR:UNKNOWN_COMMAND
- ERR:INVALID_VALUE

## Observacao

Os comandos deverao ser enviados preferencialmente com quebra de linha ao final.
