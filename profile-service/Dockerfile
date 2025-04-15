FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/profile-service.jar profile-service.jar
EXPOSE 8085
ENTRYPOINT  ["java","-jar","profile-service.jar"]