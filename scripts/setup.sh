#!/usr/bin/env bash

# Enable i2c (0 = on, 1 = off)
raspi-config nonint do_i2c 0

# Enable serial hardware and disable serial console
raspi-config nonint do_serial_hw 0
raspi-config nonint do_serial_cons 1

# Install tools
apt install i2c-tools openjdk-17-jdk-headless -y

# Install wiringpi
wget https://project-downloads.drogon.net/wiringpi-latest.deb
apt install ./wiringpi-latest.deb
rm wiringpi-latest.deb

# Install pi4j
curl -sSL https://pi4j.com/install | bash
