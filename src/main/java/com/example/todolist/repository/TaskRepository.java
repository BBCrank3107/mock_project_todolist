package com.example.todolist.repository;

import com.example.todolist.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Tìm kiếm task theo từ khóa trong tiêu đề (Không phân biệt hoa thường)
    List<Task> findByTitleContainingIgnoreCase(String keyword);

    // Lọc task theo trạng thái ("Incomplete" hoặc "Completed")
    List<Task> findByStatus(String status);
}
