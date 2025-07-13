FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/RoomSimulatorService-1.0.0.jar RoomSimulatorService.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "RoomSimulatorService.jar"]