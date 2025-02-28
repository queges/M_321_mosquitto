# MQTT-System mit Dummy-Sensoren

## 1. Einleitung
Diese Dokumentation beschreibt den Aufbau und die Erweiterung eines MQTT-Systems auf einer Ubuntu-VM. Ziel ist es, ein MQTT-System mit mehreren Dummy-Sensoren zu erstellen, die fortlaufend Daten an einen MQTT-Broker senden.

## 2. Theoretische Grundlagen

### 2.1 Komponenten einer MQTT-Applikation
Eine MQTT-Applikation besteht aus drei Hauptkomponenten:
1. **Broker** – Vermittelt Nachrichten zwischen Publisher und Subscriber.
2. **Publisher** – Sendet Nachrichten an ein bestimmtes Topic.
3. **Subscriber** – Abonniert Topics und empfängt Nachrichten.

### 2.2 Nachrichtenzustellung
Daten gelangen vom Sender zum Empfänger über ein **Publish-Subscribe-Modell**. Der Publisher sendet Nachrichten an ein Topic, das der Broker verwaltet. Abonnenten, die dieses Topic abonniert haben, erhalten die Nachrichten.

### 2.3 Empfang aller Nachrichten
Ein Subscriber kann alle Nachrichten empfangen, indem er ein **Wildcard-Topic** (`#` oder `+`) abonniert.

### 2.4 Anwendungsgebiete von MQTT
- IoT-Systeme (z. B. smarte Sensoren, Home Automation)
- Industrielle Automatisierung
- Überwachungssysteme
- Kommunikationssysteme für Fahrzeuge

### 2.5 Vorteile von MQTT
- Leichtgewichtig und ressourcenschonend
- Unterstützt geringe Bandbreite und hohe Latenzen
- Skalierbar für viele Geräte

## 3. Praktische Umsetzung

### 3.1 Installation der benötigten Software
Die folgenden Komponenten müssen auf der Ubuntu-VM installiert werden:

```bash
sudo apt update
sudo apt install -y docker-ce mosquitto-clients
```

### 3.2 Einrichtung des MQTT-Brokers mit Docker
Ein MQTT-Broker wird als Docker-Container mit Eclipse Mosquitto bereitgestellt.

#### Konfigurationsdatei `mosquitto.conf`
Erstelle eine Datei `mosquitto.conf` mit folgendem Inhalt:

```plaintext
listener 1883
allow_anonymous true
```

#### Starten des Containers mit der Konfiguration

```bash
docker run -d --name mosquitto \
    -p 1883:1883 -p 9001:9001 \
    -v $(pwd)/mosquitto/config/mosquitto.conf:/mosquitto/config/mosquitto.conf \
    eclipse-mosquitto
```

### 3.3 Testen des Brokers

#### Abonnieren eines Topics:
```bash
mosquitto_sub -t test
```

#### Senden einer Nachricht:
```bash
mosquitto_pub -t test -m "hello world"
```

**Erwartetes Ergebnis:** Die Nachricht `"hello world"` erscheint im ersten Terminal.

## 4. Erweiterung mit Dummy-Sensoren

### 4.1 Manuelle Simulation mehrerer Sensoren
Jeder Sensor sendet eine Zufallszahl an ein spezifisches Topic im Sekundentakt. Dies kann durch mehrere Terminals simuliert werden:

```bash
while true; do mosquitto_pub -t sensor1 -m $RANDOM; sleep 1; done
```

```bash
while true; do mosquitto_pub -t sensor2 -m $RANDOM; sleep 1; done
```

### 4.2 Automatisierung mit einem Bash-Skript
Ein Skript zur Simulation mehrerer Sensoren:

```bash
#!/bin/bash
SENSOR_NAME=$1
while true; do
    VALUE=$RANDOM
    mosquitto_pub -t "$SENSOR_NAME" -m "$VALUE"
    sleep 1
done
```

#### Speichern als `sensor.sh` und ausführbar machen:
```bash
chmod +x sensor.sh
```

#### Aufruf für mehrere Sensoren:
```bash
./sensor.sh sensor1 &
./sensor.sh sensor2 &
```

## 5. Fazit
Diese Dokumentation beschreibt den Aufbau eines MQTT-Systems mit Docker und die Simulation von Sensoren. Durch die Automatisierung mit Bash-Skripten wird die Verwaltung mehrerer Sensoren erleichtert. Dies bildet die Grundlage für erweiterte IoT-Anwendungen.
```
