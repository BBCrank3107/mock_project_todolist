package com.example.todolist.config;

import com.example.todolist.model.Task;
import com.example.todolist.model.User;
import com.example.todolist.repository.TaskRepository;
import com.example.todolist.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      TaskRepository taskRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            // Khởi tạo tài khoản mẫu nếu chưa có dữ liệu
            if (userRepository.count() == 0) {
                // User 1: john
                User john = new User("john", passwordEncoder.encode("password123"), "ROLE_USER");
                userRepository.save(john);

                // User 2: alice
                User alice = new User("alice", passwordEncoder.encode("password123"), "ROLE_USER");
                userRepository.save(alice);

                // Task của john
                Task task1 = new Task(null, "Complete Spring Boot Mock Project", "Completed", john);
                Task task2 = new Task(null, "Prepare presentation slides for Mentor", "Incomplete", john);
                taskRepository.save(task1);
                taskRepository.save(task2);

                // Task riêng của alice
                Task task3 = new Task(null, "Alice's personal confidential task", "Incomplete", alice);
                taskRepository.save(task3);

                System.out.println(">>> Demo users initialized successfully:");
                System.out.println("    - User 1: username='john', password='password123'");
                System.out.println("    - User 2: username='alice', password='password123'");
            }
        };
    }
}
