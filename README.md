# eKYC Onboarding System

Backend hệ thống định danh điện tử (eKYC) phục vụ quy trình onboarding khách hàng theo tiêu chuẩn ngân hàng — bao gồm đăng ký tài khoản, xác thực OTP, nộp hồ sơ KYC, upload giấy tờ tùy thân và review hồ sơ bởi staff/admin.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3, Spring Security, Spring Data JPA |
| Database | PostgreSQL |
| Auth | JWT, Refresh Token, BCrypt |
| Build | Maven |
| Infrastructure | Docker, Docker Compose |

---

## Features

### Authentication
- Đăng ký tài khoản với xác thực OTP qua email
- Đăng nhập với JWT + Refresh Token rotation
- Bảo vệ brute-force login (giới hạn số lần đăng nhập sai)
- Phân quyền role-based: `CUSTOMER` / `STAFF` / `ADMIN`

### KYC Workflow
Hồ sơ KYC đi qua state machine với 6 trạng thái:

```
DRAFT → SUBMITTED → UNDER_REVIEW → APPROVED
                                 → REJECTED
                                 → RESUBMIT_REQUIRED
```

- Customer nộp hồ sơ, upload ảnh CCCD (mặt trước, mặt sau) và ảnh selfie
- Staff/Admin review hồ sơ, duyệt / từ chối / yêu cầu nộp lại
- Toàn bộ thay đổi trạng thái được ghi vào state log

### Staff / Admin API
- Xem danh sách hồ sơ với filter theo trạng thái và phân trang
- Review hồ sơ: duyệt, từ chối, yêu cầu bổ sung giấy tờ
- Xem lịch sử thay đổi trạng thái của từng hồ sơ

### Audit & Performance
- Async audit log ghi lại toàn bộ thao tác trên hệ thống
- Database index trên các trường thường truy vấn: `userId`, `status`, `identityNumber`

---

## Project Structure

```
src/
├── modules/
│   ├── auth/         # Đăng ký, đăng nhập, OTP, JWT
│   ├── kyc/          # KYC workflow, state machine, document upload
│   ├── account/      # Quản lý tài khoản người dùng
│   └── common/       # Shared utilities, audit log, exceptions
```

> Tổ chức theo module (module-based) thay vì layer-based để dễ mở rộng từng domain độc lập.

---

## Getting Started

### Prerequisites
- Java 17+
- Docker & Docker Compose

### Run with Docker Compose

```bash
# Clone repo
git clone https://github.com/doduy1911/EKYC.git
cd EKYC

# Start PostgreSQL + App
docker-compose up -d
```

### Run locally

```bash
# Start chỉ PostgreSQL
docker-compose up -d db

# Run app
./mvnw spring-boot:run
```

App chạy tại: `http://localhost:8080`

---

## API Overview

### Auth
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Đăng ký tài khoản |
| POST | `/api/auth/verify-otp` | Xác thực OTP |
| POST | `/api/auth/login` | Đăng nhập |
| POST | `/api/auth/refresh` | Refresh access token |
| POST | `/api/auth/logout` | Đăng xuất |

### KYC (Customer)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/kyc/submit` | Nộp hồ sơ KYC |
| POST | `/api/kyc/upload` | Upload ảnh giấy tờ (multipart) |
| GET | `/api/kyc/me` | Xem trạng thái hồ sơ của mình |

### KYC (Staff / Admin)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/kyc` | Danh sách hồ sơ (filter + phân trang) |
| GET | `/api/admin/kyc/{id}` | Chi tiết hồ sơ |
| POST | `/api/admin/kyc/{id}/approve` | Duyệt hồ sơ |
| POST | `/api/admin/kyc/{id}/reject` | Từ chối hồ sơ |
| POST | `/api/admin/kyc/{id}/resubmit` | Yêu cầu nộp lại |

---

## Environment Variables

```env
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/ekyc
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

# JWT
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000

# Mail (OTP)
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_USERNAME=your_email
SPRING_MAIL_PASSWORD=your_app_password
```

---

## Author

**Đỗ Đình Duy** · [dev.dinhduy@gmail.com](mailto:dev.dinhduy@gmail.com) · [GitHub](https://github.com/doduy1911)
