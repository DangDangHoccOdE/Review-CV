FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/image-service.jar image-service.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "image-service.jar"]
