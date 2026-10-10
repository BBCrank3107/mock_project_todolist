package com.example.todolist.service;

import com.example.todolist.exception.ResourceNotFoundException;
import com.example.todolist.model.Task;
import com.example.todolist.model.User;
import com.example.todolist.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private User sampleUser;
    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleUser = new User("john", "password123");
        sampleUser.setId(1L);

        sampleTask = new Task(100L, "Sample Task", "Incomplete", sampleUser);
    }

    @Test
    @DisplayName("Unit Test: Tạo task mới gắn với User thành công")
    void testSaveTaskForUser_Success() {
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        Task created = taskService.saveTaskForUser(new Task("Sample Task"), sampleUser);

        assertNotNull(created);
        assertEquals("Sample Task", created.getTitle());
        assertEquals("Incomplete", created.getStatus());
        assertEquals(sampleUser, created.getUser());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Unit Test: Lấy danh sách task của User")
    void testGetTasksByUser_Success() {
        Task task2 = new Task(101L, "Second Task", "Completed", sampleUser);
        when(taskRepository.findByUser(sampleUser)).thenReturn(Arrays.asList(sampleTask, task2));

        List<Task> result = taskService.getTasksByUser(sampleUser);

        assertEquals(2, result.size());
        assertEquals("Sample Task", result.get(0).getTitle());
        verify(taskRepository, times(1)).findByUser(sampleUser);
    }

    @Test
    @DisplayName("Unit Test: Đổi trạng thái từ Incomplete sang Completed")
    void testToggleStatusForUser_Success() {
        when(taskRepository.findByIdAndUser(100L, sampleUser)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task updated = taskService.toggleStatusForUser(100L, sampleUser);

        assertEquals("Completed", updated.getStatus());
        verify(taskRepository, times(1)).save(sampleTask);
    }

    @Test
    @DisplayName("Unit Test: Xóa task của User")
    void testDeleteTaskForUser_Success() {
        when(taskRepository.findByIdAndUser(100L, sampleUser)).thenReturn(Optional.of(sampleTask));
        doNothing().when(taskRepository).delete(sampleTask);

        taskService.deleteTaskForUser(100L, sampleUser);

        verify(taskRepository, times(1)).delete(sampleTask);
    }

    @Test
    @DisplayName("Unit Test: Ném ngoại lệ ResourceNotFoundException khi task không tồn tại hoặc không thuộc User")
    void testGetTaskByIdAndUser_NotFound_ThrowsException() {
        when(taskRepository.findByIdAndUser(999L, sampleUser)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.getTaskByIdAndUser(999L, sampleUser);
        });

        verify(taskRepository, times(1)).findByIdAndUser(999L, sampleUser);
    }
}
