package org.outofoffice.eida.application;

import lombok.RequiredArgsConstructor;
import org.outofoffice.eida.domain.*;

import java.util.List;

@RequiredArgsConstructor
public class EnrollmentService {
    private final SubjectService subjectService;
    private final StudentService studentService;
    private final EnrollmentRepository enrolmentRepository;

    public void insert(String subjectName, String studentCode) {
        Student student = studentService.mustFind(studentCode);
        Subject subject = subjectService.mustFind(subjectName);

        String compositeKey = studentCode + "-" + subjectName;
        Enrollment enrolment = new Enrollment(compositeKey, student, subject);
        enrolmentRepository.insert(enrolment);
    }

    public Enrollment mustFind(String studentCode, String subjectName) {
        String compositeKey = studentCode + "-" + subjectName;
        return enrolmentRepository.find(compositeKey).orElseThrow();
    }

    public List<Enrollment> findByStudent(String studentCode) {
        return enrolmentRepository.joinList(e -> e.getStudent().getStudentCode().equals(studentCode), "subject");
    }

    public List<Enrollment> findBySubject(String subjectName) {
        return enrolmentRepository.joinList(e -> e.getSubject().getSubjectName().equals(subjectName), "student");
    }

}
