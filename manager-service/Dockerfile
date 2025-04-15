FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/manager-service.jar manager-service.jar
EXPOSE 8091
ENTRYPOINT  ["java","-jar","manager-service.jar"]