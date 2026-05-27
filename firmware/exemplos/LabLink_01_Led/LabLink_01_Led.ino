/*
  LabLink_01_Led

  Firmware de teste para validar comunicação Bluetooth entre o app LabLink
  e um Arduino usando HC-05/HC-06.

  Comandos aceitos:
    PING
    LED:ON
    LED:OFF

  Respostas enviadas:
    OK:PONG
    OK:LED_ON
    OK:LED_OFF

  Ligações sugeridas:
    HC-06 VCC -> 5V do Arduino
    HC-06 GND -> GND do Arduino
    HC-06 TX  -> Arduino D2
    HC-06 RX  <- Arduino D3 com divisor resistivo

  LED controlado:
    LED interno do Arduino no pino 13.
*/

#include <SoftwareSerial.h>

const byte LABLINK_BT_RX_PIN = 2; // Arduino recebe dados do TX do HC-06
const byte LABLINK_BT_TX_PIN = 3; // Arduino envia dados para o RX do HC-06
const long LABLINK_BT_BAUD = 9600;

const byte LED_PIN = 13;

SoftwareSerial labLinkBluetooth(LABLINK_BT_RX_PIN, LABLINK_BT_TX_PIN);

String labLinkCommand = "";

void setup() {
  pinMode(LED_PIN, OUTPUT);
  digitalWrite(LED_PIN, LOW);

  Serial.begin(9600);
  labLinkBluetooth.begin(LABLINK_BT_BAUD);

  Serial.println("LabLink_01_Led iniciado.");
  Serial.println("Comandos disponiveis: PING, LED:ON, LED:OFF");

  labLinkBluetooth.println("READY:LABLINK_LED");
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
    labLinkBluetooth.println("OK:PONG");
    Serial.println("Resposta enviada: OK:PONG");
  }
  else if (command == "LED:ON") {
    digitalWrite(LED_PIN, HIGH);

    labLinkBluetooth.println("OK:LED_ON");
    Serial.println("LED ligado.");
    Serial.println("Resposta enviada: OK:LED_ON");
  }
  else if (command == "LED:OFF") {
    digitalWrite(LED_PIN, LOW);

    labLinkBluetooth.println("OK:LED_OFF");
    Serial.println("LED desligado.");
    Serial.println("Resposta enviada: OK:LED_OFF");
  }
  else {
    labLinkBluetooth.print("ERR:UNKNOWN_COMMAND:");
    labLinkBluetooth.println(command);

    Serial.print("Comando desconhecido: ");
    Serial.println(command);
  }
}
