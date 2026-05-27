/*
  LabLink — Exemplo 02: Controles Genericos

  Mantem compatibilidade com:
  PING, LED:ON, LED:OFF

  Adiciona:
  DIGITAL:D2:ON
  DIGITAL:D2:OFF
  PWM:D3:128
  PULSE:D4:500
  READ:A0
*/

#include <SoftwareSerial.h>

const byte LABLINK_BT_RX_PIN = 2; // Arduino recebe dados do TX do HC-06
const byte LABLINK_BT_TX_PIN = 3; // Arduino envia dados para o RX do HC-06
const long LABLINK_BT_BAUD = 9600;
const int BUILTIN_LED_PIN = 13;
const unsigned long COMMAND_IDLE_TIMEOUT_MS = 80;

SoftwareSerial labLinkBluetooth(LABLINK_BT_RX_PIN, LABLINK_BT_TX_PIN);

String inputLine = "";
unsigned long lastReceivedAt = 0;

void setup() {
  Serial.begin(9600);
  labLinkBluetooth.begin(LABLINK_BT_BAUD);
  pinMode(BUILTIN_LED_PIN, OUTPUT);
  digitalWrite(BUILTIN_LED_PIN, LOW);
  labLinkBluetooth.println("OK:LABLINK_READY");
}

void loop() {
  readSerialCommands();
}

void readSerialCommands() {
  while (labLinkBluetooth.available() > 0) {
    char receivedChar = (char)labLinkBluetooth.read();

    if (receivedChar == '\n' || receivedChar == '\r') {
      if (inputLine.length() > 0) {
        processCommand(inputLine);
        inputLine = "";
      }
    } else {
      inputLine += receivedChar;
      lastReceivedAt = millis();

      if (inputLine.length() > 64) {
        inputLine = "";
        labLinkBluetooth.println("ERR:COMMAND_TOO_LONG");
      }
    }
  }

  if (inputLine.length() > 0 && millis() - lastReceivedAt >= COMMAND_IDLE_TIMEOUT_MS) {
    processCommand(inputLine);
    inputLine = "";
  }
}

void processCommand(String command) {
  command.trim();
  command.toUpperCase();

  if (command.length() == 0) return;

  if (command == "PING") {
    labLinkBluetooth.println("OK:PONG");
    return;
  }

  if (command == "LED:ON") {
    digitalWrite(BUILTIN_LED_PIN, HIGH);
    labLinkBluetooth.println("OK:LED_ON");
    return;
  }

  if (command == "LED:OFF") {
    digitalWrite(BUILTIN_LED_PIN, LOW);
    labLinkBluetooth.println("OK:LED_OFF");
    return;
  }

  if (command.startsWith("DIGITAL:")) {
    handleDigitalCommand(command);
    return;
  }

  if (command.startsWith("PWM:")) {
    handlePwmCommand(command);
    return;
  }

  if (command.startsWith("PULSE:")) {
    handlePulseCommand(command);
    return;
  }

  if (command.startsWith("READ:")) {
    handleReadCommand(command);
    return;
  }

  labLinkBluetooth.print("ERR:UNKNOWN_COMMAND:");
  labLinkBluetooth.println(command);
}

void handleDigitalCommand(String command) {
  int firstSeparator = command.indexOf(':');
  int secondSeparator = command.indexOf(':', firstSeparator + 1);

  if (secondSeparator < 0) {
    labLinkBluetooth.println("ERR:INVALID_DIGITAL_FORMAT");
    return;
  }

  String pinToken = command.substring(firstSeparator + 1, secondSeparator);
  String stateToken = command.substring(secondSeparator + 1);

  int pin = parseDigitalPin(pinToken);

  if (!isValidDigitalPin(pin)) {
    labLinkBluetooth.println("ERR:INVALID_DIGITAL_PIN");
    return;
  }

  if (stateToken != "ON" && stateToken != "OFF") {
    labLinkBluetooth.println("ERR:INVALID_DIGITAL_STATE");
    return;
  }

  pinMode(pin, OUTPUT);
  digitalWrite(pin, stateToken == "ON" ? HIGH : LOW);

  labLinkBluetooth.print("OK:DIGITAL:");
  labLinkBluetooth.print(pinLabel(pin));
  labLinkBluetooth.print(":");
  labLinkBluetooth.println(stateToken);
}

