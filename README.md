# TechLap Backend

Backend REST API cho hệ thống thương mại điện tử **TechLap Shop** — chuyên bán laptop và thiết bị công nghệ.

## Công nghệ sử dụng

- **Java 21**
- **Spring Boot 3.5.4**
- **Spring Security** + **OAuth2 Resource Server** (JWT)
- **Spring Data JPA** + **QueryDSL** + **SpringFilter**
- **MySQL** (production) / **H2** (test)
- **Spring Mail** (Gmail SMTP)
- **Thymeleaf**
- **ModelMapper**, **Lombok**, **Vavr**
- **Gradle (Kotlin DSL)**

## Yêu cầu môi trường

| Công cụ | Phiên bản tối thiểu |
|---------|---------------------|
| JDK     | 21                  |
| MySQL   | 8.x                 |
| Gradle  | wrapper đi kèm      |

## Cài đặt & Chạy

### 1. Clone repository

```bash
git clone <repo-url>
cd techlap.be
```

### 2. Cấu hình cơ sở dữ liệu

Mở **MySQL Workbench**, chọn **Server → Data Import**, sau đó import file `db_techlap.sql` có trong project vào là xong.

Sau khi import, cập nhật thông tin kết nối trong `src/main/resources/application.properties` nếu cần:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/techlap
spring.datasource.username=<your_username>
spring.datasource.password=<your_password>
```

### 3. Chạy ứng dụng

```bash
./gradlew bootRun
```

Hoặc trên Windows:

```bash
gradlew.bat bootRun
```

Ứng dụng sẽ khởi động tại: `http://localhost:8080`

## Cấu trúc project

```
src/main/java/com/example/techlap/
├── config/          # Cấu hình Security, JWT, Payment, ...
├── controller/      # REST Controllers
├── domain/          # Entity, DTO (request/response), Enum, Criteria
├── exception/       # Global exception handler
├── repository/      # Spring Data JPA repositories
├── service/         # Business logic
└── util/            # Tiện ích (JWT, Payment, ...)
```

## Tính năng chính

- **Xác thực & Phân quyền**: JWT Access/Refresh Token, RBAC (Role - Permission)
- **Quản lý sản phẩm**: Danh mục, thương hiệu, hình ảnh sản phẩm
- **Giỏ hàng & Đơn hàng**: Thêm/xóa sản phẩm, đặt hàng, theo dõi trạng thái
- **Thanh toán VNPay**: Tích hợp cổng thanh toán VNPay (sandbox)
- **Upload file**: Hỗ trợ upload ảnh sản phẩm (tối đa 50MB)
- **Gửi email**: Xác thực tài khoản, đặt lại mật khẩu qua Gmail SMTP
- **Thống kê**: Doanh thu theo tháng, sản phẩm bán chạy
- **Phân trang & Lọc**: Hỗ trợ filter nâng cao với SpringFilter + QueryDSL

## Biến môi trường quan trọng

| Biến | Mô tả |
|------|-------|
| `MYSQL_HOST` | Host của MySQL (mặc định: `localhost`) |
| `spring.datasource.password` | Mật khẩu MySQL |
| `techlap.jwt.base64-secret` | Secret key ký JWT |
| `spring.mail.username` | Địa chỉ Gmail dùng để gửi mail |
| `spring.mail.password` | App password của Gmail |
| `payment.vnPay.secretKey` | Secret key VNPay |
| `payment.vnPay.tmnCode` | TMN Code VNPay |
