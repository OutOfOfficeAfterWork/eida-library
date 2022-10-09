package com.example.springapp.config.auth;

import com.example.springapp.application.StudentService;
import com.example.springapp.domain.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginComponent implements UserDetailsService {

    private static final String ADMIN_LOGIN_ID = "admin";

    private final StudentService studentService;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        if (loginId.equals(ADMIN_LOGIN_ID)) {
            return new AdminUser();
        } else {
            Student student = studentService.mustFind(loginId);
            return new StudentUser(student);
        }
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }
}
