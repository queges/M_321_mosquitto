FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/Mosquitto-1.0-SIGMA.jar app.jar
CMD ["java", "-jar", "app.jar"]
