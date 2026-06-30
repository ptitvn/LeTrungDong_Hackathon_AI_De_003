# HACKATHON MÔN AI APPLICATION IN ACTION - ĐỀ 003

**Sinh viên:** Lê Trung Đông  
**Lớp:** HN-K24-CNTT3  
**Mã đề:** 003

---

## Cấu trúc thư mục

```
HN-K24-CNTT3-LeTrungDong-003
├── src
│   ├── refactoring              # Code Phần 1 - Tái cấu trúc TicketingService
│   │   ├── SeatPricingStrategy.java
│   │   ├── StandardSeatPricing.java
│   │   ├── VipSeatPricing.java
│   │   ├── SweetboxSeatPricing.java
│   │   ├── DiscountStrategy.java
│   │   ├── StudentDiscount.java
│   │   ├── FestivalDiscount.java
│   │   ├── LoyaltyPointCalculator.java
│   │   ├── DefaultLoyaltyPointCalculator.java
│   │   ├── NotificationService.java
│   │   ├── PushNotificationService.java
│   │   └── TicketingService.java
│   └── security                 # Code Phần 2 - Sửa lỗi JWT Filter
│       ├── JwtAuthenticationFilter.java
│       ├── JwtAuthenticationEntryPoint.java
│       └── SecurityConfig.java
├── docs
│   └── erd_diagram.mmd          # Mã Mermaid vẽ ERD (Phần 3)
├── README.md                    # Báo cáo này
└── .gitignore
```

---

## PHẦN 1: TÁI CẤU TRÚC HỆ THỐNG ĐỂ DỄ MỞ RỘNG

### 1.1. Mục tiêu kỹ thuật

Code gốc `TicketingService.bookTicket()` vi phạm **Open/Closed Principle** — gộp 4 concern vào 1 hàm: tính giá ghế (if/else VIP, SWEETBOX), áp khuyến mãi (if/else STUDENT, FESTIVAL), tích điểm, và gửi thông báo. Mỗi khi thêm loại ghế mới hoặc chương trình khuyến mãi lễ Tết, phải sửa trực tiếp hàm cốt lõi.

**Giải pháp:** Áp dụng **Strategy Pattern** + **Dependency Injection** để bóc tách từng concern:

| Concern | Interface | Implementations | Nguyên tắc SOLID |
|---------|-----------|-----------------|------------------|
| Tính giá theo ghế | `SeatPricingStrategy` | `StandardSeatPricing`, `VipSeatPricing`, `SweetboxSeatPricing` | **OCP** - Open/Closed |
| Khuyến mãi | `DiscountStrategy` | `StudentDiscount`, `FestivalDiscount` | **OCP** - Open/Closed |
| Tích điểm | `LoyaltyPointCalculator` | `DefaultLoyaltyPointCalculator` | **SRP** - Single Responsibility |
| Thông báo | `NotificationService` | `PushNotificationService` | **DIP** - Dependency Inversion |

**Kết quả:** Thêm loại ghế mới (VD: `COUPLE`), thêm chương trình khuyến mãi mới (VD: `NEWYEAR`), đổi cơ chế tích điểm hoặc kênh thông báo → chỉ cần tạo class mới implement interface — **không sửa hàm `bookTicket()` cốt lõi**.

### 1.2. Lịch sử Prompt (Prompt Chain)

**Prompt 1 — Phân tích code gốc:**
> "Phân tích đoạn code TicketingService.java sau đây. Code hiện tại gộp logic tính giá theo loại ghế (VIP +20000, SWEETBOX +50000, STANDARD), áp mã khuyến mãi (STUDENT giảm 10%, FESTIVAL giảm 40000), tính điểm loyalty (total/10000), và gửi push notification vào cùng một hàm bookTicket(). Hãy chỉ ra cụ thể từng vi phạm nguyên tắc SOLID và rủi ro bảo trì khi hệ thống mở rộng."

