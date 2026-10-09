package com.example.todolist.repository;

import com.example.todolist.model.Task;
import com.example.todolist.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // 1. Lấy tất cả task thuộc về một user cụ thể
    List<Task> findByUser(User user);

    // 2. Tìm kiếm task của user theo từ khóa tiêu đề (không phân biệt hoa thường)
    List<Task> findByUserAndTitleContainingIgnoreCase(User user, String keyword);

    // 3. Tìm 1 task theo ID và User để đảm bảo người dùng chỉ tương tác với task của chính mình
    Optional<Task> findByIdAndUser(Long id, User user);

    // 4. Lọc task của user theo trạng thái
    List<Task> findByUserAndStatus(User user, String status);
}
