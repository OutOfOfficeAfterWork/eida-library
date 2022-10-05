package org.outofoffice.eida.application;

import lombok.RequiredArgsConstructor;
import org.outofoffice.eida.domain.Major;
import org.outofoffice.eida.domain.Student;
import org.outofoffice.eida.domain.StudentRepository;

import java.util.List;

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
