# Decisao 0001 — Fundacao do LabLink

## Status

Consolidada.

## Decisao

O LabLink sera iniciado como aplicativo Android nativo em Kotlin para controlar Arduino via Bluetooth Classic usando modulos HC-05 ou HC-06.

## Publico

Membros do Laboratorio de Circuitos que desejam controlar projetos eletronicos pelo celular.

## Escopo inicial

- Android nativo.
- Kotlin.
- Bluetooth Classic / SPP.
- Arduino com HC-05 ou HC-06.
- Controles basicos: LED, PWM, servo, motores e terminal serial.

## Fora do escopo inicial

- iOS.
- BLE.
- ESP32 BLE.
- Gerador automatico de codigo .ino dentro do aplicativo.
- Plataforma multiplataforma.

## Observacao

A compatibilidade com iOS devera ser tratada futuramente por uma linha baseada em BLE, provavelmente com ESP32.
