FROM openjdk:21-jdk-slim
WORKDIR /app
COPY /target/trip-service*.jar /app/trip-service.jar
EXPOSE 8082
CMD ["java", "-Xms256m", "-Xmx256m", "-jar", "-Dspring.profiles.active=release", "/app/trip-service.jar"]