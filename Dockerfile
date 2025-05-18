# ---------- Build stage ----------
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

# ---------- Run stage ----------
FROM eclipse-temurin:17-jdk-jammy AS runtime
WORKDIR /app

COPY --from=build /app/target/appointmentBooking-0.0.1-SNAPSHOT.jar /app/

COPY docker-entrypoint.sh /app/docker-entrypoint.sh
RUN chmod +x /app/docker-entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/app/docker-entrypoint.sh"]