package com.example.springapp.application;

import com.example.springapp.domain.Major;
import com.example.springapp.domain.Student;
import com.example.springapp.domain.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final MajorService majorService;
    private final StudentRepository studentRepository;

    public void save(String code, String name, String birthDate, String gender, String majorName) {
        Major major = majorService.mustFind(majorName);
        Student student = new Student(code, name, birthDate, gender, major);
        studentRepository.insert(student);
    }

    public Student mustFind(String studentCode) {
        return studentRepository.joinFind(studentCode, "major").orElseThrow();
    }

    public List<Student> findAll() {
        return studentRepository.listAll();
    }
}
