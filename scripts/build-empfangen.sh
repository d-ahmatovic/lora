#!/bin/bash

# Build-Script für LoRa Empfangen-Anwendung
# Kompiliert alle Java-Dateien und erstellt ein Fat-JAR mit eingebetteten Abhängigkeiten

set -e  # Script beenden bei Fehlern

# Verzeichnisse definieren
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SRC_DIR="$PROJECT_ROOT/src"
BUILD_DIR="$PROJECT_ROOT/build/empfangen"
CLASSES_DIR="$BUILD_DIR/classes"
JAR_NAME="lora-empfangen.jar"
MAIN_CLASS="Main"

echo "LoRa Empfangen Build-Script"
echo "============================"
echo "Projekt-Root: $PROJECT_ROOT"
echo ""

# Build-Verzeichnisse erstellen
echo "Erstelle Build-Verzeichnisse..."
mkdir -p "$CLASSES_DIR"
mkdir -p "$BUILD_DIR/extracted"

# Java-Dateien kompilieren
echo "Kompiliere Java-Dateien..."
javac -cp "$SRC_DIR/jSerialComm-2.10.4.jar" \
      -d "$CLASSES_DIR" \
      "$SRC_DIR/shared/"*.java \
      "$SRC_DIR/empfangen/"*.java

if [ $? -ne 0 ]; then
    echo "FEHLER: Kompilierung fehlgeschlagen!"
    exit 1
fi

# JAR-Abhängigkeiten extrahieren
echo "Extrahiere JAR-Abhängigkeiten..."
cd "$BUILD_DIR/extracted"
jar -xf "$SRC_DIR/jSerialComm-2.10.4.jar"
# META-INF aus extrahierter JAR löschen um Konflikte zu vermeiden
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