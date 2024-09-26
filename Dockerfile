# Use an official Maven runtime as a parent image for building
FROM maven:3.8.3-openjdk-11 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the project files into the container
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Use selenium/standalone-chrome as the base image for the runtime
FROM selenium/standalone-chrome:114.0

# Install OpenJDK 11
USER root
RUN apt-get update && apt-get install -y openjdk-11-jre-headless && apt-get clean

# Set the working directory in the container
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Copy the dependencies
COPY --from=build /root/.m2 /root/.m2

# Set environment variables
ENV LOG_LEVEL=INFO
ENV DOCKER_ENV=true

# Expose the application port
EXPOSE 8080

# Expose Selenium port
EXPOSE 4444

# Switch back to the selenium user
USER seluser

# Run the application
CMD ["java", "-jar", "app.jar"]