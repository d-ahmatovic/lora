#!/bin/bash

# Build-Script für LoRa Senden-Anwendung
# Kompiliert alle Java-Dateien und erstellt ein Fat-JAR mit eingebetteten Abhängigkeiten

set -e  # Script beenden bei Fehlern

# Verzeichnisse definieren
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SRC_DIR="$PROJECT_ROOT/src"
BUILD_DIR="$PROJECT_ROOT/build/senden"
CLASSES_DIR="$BUILD_DIR/classes"
JAR_NAME="lora-senden.jar"
MAIN_CLASS="Main"

# Externe Bibliotheken
JSERIAL_JAR="$SRC_DIR/jSerialComm-2.10.4.jar"
PI4J_JAR="/opt/pi4j/lib/pi4j-core.jar"

echo "LoRa Senden Build-Script"
echo "========================="
echo "Projekt-Root: $PROJECT_ROOT"
echo ""

# Prüfe ob externe Bibliotheken vorhanden sind
echo "Prüfe Abhängigkeiten..."
if [ ! -f "$JSERIAL_JAR" ]; then
    echo "FEHLER: jSerialComm-2.10.4.jar nicht gefunden in $JSERIAL_JAR"
    exit 1
fi

if [ ! -f "$PI4J_JAR" ]; then
    echo "FEHLER: pi4j-core.jar nicht gefunden in $PI4J_JAR"
    echo "Installiere pi4j oder passe den Pfad an."
    exit 1
fi

echo "✓ jSerialComm: $JSERIAL_JAR"
echo "✓ Pi4J: $PI4J_JAR"
echo ""

# Build-Verzeichnisse erstellen
echo "Erstelle Build-Verzeichnisse..."
mkdir -p "$CLASSES_DIR"
mkdir -p "$BUILD_DIR/extracted"

# Java-Dateien kompilieren
echo "Kompiliere Java-Dateien..."
javac -cp "$JSERIAL_JAR:$PI4J_JAR" \
      -d "$CLASSES_DIR" \
      "$SRC_DIR/shared/"*.java \
      "$SRC_DIR/senden/"*.java

if [ $? -ne 0 ]; then
    echo "FEHLER: Kompilierung fehlgeschlagen!"
    exit 1
fi

# JAR-Abhängigkeiten extrahieren
echo "Extrahiere JAR-Abhängigkeiten..."
cd "$BUILD_DIR/extracted"

echo "  - Extrahiere jSerialComm..."
jar -xf "$JSERIAL_JAR"
# META-INF aus erster JAR löschen um Konflikte zu vermeiden
rm -rf META-INF/

echo "  - Extrahiere Pi4J..."
jar -xf "$PI4J_JAR"
# META-INF aus zweiter JAR löschen um Konflikte zu vermeiden
rm -rf META-INF/

# Fat-JAR erstellen
echo "Erstelle Fat-JAR..."
cd "$CLASSES_DIR"
# Alle Klassen und extrahierte JAR-Inhalte zusammenführen
cp -r "$BUILD_DIR/extracted/"* .

# Manifest erstellen
echo "Main-Class: $MAIN_CLASS" > manifest.txt

# JAR-Datei erstellen
jar -cfm "$BUILD_DIR/$JAR_NAME" manifest.txt *

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ Build erfolgreich!"
    echo "✓ JAR-Datei erstellt: $BUILD_DIR/$JAR_NAME"
    echo "✓ Eingebettete Bibliotheken:"
    echo "  - jSerialComm-2.10.4.jar"
    echo "  - pi4j-core.jar"
    echo ""
    echo "Ausführen mit: java -jar $BUILD_DIR/$JAR_NAME"
else
    echo "FEHLER: JAR-Erstellung fehlgeschlagen!"
    exit 1
fi

# Aufräumen
echo "Räume temporäre Dateien auf..."
rm -rf "$BUILD_DIR/classes" "$BUILD_DIR/extracted"

echo "Build abgeschlossen!" 