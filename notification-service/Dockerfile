FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/notification-service.jar notification-service.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "notification-service.jar"]
