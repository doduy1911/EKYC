# [HỌ VÀ TÊN]

**Junior Backend Developer / Java Developer**

- SĐT: [Số điện thoại]
- Email: [Email]
- GitHub: [GitHub]
- Địa chỉ: [Hà Nội, Việt Nam]

## Mục tiêu nghề nghiệp

Backend Developer có 1 năm kinh nghiệm làm việc, có nền tảng Java Spring Boot, Node.js và Python. Có kinh nghiệm xây dựng REST API, xử lý nghiệp vụ backend, xác thực/phân quyền, làm việc với PostgreSQL, Redis và các hệ thống AI backend. Mong muốn ứng tuyển vị trí Java Developer để phát triển hệ thống backend/API trong môi trường doanh nghiệp, đặc biệt là các hệ thống có nghiệp vụ tài chính, ngân hàng hoặc định danh điện tử.

## Kỹ năng kỹ thuật

- **Backend:** Java, Spring Boot, Node.js, Express.js, Python
- **Database:** PostgreSQL, Spring Data JPA, Sequelize ORM
- **Security:** Spring Security, JWT, Refresh Token, BCrypt, OTP, phân quyền theo role
- **API:** RESTful API, WebSocket, multipart file upload, tích hợp API
- **Queue/Cache:** Redis queue, Redis pub/sub, Redis cache
- **AI Backend:** RAG, embedding, vector database, LLM integration, STT/TTS workflow
- **Tools:** Maven, Docker, Docker Compose, Git

## Kinh nghiệm làm việc

### Backend Developer

**[Tên công ty]** | [Thời gian làm việc]

- Tham gia phát triển và bảo trì backend API cho hệ thống web/mobile.
- Xây dựng business logic, service layer, database model và API endpoint.
- Tích hợp xác thực, phân quyền và các dịch vụ bên thứ ba.
- Làm việc với Java Spring Boot, Node.js và Python trong các bài toán backend.
- Phối hợp với team để phân tích yêu cầu, debug lỗi và hoàn thành tính năng.

## Dự án nổi bật

### EKYC Onboarding System

**Công nghệ:** Java 17, Spring Boot, Spring Security, Spring Data JPA, PostgreSQL, JWT, BCrypt, Maven, Docker Compose

Hệ thống backend phục vụ quy trình định danh điện tử eKYC, gồm đăng ký, đăng nhập, xác thực OTP, nộp hồ sơ KYC, upload giấy tờ và review hồ sơ bởi staff/admin.

- Xây dựng REST API cho đăng ký, đăng nhập, xác thực OTP, nộp hồ sơ KYC và review hồ sơ.
- Triển khai authentication/authorization bằng Spring Security, JWT, Refresh Token và BCrypt.
- Thiết kế workflow trạng thái KYC: `DRAFT`, `SUBMITTED`, `UNDER_REVIEW`, `APPROVED`, `REJECTED`, `RESUBMIT_REQUIRED`.
- Xây dựng API staff/admin để xem danh sách hồ sơ, lọc theo trạng thái, phân trang, nhận review, duyệt/từ chối/yêu cầu nộp lại.
- Sử dụng PostgreSQL và Spring Data JPA để quản lý user, OTP, refresh token, hồ sơ KYC, document, state log và audit log.
- Xử lý upload ảnh giấy tờ mặt trước, mặt sau và ảnh selfie bằng multipart file.
- Thêm audit log và index database cho các trường thường truy vấn như userId, status, identity number.

### AI Voice Chatbot Backend

**Công nghệ:** Node.js, Express.js, WebSocket, PostgreSQL, Sequelize, Redis, Python, Qdrant, LangChain, Vertex AI, Soniox STT, Docker Compose

Hệ thống backend chatbot voice realtime, hỗ trợ giao tiếp qua WebSocket, xử lý giọng nói, RAG tài liệu và sinh phản hồi AI bằng các worker Python.

- Xây dựng REST API bằng Node.js/Express cho authentication, quản lý user/group/prompt, upload file RAG và chat.
- Tích hợp JWT cho REST API và WebSocket connection.
- Sử dụng PostgreSQL/Sequelize để quản lý user, role, group, prompt và summary prompt.
- Xây dựng luồng WebSocket realtime cho robot/client gửi audio và nhận phản hồi AI voice.
- Tích hợp Soniox STT để chuyển audio realtime thành text.
- Thiết kế Redis queue/pub-sub để tách Node.js API server, AI worker, embedding worker và TTS worker.
- Xây dựng Python AI worker sử dụng LangChain/Vertex AI để sinh phản hồi theo prompt và ngữ cảnh.
- Xây dựng RAG pipeline gồm upload tài liệu, clean data, chunking, embedding và lưu vector vào Qdrant.
- Sử dụng Redis cache để lưu prompt theo group, giảm truy vấn database lặp lại.
- Dùng Docker Compose để chạy PostgreSQL, Redis và Qdrant.

## Học vấn

**[Tên trường]**  
[Ngành học] | [Năm tốt nghiệp]

## Điểm mạnh

- Có nền tảng backend tốt với Java Spring Boot và RESTful API.
- Hiểu authentication, authorization, database design và business workflow.
- Có project eKYC gần với nghiệp vụ tài chính/ngân hàng.
- Có khả năng học nhanh công nghệ mới qua kinh nghiệm Node.js, Python và AI backend.
