package com.example.springapp.config.auth;

import com.example.springapp.domain.Student;
import lombok.Getter;
import org.springframework.security.core.userdetails.User;

import java.util.Set;

@Getter
public class StudentUser extends User {
    private final Student student;

    public StudentUser(Student student) {
        super(student.getStudentCode(),
            student.getBirthDate(),
            Set.of());
        this.student = student;
    }

    @Override
    public String toString(){
        return super.toString() + student.toString();
    }

}
