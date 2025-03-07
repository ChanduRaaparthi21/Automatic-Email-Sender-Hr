# Use an official OpenJDK 17 runtime image as a base
FROM openjdk:17-jdk-slim

# Set the working directory inside the container


# Copy the built JAR file from the target directory to the container
ADD target/emailsender.jar emailsender.jar

# Expose the application port (change if necessary)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/emailsender.jar"]