**Prompt 2 — Thiết kế kiến trúc mới:**
> "Dựa trên phân tích trên, hãy thiết kế lại hệ thống đặt vé sử dụng Strategy Pattern, đảm bảo 4 yêu cầu: (1) Thêm loại ghế mới (COUPLE) không sửa hàm bookTicket(), (2) Thêm khuyến mãi lễ Tết mới không sửa code cũ, (3) Thay đổi cơ chế tích điểm không ảnh hưởng logic đặt vé, (4) Đổi từ push notification sang SMS không sửa nghiệp vụ. Đưa ra danh sách interface và class cần tạo."

**Prompt 3 — Sinh mã nguồn:**
> "Viết code Java cho toàn bộ thiết kế: SeatPricingStrategy interface + 3 impl (Standard, VIP, Sweetbox), DiscountStrategy interface + 2 impl (Student, Festival), LoyaltyPointCalculator interface + DefaultLoyaltyPointCalculator, NotificationService interface + PushNotificationService, và TicketingService mới sử dụng constructor injection."

**Prompt 4 — Kiểm chứng tính mở rộng:**
> "Bây giờ hãy thêm loại ghế COUPLE_SEAT (+80000) và khuyến mãi NEWYEAR (giảm 25%) vào hệ thống đã tái cấu trúc. Chứng minh rằng chỉ cần thêm class mới mà không cần sửa TicketingService."

### 1.3. Phân tích lỗi AI

**Lỗi AI lần sinh code đầu tiên:** AI tạo `SeatPricingStrategy` với method `calculatePrice(Seat seat, double basePrice)` — truyền cả object `Seat` vào strategy. Điều này khiến strategy phụ thuộc vào model `Seat`, vi phạm **Interface Segregation Principle**. Strategy chỉ cần biết loại ghế (String) và giá cơ bản, không cần toàn bộ object.

**Cách khắc phục:** Yêu cầu AI tách thành 2 method: `supports(String seatType)` để kiểm tra loại ghế và `calculatePrice(double basePrice)` để tính giá. Cách này giữ strategy độc lập với model, dễ test hơn:

```java
// Code AI sinh lần đầu (CHƯA TỐI ƯU):
public interface SeatPricingStrategy {
    double calculatePrice(Seat seat, double basePrice);
}

// Code đã sửa (TỐI ƯU):
public interface SeatPricingStrategy {
    boolean supports(String seatType);
    double calculatePrice(double basePrice);
}
```

---

## PHẦN 2: DEBUGGING BẢO MẬT VÀ XỬ LÝ LỖI HỆ THỐNG

### 2.1. Phân tích nguyên nhân gốc rễ (Root Cause Analysis)

**Log lỗi:**
```
io.jsonwebtoken.security.SignatureException: JWT signature does not match locally computed signature.
  at io.jsonwebtoken.impl.DefaultJwtParser.parse(DefaultJwtParser.java:381)
  at com.rikkei.security.JwtAuthenticationFilter.doFilterInternal(...)
```

**Luồng lỗi xảy ra:**

```
Request có token JWT bị giả mạo / sai chữ ký
  → authHeader != null ✓ và startsWith("Bearer ") ✓ → đi vào block parse
    → Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token)
      → SignatureException được throw (chữ ký không khớp)
        → Không có try-catch bắt exception
          → Exception lan ra ngoài filter chain
            → Servlet container trả HTTP 500 Internal Server Error
```

**3 lỗi trong code gốc:**

| # | Lỗi | Mức độ | Giải thích |
|---|------|--------|------------|
| 1 | Không try-catch quanh `Jwts.parser()` | **Critical** | Mọi token sai chữ ký đều crash server thành 500 |
| 2 | Hardcode `SECRET_KEY` trong source code | **Critical** | Lộ secret key khi push lên GitHub/repo |
| 3 | Không có `AuthenticationEntryPoint` | **High** | Không thể trả JSON error response thống nhất cho client |

