FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/
COPY api-gateway/pom.xml api-gateway/
COPY movie-service/pom.xml movie-service/
COPY theatre-service/pom.xml theatre-service/
COPY showtime-service/pom.xml showtime-service/
COPY user-service/pom.xml user-service/
COPY booking-service/pom.xml booking-service/
COPY payment-service/pom.xml payment-service/

RUN mvn dependency:go-offline -B

COPY eureka-server/src eureka-server/src
COPY api-gateway/src api-gateway/src
COPY movie-service/src movie-service/src
COPY theatre-service/src theatre-service/src
COPY showtime-service/src showtime-service/src
COPY user-service/src user-service/src
COPY booking-service/src booking-service/src
COPY payment-service/src payment-service/src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ARG SERVICE_NAME

COPY --from=builder /build/${SERVICE_NAME}/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]