void handlePwmCommand(String command) {
  int firstSeparator = command.indexOf(':');
  int secondSeparator = command.indexOf(':', firstSeparator + 1);

  if (secondSeparator < 0) {
    labLinkBluetooth.println("ERR:INVALID_PWM_FORMAT");
    return;
  }

  String pinToken = command.substring(firstSeparator + 1, secondSeparator);
  String valueToken = command.substring(secondSeparator + 1);

  int pin = parseDigitalPin(pinToken);
  int value = valueToken.toInt();

  if (!isValidPwmPin(pin)) {
    labLinkBluetooth.println("ERR:INVALID_PWM_PIN");
    return;
  }

  if (value < 0 || value > 255) {
    labLinkBluetooth.println("ERR:INVALID_PWM_VALUE");
    return;
  }

  pinMode(pin, OUTPUT);
  analogWrite(pin, value);

  labLinkBluetooth.print("OK:PWM:");
  labLinkBluetooth.print(pinLabel(pin));
  labLinkBluetooth.print(":");
  labLinkBluetooth.println(value);
}

void handlePulseCommand(String command) {
  int firstSeparator = command.indexOf(':');
  int secondSeparator = command.indexOf(':', firstSeparator + 1);

  if (secondSeparator < 0) {
    labLinkBluetooth.println("ERR:INVALID_PULSE_FORMAT");
    return;
  }

  String pinToken = command.substring(firstSeparator + 1, secondSeparator);
  String durationToken = command.substring(secondSeparator + 1);

  int pin = parseDigitalPin(pinToken);
  int durationMs = durationToken.toInt();

  if (!isValidDigitalPin(pin)) {
    labLinkBluetooth.println("ERR:INVALID_PULSE_PIN");
    return;
  }

  if (durationMs <= 0 || durationMs > 5000) {
    labLinkBluetooth.println("ERR:INVALID_PULSE_DURATION");
    return;
  }

  pinMode(pin, OUTPUT);
  digitalWrite(pin, HIGH);
  delay(durationMs);
  digitalWrite(pin, LOW);

  labLinkBluetooth.print("OK:PULSE:");
  labLinkBluetooth.print(pinLabel(pin));
  labLinkBluetooth.print(":");
  labLinkBluetooth.println(durationMs);
}

void handleReadCommand(String command) {
  int firstSeparator = command.indexOf(':');

  if (firstSeparator < 0) {
    labLinkBluetooth.println("ERR:INVALID_READ_FORMAT");
    return;
  }

  String pinToken = command.substring(firstSeparator + 1);
  int analogPin = parseAnalogPin(pinToken);

  if (!isValidAnalogPin(analogPin)) {
    labLinkBluetooth.println("ERR:INVALID_ANALOG_PIN");
    return;
  }

  int value = analogRead(analogPin);

  labLinkBluetooth.print("OK:READ:");
  labLinkBluetooth.print(pinToken);
  labLinkBluetooth.print(":");
  labLinkBluetooth.println(value);
}

int parseDigitalPin(String token) {
  token.trim();
  token.toUpperCase();

  if (token.startsWith("D")) {
    token = token.substring(1);
  }

  return token.toInt();
}

int parseAnalogPin(String token) {
  token.trim();
  token.toUpperCase();

  if (token == "A0") return A0;
  if (token == "A1") return A1;
  if (token == "A2") return A2;
  if (token == "A3") return A3;
  if (token == "A4") return A4;
  if (token == "A5") return A5;

  return -1;
}

bool isValidDigitalPin(int pin) {
  return pin >= 2 && pin <= 13;
}

bool isValidPwmPin(int pin) {
  return pin == 3 || pin == 5 || pin == 6 || pin == 9 || pin == 10 || pin == 11;
}

bool isValidAnalogPin(int pin) {
  return pin == A0 || pin == A1 || pin == A2 || pin == A3 || pin == A4 || pin == A5;
}

String pinLabel(int pin) {
  return "D" + String(pin);
}