**Điểm khác biệt so với null check:** Code gốc đã kiểm tra `authHeader != null` đúng cách, nhưng khi token **có mặt nhưng bị giả mạo**, `Jwts.parser()` sẽ throw `SignatureException` — và exception này không được catch.

### 2.2. Giải pháp: AuthenticationEntryPoint (Xử lý tập trung)

**Tại sao KHÔNG nên chỉ dùng try-catch trong Filter?**

1. **Vi phạm SRP:** Filter chịu trách nhiệm parse và xác thực token, không phải format error response. Nếu gộp cả hai, filter trở thành "thùng rác" cho mọi logic.

2. **Không nhất quán:** Nếu có nhiều filter (VD: `JwtFilter`, `ApiKeyFilter`, `OAuth2Filter`), mỗi filter tự try-catch và format JSON → mỗi cái trả format khác nhau. Client không biết handle thế nào.

3. **Bỏ sót case:** Try-catch chỉ bắt exception trong block đó. Nhưng lỗi xác thực có thể xảy ra ở nhiều điểm: filter, provider, handler... `AuthenticationEntryPoint` là **điểm hội tụ duy nhất** mà Spring Security gọi khi request không authenticated.

4. **Đúng kiến trúc Spring Security:** Framework đã thiết kế `AuthenticationEntryPoint` cho mục đích này — nếu dùng try-catch ta đang "chống lại" framework thay vì tận dụng nó.

**Giải pháp 2 lớp:**

- **Lớp 1 — `JwtAuthenticationFilter`:** Parse token trong try-catch. Nếu `SignatureException` (hoặc bất kỳ JWT exception) → `SecurityContextHolder.clearContext()` → request tiếp tục nhưng không có authentication → Spring Security tự gọi EntryPoint.

- **Lớp 2 — `JwtAuthenticationEntryPoint`:** Được Spring Security gọi tự động khi request không authenticated truy cập endpoint protected. Trả JSON response 401 thống nhất:

```json
{"error": "MISSING_TOKEN", "message": "Authorization header is required..."}
{"error": "INVALID_TOKEN_FORMAT", "message": "Authorization header must start with 'Bearer '..."}
{"error": "INVALID_OR_EXPIRED_TOKEN", "message": "The provided token is invalid, expired, or has been tampered with."}
```

### 2.3. Lịch sử Prompt (Prompt Chain)

**Prompt 1 — Phân tích lỗi:**
> "Phân tích đoạn code JwtAuthenticationFilter.java và log lỗi: SignatureException: JWT signature does not match locally computed signature. Code đã kiểm tra authHeader != null nhưng server vẫn crash 500 khi token sai chữ ký. Giải thích luồng xử lý từng bước khi một request có Bearer token bị giả mạo đi qua filter này."

**Prompt 2 — So sánh giải pháp:**
> "So sánh 2 cách xử lý: (A) Thêm try-catch bên trong doFilterInternal và trả lỗi trực tiếp, (B) Dùng AuthenticationEntryPoint tập trung. Phân tích ưu nhược điểm mỗi cách theo nguyên tắc SRP và khả năng mở rộng khi có nhiều filter."

**Prompt 3 — Sinh code giải pháp:**
> "Viết code Java cho 3 file: (1) JwtAuthenticationFilter.java đã sửa — catch SignatureException/ExpiredJwtException/MalformedJwtException, clear context thay vì throw, inject secret từ config, (2) JwtAuthenticationEntryPoint.java — trả JSON 401 phân biệt MISSING_TOKEN / INVALID_TOKEN_FORMAT / INVALID_OR_EXPIRED_TOKEN, (3) SecurityConfig.java — đăng ký cả filter và entry point."

### 2.4. Phân tích lỗi AI

**Lỗi AI lần đầu:** AI vẫn giữ `SECRET_KEY` hardcode trong filter (`private final String SECRET_KEY = "rikkei_secret_key..."`) thay vì inject từ file cấu hình. Khi tôi hỏi về rủi ro bảo mật, AI mới nhận ra và chuyển sang dùng `@Value("${jwt.secret}")` để đọc từ `application.properties`, đảm bảo secret key không bị lộ trong source code.

