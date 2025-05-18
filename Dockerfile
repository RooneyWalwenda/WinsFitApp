# ---------- Build stage ----------
FROM maven:3.8.7-openjdk-17-slim AS build
WORKDIR /app

# Copy all source code
COPY . .

# Package the application
RUN mvn clean package -DskipTests

# ---------- Run stage ----------
FROM maven:3.9.4-openjdk-17

WORKDIR /app

# Copy JAR from the build stage
COPY --from=build /app/target/appointmentBooking-0.0.1-SNAPSHOT.jar /app/

# Copy and set permissions for entrypoint script
COPY docker-entrypoint.sh /app/docker-entrypoint.sh
RUN chmod +x /app/docker-entrypoint.sh

# Expose the app port
EXPOSE 8080

# Run the app
ENTRYPOINT ["/app/docker-entrypoint.sh"]
