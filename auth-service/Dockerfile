FROM openjdk:21-jdk-slim
WORKDIR /app
COPY /target/auth-service*.jar /app/auth-service.jar
EXPOSE 8081
CMD ["java", "-Xms256m", "-Xmx256m", "-jar", "-Dspring.profiles.active=release", "/app/auth-service.jar"]