---

## PHẦN 3: PHÂN TÍCH VÀ THIẾT KẾ HỆ THỐNG VỚI AI

### 3.1. Nhiệm vụ 1: Đề xuất Tech Stack

**Prompt đã dùng:**
> "Bạn là System Architect, hãy đề xuất Tech Stack cho nền tảng E-Learning 'Rikkei LMS' với các yêu cầu nghiệp vụ: (1) Quản lý 3 loại user: Student, Instructor, Moderator, (2) Chia sẻ doanh thu 70% Instructor - 30% Platform, (3) Khuyến mãi gộp: mua từ 2 khóa trở lên giảm 15% tổng bill, (4) Gói Subscription Pro: truy cập miễn phí khóa cơ bản, trả phí khóa nâng cao, (5) Theo dõi tiến độ học tập đồng bộ realtime giữa các thiết bị. Yêu cầu tối ưu hóa lưu trữ tiến độ học tập. Giải thích lý do cho từng lựa chọn."

**Giải pháp đề xuất:**

| Layer | Công nghệ | Lý do |
|-------|-----------|-------|
| **Backend** | Spring Boot 3 + Java 17 | Ecosystem lớn, Spring Security phân quyền 3 role, Spring Data JPA cho nghiệp vụ phức tạp |
| **Database chính** | PostgreSQL | ACID cho giao dịch thanh toán/chia doanh thu, JSONB cho metadata linh hoạt |
| **Cache & Progress** | Redis | Write-through cache cho tiến độ realtime, latency thấp khi cập nhật `lastPosition` mỗi 5 giây |
| **Message Queue** | RabbitMQ | Xử lý async: tính doanh thu, gửi email, batch sync tiến độ từ Redis → PostgreSQL |
| **Frontend** | React + TypeScript | Component-based phù hợp video player, progress tracker, dashboard |
| **Video Storage** | AWS S3 + CloudFront | CDN phân phối video nhanh, presigned URL bảo mật nội dung khóa học |
| **Payment** | Stripe API | Card quốc tế, subscription billing, webhook cho chia doanh thu tự động |

**Giải pháp tối ưu lưu trữ tiến độ:**
- **Redis** lưu tiến độ realtime — mỗi 5 giây cập nhật `lastPositionSeconds` mà không tạo áp lực lên DB chính.
- **Batch job** mỗi 5 phút sync Redis → PostgreSQL để đảm bảo dữ liệu persistent.
- Khi sinh viên đổi thiết bị → đọc từ Redis (nhanh) → học tiếp từ đúng vị trí.

**Nhận xét phản biện:**

 **Đồng ý:** Redis cho tiến độ học tập — write throughput cao, latency thấp, phù hợp cho dữ liệu cập nhật liên tục. PostgreSQL cho dữ liệu nghiệp vụ cần ACID (thanh toán, enrollment).

**Phản đối một phần:** AI ban đầu đề xuất dùng **WebSocket** để đồng bộ tiến độ giữa các thiết bị. Tôi phản đối vì:
- WebSocket tốn tài nguyên server khi duy trì persistent connection cho mọi user.
- Với yêu cầu "học tiếp trên thiết bị khác", ta chỉ cần **pull-based** (đọc tiến độ mới nhất từ Redis khi mở app) thay vì push realtime.
- WebSocket phù hợp cho chat, live notification — không cần thiết cho progress tracking vốn là hành vi đơn hướng (user → server).

### 3.2. Nhiệm vụ 2: Phân tích thực thể (Entity Analysis)

**Prompt đã dùng:**
> "Dựa trên nghiệp vụ Rikkei LMS (3 role user, khóa học có section/lesson, ghi danh, thanh toán chia doanh thu 70/30, khuyến mãi gộp mua 2+ khóa giảm 15%, gói Subscription Pro, theo dõi tiến độ từng video/bài tập), hãy bóc tách tất cả Entity cốt lõi của database. Với mỗi entity, liệt kê thuộc tính quan trọng và kiểu dữ liệu."

