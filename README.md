# Spring Boot Todo List Web Application (Mock Project)

> **Unit 1 - Assignment 1**: Develop a Web Application Todo List  
> **Author**: BBCrank3107  
> **Tech Stack**: Java 21 LTS | Spring Boot 3.x / 4.x | Spring Security | Spring Data JPA | H2 Database | Thymeleaf | Bootstrap 5 | Mockito

---

## 1. Project Overview & Architecture

Ứng dụng Web Todo List hoàn chỉnh được xây dựng theo mô hình **Kiến trúc 3 tầng (Layered Architecture)** chuẩn công nghiệp:

```
[Web Browser (Thymeleaf UI) / Postman (REST API)]
                       │
                       ▼
        [Controller Layer]
        - TaskController (Giao diện Web)
        - AuthController (Đăng nhập / Đăng ký)
        - TaskRestController (RESTful APIs)
                       │
                       ▼
         [Service Layer]
        - UserService / CustomUserDetailsService (Xác thực & Mã hóa BCrypt)
        - TaskService (Logic CRUD, Tìm kiếm, Phân quyền người dùng)
                       │
                       ▼
        [Repository Layer]
        - UserRepository (Spring Data JPA)
        - TaskRepository (Spring Data JPA)
                       │
                       ▼
      [In-Memory Database]
        - H2 Database (Bảng 'users' và 'tasks' trong RAM)
```

---

## 2. Các tính năng cốt lõi (Core Features)

1. **Quản lý người dùng & Bảo mật (Spring Security)**:
   - Đăng ký tài khoản (`/register`) và Đăng nhập (`/login`).
   - Mật khẩu được mã hóa an toàn bằng thuật toán **BCrypt** (`BCryptPasswordEncoder`).
   - **Cách ly dữ liệu (Data Isolation)**: Mỗi Task được liên kết `@ManyToOne` với một User. Người dùng chỉ xem, sửa, xóa các Task của chính mình.
2. **Quản lý công việc (Task CRUD)**:
   - **Create**: Thêm task mới (Tiêu đề bắt buộc `@NotBlank`, trạng thái mặc định "Incomplete").
   - **Read**: Hiển thị danh sách task riêng của user.
   - **Update**: Chỉnh sửa tiêu đề qua Modal, chuyển đổi trạng thái (Incomplete $\leftrightarrow$ Completed).
   - **Delete**: Xóa task có hộp thoại xác nhận.
3. **Tìm kiếm (Search)**:
   - Tìm kiếm task theo từ khóa tiêu đề (không phân biệt hoa thường).
4. **RESTful APIs**:
   - Cung cấp đầy đủ các endpoint chuẩn REST (`GET`, `POST`, `PUT`, `DELETE /api/v1/tasks`) hỗ trợ HTTP Basic Authentication cho Postman.
5. **Validation & Exception Handling**:
   - Sử dụng `@Valid` và Hibernate Validator kiểm tra dữ liệu đầu vào.
   - Xử lý ngoại lệ tập trung qua `@RestControllerAdvice` (`GlobalExceptionHandler`).
6. **Kiểm thử tự động (Unit Testing)**:
   - Viết các bài kiểm thử cô lập tầng Service bằng **JUnit 5** và **Mockito**.

---

## 3. Tài khoản Demo (Sẵn sàng kiểm thử ngay)

Khi khởi động ứng dụng, hệ thống tự động sinh 2 tài khoản demo có sẵn dữ liệu mẫu để kiểm thử tính năng **Cách ly dữ liệu**:

| Tài khoản | Username | Password | Dữ liệu mẫu |
|:---:|:---:|:---:|---|
| **User 1** | `john` | `password123` | Có 2 task riêng của John |
| **User 2** | `alice` | `password123` | Có 1 task riêng của Alice |

> *Ghi chú: Khi đăng nhập bằng `john`, bạn hoàn toàn không thể xem hoặc sửa task của `alice` và ngược lại.*

