# Sử dụng OpenJDK 17 làm base image
FROM openjdk:17-jdk-slim

# Thiết lập thư mục làm việc bên trong container
WORKDIR /app

# Sao chép file JAR từ thư mục target vào container
COPY target/notification-service.jar app.jar

# Cấu hình lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
