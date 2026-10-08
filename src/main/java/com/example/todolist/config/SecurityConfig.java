package com.example.todolist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Tắt CSRF cho H2-Console và RESTful API để dễ dàng test bằng Postman
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/api/**")
            )
            // 2. Cấu hình FrameOptions để cho phép hiển thị iframe của H2 Console
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )
            // 3. Phân quyền đường dẫn
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "/h2-console/**", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/api/**").authenticated() // REST API yêu cầu xác thực (Basic Auth)
                .anyRequest().authenticated()               // Tất cả trang khác đều phải đăng nhập
            )
            // 4. Cấu hình Form Login cho Web giao diện
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            // 5. Cấu hình Logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            // 6. Cho phép HTTP Basic Authentication để Postman kiểm thử REST API thuận tiện
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
