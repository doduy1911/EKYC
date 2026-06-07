# Bộ câu hỏi phỏng vấn Java Developer theo project của bạn

## 1. Giới thiệu bản thân

**Câu hỏi:** Em hãy giới thiệu ngắn gọn về bản thân và kinh nghiệm của em.

**Gợi ý trả lời:**

Em là Backend Developer có khoảng 1 năm kinh nghiệm làm việc. Em có nền tảng chính về Java Spring Boot, ngoài ra có làm thêm Node.js và Python cho các hệ thống backend AI. Project Java chính của em là hệ thống EKYC Onboarding, trong đó em xây dựng REST API cho đăng ký, đăng nhập, OTP, nộp hồ sơ KYC, upload giấy tờ và luồng staff/admin review hồ sơ. Ngoài ra em có một project AI Voice Chatbot Backend dùng Node.js, WebSocket, Redis, PostgreSQL, Python worker, RAG và Qdrant.

## 2. Java Core

### OOP là gì?

OOP là lập trình hướng đối tượng, tổ chức chương trình theo các object gồm thuộc tính và hành vi. Bốn tính chất chính là đóng gói, kế thừa, đa hình và trừu tượng.

### Interface khác abstract class như thế nào?

Interface thường dùng để định nghĩa contract/hành vi mà class phải implement, một class có thể implement nhiều interface. Abstract class dùng khi muốn chia sẻ logic hoặc state chung cho các class con, nhưng Java chỉ cho extends một class.

### ArrayList và LinkedList khác nhau thế nào?

ArrayList dùng mảng động, truy cập theo index nhanh hơn. LinkedList dùng node liên kết, thêm/xóa ở đầu hoặc giữa có thể thuận tiện hơn nhưng truy cập theo index chậm hơn. Trong thực tế nếu chỉ lưu danh sách và duyệt/truy cập bình thường thì thường dùng ArrayList.

### HashMap hoạt động thế nào?

HashMap lưu key-value. Khi put/get, Java dùng `hashCode()` của key để xác định bucket, sau đó dùng `equals()` để so sánh key thật sự. Vì vậy nếu override `equals()` thì nên override cả `hashCode()`.

### Checked exception và unchecked exception khác nhau thế nào?

Checked exception bắt buộc phải xử lý bằng try-catch hoặc throws, ví dụ IOException. Unchecked exception kế thừa RuntimeException, không bắt buộc xử lý, thường dùng cho lỗi logic hoặc business validation.

## 3. Spring Boot

### Spring Boot là gì?

Spring Boot là framework giúp xây dựng ứng dụng Spring nhanh hơn nhờ auto-configuration, embedded server và starter dependencies. Với Spring Boot có thể tạo REST API mà không cần cấu hình thủ công quá nhiều.

### `@RestController` khác `@Controller` thế nào?

`@Controller` thường dùng cho MVC trả về view. `@RestController` tương đương `@Controller` + `@ResponseBody`, dùng để trả JSON/XML trực tiếp cho REST API.

### `@Service`, `@Repository`, `@Component` khác nhau thế nào?

Cả ba đều là Spring Bean. `@Component` là annotation chung. `@Service` dùng cho business logic. `@Repository` dùng cho tầng truy cập dữ liệu và có hỗ trợ chuyển đổi exception liên quan database.

### Dependency Injection là gì?

Dependency Injection là cơ chế Spring tự inject dependency vào class thay vì class tự tạo object bằng `new`. Cách này giúp code dễ test, dễ thay thế implementation và giảm coupling.

### REST API là gì?

REST API là kiểu thiết kế API dùng HTTP method như GET, POST, PUT, DELETE để thao tác với resource. API thường trả JSON và dùng HTTP status code để biểu diễn kết quả.

### Trong project EKYC của em có những layer nào?

