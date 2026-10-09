package com.example.todolist.controller;

import com.example.todolist.model.Task;
import com.example.todolist.model.User;
import com.example.todolist.service.TaskService;
import com.example.todolist.service.UserService;
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

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskRestController {

    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public TaskRestController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    private User getAuthenticatedUser(Principal principal) {
        String username = principal.getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    // 1. GET /api/v1/tasks : Lấy danh sách task của User đang xác thực
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(
            @RequestParam(value = "keyword", required = false) String keyword,
            Principal principal) {
        User user = getAuthenticatedUser(principal);
        List<Task> tasks;
        if (keyword != null && !keyword.trim().isEmpty()) {
            tasks = taskService.searchTasksByUser(user, keyword);
        } else {
            tasks = taskService.getTasksByUser(user);
        }
        return ResponseEntity.ok(tasks);
    }

    // 2. GET /api/v1/tasks/{id} : Lấy chi tiết 1 task của User
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable("id") Long id, Principal principal) {
        User user = getAuthenticatedUser(principal);
        Task task = taskService.getTaskByIdAndUser(id, user);
        return ResponseEntity.ok(task);
    }

    // 3. POST /api/v1/tasks : Tạo task mới gắn với User
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task, Principal principal) {
        User user = getAuthenticatedUser(principal);
        Task savedTask = taskService.saveTaskForUser(task, user);
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    // 4. PUT /api/v1/tasks/{id} : Cập nhật task của User
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable("id") Long id,
                                          @Valid @RequestBody Task taskDetails,
                                          Principal principal) {
        User user = getAuthenticatedUser(principal);
        Task updatedTask = taskService.updateTaskForUser(id, taskDetails, user);
        return ResponseEntity.ok(updatedTask);
    }

    // 5. DELETE /api/v1/tasks/{id} : Xóa task của User
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTask(@PathVariable("id") Long id, Principal principal) {
        User user = getAuthenticatedUser(principal);
        taskService.deleteTaskForUser(id, user);
        Map<String, Object> response = new HashMap<>();
        response.put("deleted", true);
        response.put("message", "Task with id " + id + " has been successfully deleted.");
        return ResponseEntity.ok(response);
    }
}
