package com.example.todolist.controller;

import com.example.todolist.model.Task;
import com.example.todolist.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskRestController {

    private final TaskService taskService;

    @Autowired
    public TaskRestController(TaskService taskService) {
        this.taskService = taskService;
    }

    // 1. GET /api/v1/tasks : Lấy tất cả task (hoặc tìm kiếm theo keyword)
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(
            @RequestParam(value = "keyword", required = false) String keyword) {
        List<Task> tasks;
        if (keyword != null && !keyword.trim().isEmpty()) {
            tasks = taskService.searchTasks(keyword);
        } else {
            tasks = taskService.getAllTasks();
        }
        return ResponseEntity.ok(tasks);
    }

    // 2. GET /api/v1/tasks/{id} : Lấy chi tiết 1 task theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable("id") Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    // 3. POST /api/v1/tasks : Tạo task mới
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        Task savedTask = taskService.saveTask(task);
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    // 4. PUT /api/v1/tasks/{id} : Cập nhật task
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable("id") Long id,
                                          @Valid @RequestBody Task taskDetails) {
        Task updatedTask = taskService.updateTask(id, taskDetails);
        return ResponseEntity.ok(updatedTask);
    }

    // 5. DELETE /api/v1/tasks/{id} : Xóa task
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTask(@PathVariable("id") Long id) {
        taskService.deleteTask(id);
        Map<String, Object> response = new HashMap<>();
        response.put("deleted", true);
        response.put("message", "Task with id " + id + " has been successfully deleted.");
        return ResponseEntity.ok(response);
    }
}