Project của em chia theo controller, service, repository, entity và DTO. Controller nhận request, validate dữ liệu và gọi service. Service xử lý business logic như submit/review KYC. Repository làm việc với database thông qua Spring Data JPA. Entity map với bảng PostgreSQL. DTO dùng cho request/response.

## 4. Spring Security/JWT

### JWT là gì?

JWT là token chứa thông tin người dùng dưới dạng signed token. Sau khi login thành công, server trả access token cho client. Client gửi token trong header Authorization, server verify token để xác định user và quyền truy cập.

### Access token và refresh token khác nhau thế nào?

Access token dùng để gọi API và thường có thời gian sống ngắn. Refresh token dùng để cấp lại access token và thường sống lâu hơn. Cách này giảm rủi ro khi access token bị lộ.

### BCrypt dùng để làm gì?

BCrypt dùng để hash password trước khi lưu vào database. Khi login, hệ thống không giải mã password mà so sánh password người dùng nhập với hash đã lưu bằng `matches()`.

### Trong project EKYC em phân quyền như thế nào?

Project có role như USER, STAFF, ADMIN. Các endpoint public như register/login/verify OTP được permitAll. Các API còn lại yêu cầu authenticated. API staff/admin KYC được cấu hình cho STAFF hoặc ADMIN thông qua Spring Security và `@PreAuthorize`.

## 5. Database/JPA

### JPA Repository là gì?

JpaRepository là interface của Spring Data JPA cung cấp sẵn các hàm CRUD như save, findById, findAll, delete. Có thể tự định nghĩa method query theo convention hoặc dùng `@Query`.

### Transaction là gì?

Transaction đảm bảo một nhóm thao tác database hoặc thành công toàn bộ hoặc rollback toàn bộ nếu có lỗi. Trong project EKYC, các thao tác submit/review KYC nên dùng transaction vì vừa lưu submission, document, state log và cập nhật trạng thái.

### Index dùng để làm gì?

Index giúp tăng tốc truy vấn trên các cột thường dùng để filter/search/sort. Trong project EKYC, em thêm index cho userId, status và identity number vì đây là các trường hay truy vấn.

### Pagination dùng khi nào?

Pagination dùng khi danh sách dữ liệu lớn, tránh trả toàn bộ dữ liệu trong một request. Trong project EKYC, API staff/admin dùng Page/Pageable để xem danh sách hồ sơ KYC theo từng trang.

## 6. Project EKYC

### Em hãy mô tả project EKYC của em.

Đây là backend hệ thống eKYC Onboarding. User có thể đăng ký, xác thực OTP qua email, đăng nhập bằng JWT, nộp hồ sơ KYC kèm ảnh giấy tờ. Staff/admin có thể xem danh sách hồ sơ, lọc theo trạng thái, nhận review, duyệt, từ chối hoặc yêu cầu nộp lại. Hệ thống có lưu state log và audit log cho các hành động quan trọng.

### Luồng submit KYC hoạt động thế nào?

User gọi API submit với thông tin cá nhân và file ảnh giấy tờ. Service kiểm tra user có hồ sơ active hay không, kiểm tra số lần nộp, tạo KycSubmission, lưu document, chuyển trạng thái sang SUBMITTED, cập nhật thời gian submit và trả response.

### Vì sao cần state log trong KYC?

State log giúp lưu lịch sử chuyển trạng thái của hồ sơ, ví dụ từ DRAFT sang SUBMITTED, từ SUBMITTED sang UNDER_REVIEW, sau đó APPROVED hoặc REJECTED. Điều này quan trọng trong nghiệp vụ tài chính/ngân hàng vì cần trace lại ai xử lý, thời điểm nào và lý do gì.

### Nếu API danh sách KYC bị chậm, em kiểm tra gì?

Em sẽ kiểm tra query database, số lượng dữ liệu trả về, có pagination chưa, có index trên status/createdAt/userId chưa, có N+1 query khi load documents không, và log thời gian xử lý ở service/repository. Sau đó tối ưu query hoặc chỉ trả dữ liệu cần thiết.

