package com.example.todolist.controller;

import com.example.todolist.model.Task;
import com.example.todolist.model.User;
import com.example.todolist.service.TaskService;
import com.example.todolist.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    // 1. Trang chủ: Hiển thị danh sách task của User đang đăng nhập
    @GetMapping("/")
    public String viewHomePage(@RequestParam(value = "keyword", required = false) String keyword,
                               Model model) {
        User currentUser = userService.getCurrentUser();
        List<Task> tasks;

        if (keyword != null && !keyword.trim().isEmpty()) {
            tasks = taskService.searchTasksByUser(currentUser, keyword);
            model.addAttribute("keyword", keyword);
        } else {
            tasks = taskService.getTasksByUser(currentUser);
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("tasks", tasks);
        if (!model.containsAttribute("newTask")) {
            model.addAttribute("newTask", new Task());
        }
        return "index";
    }

    // 2. Thêm task mới gắn với User đang đăng nhập
    @PostMapping("/tasks/add")
    public String addTask(@Valid @ModelAttribute("newTask") Task task,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("tasks", taskService.getTasksByUser(currentUser));
            return "index";
        }

        taskService.saveTaskForUser(task, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Task added successfully!");
        return "redirect:/";
    }

    // 3. Đổi trạng thái Hoàn thành / Chưa hoàn thành của task
    @GetMapping("/tasks/toggle/{id}")
    public String toggleTask(@PathVariable("id") Long id,
                             RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();
        Task updated = taskService.toggleStatusForUser(id, currentUser);
        redirectAttributes.addFlashAttribute("successMessage",
                "Task marked as " + updated.getStatus() + "!");
        return "redirect:/";
    }

    // 4. Cập nhật task của User
    @PostMapping("/tasks/update/{id}")
    public String updateTask(@PathVariable("id") Long id,
                             @ModelAttribute Task taskDetails,
                             RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();
        taskService.updateTaskForUser(id, taskDetails, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Task updated successfully!");
        return "redirect:/";
    }

    // 5. Xóa task của User
    @GetMapping("/tasks/delete/{id}")
    public String deleteTask(@PathVariable("id") Long id,
                             RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();
        taskService.deleteTaskForUser(id, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Task deleted successfully!");
        return "redirect:/";
    }
}
