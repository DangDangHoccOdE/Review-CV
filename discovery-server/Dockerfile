FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/discovery-server.jar discovery-server.jar
EXPOSE 8671
ENTRYPOINT ["java", "-jar", "discovery-server.jar"]