package com.example.todolist.service;

import com.example.todolist.exception.ResourceNotFoundException;
import com.example.todolist.model.Task;
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

    // 1. Lấy tất cả các task
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // 2. Tìm task theo ID (Ném ngoại lệ nếu không tìm thấy)
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    // 3. Tạo mới hoặc lưu task
    public Task saveTask(Task task) {
        if (task.getStatus() == null || task.getStatus().isBlank()) {
            task.setStatus("Incomplete");
        }
        return taskRepository.save(task);
    }

    // 4. Cập nhật task
    public Task updateTask(Long id, Task taskDetails) {
        Task existingTask = getTaskById(id);
        existingTask.setTitle(taskDetails.getTitle());
        if (taskDetails.getStatus() != null && !taskDetails.getStatus().isBlank()) {
            existingTask.setStatus(taskDetails.getStatus());
        }
        return taskRepository.save(existingTask);
    }

    // 5. Đổi trạng thái giữa "Incomplete" và "Completed"
    public Task toggleStatus(Long id) {
        Task task = getTaskById(id);
        if ("Completed".equalsIgnoreCase(task.getStatus())) {
            task.setStatus("Incomplete");
        } else {
            task.setStatus("Completed");
        }
        return taskRepository.save(task);
    }

    // 6. Xóa task theo ID
    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }

    // 7. Tìm kiếm task theo từ khóa
    public List<Task> searchTasks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return taskRepository.findAll();
        }
        return taskRepository.findByTitleContainingIgnoreCase(keyword.trim());
    }
}
