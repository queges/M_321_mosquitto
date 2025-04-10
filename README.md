# Inhaltsverzeichnis

- [MQTT-System mit Dummy-Sensoren](#mqtt-system-mit-dummy-sensoren)
- [MQTT Publisher mit Java und Maven](#mqtt-publisher-mit-java-und-maven)
- [MQTT-System Migration in Docker Compose](#mqtt-system-migration-in-docker-compose)
- [Monitoring mit Prometheus, CAdvisor, Grafana und MQTT](#monitoring-mit-prometheus,-cadvisor,-grafana-und-mqtt)

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

## 5. Virtualisierung

### 5.1 Connection Settings
![img](imgs/ConnectionSettings.png)

### 5.2 Dashboard
![img](imgs/grafanaDashboard.png)


## 6. Fazit
Diese Dokumentation beschreibt den Aufbau eines MQTT-Systems mit Docker und die Simulation von Sensoren. Durch die Automatisierung mit Bash-Skripten wird die Verwaltung mehrerer Sensoren erleichtert. Dies bildet die Grundlage für erweiterte IoT-Anwendungen.

# MQTT Publisher mit Java und Maven

## 1. Einführung
Dieses Projekt implementiert einen MQTT-Publisher in Java, der periodisch Werte an ein spezifiziertes Topic sendet. Die Werte werden mit der **sinusförmigen Funktion** generiert.

## 2. Projekt-Erstellung mit Maven
Das Projekt wird mit **Maven** erstellt. Dazu wird folgender Befehl verwendet:

```bash
mvn archetype:generate -DartifactId=MavenDemo -DgroupId=ch.wiss.m321 -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.5
```

### Erklärung der Parameter:
- `-DartifactId=MavenDemo` → Der Name des generierten Projekts
- `-DgroupId=ch.wiss.m321` → Die Gruppierung des Pakets (z. B. Unternehmens- oder Schul-ID)
- `-DarchetypeArtifactId=maven-archetype-quickstart` → Verwendet die Quickstart-Archetype für ein einfaches Java-Projekt
- `-DarchetypeVersion=1.5` → Definiert die Archetype-Version

Nach dem Ausführen dieses Befehls wird das Projektverzeichnis `MavenDemo` mit einer Standardprojektstruktur generiert.

## 3. Implementierung des MQTT-Publishers
Der Code für den MQTT-Publisher befindet sich in der Datei [`App.java`](MavenDemo/src/main/java/ch/wiss/m321/App.java).

## 4. Kompilieren und Ausführen
### 4.1 Projekt bauen
Das Projekt kann mit **Maven** kompiliert werden:

```bash
mvn clean package
```

Nach der erfolgreichen Kompilierung befindet sich die ausführbare `.jar`-Datei im `target`-Ordner.

### 4.2 Programm ausführen
Das Programm erwartet beim Start ein MQTT-Topic als Argument:

```bash
java -jar target/MavenDemo-1.0-SNAPSHOT.jar Demo
```

Hierbei wird das Topic `Demo` verwendet.

## 5. Testen mit MQTT-Tools
### 5.1 Abonnieren eines Topics
In einem separaten Terminal kann ein **Subscriber** gestartet werden, um die gesendeten Werte zu empfangen:

```bash
mosquitto_sub -t Demo
```

### 5.2 Erwartetes Ergebnis
Alle **500 Millisekunden** wird ein neuer Wert (basierend auf der Sinusfunktion) ausgegeben.

## 6. Grafana
### 6.1 Monitoring
![img](imgs/Monitoring.png)


### 6.2 Grafana Settings
![img](imgs/GrafanaSettings.png)


## 7. Fazit
- Das Projekt demonstriert die Verwendung von **Java** und **MQTT** mit **Maven**.
- Es sendet kontinuierlich **Sinuswerte** an ein spezifiziertes MQTT-Topic.
- Die Implementierung kann leicht erweitert werden, z. B. um mehrere Sensoren oder eine stabilere Client-Verbindung.



# MQTT-System Migration in Docker Compose

---

## 1. Projektziel

Migration einer bestehenden MQTT-Anwendung mit Mosquitto (Broker), Publisher (Java) und Sensor (Shell-Skript) in eine verteilte Docker-Compose-Umgebung mit Persistenz und Sicherheitsmaßnahmen.

---

## 2. Projektstruktur

```
.
├── docker-compose.yaml
├── mosquitto.conf
├── sensor.sh
└── MavenDemo
    ├── Dockerfile
    ├── pom.xml
    └── src/
```

---

## 3. Verwendete Technologien

- Docker & Docker Compose
- Eclipse Mosquitto (MQTT-Broker)
- Java MQTT Publisher (Maven)
- Bash-basiertes Sensor-Skript

---

## 4. Docker-Compose Konfiguration

```yaml
version: '3.8'

services:
  mosquitto:
    image: eclipse-mosquitto
    container_name: mosquitto
    restart: unless-stopped
    ports:
      - "1883:1883"
      - "9001:9001"
    volumes:
      - ./mosquitto.conf:/mosquitto/config/mosquitto.conf
      - ./sensor.sh:/sensor.sh
      - mosquitto_data:/mosquitto/data
      - mosquitto_log:/mosquitto/log

  publisher:
    build:
      context: ./MavenDemo
    container_name: mqtt-publisher
    depends_on:
      - mosquitto
    restart: unless-stopped

volumes:
  mosquitto_data:
  mosquitto_log:
```

---

## 5. Dockerfile (Publisher)

```Dockerfile
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/Mosquitto-1.0-SIGMA.jar app.jar
CMD ["java", "-jar", "app.jar"]
```

---

## 6. Sensor Skript

```bash
#!/bin/bash
while true
do
  mosquitto_pub -h mosquitto -t sensor/temperature -m "Temp: $RANDOM"
  sleep 5
done
```

---

## 7. Testplan

| Test-ID | Testziel                            | Vorgehen                         | Erwartetes Ergebnis               |
|---------|-------------------------------------|----------------------------------|-----------------------------------|
| T1      | Mosquitto startet korrekt           | `docker-compose up -d`          | Container „mosquitto“ läuft       |
| T2      | Publisher sendet MQTT-Nachricht     | `docker logs mqtt-publisher`    | Nachricht im Log sichtbar         |
| T3      | Sensor-Skript sendet Daten          | `docker exec mosquitto ...`     | Nachrichten werden gesendet       |
| T4      | Persistenz funktioniert             | Neustart & Daten prüfen         | Daten bleiben erhalten            |
| T5      | Netzwerkverbindung aller Container  | `docker network inspect`        | Services sind verbunden           |

---

## 8. Testprotokoll

| Test-ID | Ergebnis                           | Status | Bemerkung                        |
|---------|------------------------------------|--------|----------------------------------|
| T1      | Mosquitto läuft mit richtigen Ports| OK     | Keine Fehler                     |
| T2      | Publisher sendet korrekt           | OK     | Verbindung stabil                |
| T3      | Sensor sendet Nachrichten          | OK     | Ausgabe korrekt im Topic         |
| T4      | Volume persistiert Daten           | OK     | Daten bleiben auch nach Restart  |
| T5      | Services im selben Netzwerk        | OK     | Kommunikation funktioniert       |

---

## 9. Sicherheitsmaßnahmen

- Offizielle Images verwendet
- Nur notwendige Ports (1883, 9001) freigegeben
- Volumes statt Bind-Mounts für bessere Kontrolle
- Grundlage für User-Isolation vorbereitet
- Ressourcenbegrenzung möglich (`mem_limit`, `cpus`)

---

## 10. Migrationsvergleich

| Kriterium       | Ohne Container (manuell)   | Mit Docker Compose              |
|-----------------|----------------------------|---------------------------------|
| Einrichtung     | Komplex, viele Tools nötig | Automatisiert mit 1 Befehl     |
| Wartung         | Aufwendig                  | Sehr einfach via Compose        |
| Portabilität    | Schlechter                 | Plattformunabhängig             |
| Skalierbarkeit  | Kaum möglich               | Horizontal skalierbar           |

---

### 11. Dashboard
![img](imgs/GrafanaMigration.png)

---

## 12. Fazit

Dieses Projekt zeigt, wie eine klassische MQTT-Anwendung mithilfe von Docker Compose in ein verteiltes, persistentes und sicheres System überführt werden kann. Alle Anforderungen des Sidequests SQ5 A/C wurden erfüllt.


# Monitoring mit Prometheus, CAdvisor, Grafana und MQTT

## 📦 Projektbeschreibung

In diesem Projekt wird ein containerisiertes Monitoring-System aufgebaut, das Prometheus, CAdvisor, Node Exporter, Grafana sowie einen MQTT-Broker integriert. Ziel ist es, die Systemressourcen von Containern zu überwachen, Daten aufzubereiten, Alerts auszulösen und diese visuell darzustellen.

---

## 🛠️ Vorgehensweise: Prometheus inkl. Alerts und CAdvisor

### 🔧 Installation & Konfiguration

- **Prometheus** und **Alertmanager** wurden mit Docker betrieben.
- Die `prometheus.yml`-Datei enthält statische Targets für `cadvisor` und `node-exporter`.
- Alerts sind in einer eigenen Datei `alert_rules.yml` definiert und per `rule_files` in Prometheus eingebunden.
- **CAdvisor** wurde als Container gestartet, um Docker-Metriken bereitzustellen.

```bash
docker run \
  -d \
  --name cadvisor \
  --volume=/:/rootfs:ro \
  --volume=/var/run:/var/run:ro \
  --volume=/sys:/sys:ro \
  --volume=/var/lib/docker/:/var/lib/docker:ro \
  --publish=8080:8080 \
  google/cadvisor:latest
```

---

## 🔄 Zusammenspiel: Prometheus, CAdvisor, Grafana & Alerts

### 📈 Datenfluss

1. **CAdvisor** sammelt Containerdaten.
2. **Prometheus** scraped Metriken in Intervallen.
3. **Alertmanager** wird bei definierten Schwellenwerten aktiv.
4. **Grafana** visualisiert alle Metriken und den Alertstatus.

![img](imgs/Illustration.jpeg)


### 🖼️ Visualisierung

Dashboards wurden in Grafana importiert und angepasst. Alerts sind direkt sichtbar und zeigen den Zustand des Systems an. Ein typischer Alert sieht so aus:

```yaml
- alert: HighCPUUsage
  expr: rate(container_cpu_usage_seconds_total[1m]) > 0.8
  for: 1m
  labels:
    severity: warning
  annotations:
    summary: "CPU usage is high"
```

---

## ☁️ Cloudfähiges Container-Monitoring

Das gesamte Setup läuft in einer **Docker-Compose Umgebung**. Die Konfiguration ermöglicht einfaches Starten aller Services per:

```bash
docker-compose up -d
```

### Dienste:

- Prometheus
- Alertmanager
- Grafana
- Node Exporter
- CAdvisor
- MQTT Broker
- Dummy Sensoren (Shell + Java)

---

## ✅ Funktionierende Prometheus Alerts (Live-Abnahme)

Die Alerts wurden unter Lastbedingungen (z. B. via `stress-ng`) erfolgreich getestet. Die Auslösung und Sichtbarkeit in Grafana wurde live demonstriert. Die Daten der Sensoren wurden über MQTT verarbeitet und zur Analyse bereitgestellt.

---

## 🧪 Testplan & Testprotokoll für Alerting

| Testfall                                      | Vorgehen                                            | Erwartung        | Resultat |
|----------------------------------------------|-----------------------------------------------------|------------------|----------|
| CAdvisor erreichbar                           | Zugriff auf `localhost:8080/metrics`               | 200 OK, Daten    | ✅ OK     |
| Prometheus scrapt Daten                       | UI zeigt CAdvisor & Node Exporter                  | Targets grün     | ✅ OK     |
| Alert bei CPU-Last                            | `stress-ng` ausführen                               | Alert erscheint  | ✅ OK     |
| Alertmanager Benachrichtigung sichtbar        | Alert auslösen und via UI prüfen                   | "FIRING"         | ✅ OK     |
| Grafana zeigt Daten                           | Dashboard öffnen                                    | Graph sichtbar   | ✅ OK     |

---

## 💬 Persönliches Fazit zum Modul 321

> *„Das Modul 321 hat mir gezeigt, wie wichtig gutes Monitoring in modernen IT-Systemen ist. Die Kombination aus Prometheus, CAdvisor und Grafana war nicht nur spannend, sondern auch sehr praxisnah. Besonders gefallen hat mir die Visualisierung von Metriken und die Möglichkeit, Alerts individuell zu definieren. Dieses Wissen werde ich in zukünftigen Projekten sicher anwenden können – inshallah.“*
