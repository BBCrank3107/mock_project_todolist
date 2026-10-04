package com.example.todolist.controller;

import com.example.todolist.model.Task;
import com.example.todolist.service.TaskService;
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

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // 1. Trang chủ: Hiển thị danh sách task và ô tìm kiếm
    @GetMapping("/")
    public String viewHomePage(@RequestParam(value = "keyword", required = false) String keyword,
                               Model model) {
        List<Task> tasks;
        if (keyword != null && !keyword.trim().isEmpty()) {
            tasks = taskService.searchTasks(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            tasks = taskService.getAllTasks();
        }

        model.addAttribute("tasks", tasks);
        if (!model.containsAttribute("newTask")) {
            model.addAttribute("newTask", new Task());
        }
        return "index";
    }

    // 2. Thêm task mới (có validation)
    @PostMapping("/tasks/add")
    public String addTask(@Valid @ModelAttribute("newTask") Task task,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            // Khi có lỗi validation, load lại danh sách task và trả về trang index để hiển thị lỗi
            model.addAttribute("tasks", taskService.getAllTasks());
            return "index";
        }

        taskService.saveTask(task);
        redirectAttributes.addFlashAttribute("successMessage", "Task added successfully!");
        return "redirect:/";
    }

    // 3. Đổi trạng thái Hoàn thành / Chưa hoàn thành
    @GetMapping("/tasks/toggle/{id}")
    public String toggleTask(@PathVariable("id") Long id,
                             RedirectAttributes redirectAttributes) {
        Task updated = taskService.toggleStatus(id);
        redirectAttributes.addFlashAttribute("successMessage",
                "Task marked as " + updated.getStatus() + "!");
        return "redirect:/";
    }

    // 4. Cập nhật task (từ Modal Edit)
    @PostMapping("/tasks/update/{id}")
    public String updateTask(@PathVariable("id") Long id,
                             @ModelAttribute Task taskDetails,
                             RedirectAttributes redirectAttributes) {
        taskService.updateTask(id, taskDetails);
        redirectAttributes.addFlashAttribute("successMessage", "Task updated successfully!");
        return "redirect:/";
    }

    // 5. Xóa task
    @GetMapping("/tasks/delete/{id}")
    public String deleteTask(@PathVariable("id") Long id,
                             RedirectAttributes redirectAttributes) {
        taskService.deleteTask(id);
        redirectAttributes.addFlashAttribute("successMessage", "Task deleted successfully!");
        return "redirect:/";
    }
}
