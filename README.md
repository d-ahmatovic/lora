# LoRa Kommunikationssystem

Dieses Projekt implementiert ein LoRa-basiertes Kommunikationssystem mit zwei separaten Anwendungen: einer zum Senden und einer zum Empfangen von Sensordaten.

## Projektstruktur

```
LoRa/
├── src/
│   ├── shared/           # Gemeinsam genutzte Klassen
│   │   ├── Encryption.java
│   │   └── Selection.java
│   ├── empfangen/        # Empfänger-Anwendung
│   │   └── Main.java
│   ├── senden/           # Sender-Anwendung
│   │   ├── Main.java
│   │   ├── Sensor.java
│   │   └── SensorData.java
│   └── jSerialComm-2.10.4.jar  # SerialPort-Bibliothek
├── scripts/              # Build-Scripts
│   ├── build-empfangen.sh
│   └── build-senden.sh
└── build/               # Wird automatisch erstellt
    ├── empfangen/
    └── senden/
```

## Abhängigkeiten

- **Java JDK 8+** (javac und jar müssen verfügbar sein)
- **jSerialComm-2.10.4.jar** (bereits im Projekt enthalten)
- **Pi4J Core** (`/opt/pi4j/lib/pi4j-core.jar`) - nur für Sender-Anwendung

## Berechtigungen für serielle Ports (Linux)

⚠️ **Wichtig:** Unter Linux benötigen Sie Berechtigungen für den Zugriff auf serielle Ports!

### Option 1: Als Root ausführen
```bash
sudo java -jar build/empfangen/lora-empfangen.jar
sudo java -jar build/senden/lora-senden.jar
```

### Option 2: Benutzer zur dialout-Gruppe hinzufügen (empfohlen)
```bash
sudo usermod -a -G dialout $USER
```
**Nach diesem Befehl müssen Sie sich ab- und wieder anmelden!**

### Berechtigungen prüfen
```bash
# Serielle Ports anzeigen
ls -l /dev/ttyUSB* /dev/ttyACM*

# Ihre Gruppenmitgliedschaft prüfen
groups
```

## Build-Prozess

Das Projekt verwendet zwei separate Build-Scripts, die jeweils eine eigenständige JAR-Datei mit allen eingebetteten Abhängigkeiten (Fat-JAR) erstellen.

### Empfänger-Anwendung bauen

```bash
./scripts/build-empfangen.sh
```

Dieser Befehl:
1. Kompiliert alle Java-Dateien aus `src/shared/` und `src/empfangen/`
2. Extrahiert die Inhalte von `jSerialComm-2.10.4.jar`
3. Erstellt eine Fat-JAR-Datei: `build/empfangen/lora-empfangen.jar`

### Sender-Anwendung bauen

```bash
./scripts/build-senden.sh
```

Dieser Befehl:
1. Kompiliert alle Java-Dateien aus `src/shared/` und `src/senden/`
2. Extrahiert die Inhalte von `jSerialComm-2.10.4.jar` und `pi4j-core.jar`
3. Erstellt eine Fat-JAR-Datei: `build/senden/lora-senden.jar`

**Hinweis:** Das Sender-Build-Script benötigt Pi4J, das unter `/opt/pi4j/lib/pi4j-core.jar` installiert sein muss.

## Anwendungen ausführen

Nach einem erfolgreichen Build können die Anwendungen direkt ausgeführt werden:

### Empfänger starten
```bash
java -jar build/empfangen/lora-empfangen.jar
```

### Sender starten
```bash
java -jar build/senden/lora-senden.jar
```

## Features

- **Fat-JAR**: Alle Abhängigkeiten sind in die JAR-Dateien eingebettet
- **Gemeinsame Codebasis**: Beide Anwendungen nutzen die geteilten Klassen aus `src/shared/`
- **Automatisches Build**: Vollständig automatisierte Build-Scripts
- **Fehlerbehandlung**: Scripts brechen bei Kompilierungsfehlern ab
- **Saubere Aufräumung**: Temporäre Build-Dateien werden automatisch entfernt

## Funktionsweise

### Empfänger
- Öffnet einen seriellen Port
- Empfängt verschlüsselte Datenpakete im Format `OETTI|temp|pressure|OETTI`
- Entschlüsselt die Daten mit Caesar-Cipher (Key=3)
- Zeigt Temperatur und Luftdruck an

### Sender
- Öffnet einen seriellen Port
- Generiert Sensordaten (Temperatur und Luftdruck)
- Verschlüsselt die Daten mit Caesar-Cipher (Key=3)
- Sendet Datenpakete im Format `OETTI|temp|pressure|OETTI`

## Entwicklung

### Neue Features hinzufügen
1. Java-Dateien im entsprechenden Verzeichnis (`src/empfangen/`, `src/senden/`, oder `src/shared/`) hinzufügen
2. Build-Script ausführen
3. Testen

### Debugging
Bei Build-Fehlern zeigen die Scripts detaillierte Fehlermeldungen an. Häufige Probleme:
- JDK nicht installiert oder nicht im PATH
- Nicht ausführbare Script-Dateien (mit `chmod +x` beheben)
- Syntaxfehler in Java-Dateien

### Häufige Runtime-Probleme
- **`NoSuchElementException: No line found`**: Meist ein Berechtigungsproblem für serielle Ports
  - Lösung: Als root ausführen oder Benutzer zur `dialout`-Gruppe hinzufügen
- **`Port kann nicht geöffnet werden`**: Port bereits in Verwendung oder keine Berechtigung
- **`Port nicht gefunden`**: Gerät nicht angeschlossen oder falscher Port gewählt

## Lizenz

Dieses Projekt verwendet die jSerialComm-Bibliothek. Weitere Informationen zur Lizenz finden Sie in der entsprechenden Dokumentation. 