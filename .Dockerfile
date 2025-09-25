# Sử dụng JDK chính thức (Java 21 cho Spring Boot mới)
FROM eclipse-temurin:21-jdk-alpine

# Set thư mục làm việc trong container
WORKDIR /app

# Copy toàn bộ source code vào container
COPY . .

# Build project bằng Gradle Wrapper (sẽ tạo file jar)
RUN ./gradlew clean build -x test

# Copy file jar đã build ra thư mục app.jar
RUN cp build/libs/*.jar app.jar

# Mở cổng 8080
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java","-jar","app.jar"]
