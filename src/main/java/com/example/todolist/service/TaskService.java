package com.example.todolist.service;

import com.example.todolist.exception.ResourceNotFoundException;
import com.example.todolist.model.Task;
import com.example.todolist.model.User;
import com.example.todolist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // 1. Lấy tất cả các task của một User cụ thể (Bảo mật cách ly dữ liệu)
    public List<Task> getTasksByUser(User user) {
        return taskRepository.findByUser(user);
    }

    // 2. Tìm kiếm task của user theo từ khóa
    public List<Task> searchTasksByUser(User user, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return taskRepository.findByUser(user);
        }
        return taskRepository.findByUserAndTitleContainingIgnoreCase(user, keyword.trim());
    }

    // 3. Tìm task theo ID và User (Nếu không tìm thấy hoặc task thuộc user khác -> ném ngoại lệ)
    public Task getTaskByIdAndUser(Long id, User user) {
        return taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found or access denied for id: " + id));
    }

    // 4. Tạo mới task gắn với User
    public Task saveTaskForUser(Task task, User user) {
        if (task.getStatus() == null || task.getStatus().isBlank()) {
            task.setStatus("Incomplete");
        }
        task.setUser(user);
        return taskRepository.save(task);
    }

    // 5. Cập nhật task của User
    public Task updateTaskForUser(Long id, Task taskDetails, User user) {
        Task existingTask = getTaskByIdAndUser(id, user);
        existingTask.setTitle(taskDetails.getTitle());
        if (taskDetails.getStatus() != null && !taskDetails.getStatus().isBlank()) {
            existingTask.setStatus(taskDetails.getStatus());
        }
        return taskRepository.save(existingTask);
    }

    // 6. Đổi trạng thái giữa "Incomplete" và "Completed" cho task của User
    public Task toggleStatusForUser(Long id, User user) {
        Task task = getTaskByIdAndUser(id, user);
        if ("Completed".equalsIgnoreCase(task.getStatus())) {
            task.setStatus("Incomplete");
        } else {
            task.setStatus("Completed");
        }
        return taskRepository.save(task);
    }

    // 7. Xóa task của User
    public void deleteTaskForUser(Long id, User user) {
        Task task = getTaskByIdAndUser(id, user);
        taskRepository.delete(task);
    }

    // --- Các hàm dự phòng / phục vụ test không cần user ---
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    public Task saveTask(Task task) {
        if (task.getStatus() == null || task.getStatus().isBlank()) {
            task.setStatus("Incomplete");
        }
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }

    public Task toggleStatus(Long id) {
        Task task = getTaskById(id);
        if ("Completed".equalsIgnoreCase(task.getStatus())) {
            task.setStatus("Incomplete");
        } else {
            task.setStatus("Completed");
        }
        return taskRepository.save(task);
    }
}
