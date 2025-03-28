FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/email-service.jar email-service.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "email-service.jar"]