# Cuidados de hardware do LabLink

## HC-05 / HC-06

- Usar GND comum entre Arduino e modulo Bluetooth.
- Usar alimentacao adequada para o modulo.
- O RX do HC-05/HC-06 deve receber sinal em nivel adequado.
- Recomenda-se divisor resistivo quando o TX do Arduino for 5 V.
- Evitar usar os pinos 0 e 1 do Arduino nos exemplos iniciais, para nao atrapalhar o upload do codigo.
- Preferir SoftwareSerial nos exemplos didaticos iniciais.

## Motores

- Nao ligar motor diretamente nos pinos do Arduino.
- Usar driver adequado, como ponte H ou modulo compativel.
- Usar fonte adequada para motores.
- Compartilhar GND entre Arduino, driver e fonte.

## Reles e cargas

- No escopo inicial, evitar rede eletrica.
- Usar reles apenas em baixa tensao nos exemplos didaticos.
- Nao incentivar venda ou uso real de projeto sem testes adequados.

## Postura tecnica

O LabLink deve seguir a postura anti-gambiarra do Laboratorio de Circuitos: fazer funcionar, mas tambem fazer com responsabilidade.
