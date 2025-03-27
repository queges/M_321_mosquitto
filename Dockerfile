FROM maven:latest AS build

WORKDIR /app

COPY MavenDemo/ .

RUN mvn clean package

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/target/Mosquitto-1.0-SIGMA-jar-with-dependencies.jar app.jar

CMD ["sh", "-c", "java -jar app.jar $(hostname)/Demo"]