**Danh sách Entities:**

| # | Entity | Mô tả | Thuộc tính chính |
|---|--------|--------|------------------|
| 1 | **User** | Người dùng hệ thống | id, email, password, fullName, role (STUDENT/INSTRUCTOR/MODERATOR), isActive |
| 2 | **Course** | Khóa học | id, instructorId, title, price, level (BASIC/ADVANCED), status |
| 3 | **CourseSection** | Chương trong khóa học | id, courseId, title, orderIndex |
| 4 | **Lesson** | Bài học (video/bài tập) | id, sectionId, title, videoUrl, durationSeconds, type (VIDEO/EXERCISE) |
| 5 | **Enrollment** | Ghi danh học viên | id, studentId, courseId, paidAmount, enrolledAt, status |
| 6 | **LearningProgress** | Tiến độ từng bài | id, enrollmentId, lessonId, isCompleted, lastPositionSeconds, lastAccessedAt |
| 7 | **Payment** | Thanh toán | id, enrollmentId, amount, platformFee (30%), instructorRevenue (70%), method, status |
| 8 | **Subscription** | Gói Pro | id, userId, plan (FREE/PRO), startDate, endDate, isActive |
| 9 | **Coupon** | Mã khuyến mãi | id, code, discountType, discountValue, validFrom, validTo, maxUsage |
| 10 | **Cart** | Giỏ hàng (hỗ trợ mua gộp) | id, studentId, createdAt |
| 11 | **CartItem** | Item trong giỏ | id, cartId, courseId |
| 12 | **Order** | Đơn hàng | id, studentId, couponId, subtotal, discountAmount, totalAmount |
| 13 | **OrderItem** | Khóa học trong đơn | id, orderId, courseId, price |
| 14 | **Review** | Đánh giá khóa học | id, studentId, courseId, rating (1-5), comment |

### 3.3. Nhiệm vụ 3: Sơ đồ ERD

**Prompt đã dùng:**
> "Tạo mã Mermaid erDiagram cho 14 entity đã liệt kê, bao gồm đầy đủ thuộc tính, khóa chính (PK), khóa ngoại (FK), và quan hệ giữa các bảng. Đảm bảo thể hiện đúng quan hệ 1-N, N-N theo ngữ nghĩa nghiệp vụ."

**File mã ERD:** `docs/erd_diagram.mmd`

Để render sơ đồ ERD, sử dụng một trong các cách:
- **Mermaid Live Editor:** Truy cập https://mermaid.live → paste nội dung file `.mmd` → export PNG
- **VS Code:** Cài extension "Mermaid Preview", mở file và preview
- **GitHub:** Tự động render Mermaid trong markdown

**Các quan hệ chính trong ERD:**

```
User ──1:N──> Course          (Instructor tạo nhiều khóa học)
User ──1:N──> Enrollment      (Student ghi danh nhiều khóa)
User ──1:1──> Subscription    (Mỗi user 1 gói subscription)
Course ──1:N──> CourseSection ──1:N──> Lesson
Enrollment ──1:N──> LearningProgress  (Theo dõi từng bài)
Enrollment ──1:1──> Payment
User ──1:1──> Cart ──1:N──> CartItem
User ──1:N──> Order ──1:N──> OrderItem
Order ──N:1──> Coupon
```

---

## TỔNG KẾT

| Phần | Nội dung | Giải pháp chính |
|------|----------|-----------------|
| 1 | Tái cấu trúc TicketingService | Strategy Pattern tách Seat Pricing, Discount, Loyalty, Notification |
| 2 | Sửa lỗi JWT SignatureException → 500 | Try-catch trong Filter + AuthenticationEntryPoint trả JSON 401 tập trung |
| 3 | Thiết kế hệ thống Rikkei LMS | Spring Boot + PostgreSQL + Redis, 14 Entities, ERD Mermaid |
