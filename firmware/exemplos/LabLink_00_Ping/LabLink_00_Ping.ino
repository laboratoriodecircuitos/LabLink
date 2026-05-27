/*
  LabLink_00_Ping

  Firmware mínimo de teste para validar comunicação serial Bluetooth
  entre o aplicativo LabLink e um Arduino usando HC-05/HC-06.

  Comando esperado pelo Arduino:
    PING

  Resposta enviada pelo Arduino:
    OK:PONG

  Ligações sugeridas:
    HC-06 VCC -> 5V do Arduino
    HC-06 GND -> GND do Arduino
    HC-06 TX  -> Arduino D2
    HC-06 RX  <- Arduino D3 com divisor resistivo

  Observação:
    Evite usar os pinos 0 e 1 neste exemplo para não atrapalhar
    o upload do código pela Arduino IDE.
*/

#include <SoftwareSerial.h>

const byte LABLINK_BT_RX_PIN = 2; // Arduino recebe dados do TX do HC-06
const byte LABLINK_BT_TX_PIN = 3; // Arduino envia dados para o RX do HC-06
const long LABLINK_BT_BAUD = 9600;
const byte STATUS_LED_PIN = 13;

SoftwareSerial labLinkBluetooth(LABLINK_BT_RX_PIN, LABLINK_BT_TX_PIN);

String labLinkCommand = "";

void setup() {
  pinMode(STATUS_LED_PIN, OUTPUT);

  Serial.begin(9600);
  labLinkBluetooth.begin(LABLINK_BT_BAUD);

  Serial.println("LabLink_00_Ping iniciado.");
  Serial.println("Aguardando comando PING via Bluetooth...");

  labLinkBluetooth.println("READY:LABLINK_PING");
}

void loop() {
  readLabLinkBluetooth();
}

void readLabLinkBluetooth() {
  while (labLinkBluetooth.available() > 0) {
    char receivedChar = labLinkBluetooth.read();

    if (receivedChar == '\n') {
      labLinkCommand.trim();

      if (labLinkCommand.length() > 0) {
        handleLabLinkCommand(labLinkCommand);
      }

      labLinkCommand = "";
    } else {
      labLinkCommand += receivedChar;
    }
  }
}

void handleLabLinkCommand(String command) {
  Serial.print("Comando recebido: ");
  Serial.println(command);

  if (command == "PING") {
    digitalWrite(STATUS_LED_PIN, HIGH);
    delay(80);
    digitalWrite(STATUS_LED_PIN, LOW);

    labLinkBluetooth.println("OK:PONG");
    Serial.println("Resposta enviada: OK:PONG");
  } else {
    labLinkBluetooth.print("ERR:UNKNOWN_COMMAND:");
    labLinkBluetooth.println(command);

    Serial.print("Comando desconhecido: ");
    Serial.println(command);
  }
}