---

## 4. Hướng dẫn chạy ứng dụng

### Yêu cầu môi trường:
- Java JDK 21+
- Maven (sẵn có qua Maven Wrapper `mvnw`)

### Khởi động ứng dụng:
- **Cách 1: Chạy bằng Terminal**:
  ```powershell
  .\mvnw.cmd spring-boot:run
  ```
- **Cách 2: Chạy trong Eclipse**:
  - Chuột phải vào project $\rightarrow$ chọn **Run As** $\rightarrow$ **Spring Boot App**.

### Truy cập các cổng dịch vụ:
- **Trang chủ ứng dụng**: `http://localhost:8080`
- **Trang đăng nhập**: `http://localhost:8080/login`
- **Trang đăng ký**: `http://localhost:8080/register`
- **H2 Database Console**: `http://localhost:8080/h2-console`
  - *JDBC URL*: `jdbc:h2:mem:tododb`
  - *User Name*: `sa`
  - *Password*: *(để trống)*

---

## 5. Tài liệu RESTful API (Kiểm thử Postman)

Yêu cầu cấu hình **Authorization** trong Postman là **Basic Auth**:
- Username: `john`
- Password: `password123`

| Method | Endpoint | Mô tả | Request Body | Status Code |
|:---:|---|---|:---:|:---:|
| `GET` | `/api/v1/tasks` | Lấy danh sách task của User | *(None)* | `200 OK` |
| `GET` | `/api/v1/tasks?keyword=Mock` | Tìm kiếm task theo từ khóa | *(None)* | `200 OK` |
| `GET` | `/api/v1/tasks/{id}` | Chi tiết 1 task theo ID | *(None)* | `200 OK` |
| `POST` | `/api/v1/tasks` | Tạo task mới | `{"title": "Learn Spring Security"}` | `201 Created` |
| `PUT` | `/api/v1/tasks/{id}` | Cập nhật tiêu đề / trạng thái | `{"title": "Done", "status": "Completed"}` | `200 OK` |
| `DELETE` | `/api/v1/tasks/{id}` | Xóa task | *(None)* | `200 OK` |

---

## 6. Trả lời các câu hỏi bảo vệ trước Mentor

1. **Làm sao để đảm bảo User A không xem được Task của User B?**  
   *Trả lời*: Trong thực thể `Task` có trường `user_id` liên kết `@ManyToOne` với `User`. Mọi thao tác truy vấn trong `TaskRepository` đều lọc theo `User` đang đăng nhập (`findByUser` hoặc `findByIdAndUser`). Nếu truy cập sai ID của người khác, hệ thống lập tức trả về lỗi `ResourceNotFoundException`.
2. **Mật khẩu người dùng được lưu trữ và bảo mật thế nào?**  
   *Trả lời*: Mật khẩu được mã hóa một chiều bằng thuật toán `BCryptPasswordEncoder` của Spring Security trước khi lưu vào DB. Khi serialize sang JSON trong REST API, trường `password` được đánh dấu `@JsonIgnore` để bảo mật tuyệt đối.
3. **Tại sao sử dụng H2 Database?**  
   *Trả lời*: H2 là một in-memory database nhẹ, không cần cài đặt thêm phần mềm máy chủ, dữ liệu chạy trong RAM giúp khởi tạo và kiểm thử ứng dụng cực kỳ nhanh chóng. Có thể theo dõi trực tiếp các bảng qua `/h2-console`.
4. **Cơ chế Validation hoạt động ra sao?**  
   *Trả lời*: Sử dụng các annotation của Bean Validation (`@NotBlank`, `@Size`) trên Model. Ở Controller, annotation `@Valid` kích hoạt kiểm tra. Nếu có lỗi, `@RestControllerAdvice` bắt và trả về cấu trúc lỗi JSON chi tiết kèm HTTP 400 Bad Request.