### Nếu hồ sơ đang UNDER_REVIEW mà user submit lại thì xử lý thế nào?

Về nghiệp vụ nên không cho submit lại khi hồ sơ vẫn active. Chỉ cho submit lại nếu trạng thái là REJECTED hoặc RESUBMIT_REQUIRED tùy rule. Trong project em có kiểm tra active submission để tránh tạo nhiều hồ sơ xử lý song song.

## 7. Project AI Voice Chatbot Backend

### Em hãy mô tả project AI Voice Chatbot Backend.

Đây là hệ thống chatbot voice realtime. Backend Node.js xử lý REST API, authentication, WebSocket và upload tài liệu. Redis được dùng làm queue/pub-sub/cache. Python workers xử lý AI chat, embedding, RAG và TTS. Hệ thống dùng PostgreSQL để lưu user/group/prompt, Qdrant để lưu vector embedding, Soniox cho STT realtime và Vertex AI/LangChain để sinh câu trả lời.

### Vì sao dùng Redis queue/pub-sub?

Vì các tác vụ AI như embedding, LLM response và TTS có thể tốn thời gian. Redis queue giúp tách API server khỏi worker xử lý nặng, tránh block request/WebSocket. Pub/sub giúp worker trả kết quả realtime về backend để gửi lại client.

### RAG là gì?

RAG là Retrieval-Augmented Generation. Trước khi gọi LLM, hệ thống tìm các đoạn tài liệu liên quan trong vector database, sau đó đưa nội dung đó vào prompt để LLM trả lời chính xác theo dữ liệu nội bộ hơn.

### Qdrant dùng để làm gì?

Qdrant là vector database. Trong project, tài liệu được clean, chunk, embedding thành vector rồi lưu vào Qdrant. Khi user hỏi, hệ thống có thể tìm các chunk liên quan theo semantic similarity để đưa vào context cho AI.

### WebSocket dùng để làm gì trong project?

WebSocket dùng cho giao tiếp realtime giữa client/robot và server. Client có thể gửi audio hoặc text lên server, server gửi status, STT preview và AI voice reply về client mà không cần polling HTTP liên tục.

## 8. Câu hỏi tình huống

### Nếu requirement chưa rõ thì em làm gì?

Em sẽ hỏi lại để làm rõ input/output, rule nghiệp vụ, role nào được thao tác, trạng thái dữ liệu thay đổi thế nào và case lỗi cần xử lý. Sau đó em mới thiết kế API và database để tránh làm sai nghiệp vụ.

### Khi gặp bug em debug thế nào?

Em tái hiện lỗi trước, xem request/response, kiểm tra log, đặt breakpoint hoặc log ở controller/service/repository, kiểm tra dữ liệu database, sau đó khoanh vùng nguyên nhân và sửa. Nếu là lỗi nghiệp vụ, em sẽ kiểm tra lại rule với requirement.

### Nếu phải học IBM BPM thì em tiếp cận thế nào?

Em chưa làm trực tiếp IBM BPM, nhưng em đã làm workflow trạng thái trong project EKYC và hiểu cách một quy trình nghiệp vụ chuyển qua nhiều bước. Nếu vào dự án cần IBM BPM, em sẽ học từ tài liệu dự án, xem các process hiện có, hiểu input/output của từng step rồi bắt đầu từ các task nhỏ.

## 9. Câu hỏi nên hỏi lại nhà tuyển dụng

- Dự án hiện tại dùng Java Spring Boot version nào?
- Team đang làm backend thuần hay có cả frontend React/Vue?
- IBM BPM trong dự án dùng ở mức thiết kế process hay tích hợp API với hệ thống Java?
- Vị trí này chủ yếu maintain hệ thống có sẵn hay phát triển tính năng mới?
- Dự án có liên quan trực tiếp đến ngân hàng/tài chính không?
