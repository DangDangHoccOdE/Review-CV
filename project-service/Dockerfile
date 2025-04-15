FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/project-service.jar project-service.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "project-service.jar"]
