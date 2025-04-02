# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy JAR file into container
COPY target/appointmentBooking-0.0.1-SNAPSHOT.jar /app/

# Copy and set permissions for entrypoint script
COPY docker-entrypoint.sh /app/docker-entrypoint.sh
RUN chmod +x /app/docker-entrypoint.sh

# Expose the application port
EXPOSE 8080

# Use the entrypoint script
ENTRYPOINT ["/app/docker-entrypoint.sh"]
